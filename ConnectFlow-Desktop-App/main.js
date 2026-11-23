const { app, BrowserWindow, dialog } = require('electron');
const path = require('path');
const http = require('http');
const { spawn } = require('child_process');

let mainWindow = null;
let backendProcess = null;
const BACKEND_PORT = 8080;
const HEALTH_URL = `http://localhost:${BACKEND_PORT}/`;

function getJavaExecutable() {
    const isPackaged = app.isPackaged;
    if (isPackaged) {
        const bundledJava = path.join(process.resourcesPath, 'jre-min', 'bin', 'java.exe');
        if (require('fs').existsSync(bundledJava)) return bundledJava;
    } else {
        const localJre = path.join(__dirname, '..', 'jre-min', 'bin', 'java.exe');
        if (require('fs').existsSync(localJre)) return localJre;
    }
    return 'java';
}

function getJarPath() {
    const isPackaged = app.isPackaged;
    if (isPackaged) {
        return path.join(process.resourcesPath, 'connectflow-backend-1.0.0.jar');
    }
    return path.join(__dirname, '..', 'target', 'connectflow-backend-1.0.0.jar');
}

function startBackend() {
    const javaExe = getJavaExecutable();
    const jarPath = getJarPath();
    const fs = require('fs');

    if (!fs.existsSync(jarPath)) {
        console.warn(`[ConnectFlow] Backend JAR not found at ${jarPath}. Loading local UI directly.`);
        return null;
    }

    console.log(`[ConnectFlow] Spawning backend: ${javaExe} -jar ${jarPath}`);
    const proc = spawn(javaExe, ['-jar', jarPath, `--server.port=${BACKEND_PORT}`], {
        cwd: path.dirname(jarPath),
        detached: false,
        stdio: 'ignore',
        windowsHide: true
    });

    proc.on('error', (err) => {
        console.error('[ConnectFlow] Failed to start backend:', err);
    });

    return proc;
}

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
                    resolve(false);
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

    http.get(HEALTH_URL, () => {
        mainWindow.loadURL(HEALTH_URL);
    }).on('error', () => {
        const localHtml = path.join(__dirname, 'app', 'index.html');
        mainWindow.loadFile(localHtml);
    });

    mainWindow.on('closed', () => {
        mainWindow = null;
    });
}

function stopBackend() {
    if (backendProcess && backendProcess.pid) {
        try {
            console.log(`[ConnectFlow] Terminating backend PID: ${backendProcess.pid}`);
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
