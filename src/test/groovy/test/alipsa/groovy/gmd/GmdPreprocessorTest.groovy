package test.alipsa.groovy.gmd

import org.apache.commons.lang3.StringUtils

import static org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import se.alipsa.groovy.gmd.GmdPreprocessor

class GmdPreprocessorTest {

    @Test
    void testEcho() {
        String text = """
Before
```{groovy echo=TRUE}
    // just some Groovy code
    def x = 5
    out.println('Hello World')
```
After code block"""
        String processed = GmdPreprocessor.processCodeBlocks(text)
        String expected = """
Before
```groovy
    // just some Groovy code
    def x = 5
    out.println('Hello World')
```
Hello World
After code block"""

        if (!expected.equals(processed)) {
            println("Difference is: " + StringUtils.difference(expected, processed)
                + ", at index: " + StringUtils.indexOfDifference(expected, processed))
        }
        assertEquals(expected, processed)
    }

    @Test
    void testCodeOnly() {
        String text = """
            Before
            ```{groovy echo=false}
            // just some Groovy code
            def x = 5
            out.println('Hello World')  
            ```
            After"""

        assertEquals("""
            Before
Hello World
            After""", GmdPreprocessor.processCodeBlocks(text))
    }

    @Test
    void testNoEcho() {
        String text = """
            Before
            ```{groovy}
            // just some Groovy code
            def x = 5
            out.println('Hello World')  
            ```
            After
        """

        assertEquals("""
            Before
```groovy
            // just some Groovy code
            def x = 5
            out.println('Hello World')  
```
Hello World
            After
        """, GmdPreprocessor.processCodeBlocks(text))
    }

    @Test
    void testInlineVars() {
        def text = """
        ```{groovy echo=false}
            aVal = 123 + 234
        ```
        123 + 234 = `= aVal `
        """
        assertEquals("""
        123 + 234 = 357
        """, GmdPreprocessor.processCodeBlocks(text))
    }
}
