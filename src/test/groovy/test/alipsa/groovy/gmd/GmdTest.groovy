package test.alipsa.groovy.gmd

import org.junit.jupiter.api.Assertions
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
        Assertions.assertEquals("""
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
        Assertions.assertEquals("""\
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
        gmd.gmdToPdf(text, pdfFile)
        println "created ${pdfFile}, removing it"
        pdfFile.delete()
    }
}
