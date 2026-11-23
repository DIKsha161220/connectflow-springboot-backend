Dim fso, zipPath, folderPath, sa, zipFile, sourceFolder
Set fso = CreateObject("Scripting.FileSystemObject")
strDir = fso.GetParentFolderName(WScript.ScriptFullName)

zipPath = strDir & "\ConnectFlow-Desktop-App.zip"
folderPath = strDir & "\ConnectFlow-Desktop-App"

If fso.FileExists(zipPath) Then
    fso.DeleteFile zipPath, True
End If

' Create empty zip file
fso.CreateTextFile(zipPath, True).Write "PK" & Chr(5) & Chr(6) & String(18, vbNullChar)

Set sa = CreateObject("Shell.Application")
Set zipFile = sa.NameSpace(zipPath)
Set sourceFolder = sa.NameSpace(folderPath)

zipFile.CopyHere sourceFolder.Items, 4 + 16
WScript.Sleep 2000

WScript.Echo "SUCCESS: ConnectFlow-Desktop-App.zip तयार झाली आहे!"
