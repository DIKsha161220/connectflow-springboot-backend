Set WshShell = CreateObject("WScript.Shell")
strDesktop = WshShell.SpecialFolders("Desktop")
Set oShortcut = WshShell.CreateShortcut(strDesktop & "\ConnectFlow-CRM.lnk")
oShortcut.TargetPath = "D:\CRM-BACKEND12\ConnectFlow-CRM.exe"
oShortcut.WorkingDirectory = "D:\CRM-BACKEND12"
oShortcut.Description = "ConnectFlow CRM Desktop System"
oShortcut.Save
