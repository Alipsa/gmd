# gmd - Groovy Markdown

Groovy markdown is basically markdown with some groovy code for dynamic rendering.
It is based on the Groovy [StreamingTemplateEngine](https://groovy-lang.org/templating.html) and the [Flexmark
Markdown package](https://github.com/vsch/flexmark-java).

A gmd file (or text) is markdown with groovy code in codeblocks starting with \```{groovy} and ending with \```
(similar to rmd and mdr files). An alternative syntax where code is enclosed between <% %> bracket 
(or <%= %> for direct value output) or is also supported.

There are some advantages with the codeblock (\```{groovy}) approach over the scriptlet (<% %>) one, 
mainly that variables and functions are "remembered" and can be used further down in the gmd document.

Here is an example:

```markdown
    # The thing
    Here it is
    ```{groovy}
      import java.time.LocalDate
      import java.time.format.TextStyle
      import java.util.Locale

      def now = LocalDate.parse("2022-07-23")
      def dayName(theDate) {
        return theDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault())
      }
      out.println "## Today (" + dayName(now) + ") is " + now + "."
    ```
    How about that?    
```
This will generate the following markdown
````markdown
# The thing
Here it is
```groovy
  import java.time.LocalDate
  import java.time.format.TextStyle
  import java.util.Locale

  def now = LocalDate.parse("2022-07-23")
  def dayName(theDate) {
    return theDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault())
  }
  out.println "## Today (" + dayName(now) + ") is " + now + "."
```
Today (Saturday) is 2022-07-23.

How about that?
````
If you don't want to echo the code in the Markdown document you can set the
echo property to false e.g. \```{groovy echo=false}

Inline variables (similar to the <%= expression %> syntax in scriptlets) can be done using \`= expression \`
here is an example:
````markdown
```{groovy echo=false}
    aVal = 123 + 234
```
123 + 234 = `= aVal `
````
Which will result in
```markdown
123 + 234 = 357
```

Here is a simple example of using scriptlets (<% %>):

```jsp
<% 
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

def now = LocalDate.parse("2022-07-23")

def dayName(theDate) {
  return theDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault())
}
%>
# Hello
Today (<%= dayName(now) %>) is <%= now %>.

The weather in next couple of days will be:
<%
  def weather = [ "Sunny", "Rainy", "Cloudy", "Windy" ]
  for (i in 1..3) {
    def day = now.plusDays(i)
    Collections.shuffle weather
    out.println("- " + dayName(day) + ": " + weather.first())
  }
%>
```

Which will generate the following markdown:
```markdown
# Hello
Today (Saturday) is 2022-07-23.

The weather in next couple of days will be:
- Sunday: Cloudy
- Monday: Cloudy
- Tuesday: Sunny

```

This kind of markdown text can then be transformed to html and pdf using the Gmd class e.g:
```groovy
def gmd = new se.alipsa.groovy.gmd.Gmd()
// html is a string of html markup
def html = gmd.gmdToHtml(text)

// create a pdf file from the html
def pdfFile = File.createTempFile("weather", ".pdf")
gmd.htmlToPdf(html, pdfFile)
```

If you want to pass parameters to be used in the gmd text/file you can do that like this:
```groovy
def text = 'Hello ${name}!'
def gmd = new se.alipsa.groovy.gmd.Gmd()
def md = gmd.gmdToMd(text, [name: "Per"])

// Or directly to html
def html = gmd.gmdToHtml(text, [name: "Per"])

// the html can then be used to create a pdf pdf
gmd.htmlToPdf(html, [name: "Per"], new File("pdfFile.pdf"))
```

For "Special" characters e.g. match symbol, you could use the html escape codes. E.g.
to write `X = ∑(√2π + ∛3)`, you could do `X = &amp;sum;(&amp;radic;2&amp;pi; + &amp;#8731;3)` and scope the 
expression with parenthesis as appropriate. Otherwise, it will show up as `X = ?(?2? + ?3)` when you turn it into html or pdf.
See [HTML Math Symbols](https://www.toptal.com/designers/htmlarrows/math/) for an extensive list.
An alternative is to generate a whole html doc encoded in UTF-8 that includes unicode fonts. 
The gmdToHtmlDoc() and mdToHtmlDoc() does just that. Those methods also includes highlightJs and Bootstrap in the html.

HighlightJS requires the execution of the highligtJs init script for the code sections to be properly formatted. 
In order for this to happen, the html code need to be rendered in a browser with javascript support. 

Here is an example of doing this using a javaFx WebView:
```groovy
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
import javafx.scene.web.WebView

import se.alipsa.groovy.gmd.Gmd

/**
 * We load the html into a web view so that the highlight javascript properly add classes to code parts
 * then we extract the DOM from the web view and use that to produce the PDF
 * @param html a string containing the html to render
 * @param target the pdf file to write to
 * @param gmd the Gmd object used to write the pdf
 */
void saveHtmlAsPdf(String html, File target, Gmd gmd) {
  WebView webview = new WebView()
  final WebEngine webEngine = webview.getEngine()
  webEngine.setJavaScriptEnabled(true)
  webEngine.setUserStyleSheetLocation(Gmd.BOOTSTRAP_CSS)
  webEngine.getLoadWorker().stateProperty().addListener(
    (ov, oldState, newState) -> {
      if (newState == Worker.State.SUCCEEDED) {
        Document doc = webEngine.getDocument()

        try(OutputStream os = Files.newOutputStream(target.toPath()))  {
          String viewContent = toString(doc)

          // the raw DOM document will not work so we have to parse it again with jsoup to get
          // something that the PdfRendererBuilder (used in gmd) understands
          org.jsoup.nodes.Document doc2 = Jsoup.parse(viewContent)
          doc2.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
              .escapeMode(Entities.EscapeMode.extended)
              .charset(StandardCharsets.UTF_8)
              .prettyPrint(false)
          Document doc3 = new W3CDom().fromJsoup(doc2)
          gmd.htmlToPdf(doc3, os)
        } 
      }
    })
  webEngine.loadContent(html);
}

/**
 * Convert a W3C document to a string
 * @param doc
 * @return the String representation of the document
 * @throws TransformerException if it is not possible to transform the document
 */
String toString(Document doc) throws TransformerException {
  Transformer transformer = TransformerFactory.newInstance().newTransformer();
  transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
  transformer.setOutputProperty(OutputKeys.METHOD, "html");
  transformer.setOutputProperty(OutputKeys.INDENT, "no");
  transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

  StringWriter sw = new StringWriter();
  transformer.transform(new DOMSource(doc), new StreamResult(sw));
  return sw.toString();
}

// Example usage:
def text = """
# Test

<%
def a = 3
for (i in 1..a) {
  out.println('Hello ' + i)  
}
%>

- first 
- second

&grave;&grave;&grave;groovy
def q = 213
println('q is ' + q)
&grave;&grave;&grave;
 
X = ∑(√2π + ∛3) = <%=Math.sqrt(2* Math.PI) + Math.cbrt(3)%>
"""
def gmd = new Gmd()
def html = gmd.gmdToHtmlDoc(text)

// create a pdf file from the html
def pdfFile = File.createTempFile("test", ".pdf")
saveHtmlAsPdf(html, pdfFile, gmd)

```
Alternatives to using JavaFx WebView might be [Web-K](https://github.com/Earnix/Web-K) or [J2V8](https://github.com/eclipsesource/J2V8)
but I have not tested any of those.

The library is available from maven central:

Gradle: 
```groovy
implementation "se.alipsa.groovy:gmd:1.0.7"
```

Maven:
```xml
<dependency>
    <groupId>se.alipsa.groovy</groupId>
    <artifactId>gmd</artifactId>
    <version>1.0.7</version>
</dependency>
```

Release history

### v1.0.7, 2023-02-24
- Fix bug in code md snippets so that \```{groovy} now becomes \```groovy
- Add support for value insertion (`=)
- Throw gmd exceptions if something goes wrong

### v1.0.6, 2023-02-17
- add support for executing groovy code in the code md code snippets

### v1.0.5, 2023-02-15
- Change groovy dependency from implementation to compileOnly

### v1.0.4, 2022-08-16
- htmlToPdf now creates the file if it does not exist
- upgrade bootstrap to 5.2.0

### v1.0.3, 2022-07-29
- remove gmdToPdf and mdToPdf methods since the output is not faithful to the html
- add docs on how to render a pdf faithful to the html

### v1.0.2, 2022-07-26
- add htmlToPdf methods

### v1.0.1, 2022-07-25
- upgrade to groovy 4.0.4
- Fix deploy script so publish to maven central works

### v1.0.0, 2022-07-24
- initial version