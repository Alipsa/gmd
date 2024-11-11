package se.alipsa.groovy.gmd;

import se.alipsa.groovy.charts.Chart;
import se.alipsa.groovy.charts.Plot;
import se.alipsa.groovy.matrix.Matrix;

class Printer extends PrintWriter {

    Printer() {
        super(new StringWriter());
    }

    void print(Character[] x) {
        x.each {
            print(it)
        }
    }
    /**
     * Prints an array of characters and then terminates the line.  This method
     * behaves as though it invokes {@link #print(char[])} and then
     * {@link #println()}.
     *
     * @param x the array of {@code char} values to be printed
     */
    void println(Character[] x) {
        print(x)
        println()
    }

    void print(Matrix x, Map<String,String> tableAttributes) {
        // todo: Figure out how to get the Table extension to work or add Matrix.toHtml(tableAttributes) as a work around
        print(x.toMarkdown(tableAttributes))
    }

    void println(Matrix x, Map<String,String> tableAttributes) {
        println(x.toMarkdown(tableAttributes))
    }

    void print(Matrix x) {
        print(x, ["class": "table"])
    }

    void println(Matrix x) {
        println(x, ["class": "table"])
    }


    private static String chartToMd(Chart x, double width, double height, String alt, Map<String, String> attributes) {
        StringBuilder attr = new StringBuilder()
        if (attributes.size() > 0) {
            attr.append('{')
            attributes.each {
                attr.append(it.key).append('=').append(it.value).append(' ')
            }
            attr.append('}')
        }
        return "!['${alt}'](${Plot.base64(x, width, height)})${attr.toString()}"
    }

    void print(Chart x, double width = 800, double height = 600, String alt = '', Map<String, String> attributes = [:]) {
        print(chartToMd(x, width, height, alt, attributes))
    }

    void println(Chart x, double width = 800, double height = 600, String alt = '', Map<String, String> attributes = [:]) {
        println(chartToMd(x, width, height, alt, attributes))
    }

    @Override
    String toString() {
        return super.out.toString();
    }

    void clear() {
        ((StringWriter)super.out).getBuffer().setLength(0);
    }
}
