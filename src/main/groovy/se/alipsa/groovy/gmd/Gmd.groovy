package se.alipsa.groovy.gmd

import org.codehaus.groovy.control.CompilationFailedException

import static se.alipsa.groovy.gmd.HtmlDecorator.*
import com.openhtmltopdf.mathmlsupport.MathMLDrawer
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import com.openhtmltopdf.svgsupport.BatikSVGDrawer
import com.openhtmltopdf.util.XRLog
import com.vladsch.flexmark.ext.attributes.AttributesExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.toc.TocExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension
import com.vladsch.flexmark.profile.pegdown.Extensions
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.DataHolder
import com.vladsch.flexmark.util.data.MutableDataSet
import groovy.text.SimpleTemplateEngine
import org.w3c.dom.Document

import java.nio.file.Files

/**
 * Key class for this Groovy Markdown implementation
 */
class Gmd {
    //final SimpleTemplateEngine engine
    final Parser parser
    final HtmlRenderer renderer
    final DataHolder pdfOptions

    Gmd() {
        XRLog.setLoggerImpl(new Log4jXRLogger());
        //engine = new SimpleTemplateEngine()
        MutableDataSet options = new MutableDataSet()

        // add extensions
        options.set(Parser.EXTENSIONS, Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                AttributesExtension.create())
        )
        // convert soft-breaks to hard breaks
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n")
        parser = Parser.builder(options).build()
        renderer = HtmlRenderer.builder(options)
                .build();

        pdfOptions = PegdownOptionsAdapter.flexmarkOptions(
                Extensions.ALL & ~(Extensions.ANCHORLINKS | Extensions.EXTANCHORLINKS_WRAP)
                , TocExtension.create()).toMutable()
                .set(TocExtension.LIST_CLASS, PdfConverterExtension.DEFAULT_TOC_LIST_CLASS)
                .toImmutable();
    }

    /**
     * Process the Groovy Markdown text into standard markdown
     * @param text
     * @param bindings the variables to resolve in the text
     * @return a markdown text document
     */
    String gmdToMd(String text, Map bindings) throws GmdException {
        try {
            //def template = engine.createTemplate(GmdPreprocessor.processCodeBlocks(text, bindings))
            //return String.valueOf(template.make(bindings)).replace("\r\n", "\n")
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
        /*
        def updatedText = GmdTemplateEngine.processCodeBlocks(text)
        def template = engine.createTemplate(updatedText)
        return String.valueOf(template.make()).replace("\r\n", "\n")
        */
    }

    String mdToHtml(String markdown) throws GmdException {
        Node document = parser.parse(markdown)
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

    void mdToPdf(String md, File target)  throws GmdException {
        try(def out = Files.newOutputStream(target.toPath())) {
            mdToPdf(md, out)
        } catch (Exception e) {
            throw new GmdException("Failed to convert markdown to pdf", e)
        }
    }

    void mdToPdf(String md, OutputStream target) throws GmdException {
        String html = mdToHtmlDoc(md)
        // TODO: "run" the html so that highlightJs can add appropriate style to the code sections
        PdfConverterExtension.exportToPdf(target, html, "", pdfOptions)
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
        PdfConverterExtension.exportToPdf(target, html, "", pdfOptions)
    }

    void htmlToPdf(String html, File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File parameter cannot be null")
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        try (OutputStream out = Files.newOutputStream(file.toPath())) {
            htmlToPdf(html, out)
        }
    }

    void htmlToPdf(Document doc, OutputStream os) throws IOException {
        PdfRendererBuilder builder = new PdfRendererBuilder()
                .withW3cDocument(doc, new File(".").toURI().toString())
                .useSVGDrawer(new BatikSVGDrawer())
                .useMathMLDrawer(new MathMLDrawer())
                .toStream(os);
        builder.run();
    }

}
