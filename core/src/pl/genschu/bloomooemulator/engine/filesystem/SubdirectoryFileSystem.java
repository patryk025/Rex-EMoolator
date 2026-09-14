package pl.genschu.bloomooemulator.engine.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;

/** Read-only view of a game directory inside a folder or archive. */
public final class SubdirectoryFileSystem implements IFileSystem {
    private final IFileSystem source;
    private final String root;

    public SubdirectoryFileSystem(IFileSystem source, String root) throws IOException {
        this.source = source;
        this.root = normalize(root);
        if (!source.isDirectory(this.root)) throw new IOException("Game directory not found: " + root);
    }

    public static String normalize(String path) {
        if (path == null || path.isBlank()) return "";
        String value = path.replace('\\', '/');
        if (value.startsWith("/") || value.contains(":"))
            throw new IllegalArgumentException("Expected a relative game path: " + path);
        ArrayDeque<String> parts = new ArrayDeque<>();
        for (String part : value.split("/")) {
            if (part.isEmpty() || part.equals(".")) continue;
            if (part.equals("..")) {
                if (parts.isEmpty()) throw new IllegalArgumentException("Path escapes game directory: " + path);
                parts.removeLast();
            } else parts.addLast(part);
        }
        return String.join("/", parts);
    }

    private String resolve(String path) {
        String relative = normalize(path);
        return root.isEmpty() ? relative : relative.isEmpty() ? root : root + "/" + relative;
    }

    @Override public InputStream open(String path) throws IOException { return source.open(resolve(path)); }
    @Override public DataSource openSource(String path) throws IOException { return source.openSource(resolve(path)); }
    @Override public boolean exists(String path) { return source.exists(resolve(path)); }
    @Override public boolean isDirectory(String path) { return source.isDirectory(resolve(path)); }
    @Override public String[] list(String path) { return source.list(resolve(path)); }
    @Override public long length(String path) { return source.length(resolve(path)); }
}
