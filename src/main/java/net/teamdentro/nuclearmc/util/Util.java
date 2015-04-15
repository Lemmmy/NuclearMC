package net.teamdentro.nuclearmc.util;

public class Util {
    /**
     * Clamp a generic numeric
     *
     * @param val The numeric value to clamp
     * @param min The minimum value
     * @param max The maximum value
     * @param <T> Some bullshit
     * @return The clamped value
     */
    public static <T extends Comparable<T>> T clamp(T val, T min, T max) {
        if (val.compareTo(min) < 0) return min;
        else if (val.compareTo(max) > 0) return max;
        else return val;
    }

    /**
     * Split a string in Pascal Case.
     * <p/>
     * This will transform strings like such:
     * <p/>
     * <table>
     * <thead>
     * <tr><th>Input</th><th>Output</th></tr>
     * <thead>
     * <tbody>
     * <tr><td>lowercase</td><td>lowecase</td></tr>
     * <tr><td>Class</td><td>Class</td></tr>
     * <tr><td>MyClass</td><td>My Class</td></tr>
     * <tr><td>HTML</td><td>HTML</td></tr>
     * <tr><td>PDFLoader</td><td>PDF Loader</td></tr>
     * <tr><td>AString</td><td>A String</td></tr>
     * <tr><td>SimpleXMLParser</td><td>Simple XML Parser</td></tr>
     * <tr><td>GL11Version</td><td>GL 11 Version</td></tr>
     * <tr><td>99Bottles</td><td>99 bottles</td></tr>
     * <tr><td>May5</td><td>May 5</td></tr>
     * <tr><td>BFG9000</td><td>BFG 9000</td></tr>
     * </tbody>
     * </table>
     *
     * @param s The input string
     * @return The Pascal split string
     * @author polygenelubricants
     * you didn't think I'd write this myself, did you? :3
     */
    public static String splitPascalCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }
}
