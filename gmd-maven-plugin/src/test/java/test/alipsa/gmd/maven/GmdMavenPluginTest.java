package test.alipsa.gmd.maven;

import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import se.alipsa.gmd.maven.GmdMavenPlugin;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class GmdMavenPluginTest {

  @Rule
  public MojoRule rule = new MojoRule();

  @Test
  public void testGmdMavenPlugin() throws Exception {
    File pomFile = new File("src/test/projects/");
    assertTrue(pomFile.exists());
    MavenProject project = rule.readMavenProject(pomFile);
    GmdMavenPlugin plugin = (GmdMavenPlugin) rule.lookupConfiguredMojo( project, "processGmd" );

    // Execute the plugin
    plugin.execute();

    // Verify that the output files were created
    File targetDir = new File(plugin.getTargetDir());
    assertTrue(targetDir.exists());
    assertTrue(targetDir.isDirectory());

    File testHtml = new File(targetDir, "test.html");
    assertTrue(testHtml.exists());
    var testContent = Files.readString(testHtml.toPath());
    assertTrue(testContent.contains("<h1>Greetings</h1>"));
    assertTrue(testContent.contains("Hello world!"));

    File testInline = new File(targetDir, "inline.html");
    assertTrue(testInline.exists());
    var testInlineHtml = Files.readString(testInline.toPath());
    assertTrue(testInlineHtml.contains("<h1>Inline</h1>"));
    assertTrue(testInlineHtml.contains("Today is "));
    assertTrue(testInlineHtml.contains(" and the time is "));
  }

}
