package se.alipsa.gmd.maven;

import java.io.File;
import java.util.Objects;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import se.alipsa.gmd.core.GmdProcessor;

/**
 * Maven plugin to process GMD files.
 */
@Mojo(name = "processGmd", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class GmdMavenPlugin extends AbstractMojo {

  @Parameter(name="sourceDir", property = "processGmd.sourceDir", defaultValue = "src/main/gmd")
  private String sourceDir;
  @Parameter(name = "targetDir", property = "processGmd.targetDir", defaultValue = "target/gmd" )
  private String targetDir;
  @Parameter(name = "outputType", property = "processGmd.outputType", defaultValue = "md" )
  private String outputType;

  /**
   * Default constructor.
   */
  public GmdMavenPlugin() {
    super();
  }

  /**
   * The directory where the GMD files are located. Default is src/main/gmd.
   *
   * @return The directory where the GMD files are located.
   */
  public String getSourceDir() {
    return sourceDir;
  }

  /**
   * The directory where the generated files will be written. Default is target/gmd
   *
   * @return The directory where the generated files will be written.
   */
  public String getTargetDir() {
    return targetDir;
  }

  /**
   * The type of output to generate. Can be one of:
   * - md
   * - html
   * - pdf
   *
   * @return The type of output to generate.
   */
  public String getOutputType() {
    return outputType;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      File srcDir = new File(sourceDir);
      if (!srcDir.exists()) {
        getLog().warn("Source directory " + sourceDir + " does not exist, nothing to do");
        return;
      }
      if (srcDir.isFile()) {
        throw new MojoFailureException(sourceDir + " is a file, not a directory");
      }
      if (Objects.requireNonNull(srcDir.list()).length == 0) {
        getLog().warn("No gmd files found in " + sourceDir + ", nothing to do");
        return;
      }
      GmdProcessor gmdProcessor = new GmdProcessor();
      gmdProcessor.process(sourceDir, targetDir, outputType);

      File td = new File(targetDir);
      if (td.exists()) {
        getLog().info("Gmd files processed and written to " + td.getCanonicalPath());
      } else {
        getLog().warn(td.getCanonicalPath() + " should exists but does not, something is probably wrong");
      }
    } catch (Exception e) {
      throw new MojoFailureException("Failed to process gmd files in " + sourceDir, e);
    }
  }
}
