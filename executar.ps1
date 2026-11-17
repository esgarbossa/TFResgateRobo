$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
New-Item -ItemType Directory -Force -Path 'build/classes' | Out-Null
$fontes = Get-ChildItem -LiteralPath 'src' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
& javac -encoding UTF-8 -d 'build/classes' $fontes
if ($LASTEXITCODE -ne 0) { throw 'Falha na compilacao.' }
& java -cp 'build/classes' Main
