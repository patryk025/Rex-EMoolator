#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <stdio.h>

int main(int argc, char **argv)
{
    HMODULE proxy;
    FARPROC wrapper;
    FARPROC forwarded;
    if (argc != 2) {
        return 10;
    }
    proxy = LoadLibraryA(argv[1]);
    if (proxy == 0) {
        return 11;
    }
    wrapper = GetProcAddress(proxy, "?domodal@CWindow@@QAE_NXZ");
    if (wrapper == 0) {
        return 12;
    }
    forwarded = GetProcAddress(proxy, "?absolute@@YAHH@Z");
    if (forwarded == 0) {
        return 13;
    }
    if (GetModuleHandleA("Piklib8.dll") == 0) {
        return 14;
    }
    printf("P8PROBE=%p Piklib8=%p wrapper=%p forwarded=%p\n",
           proxy,
           GetModuleHandleA("Piklib8.dll"),
           wrapper,
           forwarded);
    FreeLibrary(proxy);
    return 0;
}
