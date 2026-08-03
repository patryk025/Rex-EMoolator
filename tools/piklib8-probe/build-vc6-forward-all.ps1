param(
    [Parameter(Mandatory = $false)]
    [string]$OriginalExe = 'D:\Program Files\AidemMedia\Reksio i Czarodzieje\Czarodzieje.exe',

    [Parameter(Mandatory = $false)]
    [string]$Vc6MediaRoot = 'F:\',

    [Parameter(Mandatory = $false)]
    [string]$OutputDirectory = (Join-Path $PSScriptRoot 'build-vc6-forward-all')
)

$ErrorActionPreference = 'Stop'
$vcRoot = Join-Path $Vc6MediaRoot 'VC98'
$vcBin = Join-Path $vcRoot 'BIN'
$commonBin = Join-Path $Vc6MediaRoot 'COMMON\MSDEV98\BIN'
$linker = Join-Path $vcBin 'LINK.EXE'
$pdbRuntime = Join-Path $commonBin 'MSPDB60.DLL'

foreach ($required in @($linker, $pdbRuntime, $OriginalExe)) {
    if (-not (Test-Path -LiteralPath $required -PathType Leaf)) {
        throw "Required file was not found: $required"
    }
}

New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$output = (Resolve-Path -LiteralPath $OutputDirectory).Path
$definition = Join-Path $output 'p8probe.def'
$dll = Join-Path $output 'P8PROBE.dll'
$map = Join-Path $output 'P8PROBE.map'

& python (Join-Path $PSScriptRoot 'generate_proxy_def.py') `
    $OriginalExe $definition --forward-domodal
if ($LASTEXITCODE -ne 0) {
    throw 'Generating the all-forwarder definition failed.'
}

$command = @(
    "set `"PATH=$vcBin;$commonBin;%PATH%`"",
    "`"$linker`" /NOLOGO /DLL /NOENTRY /MACHINE:I386 /BASE:0x68000000 /SUBSYSTEM:WINDOWS,4.0 /DEF:`"$definition`" /OUT:`"$dll`" /MAP:`"$map`""
) -join ' && '

& cmd.exe /d /s /c $command
if ($LASTEXITCODE -ne 0) {
    throw "VC6 all-forwarder build failed with exit code $LASTEXITCODE."
}

& python (Join-Path $PSScriptRoot 'verify_build.py') `
    $OriginalExe $dll --forward-domodal
if ($LASTEXITCODE -ne 0) {
    throw 'All-forwarder DLL verification failed.'
}

Write-Host "Built and verified zero-code control proxy: $dll"
