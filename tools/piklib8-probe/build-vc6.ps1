param(
    [Parameter(Mandatory = $false)]
    [string]$OriginalExe = 'D:\Program Files\AidemMedia\Reksio i Czarodzieje\Czarodzieje.exe',

    [Parameter(Mandatory = $false)]
    [string]$Vc6MediaRoot = 'F:\',

    [Parameter(Mandatory = $false)]
    [string]$OutputDirectory = (Join-Path $PSScriptRoot 'build-vc6')
)

$ErrorActionPreference = 'Stop'
$vcRoot = Join-Path $Vc6MediaRoot 'VC98'
$vcBin = Join-Path $vcRoot 'BIN'
$commonBin = Join-Path $Vc6MediaRoot 'COMMON\MSDEV98\BIN'
$compiler = Join-Path $vcBin 'CL.EXE'
$linker = Join-Path $vcBin 'LINK.EXE'
$pdbRuntime = Join-Path $commonBin 'MSPDB60.DLL'

foreach ($required in @($compiler, $linker, $pdbRuntime)) {
    if (-not (Test-Path -LiteralPath $required)) {
        throw "Required VC6 file was not found: $required"
    }
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

$include = Join-Path $vcRoot 'INCLUDE'
$lib = Join-Path $vcRoot 'LIB'
$command = @(
    "set `"PATH=$vcBin;$commonBin;%PATH%`"",
    "set `"INCLUDE=$include`"",
    "set `"LIB=$lib`"",
    "`"$compiler`" /nologo /c /O2 /Os /Oi /G4 /W4 /GX- /GR- /Zl /DWIN32 /D_WINDOWS /DWINVER=0x0400 /D_WIN32_WINDOWS=0x0410 /Fo`"$object`" `"$source`"",
    # PIKLIB8 itself uses the default DLL base 0x10000000. The proxy is loaded
    # first, so it must not steal that address and force the legacy DLL to move.
    "`"$linker`" /NOLOGO /DLL /NODEFAULTLIB /MACHINE:I386 /BASE:0x68000000 /SUBSYSTEM:WINDOWS,4.0 /ENTRY:P8ProbeDllMain@12 /DEF:`"$definition`" /OUT:`"$dll`" /MAP:`"$map`" `"$object`" kernel32.lib"
) -join ' && '

& cmd.exe /d /s /c $command
if ($LASTEXITCODE -ne 0) {
    throw "VC6 build failed with exit code $LASTEXITCODE."
}

& python (Join-Path $PSScriptRoot 'verify_build.py') $OriginalExe $dll
if ($LASTEXITCODE -ne 0) {
    throw 'Built VC6 DLL verification failed.'
}

Write-Host "Built and verified with VC6: $dll"
