package se.alipsa.gmd.gradle

import groovy.transform.CompileStatic
import org.gradle.api.Project
import se.alipsa.gmd.core.Gmd

@CompileStatic
class GmdProcessor {

  Project project
  GmdPluginParams params

  GmdProcessor(Project project, GmdPluginParams params) {
    this.project = project
    this.params = params
  }

  void process() {
    project.logger.info("Processing GMD for ${project.group}:${project.name}:${project.version}")
    def sourceDir = params.sourceDir.get()
    def targetDir = params.targetDir.get()
    def outputType = params.outputType.get()

    project.logger.info("Source directory: $sourceDir")
    project.logger.info("Target directory: $targetDir")
    project.logger.info("Output type: $outputType")

    Gmd gmd = new Gmd()
    for (file in new File(sourceDir).listFiles()) {
      if (file.name.endsWith('.gmd')) {
        def outputFile = new File(targetDir, file.name.replace('.gmd', ".$outputType"))
        switch (outputType) {
          case 'md': outputFile.write(gmd.gmdToMd(file.text)); break
          case 'html': outputFile.write(gmd.gmdToHtml(file.text)); break
          case 'pdf': gmd.gmdToPdf(file.text, outputFile); break
        }
        project.logger.info("Processed ${file.name} to ${outputFile.name}")
      }
    }
  }
}
