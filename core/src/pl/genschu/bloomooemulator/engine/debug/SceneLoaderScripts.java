package pl.genschu.bloomooemulator.engine.debug;

/**
 * Maps a game family to the BlooMoo script combination that loads ARCADE and
 * CUTSCENE scenes. Each game implements scene transitions differently, so the
 * debug scene loader can't reuse a single path — instead it composes the
 * game-native script and runs it through the interpreter
 * ({@code Game#runScript}).
 *
 * <p>The {@value #NAME_TOKEN} token is replaced with the requested scene name.
 * A {@code null} template means the game does not support that mode.
 *
 * <p>Matching is done with {@code startsWith} on the game name so localisation
 * / distribution variants (e.g. {@code "... (wersja cdp.pl)"}) resolve to the
 * same family.
 */
public final class SceneLoaderScripts {
    private SceneLoaderScripts() {}

    private static final String NAME_TOKEN = "%NAME%";

    /**
     * Stable identifier for a game family, used as the key in the bundled
     * {@code scene_lists.json} name catalog. Returns {@code null} for games
     * without ARCADE/CUTSCENE loaders.
     */
    public static String familyId(String gameName) {
        if (gameName == null) {
            return null;
        }
        if (gameName.startsWith("Reksio i Czarodzieje")) {
            return "CZARODZIEJE";
        }
        if (gameName.startsWith("Reksio i Wehikuł Czasu")) {
            return "WEHIKUL";
        }
        if (gameName.startsWith("Reksio i Kapitan Nemo")) {
            return "NEMO";
        }
        if (gameName.startsWith("Reksio i Kretes w Akcji")) {
            return "KRETES";
        }
        return null;
    }

    public static boolean supportsArcade(String gameName) {
        return arcadeTemplate(gameName) != null;
    }

    public static boolean supportsCutscene(String gameName) {
        return cutsceneTemplate(gameName) != null;
    }

    /** @return the script to load an ARCADE scene, or {@code null} if unsupported. */
    public static String arcadeScript(String gameName, String sceneName) {
        return fill(arcadeTemplate(gameName), sceneName);
    }

    /** @return the script to load a CUTSCENE, or {@code null} if unsupported. */
    public static String cutsceneScript(String gameName, String sceneName) {
        return fill(cutsceneTemplate(gameName), sceneName);
    }

    private static String fill(String template, String sceneName) {
        return template == null ? null : template.replace(NAME_TOKEN, sceneName == null ? "" : sceneName.trim());
    }

    private static String arcadeTemplate(String g) {
        if (g == null) {
            return null;
        }
        if (g.startsWith("Reksio i Czarodzieje")) {
            return "G_SARCADEOBJECTS^SET(\"" + NAME_TOKEN + "\");PRZYGODA^GOTO(\"ARCADE\");";
        }
        if (g.startsWith("Reksio i Wehikuł Czasu")) {
            return "G_SARCADESCENE^SET(\"" + NAME_TOKEN + "\");PRZYGODA^GOTO(\"ARCADE\");";
        }
        if (g.startsWith("Reksio i Kapitan Nemo")) {
            return "GSAVE^SET(\"SCENE_NAME\",\"ARCADE\");GSAVE^SET(\"ARCADE_SCENE_NAME\",\"" + NAME_TOKEN + "\");BEHGOTOSCENE^RUN();";
        }
        return null;
    }

    private static String cutsceneTemplate(String g) {
        if (g == null) {
            return null;
        }
        if (g.startsWith("Reksio i Kapitan Nemo")) {
            return "GSAVE^SET(\"CS_SCENE_NAME\",\"" + NAME_TOKEN + "\");PRZYGODA^GOTO(\"CUTSCENKI\");";
        }
        if (g.startsWith("Reksio i Kretes w Akcji")) {
            return "GSAVE^SET(\"CS_NAME\",\"" + NAME_TOKEN + "\");BEHGOTOSCENE^RUN(\"CUTSCENKI\");";
        }
        return null;
    }
}
