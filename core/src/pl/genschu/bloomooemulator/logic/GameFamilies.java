package pl.genschu.bloomooemulator.logic;

import java.util.Locale;
import java.util.Map;
import static java.util.Map.entry;

/**
 * Maps an engine-DLL hash to a stable <em>family</em> slug.
 *
 * <p>A family groups every edition, distribution and localisation of the same
 * game under one identifier (e.g. all releases of "Reksio i UFO" — first run,
 * 2004 update, cdp.pl, K.I.D.S., Romanian — collapse to {@code "reksio-ufo"}).
 * This is what lets a patch opt in to editions its author never tested:
 * {@link pl.genschu.bloomooemulator.patch.PatchManifest#compatibilityFor}
 * returns {@link pl.genschu.bloomooemulator.patch.PatchCompatibility#FAMILY}
 * when the hashes differ but the families match.
 *
 * <p>Keyed by the same hashes as {@link KnownHashes} (which maps hash → display
 * name); kept separate because the two answer different questions. A hash absent
 * here simply has no family — patches then only ever match it by exact hash.
 *
 * <p>Distinct from {@link pl.genschu.bloomooemulator.engine.debug.SceneLoaderScripts#familyId},
 * which derives an UPPERCASE token from the display name for the debug scene
 * loader and only covers games with ARCADE/CUTSCENE loaders.
 */
public final class GameFamilies {
    private GameFamilies() {}

    private static final Map<String, String> families = Map.ofEntries(
            // Reksio i Skarb Piratów
            entry("715272459E13BD77749B2634356B3F3B8A86015E", "reksio-skarb-piratow"),
            entry("87C5CA97F599B6B793AE299A6ACD8F70CF4DB640", "reksio-skarb-piratow"),
            entry("5032FDF8EA80DA4030F01C15C686AE46990062E7", "reksio-skarb-piratow"),
            entry("DB853DBE96751160D436F267CA378DB1CBA81A52", "reksio-skarb-piratow"),

            // Reksio i UFO
            entry("128B235B6D9466A27E8A6EAD2DE20D98020222C8", "reksio-ufo"),
            entry("EDFCF8680BA29AB20E22191CFEF187BA888E7FD7", "reksio-ufo"),
            entry("5C61507C372F12AD1A867C57F2C5A0C14E482F7E", "reksio-ufo"),
            entry("9FE73F7DCA5ABF7BEFE16216CF5BA54A9E68281B", "reksio-ufo"),
            entry("5F6CBB33576D4F5493F141DDEE70CD108EA0F9D5", "reksio-ufo"),
            entry("C5B899F8FA93620A902DA117849289A542E3AF3E", "reksio-ufo"),

            // Reksio i Czarodzieje
            entry("A70F97E970C6301166E90E8872F499DA59961464", "reksio-czarodzieje"),
            entry("130BD02553829B28FAE9757A82E24881C5B6E4F2", "reksio-czarodzieje"),

            // Reksio i Wehikuł Czasu
            entry("2679920B3735624D746FDF24D7BB507A23B31A08", "reksio-wehikul-czasu"),
            entry("F352210C32D8C224C0B7611D6BE1277367BA0464", "reksio-wehikul-czasu"),

            // Reksio i Kapitan Nemo
            entry("3BE911183E59FEC67A388654E1A68B77C00BDF15", "reksio-kapitan-nemo"),
            entry("836AD62F9F97FA90A07F7282820A1CA0E092B228", "reksio-kapitan-nemo"),

            // Reksio i Kretes w Akcji!
            entry("FB9A2B81D685F7C9B03DA30644D990603D4F7A70", "reksio-kretes-w-akcji"),
            entry("E2BD2C6CCC68E5430ABD0F0D54048D8C16007D18", "reksio-kretes-w-akcji"),
            entry("698B863D50C137BA7F3E8D3BF92EAB9B2D419383", "reksio-kretes-w-akcji"),
            entry("985DDCACC8AA8949118DF6D9703BB58CBDD21BBD", "reksio-kretes-w-akcji"),

            // Wesołe Przedszkole Reksia
            entry("33CFD6523C0445F549E989739C58E8AA10329A87", "reksio-wesole-przedszkole"),

            // Poznaj Mity (each title is its own game/family)
            entry("D1CC807BCBE5A478CBEC49CA795C2A8EA63D0B67", "poznaj-mity-zlote-runo"),
            entry("4666638DD606241664691DE60F4ACFFAF4AB272D", "poznaj-mity-wojna-trojanska"),
            entry("E80093DE96363524A209BBB80901458D9D524925", "poznaj-mity-herkules-odyseusz"),
            entry("0967908F2DF1079415B02C60F567B5C2AC665473", "poznaj-mity-tezeusz")
    );

    /** @return the family slug for the given DLL hash, or {@code null} if unknown. */
    public static String familyFor(String hash) {
        if (hash == null) {
            return null;
        }
        return families.getOrDefault(hash.toUpperCase(Locale.ROOT), null);
    }
}
