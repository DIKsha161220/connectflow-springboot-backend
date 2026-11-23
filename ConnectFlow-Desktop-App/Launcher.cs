using System;
using System.Diagnostics;
using System.IO;
using System.Net.Sockets;

namespace ConnectFlow
{
    class Program
    {
        static bool IsPortOpen(string host, int port)
        {
            try
            {
                using (var client = new TcpClient())
                {
                    var result = client.BeginConnect(host, port, null, null);
                    bool success = result.AsyncWaitHandle.WaitOne(800);
                    if (!success) return false;
                    client.EndConnect(result);
                    return true;
                }
            }
            catch
            {
                return false;
            }
        }

        [STAThread]
        static void Main()
        {
            string currentDir = AppDomain.CurrentDomain.BaseDirectory;

            // Check if backend on port 8080 is running. If not, auto-start in background.
            bool backendReady = IsPortOpen("127.0.0.1", 8080);
            if (!backendReady)
            {
                string batPath = Path.Combine(currentDir, "Start-Backend.bat");
                if (!File.Exists(batPath) && Directory.GetParent(currentDir) != null)
                {
                    batPath = Path.Combine(Directory.GetParent(currentDir).FullName, "Start-Backend.bat");
                }
                if (File.Exists(batPath))
                {
                    try
                    {
                        Process.Start(new ProcessStartInfo
                        {
                            FileName = "cmd.exe",
                            Arguments = "/c \"" + batPath + "\"",
                            WorkingDirectory = Path.GetDirectoryName(batPath),
                            CreateNoWindow = true,
                            WindowStyle = ProcessWindowStyle.Hidden,
                            UseShellExecute = false
                        });
                        System.Threading.Thread.Sleep(2000);
                        backendReady = IsPortOpen("127.0.0.1", 8080);
                    }
                    catch { }
                }
            }

            // If backend is active, use http://localhost:8080 for 100% live same-origin connection to PostgreSQL
            string appTargetUrl = backendReady ? "http://localhost:8080" : null;
            if (string.IsNullOrEmpty(appTargetUrl))
            {
                string htmlPath = Path.Combine(currentDir, "app", "index.html");
                if (!File.Exists(htmlPath))
                {
                    htmlPath = Path.Combine(currentDir, "ConnectFlow-Desktop.html");
                }
                appTargetUrl = "file:///" + htmlPath.Replace('\\', '/');
            }

            string edgePath = @"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe";
            if (!File.Exists(edgePath))
            {
                edgePath = @"C:\Program Files\Microsoft\Edge\Application\msedge.exe";
            }

            if (File.Exists(edgePath))
            {
                Process.Start(new ProcessStartInfo
                {
                    FileName = edgePath,
                    Arguments = "--app=\"" + appTargetUrl + "\"",
                    UseShellExecute = true
                });
            }
            else
            {
                Process.Start(new ProcessStartInfo
                {
                    FileName = appTargetUrl,
                    UseShellExecute = true
                });
            }
        }
    }
}
