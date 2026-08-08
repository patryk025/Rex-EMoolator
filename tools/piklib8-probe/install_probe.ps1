param(
    [Parameter(Mandatory = $false)]
    [string]$GameDirectory = 'D:\Program Files\AidemMedia\Reksio i Czarodzieje',

    [Parameter(Mandatory = $false)]
    [ValidateSet('vc6', 'msvc2017')]
    [string]$Build = 'vc6',

    [Parameter(Mandatory = $false)]
    [switch]$UpdateDllOnly
)

$ErrorActionPreference = 'Stop'
$originalExe = Join-Path $GameDirectory 'Czarodzieje.exe'
$probeExe = Join-Path $GameDirectory 'Czarodzieje.probe.exe'
$probeDllDestination = Join-Path $GameDirectory 'P8PROBE.dll'
$buildDirectory = if ($Build -eq 'vc6') { 'build-vc6' } else { 'build-msvc2017' }
$probeDllSource = Join-Path (Join-Path $PSScriptRoot $buildDirectory) 'P8PROBE.dll'

foreach ($required in @($originalExe, $probeDllSource)) {
    if (-not (Test-Path -LiteralPath $required -PathType Leaf)) {
        throw "Required file was not found: $required"
    }
}

& python (Join-Path $PSScriptRoot 'verify_build.py') $originalExe $probeDllSource
if ($LASTEXITCODE -ne 0) {
    throw 'Probe DLL verification failed.'
}

if ($UpdateDllOnly) {
    $expectedProbeExeHash = '13760308FE9AD00737F50B2186B6B1090B814EFA1EC01AD27DFF898FBDFCDE02'
    if (-not (Test-Path -LiteralPath $probeExe -PathType Leaf)) {
        throw "Patched executable was not found: $probeExe"
    }
    $actualProbeExeHash = (Get-FileHash -Algorithm SHA256 -LiteralPath $probeExe).Hash
    if ($actualProbeExeHash -ne $expectedProbeExeHash) {
        throw "Refusing to update DLL: unsupported Czarodzieje.probe.exe SHA-256 $actualProbeExeHash"
    }
    [System.IO.File]::Copy($probeDllSource, $probeDllDestination, $true)
    Write-Host 'Updated only the generated probe DLL; original game files were not modified:'
    Write-Host "  $probeDllDestination"
    return
}

foreach ($destination in @($probeExe, $probeDllDestination)) {
    if (Test-Path -LiteralPath $destination) {
        throw "Refusing to overwrite an existing file: $destination"
    }
}

& python (Join-Path $PSScriptRoot 'patch_exe.py') $originalExe $probeExe
if ($LASTEXITCODE -ne 0) {
    throw 'Creating Czarodzieje.probe.exe failed.'
}
[System.IO.File]::Copy($probeDllSource, $probeDllDestination, $false)

Write-Host 'Probe installed as two new files; the originals were not modified:'
Write-Host "  $probeExe"
Write-Host "  $probeDllDestination"
Write-Host 'Run Czarodzieje.probe.exe with the game directory as the working directory.'
