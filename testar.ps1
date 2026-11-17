$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
New-Item -ItemType Directory -Force -Path 'build/testes' | Out-Null
$fontes = Get-ChildItem -LiteralPath 'src','tests' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
& javac -encoding UTF-8 -d 'build/testes' $fontes
if ($LASTEXITCODE -ne 0) { throw 'Falha na compilacao.' }
& java -cp 'build/testes' TestesSistema
if ($LASTEXITCODE -ne 0) { throw 'Os testes falharam.' }
