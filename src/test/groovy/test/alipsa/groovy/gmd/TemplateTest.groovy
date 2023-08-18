package test.alipsa.groovy.gmd

import static org.junit.jupiter.api.Assertions.*

import groovy.text.StreamingTemplateEngine
import org.junit.jupiter.api.Test
import se.alipsa.groovy.gmd.GmdTemplateEngine

class TemplateTest {
  def text = '''
  ```{groovy echo=false}
  import java.time.LocalDate 
  
  today = LocalDate.now() 
  out.print("Today is " + today)
  ```
  '''.stripIndent()

  @Test
  void testSimpleTemplateEngine() {
    def code = GmdTemplateEngine.processCodeBlocks(text)
    println code
    assertEquals("Today is 2023-08-18", code.trim())
  }

  @Test
  void testStreamingTemplateEngine() {
    def code = GmdTemplateEngine.processCodeBlocks(text)
    def result = new StreamingTemplateEngine().createTemplate(code).make()
    assertEquals("Today is 2023-08-18", result.toString().trim())
  }
}
