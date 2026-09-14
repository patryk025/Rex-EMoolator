package pl.genschu.bloomooemulator.logic;

import org.ini4j.Ini;
import pl.genschu.bloomooemulator.engine.filesystem.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/** Shared discovery for the desktop and Android libraries. */
public final class GameSourceScanner {
    private static final int MAX_DEPTH = 4;
    private static final int MAX_DIRECTORIES = 2048;

    private GameSourceScanner() {}

    public record Candidate(String rootPath, String name) {
        @Override public String toString() {
            return name + (rootPath.isEmpty() ? "" : " (" + rootPath + ")");
        }
    }

    public static List<Candidate> scan(String path, String rootPath) throws IOException {
        try {
            IFileSystem fs = AssetSourceDispatcher.openAssets(new File(path));
            String root = SubdirectoryFileSystem.normalize(rootPath);
            List<Candidate> found = new ArrayList<>();
            ArrayDeque<Candidate> pending = new ArrayDeque<>();
            pending.add(new Candidate(root, new File(path).getName()));
            int visited = 0;
            int baseDepth = root.isEmpty() ? 0 : root.split("/").length;
            while (!pending.isEmpty()) {
                if (++visited > MAX_DIRECTORIES) throw new IOException("Too many directories to scan; select a game subfolder.");
                Candidate current = pending.removeFirst();
                IFileSystem game = new SubdirectoryFileSystem(fs, current.rootPath());
                String[] entries = game.list("");
                if (entries == null) continue;
                Arrays.sort(entries, String.CASE_INSENSITIVE_ORDER);
                // This is also the entry point required by the game loader. It avoids
                // treating installer directories with a bundled DLL as playable games.
                if (game.exists("dane/application.def") && !game.isDirectory("dane/application.def")) {
                    String title = current.name();
                    if (game.exists("bloomoo.ini")) {
                        try {
                            Ini ini = new Ini();
                            byte[] bytes = GameIniResolver.boundedReader(game::length, game::open).read("bloomoo.ini");
                            ini.load(new java.io.ByteArrayInputStream(bytes));
                            String configuredTitle = ini.get("MAIN", "TITLE");
                            if (configuredTitle != null && !configuredTitle.isBlank()) title = configuredTitle.trim();
                        } catch (IOException ignored) {
                            // A directory name is still useful for a damaged/missing title.
                        }
                    }
                    found.add(new Candidate(current.rootPath(), title));
                    continue; // Do not descend into the assets of an identified game.
                }
                int depth = current.rootPath().isEmpty() ? 0 : current.rootPath().split("/").length;
                if (depth - baseDepth >= MAX_DEPTH) continue;
                for (String entry : entries) {
                    if (game.isDirectory(entry)) {
                        String child = current.rootPath().isEmpty() ? entry : current.rootPath() + "/" + entry;
                        pending.addLast(new Candidate(child, entry));
                        if (pending.size() + visited > MAX_DIRECTORIES)
                            throw new IOException("Too many directories to scan; select a game subfolder.");
                    }
                }
            }
            return found;
        } catch (RuntimeException e) {
            throw new IOException("Cannot scan game source: " + e.getMessage(), e);
        }
    }
}
