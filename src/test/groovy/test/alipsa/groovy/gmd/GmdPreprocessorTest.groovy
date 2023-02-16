package test.alipsa.groovy.gmd

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
            After
        """

        assertEquals("""
            Before
            ```{groovy echo=TRUE}
            // just some Groovy code
            def x = 5
            out.println('Hello World')  
```
<%
            // just some Groovy code
            def x = 5
            out.println('Hello World')  
%>
            After
        """, GmdPreprocessor.processCodeBlocks(text))
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
            After
        """

        assertEquals("""
            Before
<%
            // just some Groovy code
            def x = 5
            out.println('Hello World')  
%>
            After
        """, GmdPreprocessor.processCodeBlocks(text))
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
            ```{groovy}
            // just some Groovy code
            def x = 5
            out.println('Hello World')  
```
<%
            // just some Groovy code
            def x = 5
            out.println('Hello World')  
%>
            After
        """, GmdPreprocessor.processCodeBlocks(text))
    }
}
