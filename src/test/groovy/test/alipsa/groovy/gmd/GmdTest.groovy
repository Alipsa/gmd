package test.alipsa.groovy.gmd

import static org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import se.alipsa.groovy.gmd.Gmd

class GmdTest {

  def text = """\
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
        
        The weather in next 3 days will be:
        <% def weather = [ "Sunny", "Rainy", "Cloudy", "Windy" ]
          for (i = 1; i < 4; i++) {
            def day = now.plusDays(i)
            out.println "- " + dayName(day) + ": " + weather.get(i-1)
          } %>
        Now, that's something to look forward to!
        """.stripIndent()

  @Test
  void gmdToMd() {
    def gmd = new Gmd()
    def md = gmd.gmdToMd(text)
    assertEquals("""
        # Hello

        Today (Saturday) is 2022-07-23.
        
        The weather in next 3 days will be:
        - Sunday: Sunny
        - Monday: Rainy
        - Tuesday: Cloudy
        
        Now, that's something to look forward to!
        """.stripIndent(), md)
  }

  @Test
  void gmdToHtml() {
    def gmd = new Gmd()
    def html = gmd.gmdToHtml(text)
    assertEquals("""\
            <h1>Hello</h1>
            <p>Today (Saturday) is 2022-07-23.</p>
            <p>The weather in next 3 days will be:</p>
            <ul>
            <li>Sunday: Sunny</li>
            <li>Monday: Rainy</li>
            <li>Tuesday: Cloudy</li>
            </ul>
            <p>Now, that's something to look forward to!</p>
            """.stripIndent(), html)
  }

  @Test
  void gmdToPdf() {
    def gmd = new Gmd()
    def pdfFile = File.createTempFile("weather", ".pdf")
    def html = gmd.gmdToHtmlDoc(text)
    gmd.htmlToPdf(html, pdfFile)
    assertTrue(pdfFile.exists())
    pdfFile.delete()
  }

  @Test
  void gmdToMdWithParameter() {
    def text = '## Hello ${name}!'
    def gmd = new Gmd()
    def md = gmd.gmdToMd(text, [name: "Per"])
    assertEquals("## Hello Per!", md)
  }

  @Test
  void gmdToHtmlWithParameter() {
    def text = '## Hello ${name}!'
    def gmd = new Gmd()
    def html = gmd.gmdToHtml(text, [name: "Per"])
    assertEquals("<h2>Hello Per!</h2>\n", html)
  }

  @Test
  void gmdToPdfWithParameter() {
    def text = '## Hello ${name}!'
    def gmd = new Gmd()
    def html = gmd.gmdToHtmlDoc(text, [name: "Per"])
    def pdfFile = File.createTempFile("weather", ".pdf")
    gmd.htmlToPdf(html, pdfFile)
    assertTrue(pdfFile.exists())
    pdfFile.delete()
  }

  @Test
  void testHtmlWithSpecialCharacters() {
    String text = """\
        # Some equations
        X = &sum;(&radic;2&pi; + &#8731;3)
        """.stripIndent()

    def gmd = new Gmd()
    String html = gmd.gmdToHtml(text)

    assertEquals("<h1>Some equations</h1>\n" +
        "<p>X = ∑(√2π + ∛3)</p>\n", html)
  }

  @Test
  void testPdfWithSpecialCharacters() {
    String text = """\
        # Some equations
        X = ∑(√2π + ∛3)
        """.stripIndent()

    def gmd = new Gmd()
    String html = gmd.gmdToHtmlDoc(text)

    assertTrue(html.contains("<h1>Some equations</h1>\n<p>X = ∑(√2π + ∛3)</p>\n"))

    def pdfFile = File.createTempFile("special", ".pdf")
    gmd.htmlToPdf(html, pdfFile)
    assertTrue(pdfFile.exists())
    pdfFile.delete()
  }

  @Test
  void gmdToHtmlDoc() {
    String text = """\
      # Test
      <%
      def a = 3
      for (i in 1..a) {
        out.println("Hello \${i}")  
      }
      %>
      
      - first 
      - second
      
      ```groovy
      def q = 213
      println('q is ' + q)
      ``` 
      X = &sum;(&radic;2&pi; + &#8731;3)
      X = ∑(√2π + ∛3)  
    """.stripIndent()
    def gmd = new Gmd()
    def html = gmd.gmdToHtmlDoc(text)
    assertTrue html.contains("""\
      <h1>Test</h1>
      <p>Hello 1<br />
      Hello 2<br />
      Hello 3</p>
      <ul>
      <li>first</li>
      <li>second</li>
      </ul>
      <pre><code class="language-groovy">def q = 213
      println('q is ' + q)
      </code></pre>
      <p>X = ∑(√2π + ∛3)<br />
      X = ∑(√2π + ∛3)</p>
      
      </body>""".stripIndent())
    assertTrue(html.startsWith("""\
      <!DOCTYPE html PUBLIC
      "-//OPENHTMLTOPDF//MATH XHTML Character Entities With MathML 1.0//EN" "">
      <html>""".stripIndent()), "Doctype declaration is missing")
    assertTrue(html.contains("code.hljs{"), "Highligtjs style missing")
    assertTrue(html.contains("root{--bs-blue:"), "Bootrap style missing")
    assertTrue(html.contains("hljs=function()"), "highlighJs init script missing")
  }

  @Test
  void traditionalMarkdownCode() {
    def text = """
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
      out.println "Today (" + dayName(now) + ") is " + now + "."
    ```
    How about that?    
    """.stripIndent()
    def gmd = new Gmd()
    def md = gmd.gmdToMd(text)

    assertEquals("""
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
      out.println "Today (" + dayName(now) + ") is " + now + "."
    ```
    Today (Saturday) is 2022-07-23.
    
    How about that?""".stripIndent(), md)
  }
}
