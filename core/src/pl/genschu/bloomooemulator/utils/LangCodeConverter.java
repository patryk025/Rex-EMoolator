package pl.genschu.bloomooemulator.utils;

import java.util.HashMap;
import java.util.Map;

public class LangCodeConverter {
    private static final Map<String, String> lcidToIsoMap = new HashMap<>();

    // language codes extracted and converted from Polish version of Windows 10 and registry key HKEY_LOCAL_MACHINE\SOFTWARE\Classes\MIME\Database\Rfc1766
    static {
        // LCID -> ISO 639-2 mappings
        lcidToIsoMap.put("0001", "ara"); // ar - Arabic
        lcidToIsoMap.put("0004", "zho"); // zh - Chinese
        lcidToIsoMap.put("0009", "eng"); // en - English
        lcidToIsoMap.put("0401", "ara"); // ar-sa - Arabic (Saudi Arabia)
        lcidToIsoMap.put("0402", "bul"); // bg - Bulgarian
        lcidToIsoMap.put("0403", "cat"); // ca - Catalan
        lcidToIsoMap.put("0404", "zho"); // zh-tw - Chinese (Taiwan)
        lcidToIsoMap.put("0405", "ces"); // cs - Czech
        lcidToIsoMap.put("0406", "dan"); // da - Danish
        lcidToIsoMap.put("0407", "deu"); // de - German
        lcidToIsoMap.put("0408", "ell"); // el - Modern Greek
        lcidToIsoMap.put("0409", "eng"); // en-us - English (United States)
        lcidToIsoMap.put("040A", "spa"); // es - Spanish
        lcidToIsoMap.put("040B", "fin"); // fi - Finnish
        lcidToIsoMap.put("040C", "fra"); // fr - French
        lcidToIsoMap.put("040D", "heb"); // he - Hebrew
        lcidToIsoMap.put("040E", "hun"); // hu - Hungarian
        lcidToIsoMap.put("040F", "isl"); // is - Icelandic
        lcidToIsoMap.put("0410", "ita"); // it - Italian
        lcidToIsoMap.put("0411", "jpn"); // ja - Japanese
        lcidToIsoMap.put("0412", "kor"); // ko - Korean
        lcidToIsoMap.put("0413", "nld"); // nl - Dutch
        lcidToIsoMap.put("0414", "nor"); // no - Norwegian
        lcidToIsoMap.put("0415", "pol"); // pl - Polish
        lcidToIsoMap.put("0416", "por"); // pt-br - Portuguese (Brazil)
        lcidToIsoMap.put("0417", "roh"); // rm - Romansh
        lcidToIsoMap.put("0418", "ron"); // ro - Romanian
        lcidToIsoMap.put("0419", "rus"); // ru - Russian
        lcidToIsoMap.put("041A", "hrv"); // hr - Croatian
        lcidToIsoMap.put("041B", "slk"); // sk - Slovak
        lcidToIsoMap.put("041C", "sqi"); // sq - Albanian
        lcidToIsoMap.put("041D", "swe"); // sv - Swedish
        lcidToIsoMap.put("041E", "tha"); // th - Thai
        lcidToIsoMap.put("041F", "tur"); // tr - Turkish
        lcidToIsoMap.put("0420", "urd"); // ur - Urdu
        lcidToIsoMap.put("0421", "ind"); // in - Indonesian
        lcidToIsoMap.put("0422", "ukr"); // uk - Ukrainian
        lcidToIsoMap.put("0423", "bel"); // be - Belarusian
        lcidToIsoMap.put("0424", "slv"); // sl - Slovenian
        lcidToIsoMap.put("0425", "est"); // et - Estonian
        lcidToIsoMap.put("0426", "lav"); // lv - Latvian
        lcidToIsoMap.put("0427", "lit"); // lt - Lithuanian
        lcidToIsoMap.put("0429", "fas"); // fa - Persian
        lcidToIsoMap.put("042A", "vie"); // vi - Vietnamese
        lcidToIsoMap.put("042D", "eus"); // eu - Basque
        lcidToIsoMap.put("042E", "hsb"); // sb - Upper Sorbian
        lcidToIsoMap.put("042F", "mkd"); // mk - Macedonian
        lcidToIsoMap.put("0430", "nso"); // sx - Sotho, Northern
        lcidToIsoMap.put("0431", "tso"); // ts - Tsonga
        lcidToIsoMap.put("0432", "tsn"); // tn - Tswana
        lcidToIsoMap.put("0434", "xho"); // xh - Xhosa
        lcidToIsoMap.put("0435", "zul"); // zu - Zulu
        lcidToIsoMap.put("0436", "afr"); // af - Afrikaans
        lcidToIsoMap.put("0438", "fao"); // fo - Faroese
        lcidToIsoMap.put("0439", "hin"); // hi - Hindi
        lcidToIsoMap.put("043A", "mlt"); // mt - Maltese
        lcidToIsoMap.put("043C", "gla"); // gd - Scottish Gaelic
        lcidToIsoMap.put("043D", "yid"); // ji - Yiddish
        lcidToIsoMap.put("043E", "msa"); // ms - Malay (macrolanguage)
        lcidToIsoMap.put("0801", "ara"); // ar-iq - Arabic (Iraq)
        lcidToIsoMap.put("0804", "zho"); // zh-cn - Chinese (China)
        lcidToIsoMap.put("0807", "deu"); // de-ch - German (Switzerland)
        lcidToIsoMap.put("0809", "eng"); // en-gb - English (United Kingdom)
        lcidToIsoMap.put("080A", "spa"); // es-mx - Spanish (Mexico)
        lcidToIsoMap.put("080C", "fra"); // fr-be - French (Belgium)
        lcidToIsoMap.put("0810", "ita"); // it-ch - Italian (Switzerland)
        lcidToIsoMap.put("0813", "nld"); // nl-be - Dutch (Belgium)
        lcidToIsoMap.put("0814", "nor"); // no - Norwegian
        lcidToIsoMap.put("0816", "por"); // pt - Portuguese
        lcidToIsoMap.put("0818", "ron"); // ro-mo - Romanian (Moldova)
        lcidToIsoMap.put("0819", "rus"); // ru-mo - Russian (Moldova)
        lcidToIsoMap.put("081A", "srp"); // sr - Serbian
        lcidToIsoMap.put("081D", "swe"); // sv-fi - Swedish (Finland)
        lcidToIsoMap.put("0C01", "ara"); // ar-eg - Arabic (Egypt)
        lcidToIsoMap.put("0C04", "zho"); // zh-hk - Chinese (Hong Kong)
        lcidToIsoMap.put("0C07", "deu"); // de-at - German (Austria)
        lcidToIsoMap.put("0C09", "eng"); // en-au - English (Australia)
        lcidToIsoMap.put("0C0A", "spa"); // es - Spanish
        lcidToIsoMap.put("0C0C", "fra"); // fr-ca - French (Canada)
        lcidToIsoMap.put("0C1A", "srp"); // sr - Serbian
        lcidToIsoMap.put("1001", "ara"); // ar-ly - Arabic (Libya)
        lcidToIsoMap.put("1004", "zho"); // zh-sg - Chinese (Singapore)
        lcidToIsoMap.put("1007", "deu"); // de-lu - German (Luxembourg)
        lcidToIsoMap.put("1009", "eng"); // en-ca - English (Canada)
        lcidToIsoMap.put("100A", "spa"); // es-gt - Spanish (Guatemala)
        lcidToIsoMap.put("100C", "fra"); // fr-ch - French (Switzerland)
        lcidToIsoMap.put("1401", "ara"); // ar-dz - Arabic (Algeria)
        lcidToIsoMap.put("1407", "deu"); // de-li - German (Liechtenstein)
        lcidToIsoMap.put("1409", "eng"); // en-nz - English (New Zealand)
        lcidToIsoMap.put("140A", "spa"); // es-cr - Spanish (Costa Rica)
        lcidToIsoMap.put("140C", "fra"); // fr-lu - French (Luxembourg)
        lcidToIsoMap.put("1801", "ara"); // ar-ma - Arabic (Morocco)
        lcidToIsoMap.put("1809", "eng"); // en-ie - English (Ireland)
        lcidToIsoMap.put("180A", "spa"); // es-pa - Spanish (Panama)
        lcidToIsoMap.put("1C01", "ara"); // ar-tn - Arabic (Tunisia)
        lcidToIsoMap.put("1C09", "eng"); // en-za - English (South Africa)
        lcidToIsoMap.put("1C0A", "spa"); // es-do - Spanish (Dominican Republic)
        lcidToIsoMap.put("2001", "ara"); // ar-om - Arabic (Oman)
        lcidToIsoMap.put("2009", "eng"); // en-jm - English (Jamaica)
        lcidToIsoMap.put("200A", "spa"); // es-ve - Spanish (Venezuela)
        lcidToIsoMap.put("2401", "ara"); // ar-ye - Arabic (Yemen)
        lcidToIsoMap.put("240A", "spa"); // es-co - Spanish (Colombia)
        lcidToIsoMap.put("2801", "ara"); // ar-sy - Arabic (Syria)
        lcidToIsoMap.put("2809", "eng"); // en-bz - English (Belize)
        lcidToIsoMap.put("280A", "spa"); // es-pe - Spanish (Peru)
        lcidToIsoMap.put("2C01", "ara"); // ar-jo - Arabic (Jordan)
        lcidToIsoMap.put("2C09", "eng"); // en-tt - English (Trinidad and Tobago)
        lcidToIsoMap.put("2C0A", "spa"); // es-ar - Spanish (Argentina)
        lcidToIsoMap.put("3001", "ara"); // ar-lb - Arabic (Lebanon)
        lcidToIsoMap.put("300A", "spa"); // es-ec - Spanish (Ecuador)
        lcidToIsoMap.put("3401", "ara"); // ar-kw - Arabic (Kuwait)
        lcidToIsoMap.put("340A", "spa"); // es-cl - Spanish (Chile)
        lcidToIsoMap.put("3801", "ara"); // ar-ae - Arabic (United Arab Emirates)
        lcidToIsoMap.put("380A", "spa"); // es-uy - Spanish (Uruguay)
        lcidToIsoMap.put("3C01", "ara"); // ar-bh - Arabic (Bahrain)
        lcidToIsoMap.put("3C0A", "spa"); // es-py - Spanish (Paraguay)
        lcidToIsoMap.put("4001", "ara"); // ar-qa - Arabic (Qatar)
        lcidToIsoMap.put("400A", "spa"); // es-bo - Spanish (Bolivia)
        lcidToIsoMap.put("440A", "spa"); // es-sv - Spanish (El Salvador)
        lcidToIsoMap.put("480A", "spa"); // es-hn - Spanish (Honduras)
        lcidToIsoMap.put("4C0A", "spa"); // es-ni - Spanish (Nicaragua)
        lcidToIsoMap.put("500A", "spa"); // es-pr - Spanish (Puerto Rico)
    }

    public static String lcidToIsoCode(String lcid) {
        if (lcidToIsoMap.containsKey(lcid)) {
            return lcidToIsoMap.get(lcid).toUpperCase();
        } else {
            // Fallback: Nieznany LCID
            return "unknown";
        }
    }
}
