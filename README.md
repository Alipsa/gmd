# gmd - Groovy Markdown

Groovy markdown is basically markdown with some groovy code for dynamic rendering.
It is based on the GmdTemplateEngine and the [Flexmark
Markdown package](https://github.com/vsch/flexmark-java).

A gmd file (or text) is markdown with groovy code in codeblocks starting with \```{groovy} and ending with \```
(similar to rmd and mdr files) and \`= \` for direct value output.

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
## Today (Saturday) is 2022-07-23.

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

This kind of Markdown text can then be transformed to html and pdf using the Gmd class e.g:
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
def text = 'Hello `=name`!'
def gmd = new se.alipsa.groovy.gmd.Gmd()
def md = gmd.gmdToMd(text, [name: "Per"])

// Or directly to html
def html = gmd.gmdToHtml(text, [name: "Per"])

// the html can then be used to create a pdf
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

Gmd supports processing the javascript by running it in the JavaFx WebView as follows
Example usage:

```groovy
def text = """
# Test

&grave;&grave;&grave;{groovy echo=false}
def a = 3
for (i in 1..a) {
  out.println('Hello ' + i)  
}
&grave;&grave;&grave;

- first 
- second

&grave;&grave;&grave;groovy
def q = 213
println('q is ' + q)
&grave;&grave;&grave;
 
X = ∑(√2π + ∛3) = `=Math.sqrt(2* Math.PI) + Math.cbrt(3)`
"""
def gmd = new Gmd()
def html = gmd.gmdToHtmlDoc(text)

// create a pdf file from the html
def pdfFile = File.createTempFile("test", ".pdf")
gmd.processHtmlAndSaveAsPdf(html, pdfFile)
```
Alternatives to using JavaFx WebView might be [Web-K](https://github.com/Earnix/Web-K) or [J2V8](https://github.com/eclipsesource/J2V8)
, but I have not tested any of those.

The library, which requires Java 21 or later, is available from maven central:

Gradle: 
```groovy
implementation "se.alipsa.groovy:gmd:2.0.0"
```

Maven:
```xml
<build>
  <dependencies>  
    <dependency>
      <groupId>se.alipsa.groovy</groupId>
      <artifactId>gmd</artifactId>
      <version>2.0.0</version>
    </dependency>
  </dependencies>
</build>
```
## Using Gmd from the command line
The release artifacts on github contains a fat jar (e.g. gmd-bundled-2.0.0.jar)
that enables you to use Gmd from the command line.
```
java -jar gmd-bundled-2.0.0.jar toHtml test.gmd test.html
```
or for a pdf:
```
java -jar gmd-bundled-2.0.0.jar toPdf test.gmd test.pdf
```
Note: If you don't want the styled (highlight) PDF version you can use toPdfRaw instead.
