package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.patch.GoogleDriveDownloader;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class GoogleDriveDownloaderTest {
    @Test
    void resolvesModernConfirmationForm() {
        String html = "<form action=\"https://drive.usercontent.google.com/download\">"
                + "<INPUT type=\"hidden\" name=\"id\" value=\"file_id\">"
                + "<input name=\"confirm\" value=\"yes\">"
                + "<input name=\"uuid\" value=\"a&amp;b\"></form>";
        assertEquals("https://drive.usercontent.google.com/download?id=file_id&confirm=yes&uuid=a%26b",
                GoogleDriveDownloader.resolveConfirmUrl(html));
    }

    @Test
    void resolvesLegacyDownloadLink() {
        assertEquals("https://drive.google.com/uc?export=download&id=file_id&confirm=yes",
                GoogleDriveDownloader.resolveConfirmUrl(
                        "<a href=\"/uc?export=download&amp;id=file_id&amp;confirm=yes\">download</a>"));
    }

    @Test
    void handlesManyUnterminatedInputTagsWithinResponseLimit() {
        String html = "<input ".repeat(140_000);
        assertTimeoutPreemptively(Duration.ofSeconds(3), () -> {
            assertNull(GoogleDriveDownloader.resolveConfirmUrl(html));
            assertEquals("https://drive.usercontent.google.com/download?confirm=yes",
                    GoogleDriveDownloader.resolveConfirmUrl(html + "<input name=\"confirm\" value=\"yes\">"));
        });
    }
}
