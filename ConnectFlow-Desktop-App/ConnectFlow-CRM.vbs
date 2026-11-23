Set WshShell = CreateObject("WScript.Shell")
strDir = CreateObject("Scripting.FileSystemObject").GetParentFolderName(WScript.ScriptFullName)
strHtml = strDir & "\app\index.html"

' Check standard Microsoft Edge path to launch in --app mode (standalone window)
Dim fso
Set fso = CreateObject("Scripting.FileSystemObject")

Dim edgePath
edgePath = ""

If fso.FileExists("C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe") Then
    edgePath = "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
ElseIf fso.FileExists("C:\Program Files\Microsoft\Edge\Application\msedge.exe") Then
    edgePath = "C:\Program Files\Microsoft\Edge\Application\msedge.exe"
End If

If edgePath <> "" Then
    WshShell.Run """" & edgePath & """ --app=""file:///" & Replace(strHtml, "\", "/") & """", 1, False
Else
    WshShell.Run """" & strHtml & """", 1, False
End If
