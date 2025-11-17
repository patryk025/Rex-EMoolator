@echo off
REM Build script for Sekai shim DLL

echo Setting up MSVC environment...
call "C:\Program Files\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars32.bat"

echo.
echo Compiling sekai_shim.cpp...
cl /LD /MD /EHsc sekai_shim.cpp /Fe:Sekai.dll /link /DEF:Sekai_forwarding.def /NODEFAULTLIB:LIBCMT /LTCG

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build successful!
) else (
    echo.
    echo Build failed with error code %ERRORLEVEL%
)

pause
