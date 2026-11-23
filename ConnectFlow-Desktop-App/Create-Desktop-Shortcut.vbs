Set WshShell = WScript.CreateObject("WScript.Shell")
strDesktop = WshShell.SpecialFolders("Desktop")
strDir = CreateObject("Scripting.FileSystemObject").GetParentFolderName(WScript.ScriptFullName)

Set oShellLink = WshShell.CreateShortcut(strDesktop & "\ConnectFlow CRM.lnk")
oShellLink.TargetPath = strDir & "\ConnectFlow-CRM.vbs"
oShellLink.WorkingDirectory = strDir
oShellLink.WindowStyle = 1
oShellLink.Description = "ConnectFlow CRM Desktop Standalone Application"
oShellLink.IconLocation = "shell32.dll, 23"
oShellLink.Save

WScript.Echo "ConnectFlow CRM चा डेस्कटॉप आयकॉन तुमच्या Desktop वर तयार झाला आहे!"
