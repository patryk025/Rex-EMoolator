param(
    [Parameter(Mandatory = $false)]
    [string]$OriginalExe = 'D:\Program Files\AidemMedia\Reksio i Czarodzieje\Czarodzieje.exe',

    [Parameter(Mandatory = $false)]
    [string]$OutputDirectory = (Join-Path $PSScriptRoot 'build-msvc2017')
)

$ErrorActionPreference = 'Stop'
$vcvars = 'C:\Program Files (x86)\Microsoft Visual Studio\2017\BuildTools\VC\Auxiliary\Build\vcvars32.bat'

if (-not (Test-Path -LiteralPath $vcvars)) {
    throw "MSVC 2017 vcvars32.bat was not found: $vcvars"
}
if (-not (Test-Path -LiteralPath $OriginalExe)) {
    throw "Original executable was not found: $OriginalExe"
}

New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$output = (Resolve-Path -LiteralPath $OutputDirectory).Path
$source = Join-Path $PSScriptRoot 'p8probe.cpp'
$definition = Join-Path $output 'p8probe.def'
$object = Join-Path $output 'p8probe.obj'
$dll = Join-Path $output 'P8PROBE.dll'
$map = Join-Path $output 'P8PROBE.map'

& python (Join-Path $PSScriptRoot 'generate_proxy_def.py') $OriginalExe $definition
if ($LASTEXITCODE -ne 0) {
    throw 'Generating p8probe.def failed.'
}

$command = @(
    "call `"$vcvars`" >nul",
    "cl.exe /nologo /c /O2 /Os /W4 /GS- /GR- /Zl /DWIN32 /D_WINDOWS /Fo`"$object`" `"$source`"",
    # PIKLIB8 itself uses the default DLL base 0x10000000. The proxy is loaded
    # first, so it must not steal that address and force the legacy DLL to move.
    "link.exe /NOLOGO /DLL /NODEFAULTLIB /MACHINE:X86 /BASE:0x68000000 /SUBSYSTEM:WINDOWS,5.01 /DYNAMICBASE:NO /NXCOMPAT:NO /ENTRY:P8ProbeDllMain@12 /DEF:`"$definition`" /OUT:`"$dll`" /MAP:`"$map`" `"$object`" kernel32.lib"
) -join ' && '

& cmd.exe /d /s /c $command
if ($LASTEXITCODE -ne 0) {
    throw "Native build failed with exit code $LASTEXITCODE."
}

& python (Join-Path $PSScriptRoot 'verify_build.py') $OriginalExe $dll
if ($LASTEXITCODE -ne 0) {
    throw 'Built DLL verification failed.'
}

Write-Host "Built and verified: $dll"
