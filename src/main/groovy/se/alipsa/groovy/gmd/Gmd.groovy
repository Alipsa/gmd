package se.alipsa.groovy.gmd

import com.openhtmltopdf.mathmlsupport.MathMLDrawer
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import com.openhtmltopdf.svgsupport.BatikSVGDrawer
import com.openhtmltopdf.util.XRLog
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
import javafx.embed.swing.JFXPanel
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import org.codehaus.groovy.control.CompilationFailedException
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jsoup.Jsoup
import org.jsoup.helper.W3CDom
import org.jsoup.nodes.Entities
import org.w3c.dom.Document

import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.concurrent.CountDownLatch

import static se.alipsa.groovy.gmd.HtmlDecorator.BOOTSTRAP_CSS
import static se.alipsa.groovy.gmd.HtmlDecorator.decorate

/**
 * Key class for this Groovy Markdown implementation
 */
class Gmd {
  final Parser parser
  final HtmlRenderer renderer

  static void main(String[] args) {
    new GmdCommandLine(args).run()
  }

  Gmd() {
    XRLog.setLoggerImpl(new Log4jXRLogger());
    parser = Parser.builder().build()
    renderer = HtmlRenderer.builder()
        .softbreak("<br />\n")
        .extensions([TablesExtension.create()])
        .build()
  }

  /**
   * Process the Groovy Markdown text into standard markdown
   * @param text
   * @param bindings the variables to resolve in the text
   * @return a markdown text document
   */
  String gmdToMd(String text, Map bindings) throws GmdException {
    try {
      return GmdTemplateEngine.processCodeBlocks(text, bindings)
    } catch (CompilationFailedException | ClassNotFoundException | IOException e) {
      throw new GmdException("Failed to process gmd", e)
    }
  }

  /**
   * Process the Groovy Markdown text into standard markdown
   * @param text
   * @return a markdown text document
   */
  String gmdToMd(String text) throws GmdException {
    return GmdTemplateEngine.processCodeBlocks(text)
  }

  String mdToHtml(String markdown) throws GmdException {
    org.commonmark.node.Node document = parser.parse(markdown)
    return renderer.render(document)
  }

  String mdToHtmlDoc(String markdown) throws GmdException {
    return decorate(mdToHtml(markdown))
  }

  void mdToHtml(String markdown, File target) {
    Node document = parser.parse(markdown)
    target.write(renderer.render(document))
  }

  void mdToHtmlDoc(String markdown, File target) {
    if (target == null) {
      throw new IllegalArgumentException("target file cannot be null")
    }
    target.write(mdToHtmlDoc(markdown))
  }

  void mdToPdf(String md, File target) throws GmdException {
    try (def out = Files.newOutputStream(target.toPath())) {
      mdToPdf(md, out)
    } catch (Exception e) {
      throw new GmdException("Failed to convert markdown to pdf", e)
    }
  }

  void mdToPdf(String md, OutputStream target) throws GmdException {
    String html = mdToHtmlDoc(md)
    // TODO: "run" the html so that highlightJs can add appropriate style to the code sections
    //PdfConverterExtension.exportToPdf(target, html, "", pdfOptions)
    htmlToPdf(html, target)
  }

  String gmdToHtml(String gmd) throws GmdException {
    String md = gmdToMd(gmd)
    return mdToHtml(md)
  }

  String gmdToHtmlDoc(String gmd) throws GmdException {
    String md = gmdToMd(gmd)
    return mdToHtmlDoc(md)
  }

  String gmdToHtml(String gmd, Map bindings) throws GmdException {
    String md = gmdToMd(gmd, bindings)
    return mdToHtml(md)
  }

  String gmdToHtmlDoc(String gmd, Map bindings) throws GmdException {
    String md = gmdToMd(gmd, bindings)
    return mdToHtmlDoc(md)
  }

  void htmlToPdf(String html, OutputStream target) {
    // TODO: "run" the html so that highlightJs can add appropriate style to the code sections
    //PdfConverterExtension.exportToPdf(target, html, "", pdfOptions)
    var jsDoc = Jsoup.parse(html)
    Document doc = new W3CDom().fromJsoup(jsDoc)
    htmlToPdf(doc, target)
  }

  void htmlToPdf(String html, File file) throws IOException {
    if (file == null) {
      throw new IllegalArgumentException("File parameter cannot be null")
    }
    if (file.getParentFile() != null && !file.getParentFile().exists()) {
      file.getParentFile().mkdirs()
    }
    if (!file.exists()) {
      file.createNewFile()
    }
    try (OutputStream out = Files.newOutputStream(file.toPath())) {
      htmlToPdf(html, out)
    }
  }

  static void htmlToPdf(Document doc, OutputStream os) throws IOException {
    PdfRendererBuilder builder = basicBuilder()
        .withW3cDocument(doc, new File(".").toURI().toString())
        .toStream(os)
    builder.run()
  }

  private static PdfRendererBuilder basicBuilder() {
    new PdfRendererBuilder()
        .useSVGDrawer(new BatikSVGDrawer())
        .useMathMLDrawer(new MathMLDrawer())
  }

  /**
   * We load the html into a web view so that the highlight javascript properly add classes to code parts
   * then we extract the DOM from the web view and use that to produce the PDF
   * @param html a string containing the html to render
   * @param target the pdf file to write to
   * @param gmd the Gmd object used to write the pdf
   */
  static void processHtmlAndSaveAsPdf(String html, File target) {
    //noinspection GroovyResultOfObjectAllocationIgnored
    new JFXPanel() // Initiate graphics
    final CountDownLatch latchToWaitForJavaFx = new CountDownLatch(1);
    Platform.runLater {
      WebView webview = new WebView()
      final WebEngine webEngine = webview.getEngine()
      webEngine.setJavaScriptEnabled(true)
      webEngine.setUserStyleSheetLocation(BOOTSTRAP_CSS)
      webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
        @Override
        void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
          if (newState == Worker.State.SUCCEEDED) {
            Document doc = webEngine.getDocument()
            try (OutputStream os = Files.newOutputStream(target.toPath())) {
              Transformer transformer = TransformerFactory.newInstance().newTransformer()
              transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
              transformer.setOutputProperty(OutputKeys.METHOD, "html")
              transformer.setOutputProperty(OutputKeys.INDENT, "no")
              transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")

              StringWriter sw = new StringWriter()
              transformer.transform(new DOMSource(doc), new StreamResult(sw))
              String viewContent = sw.toString()
              // the raw DOM document will not work so we have to parse it again with jsoup to get
              // something that the PdfRendererBuilder (used in gmd) understands
              org.jsoup.nodes.Document doc2 = Jsoup.parse(viewContent)
              doc2.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
                  .escapeMode(Entities.EscapeMode.extended)
                  .charset(StandardCharsets.UTF_8)
                  .prettyPrint(false)
              Document doc3 = new W3CDom().fromJsoup(doc2)
              PdfRendererBuilder builder = new PdfRendererBuilder()
                  .useSVGDrawer(new BatikSVGDrawer())
                  .useMathMLDrawer(new MathMLDrawer())
                  .withW3cDocument(doc3, new File(".").toURI().toString())
                  .toStream(os)
              builder.run()
              latchToWaitForJavaFx.countDown()
            }
          }
        }
      })
      webEngine.loadContent(html)
    }
    latchToWaitForJavaFx.await()
  }
}
