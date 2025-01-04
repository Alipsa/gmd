package se.alipsa.groovy.gmd;

import se.alipsa.matrix.charts.Chart;
import se.alipsa.matrix.charts.Plot;
import se.alipsa.matrix.core.Matrix;

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
        // The Table extension in commonmark does not support custom attributes so we use toHtml as a work around
        print(x.toHtml(tableAttributes))
    }

    void println(Matrix x, Map<String,String> tableAttributes) {
        println(x.toHtml(tableAttributes))
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
