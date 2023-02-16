package se.alipsa.groovy.gmd

class GmdPreprocessor {


    /**
     * Appends a code block copied from the groovy code section e.g.
     * ```{groovy echo=TRUE}
     * // just some Groovy code
     * def x = 5
     * out.println('Hello World')
     * ```
     * becomes
     * ```{groovy echo=TRUE}
     * // just some Groovy code
     * def x = 5
     * out.println('Hello World')
     * ```
     * <%
     * // just some Groovy code
     * def x = 5
     * out.println('Hello World')*
     * %>
     *
     * @param text the gmd text to process
     * @return the gmd text with code blocks "expanded"
     */
    static String processCodeBlocks(String text) {
        boolean shouldBeProcessed = false
        boolean codeBlockStart = false
        boolean codeBlockEnd = false
        boolean echo = true
        String noSpaceLine
        StringBuilder codeBlockText = new StringBuilder()
        StringBuilder result = new StringBuilder();
        List<String> lines = text.readLines()
        int count = 0
        lines.each {line ->
            noSpaceLine = line.replace(' ', '').trim()

            if (noSpaceLine.startsWith('```{groovy')) {
                shouldBeProcessed = true
                codeBlockStart = true
                codeBlockEnd = false
                if (noSpaceLine.toLowerCase().contains("echo=false")) {
                    echo = false;
                }
            } else if (codeBlockStart && noSpaceLine.startsWith('```')) {
                codeBlockStart = false
                codeBlockEnd = true
            }

            if (codeBlockStart) {
                codeBlockText.append(line).append('\n')
            }

            if (codeBlockEnd) {
                if (echo) {
                    result.append(codeBlockText).append('```\n')
                }
                List<String> codeBlockCode = codeBlockText.readLines()
                codeBlockCode.remove(0)
                result.append("<%\n").append(String.join('\n', codeBlockCode)).append('\n%>\n')
                codeBlockText.setLength(0)
                codeBlockEnd = false
            } else if (!codeBlockStart) {
                result.append(line)
                if (count < lines.size() -1)
                    result.append('\n')
            }
            count++
        }
        if (shouldBeProcessed) {
            return result.toString()
        } else {
            return text
        }
    }
}
