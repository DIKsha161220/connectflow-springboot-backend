Set WshShell = WScript.CreateObject("WScript.Shell")
strDesktop = WshShell.SpecialFolders("Desktop")
strCurrentDir = WshShell.CurrentDirectory

Set oShellLink = WshShell.CreateShortcut(strDesktop & "\ConnectFlow CRM.lnk")
oShellLink.TargetPath = strCurrentDir & "\ConnectFlow-CRM.exe"
oShellLink.WorkingDirectory = strCurrentDir
oShellLink.WindowStyle = 1
oShellLink.Description = "ConnectFlow CRM Standalone Desktop Application"
oShellLink.IconLocation = "shell32.dll, 23"
oShellLink.Save

WScript.Echo "ConnectFlow CRM Desktop shortcut successfully created on your Desktop!"
