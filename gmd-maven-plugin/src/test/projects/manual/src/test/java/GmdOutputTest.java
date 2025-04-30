import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

class GmdOutputTest {
    @Test
    void testGmdOutput() throws IOException {
        File targetDir = new File("target/gmd");
        assertTrue(targetDir.exists());
        assertTrue(targetDir.isDirectory());
        File testHtml = new File(targetDir, "test.html");
        assertTrue(testHtml.exists());
        var testContent = Files.readString(testHtml.toPath());
        assertTrue(testContent.contains("<h1>Greetings</h1>"));
        assertTrue(testContent.contains("Hello world!"));
        System.out.println(testHtml + " is as expected");

        File testInline = new File(targetDir, "inline.html");
        assertTrue(testInline.exists());
        var testInlineHtml = Files.readString(testInline.toPath());
        assertTrue(testInlineHtml.contains("<h1>Inline</h1>"));
        assertTrue(testInlineHtml.contains("Today is "));
        assertTrue(testInlineHtml.contains(" and the time is "));
        System.out.println(testInline + " is as expected");
    }
}