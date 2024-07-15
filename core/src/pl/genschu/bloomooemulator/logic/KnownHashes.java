package pl.genschu.bloomooemulator.logic;

import java.util.HashMap;
import java.util.Map;
import static java.util.Map.entry;

public class KnownHashes {
    private static final Map<String, String> knownHashes = Map.ofEntries(
            // generated from https://raw.githubusercontent.com/Dove6/PiKlib_patcher/master/script/sheet_parser.js
            entry("715272459E13BD77749B2634356B3F3B8A86015E", "Reksio i Skarb Piratów (pierwsza wersja)"),
            entry("87C5CA97F599B6B793AE299A6ACD8F70CF4DB640", "Reksio i Skarb Piratów (zaktualizowana wersja z 2004 roku)"),
            entry("128B235B6D9466A27E8A6EAD2DE20D98020222C8", "Reksio i UFO (pierwsza wersja)"),
            entry("EDFCF8680BA29AB20E22191CFEF187BA888E7FD7", "Reksio i UFO (zaktualizowana wersja z 2004 roku)"),
            entry("A70F97E970C6301166E90E8872F499DA59961464", "Reksio i Czarodzieje"),
            entry("2679920B3735624D746FDF24D7BB507A23B31A08", "Reksio i Wehikuł Czasu"),
            entry("3BE911183E59FEC67A388654E1A68B77C00BDF15", "Reksio i Kapitan Nemo"),
            entry("FB9A2B81D685F7C9B03DA30644D990603D4F7A70", "Reksio i Kretes w Akcji!"),
            entry("130BD02553829B28FAE9757A82E24881C5B6E4F2", "Reksio i Czarodzieje (wersja cdp.pl)"),
            entry("5C61507C372F12AD1A867C57F2C5A0C14E482F7E", "Reksio i UFO (wersja K.I.D.S.)"),
            entry("5032FDF8EA80DA4030F01C15C686AE46990062E7", "Reksio i Skarb Piratów (wersja cdp.pl)"),
            entry("F352210C32D8C224C0B7611D6BE1277367BA0464", "Reksio i Wehikuł Czasu (wersja z czasopisma Przygody Reksia)"),
            entry("E2BD2C6CCC68E5430ABD0F0D54048D8C16007D18", "Reksio i Kretes w Akcji! (wersja cdp.pl)"),
            entry("9FE73F7DCA5ABF7BEFE16216CF5BA54A9E68281B", "Reksio i UFO (wersja cdp.pl)"),
            entry("836AD62F9F97FA90A07F7282820A1CA0E092B228", "Reksio i Kapitan Nemo (wersja cdp.pl)"),
            entry("698B863D50C137BA7F3E8D3BF92EAB9B2D419383", "Reksio i Kretes w Akcji! (rosyjska wersja językowa)"),
            entry("985DDCACC8AA8949118DF6D9703BB58CBDD21BBD", "Reksio i Kretes w Akcji! (seria Rodzinna Gra Przygodowa)"),
            entry("5F6CBB33576D4F5493F141DDEE70CD108EA0F9D5", "Reksio i UFO (seria 2 GRY!, w pakiecie z Miastem SeKretów)"),
            entry("33CFD6523C0445F549E989739C58E8AA10329A87", "Wesołe Przedszkole Reksia"),
            entry("C5B899F8FA93620A902DA117849289A542E3AF3E", "Reksio i UFO (rumuńska wersja językowa)"),

            // my hashes
            entry("D1CC807BCBE5A478CBEC49CA795C2A8EA63D0B67", "Poznaj Mity: Wyprawa po Złote Runo"),
            entry("4666638DD606241664691DE60F4ACFFAF4AB272D", "Poznaj Mity: Wojna Trojańska"), // jedyny znany mi egzemplarz biblioteki Piklib w wersji 7.2
            entry("E80093DE96363524A209BBB80901458D9D524925", "Poznaj Mity: Herkules/Odyseusz") // te dwie gry pracują na tej samej wersji Piklib 8 o tym samym hashu
    );

    public static String checkHash(String hash) {
        return knownHashes.getOrDefault(hash.toUpperCase(), "Nieznana gra");
    }
}
