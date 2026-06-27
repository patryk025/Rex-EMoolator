package pl.genschu.bloomooemulator.patch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Downloads a public Google Drive file into a local archive, transparently handling
 * the "Google Drive can't scan this file for viruses" interstitial that the
 * direct-download endpoint returns for files above ~100&nbsp;MB.
 *
 * <p>Flow:
 * <ol>
 *   <li>Resolve the file id from a share link (or accept a bare id).</li>
 *   <li>GET {@code drive.usercontent.google.com/download?id=…&export=download}. Small
 *       files come back as the binary directly.</li>
 *   <li>If the response is the HTML confirmation page, parse its hidden-input form
 *       (or, on the older flow, the {@code /uc} download link), carry the
 *       {@code Set-Cookie}s, and re-request with the {@code confirm}/{@code uuid}
 *       token.</li>
 * </ol>
 *
 * <p>Pure {@link HttpURLConnection} — no extra dependencies, Android API&nbsp;24-safe
 * (manual buffer loops, no {@code transferTo}/{@code readNBytes}). The caller deletes
 * the returned temp file. The downloaded bytes are validated downstream by
 * {@link PatchInstaller}'s magic-byte format detection, so an accidentally-saved HTML
 * page fails cleanly as "unsupported archive format" rather than silently.
 */
public final class GoogleDriveDownloader {
    private GoogleDriveDownloader() {}

    private static final String DOWNLOAD_HOST = "https://drive.usercontent.google.com/download";

    private static final Pattern ID_FROM_PATH = Pattern.compile("/d/([a-zA-Z0-9_-]{10,})");
    private static final Pattern ID_FROM_QUERY = Pattern.compile("[?&]id=([a-zA-Z0-9_-]{10,})");
    private static final Pattern BARE_ID = Pattern.compile("[a-zA-Z0-9_-]{10,}");
    private static final Pattern FORM_ACTION = Pattern.compile("action=\"([^\"]+)\"");
    private static final Pattern INPUT_TAG = Pattern.compile("<input\\b[^>]*>", Pattern.CASE_INSENSITIVE);
    private static final Pattern UC_HREF = Pattern.compile("href=\"(/uc\\?export=download[^\"]+)\"");

    /**
     * Downloads the Drive file referenced by {@code linkOrId} into {@code destDir}.
     *
     * @return a temp file holding the downloaded bytes (caller deletes it).
     * @throws IOException if the link is unrecognisable, the file is not public, or the
     *                     confirmation flow cannot be resolved.
     */
    public static File download(String linkOrId, File destDir) throws IOException {
        String id = extractFileId(linkOrId);
        if (id == null) {
            throw new IOException("Not a recognisable Google Drive link/id: " + linkOrId);
        }
        if (!destDir.isDirectory() && !destDir.mkdirs()) {
            throw new IOException("Cannot create " + destDir);
        }

        String url = DOWNLOAD_HOST + "?id=" + id + "&export=download";
        HttpURLConnection conn = open(url, null);
        try {
            int code = conn.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP " + code + " from Google Drive for id " + id);
            }
            if (!isHtml(conn.getHeaderField("Content-Type"))) {
                return streamToTemp(conn, destDir); // small file: binary served directly
            }
            // Large/unscannable file: resolve the confirmation token and retry with cookies.
            String cookie = joinCookies(conn);
            String html = readString(conn.getInputStream(), 1_000_000);
            String confirmUrl = resolveConfirmUrl(html);
            if (confirmUrl == null) {
                throw new IOException("Google Drive returned a confirmation page but no download "
                        + "token was found (id " + id + "). Is the file shared with 'Anyone with the link'?");
            }
            HttpURLConnection confirm = open(confirmUrl, cookie);
            try {
                int code2 = confirm.getResponseCode();
                if (code2 != HttpURLConnection.HTTP_OK) {
                    throw new IOException("HTTP " + code2 + " confirming Google Drive download for id " + id);
                }
                if (isHtml(confirm.getHeaderField("Content-Type"))) {
                    throw new IOException("Google Drive still returned HTML after confirmation (id " + id + ")");
                }
                return streamToTemp(confirm, destDir);
            } finally {
                confirm.disconnect();
            }
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Extracts a Drive file id from a share link, or returns the input unchanged if it
     * already looks like a bare id. Recognises {@code /file/d/<id>/…},
     * {@code …?id=<id>…} and {@code open?id=<id>}. Returns {@code null} if nothing matches.
     */
    public static String extractFileId(String linkOrId) {
        if (linkOrId == null || linkOrId.isBlank()) {
            return null;
        }
        String s = linkOrId.trim();
        Matcher path = ID_FROM_PATH.matcher(s);
        if (path.find()) {
            return path.group(1);
        }
        Matcher query = ID_FROM_QUERY.matcher(s);
        if (query.find()) {
            return query.group(1);
        }
        if (BARE_ID.matcher(s).matches()) {
            return s;
        }
        return null;
    }

    /**
     * Builds the confirmation download URL from a Drive interstitial page: rebuilds the
     * hidden-input form (modern flow) or falls back to the {@code /uc} download link
     * (older flow). Returns {@code null} when neither is present.
     */
    public static String resolveConfirmUrl(String html) {
        if (html == null || html.isBlank()) {
            return null;
        }
        Matcher actionMatcher = FORM_ACTION.matcher(html);
        String base = actionMatcher.find() ? htmlUnescape(actionMatcher.group(1)) : DOWNLOAD_HOST;

        StringBuilder query = new StringBuilder();
        boolean hasConfirm = false;
        Matcher tags = INPUT_TAG.matcher(html);
        while (tags.find()) {
            String tag = tags.group();
            String name = attr(tag, "name");
            if (name == null) {
                continue;
            }
            String value = htmlUnescape(attr(tag, "value"));
            if (value == null) {
                value = "";
            }
            if (query.length() > 0) {
                query.append('&');
            }
            query.append(urlEncode(name)).append('=').append(urlEncode(value));
            if (name.equals("confirm")) {
                hasConfirm = true;
            }
        }

        if (!hasConfirm) {
            Matcher href = UC_HREF.matcher(html);
            if (href.find()) {
                return "https://drive.google.com" + htmlUnescape(href.group(1));
            }
        }
        if (query.length() == 0) {
            return null;
        }
        return base + (base.contains("?") ? "&" : "?") + query;
    }

    private static HttpURLConnection open(String url, String cookie) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(60000);
        conn.setRequestProperty("User-Agent", "RexEMoolator");
        if (cookie != null && !cookie.isEmpty()) {
            conn.setRequestProperty("Cookie", cookie);
        }
        return conn;
    }

    private static File streamToTemp(HttpURLConnection conn, File destDir) throws IOException {
        File out = File.createTempFile("gdrive-", ".archive", destDir);
        try (InputStream in = new BufferedInputStream(conn.getInputStream());
             OutputStream os = new BufferedOutputStream(new FileOutputStream(out))) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        }
        return out;
    }

    /** Reads up to {@code limit} bytes as UTF-8 — used only for the small HTML interstitial. */
    private static String readString(InputStream in, int limit) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            if (out.size() > limit) {
                break;
            }
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    /** Joins the {@code name=value} of every {@code Set-Cookie} into one {@code Cookie} header value. */
    private static String joinCookies(HttpURLConnection conn) {
        List<String> setCookies = conn.getHeaderFields().get("Set-Cookie");
        if (setCookies == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String cookie : setCookies) {
            String pair = cookie.split(";", 2)[0];
            if (pair.isBlank()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(pair.trim());
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    private static boolean isHtml(String contentType) {
        return contentType != null && contentType.toLowerCase(Locale.ROOT).contains("text/html");
    }

    private static String attr(String tag, String name) {
        Matcher m = Pattern.compile("\\b" + name + "=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE).matcher(tag);
        return m.find() ? m.group(1) : null;
    }

    private static String htmlUnescape(String s) {
        if (s == null) {
            return null;
        }
        return s.replace("&amp;", "&").replace("&#39;", "'").replace("&quot;", "\"");
    }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
