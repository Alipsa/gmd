# gmd - Groovy Markdown

Groovy markdown is basically markdown with some groovy code for dynamic rendering.
It is based on the Groovy [StreamingTemplateEngine](https://groovy-lang.org/templating.html) and the [Flexmark
Markdown package](https://github.com/vsch/flexmark-java).

A gmd file (or text) is markdown with groovy code enclosed between <% %> bracket (or <%= %> for direct value output). 
Here is a simple example:

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

This kind of markdown text can then be transformed to html or pdf using the Gmd class e.g:
```groovy
def gmd = new se.alipsa.groovy.gmd.Gmd()
// html is a string of html markup
def html = gmd.gmdToHtml(text)

// create a pdf file from the groovy markdown
def pdfFile = File.createTempFile("weather", ".pdf")
gmd.gmdToPdf(text, pdfFile)
```

If you want to pass parameters to be used in the gmd text/file you can do that like this:
```groovy
def text = 'Hello ${name}!'
def gmd = new se.alipsa.groovy.gmd.Gmd()
def md = gmd.gmdToMd(text, [name: "Per"])

// Or directly to html
def html = gmd.gmdToHtml(text, [name: "Per"])

// Or pdf
gmd.gmdToPdf(text, [name: "Per"], new File("pdfFile.pdf"))
```

For "Special" characters e.g. match symbol, you should use the html escape codes. E.g.
to write `X = ∑(√2π + ∛3)`, you need to do `X = &sum;(&radic;2&pi; + &#8731;3)` and scope the 
expression with parenthesis as appropriate. Otherwise, it will show up as `X = ?(?2? + ?3)` when you turn it into html or pdf.
See [HTML Math Symbols](https://www.toptal.com/designers/htmlarrows/math/) for an extensive list.


The package is available from maven central:

Gradle: 
```groovy
implementation "se.alipsa.groovy:gmd:1.0.1"
```

Maven:
```xml
<dependency>
    <groupId>se.alipsa.groovy</groupId>
    <artifactId>gmd</artifactId>
    <version>1.0.1</version>
</dependency>
```

Release history

### v1.0.1, 2022-07-25
- upgrade to groovy 4.0.4
- Fix deploy script so publish to maven central works

### v1.0.0, 2022-07-24
- initial version