# Sekai shim
Mały runtime-DLL proxy do celów diagnostycznych, debugowania oraz możliwości wizualizowania danych z silnika fizycznego na żywo.

Wymagany: VC17 lub wyżej, biblioteka MinHook

Komenda do kompilacji:
cl /LD /MD /EHsc sekai_shim.cpp /Fe:Sekai.dll /link /DEF:Sekai_forwarding.def /NODEFAULTLIB:LIBCMT /LTCG

Wymagana obecność oryginalnej biblioteki, trzeba zmienić jej nazwę na Sekai_orig.dll. Ta libka wpina się pomiędzy bibliotekę World.dll oraz Sekai.dll, w większości proxuje calle do oryginalnej libki, ale umożliwa również przechwycenie wywołania, wykonania operacji na danych w przestrzeni adresowej biblioteki oraz w pewnym sensie zmianę działania określonego modułu biblioteki.