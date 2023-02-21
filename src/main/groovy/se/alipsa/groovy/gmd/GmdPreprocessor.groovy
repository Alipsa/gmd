package se.alipsa.groovy.gmd

import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl

import java.util.regex.Matcher

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
     * Hello World
     *
     * @param text the gmd text to process
     * @return the gmd text with code blocks "expanded"
     */
    static String processCodeBlocks(String text) {
        def classLoader = new GroovyClassLoader();
        def engine = new GroovyScriptEngineImpl(classLoader)
        try (Printer out = new Printer()) {
            engine.put("out", out)
            boolean shouldBeProcessed = false
            boolean codeBlockStart = false
            boolean codeBlockEnd = false
            boolean echo = true
            String noSpaceLine
            StringBuilder codeBlockText = new StringBuilder()
            StringBuilder result = new StringBuilder();
            List<String> lines = text.readLines()
            int count = 0
            lines.each { line ->
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
                    List<String> codeBlockCode = codeBlockText.readLines()
                    if (echo) {
                        codeBlockCode.set(0, '```groovy')
                        result.append(String.join('\n', codeBlockCode)).append('\n```\n')
                    }

                    codeBlockCode.remove(0)
                    String codeBlock = String.join('\n', codeBlockCode)
                    //result.append("<%\n").append(codeBlock).append('\n%>\n')
                    def tmp = engine.eval(codeBlock)
                    def output = out.toString()
                    if (output.length() > 0) {
                        result.append(output)
                    } else if (echo) {
                        result.append(tmp)
                    }
                    out.clear()
                    codeBlockText.setLength(0)
                    codeBlockEnd = false
                } else if (!codeBlockStart) {
                    if (line.contains('`= ')) {
                        shouldBeProcessed = true
                        result.append(expandInlineVars(line, engine))
                    } else {
                        result.append(line)
                    }
                    if (count < lines.size() - 1)
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

    /**
     * Evaluate and replace all `= ` inline code blocks
     * the expression `= aVal ` is matched into two parts
     * one containing the full expression (`= aVal `) and the other
     * just the part to be evaluated (aVal )
     */
    static String expandInlineVars(String line, GroovyScriptEngineImpl engine) {
        Matcher matcher = line =~ /`= (.+?)`/
        String newLine = line
        if (matcher.find()) {
            List<List<String>> matches = matcher.findAll()
            matches.each { expVal ->
                String expression = expVal.get(0)
                String val = expVal.get(1)
                String evaluatedVal = String.valueOf(engine.eval(val))
                newLine = newLine.replace(expression, evaluatedVal)
            }
            return newLine
        } else {
            return line
        }

    }
}
