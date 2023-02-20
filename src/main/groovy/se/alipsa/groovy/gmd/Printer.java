package se.alipsa.groovy.gmd;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Printer extends PrintWriter {
    protected boolean trouble = false;

    public Printer() {
        super(new StringWriter());
    }

    protected void newLine() {
        try {
            synchronized (lock) {
                ensureOpen();
                out.write("\n");
            }

        } catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        } catch (IOException x) {
            trouble = true;
        }

    }

    private void ensureOpen() throws IOException {
        if (out == null) throw new IOException("Stream closed");
    }

    public void println() {
        newLine();
    }

    /**
     * Prints a boolean value and then terminates the line.  This method behaves
     * as though it invokes {@link #print(boolean)} and then
     * {@link #println()}.
     *
     * @param x the {@code boolean} value to be printed
     */
    public void println(boolean x) {
        synchronized (lock) {
            print(x);
            println();
        }

    }

    /**
     * Prints a character and then terminates the line.  This method behaves as
     * though it invokes {@link #print(char)} and then {@link
     * #println()}.
     *
     * @param x the {@code char} value to be printed
     */
    public void println(char x) {
        synchronized (lock) {
            print(x);
            println();
        }

    }

    /**
     * Prints an integer and then terminates the line.  This method behaves as
     * though it invokes {@link #print(int)} and then {@link
     * #println()}.
     *
     * @param x the {@code int} value to be printed
     */
    public void println(int x) {
        synchronized (lock) {
            print(x);
            println();
        }

    }

    /**
     * Prints a long integer and then terminates the line.  This method behaves
     * as though it invokes {@link #print(long)} and then
     * {@link #println()}.
     *
     * @param x the {@code long} value to be printed
     */
    public void println(long x) {
        synchronized (lock) {
            print(x);
            println();
        }

    }

    /**
     * Prints a floating-point number and then terminates the line.  This method
     * behaves as though it invokes {@link #print(float)} and then
     * {@link #println()}.
     *
     * @param x the {@code float} value to be printed
     */
    public void println(float x) {
        synchronized (lock) {
            print(x);
            println();
        }

    }

    /**
     * Prints a double-precision floating-point number and then terminates the
     * line.  This method behaves as though it invokes {@link
     * #print(double)} and then {@link #println()}.
     *
     * @param x the {@code double} value to be printed
     */
    public void println(double x) {
        synchronized (lock) {
            print(x);
            println();
        }

    }

    /**
     * Prints an array of characters and then terminates the line.  This method
     * behaves as though it invokes {@link #print(char[])} and then
     * {@link #println()}.
     *
     * @param x the array of {@code char} values to be printed
     */
    public void println(Character[] x) {
        synchronized (lock) {
            print(x);
            println();
        }

    }

    /**
     * Prints a String and then terminates the line.  This method behaves as
     * though it invokes {@link #print(String)} and then
     * {@link #println()}.
     *
     * @param x the {@code String} value to be printed
     */
    public void println(String x) {
        synchronized (lock) {
            print(x);
            println();
        }

    }

    /**
     * Prints an Object and then terminates the line.  This method calls
     * at first String.valueOf(x) to get the printed object's string value,
     * then behaves as
     * though it invokes {@link #print(String)} and then
     * {@link #println()}.
     *
     * @param x The {@code Object} to be printed.
     */
    public void println(Object x) {
        String s = String.valueOf(x);
        synchronized (lock) {
            print(s);
            println();
        }

    }

    @Override
    public String toString() {
        return out.toString();
    }

    public void clear() {
        ((StringWriter)out).getBuffer().setLength(0);
    }
}
