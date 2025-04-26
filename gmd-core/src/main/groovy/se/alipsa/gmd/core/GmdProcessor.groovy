package se.alipsa.gmd.core

import groovy.transform.CompileStatic

@CompileStatic
class GmdProcessor {

  static void main(String[] args) {
    GmdProcessor gmdp = new GmdProcessor()
    if (args.length != 3) {
      throw new IllegalArgumentException("Expected 3 parameters (sourceDir, targetDir, outputType) but was $args.length")
    }
    def sourceDir = args[0]
    def targetDir = args[1]
    def outputType = args[2].toLowerCase()
    if (!['md', 'html', 'pdf'].contains(outputType)) {
      throw new IllegalArgumentException("Unknown output type $outputType, expected either md, html or pdf")
    }
    gmdp.process(sourceDir, targetDir, outputType)
  }

  void process(String sourceDir, String targetDir, String outputType) {
    Gmd gmd = new Gmd()
    for (file in new File(sourceDir).listFiles()) {
      if (file.name.endsWith('.gmd')) {
        File targetDirectory = new File(targetDir)
        if (!targetDirectory.exists()) {
          targetDirectory.mkdirs()
        }
        def outputFile = new File(targetDirectory, file.name.replace('.gmd', ".$outputType"))
        switch (outputType) {
          case 'md': outputFile.write(gmd.gmdToMd(file.text)); break
          case 'html': outputFile.write(gmd.gmdToHtml(file.text)); break
          case 'pdf': gmd.gmdToPdf(file.text, outputFile); break
        }
      }
    }
  }
}
