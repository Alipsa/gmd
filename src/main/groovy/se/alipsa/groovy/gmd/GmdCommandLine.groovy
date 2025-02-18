package se.alipsa.groovy.gmd

class GmdCommandLine {

  Closure runner
  /**
   *
   * @param args 0: command, 1: fromFile, 2: tofile
   */
  GmdCommandLine(String[] args) {
    if (args.length != 3) {
      throw new IllegalArgumentException("Expected 3 parameters (command, fromFile, toFile) but was $args.length")
    }
    def command = args[0].toLowerCase()
    runner = switch (command) {
      case 'tohtml' -> toHtml(args[1], args[2])
      case 'topdf' -> toPdf(args[1], args[2])
      default -> throw new IllegalArgumentException("Unknown command $command, expected either tohtml or topdf")
    }
  }

  static Closure toHtml(String from, String to) {
    return {
      Gmd gmd = new Gmd()
      def html = gmdFileToHtml(from, gmd)
      File toFile = new File(to)
      toFile.write(html)
      println "Wrote $toFile.absolutePath"
    }
  }

  static Closure toPdf(String from, String to) {
    return {
      Gmd gmd = new Gmd()
      def html = gmdFileToHtml(from, gmd)
      File toFile = new File(to)
      gmd.htmlToPdf(html, toFile)
      println "Wrote $toFile.absolutePath"
    }
  }

  private static String gmdFileToHtml(String from, Gmd gmd) {
    File fromFile = new File(from)
    if (!fromFile.exists()) {
      throw new IllegalArgumentException("From file $fromFile does not exist")
    }
    gmd.gmdToHtmlDoc(fromFile.text)
  }

  void run() {
    runner.call()
  }
}
