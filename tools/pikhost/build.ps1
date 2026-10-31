param(
    [Parameter(Mandatory = $false)]
    [string]$OutputDirectory
)

$ErrorActionPreference = 'Stop'
# Windows PowerShell leaves $PSScriptRoot empty in param defaults when started with -File.
if (-not $OutputDirectory) {
    $OutputDirectory = Join-Path $PSScriptRoot 'build'
}
# Any x86 MSVC works; the host only talks to the engine through its exports.
$vcvars = 'C:\Program Files (x86)\Microsoft Visual Studio\2017\BuildTools\VC\Auxiliary\Build\vcvars32.bat'

if (-not (Test-Path -LiteralPath $vcvars)) {
    throw "MSVC 2017 vcvars32.bat was not found: $vcvars"
}

New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$output = (Resolve-Path -LiteralPath $OutputDirectory).Path
$source = Join-Path $PSScriptRoot 'pikhost.cpp'
$object = Join-Path $output 'pikhost.obj'
$exe = Join-Path $output 'pikhost.exe'

$command = @(
    "call `"$vcvars`" >nul",
    # /EHa: engine crashes are caught as SEH exceptions and reported, not fatal.
    # /DYNAMICBASE:NO with a high base keeps 0x10000000 free for the engine DLL,
    # so crash addresses reported as RVAs match the Ghidra listings.
    "cl.exe /nologo /O1 /W3 /EHa /MT /D_CRT_SECURE_NO_WARNINGS /Fo`"$object`" /Fe`"$exe`" `"$source`" user32.lib /link /BASE:0x00400000 /DYNAMICBASE:NO /NXCOMPAT:NO"
) -join ' && '

& cmd.exe /d /s /c $command
if ($LASTEXITCODE -ne 0) {
    throw "Native build failed with exit code $LASTEXITCODE."
}

Write-Host "Built: $exe"
