import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertTrue

@CompileStatic
class TestGmd {
  @Test
  void testGmd() {
    File targetDir = new File("build/gmd")
    assertTrue(targetDir.exists(), "Target directory does not exist")
    def testHtml = new File(targetDir, 'test.html')
    assert testHtml.exists()
    assert testHtml.text.contains("<h1>Greetings</h1>")
    assert testHtml.text.contains("Hello world!")
    println "$testHtml is as expected"

    def testInlineHtml = new File(targetDir, 'inline.html')
    assert testInlineHtml.exists()
    assert testInlineHtml.text.contains("<h1>Inline</h1>")
    assert testInlineHtml.text.contains("Today is ")
    assert testInlineHtml.text.contains(" and the time is ")
    println("$testInlineHtml is as expected")
  }
}