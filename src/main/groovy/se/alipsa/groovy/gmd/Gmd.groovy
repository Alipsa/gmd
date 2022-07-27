package se.alipsa.groovy.gmd

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

    static final XHTML_MATHML_DOCTYPE = "<!DOCTYPE html PUBLIC\n \"-//OPENHTMLTOPDF//MATH XHTML Character Entities With MathML 1.0//EN\" \"\">\n"
    final SimpleTemplateEngine engine
    final Parser parser
    final HtmlRenderer renderer
    final DataHolder pdfOptions

    Gmd() {
        XRLog.setLoggerImpl(new Log4jXRLogger());
        engine = new SimpleTemplateEngine()
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
    String gmdToMd(String text, Map bindings) {
        def template = engine.createTemplate(text)
        return template.make(bindings)
    }

    /**
     * Process the Groovy Markdown text into standard markdown
     * @param text
     * @return a markdown text document
     */
    String gmdToMd(String text) {
        def template = engine.createTemplate(text)
        return template.make()
    }

    String mdToHtml(String markdown) {
        Node document = parser.parse(markdown)
        return renderer.render(document)
    }

    void mdToHtml(String markdown, File target) {
        Node document = parser.parse(markdown)
        target.text = renderer.render(document)
    }

    void mdToPdf(String md, File target) {
        try(def out = Files.newOutputStream(target.toPath())) {
            mdToPdf(md, out)
        }
    }

    void mdToPdf(String md, OutputStream target) {
        String html = mdToHtml(md)
        PdfConverterExtension.exportToPdf(target, html, "", pdfOptions)
    }

    String gmdToHtml(String gmd) {
        String md = gmdToMd(gmd)
        return mdToHtml(md)
    }

    String gmdToHtml(String gmd, Map bindings) {
        String md = gmdToMd(gmd, bindings)
        return mdToHtml(md)
    }

    void gmdToPdf(String gmd, File file) {
        try (OutputStream out = Files.newOutputStream(file.toPath())) {
            gmdToPdf(gmd, out)
        }
    }

    void gmdToPdf(String gmd, Map bindings, File file) {
        try (OutputStream out = Files.newOutputStream(file.toPath())) {
            gmdToPdf(gmd, bindings, out)
        }
    }

    void gmdToPdf(String gmd, OutputStream target) {
        String md = gmdToMd(gmd)
        mdToPdf(md, target)
    }

    void gmdToPdf(String gmd, Map bindings, OutputStream target) {
        String md = gmdToMd(gmd, bindings)
        mdToPdf(md, target)
    }

    void htmlToPdf(String html, OutputStream target) {
        PdfConverterExtension.exportToPdf(target, html, "", pdfOptions)
    }

    void htmlToPdf(String html, File file) {
        try (OutputStream out = Files.newOutputStream(file.toPath())) {
            htmlToPdf(html, out)
        }
    }

    void htmlToPdf(Document doc, OutputStream os){
        PdfRendererBuilder builder = new PdfRendererBuilder()
                .withW3cDocument(doc, new File(".").toURI().toString())
                .useSVGDrawer(new BatikSVGDrawer())
                .useMathMLDrawer(new MathMLDrawer())
                .toStream(os);
        builder.run();
    }

}
