# Sekai shim
Mały runtime-DLL proxy do celów diagnostycznych, debugowania oraz możliwości wizualizowania danych z silnika fizycznego na żywo.

Wymagany: VC17 lub wyżej.

Komenda do kompilacji:
cl /LD /MD /EHsc sekai_shim.cpp /Fe:Sekai.dll /link /DEF:Sekai_forwarding.def

Wymagana obecność oryginalnej biblioteki, trzeba zmienić jej nazwę na Sekai_orig.dll. Ta libka wpina się pomiędzy bibliotekę World.dll oraz Sekai.dll.