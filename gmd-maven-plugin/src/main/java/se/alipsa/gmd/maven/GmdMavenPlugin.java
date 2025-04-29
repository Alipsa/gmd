package se.alipsa.gmd.maven;

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

  public String getSourceDir() {
    return sourceDir;
  }

  public String getTargetDir() {
    return targetDir;
  }

  public String getOutputType() {
    return outputType;
  }

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    GmdProcessor gmdProcessor = new GmdProcessor();
    gmdProcessor.process(sourceDir, targetDir, outputType);
  }
}
