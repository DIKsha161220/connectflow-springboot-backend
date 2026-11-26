const { app, BrowserWindow, dialog } = require('electron');
const path = require('path');
const http = require('http');
const { spawn } = require('child_process');

let mainWindow = null;
let backendProcess = null;
const BACKEND_PORT = 8080;
const HEALTH_URL = `http://localhost:${BACKEND_PORT}/`;

// Find Java runtime executable (bundled JRE or system PATH)
function getJavaExecutable() {
    const isPackaged = app.isPackaged;
    if (isPackaged) {
        // In packaged app, extraResources puts jre-min in resources/jre-min
        const bundledJava = path.join(process.resourcesPath, 'jre-min', 'bin', 'java.exe');
        if (require('fs').existsSync(bundledJava)) return bundledJava;
    } else {
        const localJre = path.join(__dirname, 'jre-min', 'bin', 'java.exe');
        if (require('fs').existsSync(localJre)) return localJre;
    }
    return 'java'; // Fallback to system Java in PATH
}

// Find Spring Boot JAR file
function getJarPath() {
    const isPackaged = app.isPackaged;
    if (isPackaged) {
        return path.join(process.resourcesPath, 'connectflow-backend-1.0.0.jar');
    }
    const localJar = path.join(__dirname, 'target', 'connectflow-backend-1.0.0.jar');
    if (require('fs').existsSync(localJar)) return localJar;
    const parentJar = path.join(__dirname, '..', 'target', 'connectflow-backend-1.0.0.jar');
    if (require('fs').existsSync(parentJar)) return parentJar;
    return localJar;
}

// Start Spring Boot Backend in background
function startBackend() {
    const javaExe = getJavaExecutable();
    const jarPath = getJarPath();
    const fs = require('fs');

    if (fs.existsSync(jarPath)) {
        console.log(`[ConnectFlow] Spawning backend: ${javaExe} -jar ${jarPath}`);
        const proc = spawn(javaExe, ['-jar', jarPath, `--server.port=${BACKEND_PORT}`], {
            cwd: path.dirname(jarPath),
            detached: false,
            stdio: 'ignore',
            windowsHide: true
        });
        proc.on('error', (err) => console.error('[ConnectFlow] Failed to start backend:', err));
        return proc;
    }

    const rootDir = fs.existsSync(path.join(__dirname, 'mvnw.cmd')) ? __dirname : path.join(__dirname, '..');
    const startBat = path.join(rootDir, 'Start-Backend.bat');
    const mvnw = path.join(rootDir, 'mvnw.cmd');

    if (fs.existsSync(startBat)) {
        console.log(`[ConnectFlow] Spawning backend via Start-Backend.bat`);
        const proc = spawn('cmd.exe', ['/c', startBat], {
            cwd: rootDir,
            detached: false,
            stdio: 'ignore',
            windowsHide: true
        });
        proc.on('error', (err) => console.error('[ConnectFlow] Failed to start backend via bat:', err));
        return proc;
    }

    if (fs.existsSync(mvnw)) {
        console.log(`[ConnectFlow] Spawning backend via Maven wrapper: ${mvnw}`);
        const proc = spawn('cmd.exe', ['/c', mvnw, 'spring-boot:run'], {
            cwd: rootDir,
            detached: false,
            stdio: 'ignore',
            windowsHide: true
        });
        proc.on('error', (err) => console.error('[ConnectFlow] Failed to start backend via mvnw:', err));
        return proc;
    }

    console.warn(`[ConnectFlow] No backend found. Running offline UI.`);
    return null;
}

// Check if Backend is responding
function checkBackendReady(timeoutMs = 30000) {
    const startTime = Date.now();
    return new Promise((resolve) => {
        const interval = setInterval(() => {
            const req = http.get(HEALTH_URL, (res) => {
                clearInterval(interval);
                resolve(true);
            });

            req.on('error', () => {
                if (Date.now() - startTime > timeoutMs) {
                    clearInterval(interval);
                    resolve(false); // Timed out, proceed to open UI anyway
                }
            });
            req.end();
        }, 800);
    });
}

function createWindow() {
    mainWindow = new BrowserWindow({
        width: 1366,
        height: 850,
        minWidth: 1024,
        minHeight: 680,
        title: "ConnectFlow CRM - Standalone Desktop System",
        backgroundColor: '#faf9f8',
        autoHideMenuBar: true,
        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true
        }
    });

    // Load localhost:8080 if running, else load local index.html directly
    http.get(HEALTH_URL, () => {
        mainWindow.loadURL(HEALTH_URL);
    }).on('error', () => {
        const localHtml = path.join(__dirname, 'ConnectFlow-Desktop.html');
        mainWindow.loadFile(localHtml);
    });

    mainWindow.on('closed', () => {
        mainWindow = null;
    });
}

// Cleanly kill backend on exit
function stopBackend() {
    if (backendProcess && backendProcess.pid) {
        try {
            console.log(`[ConnectFlow] Terminating backend process PID: ${backendProcess.pid}`);
            if (process.platform === 'win32') {
                spawn('taskkill', ['/pid', backendProcess.pid, '/f', '/t']);
            } else {
                backendProcess.kill('SIGINT');
            }
        } catch (e) {
            console.error('[ConnectFlow] Error stopping backend:', e);
        }
        backendProcess = null;
    }
}

app.whenReady().then(async () => {
    backendProcess = startBackend();

    // Wait until port 8080 is live or 15 seconds max
    await checkBackendReady(15000);

    createWindow();

    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0) createWindow();
    });
});

app.on('window-all-closed', () => {
    stopBackend();
    if (process.platform !== 'darwin') {
        app.quit();
    }
});

app.on('will-quit', () => {
    stopBackend();
});
