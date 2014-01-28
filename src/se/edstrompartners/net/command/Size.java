package se.edstrompartners.net.command;

public class Size {

    public static int of(boolean b) {
        return 1;
    }

    public static int of(byte b) {
        return 1;
    }

    public static int of(short s) {
        return 2;
    }

    public static int of(char c) {
        return 2;
    }

    public static int of(int i) {
        return 4;
    }

    public static int of(float f) {
        return 4;
    }

    public static int of(double d) {
        return 8;
    }

    public static int of(long l) {
        return 8;
    }

    public static int of(boolean[] b) {
        return 4 + b.length;
    }

    public static int of(byte[] b) {
        return 4 + b.length;
    }

    public static int of(short[] s) {
        return 4 + 2 * s.length;
    }

    public static int of(char[] c) {
        return 4 + 2 * c.length;
    }

    public static int of(int[] i) {
        return 4 + 4 * i.length;
    }

    public static int of(float[] f) {
        return 4 + 4 * f.length;
    }

    public static int of(double[] d) {
        return 4 + 8 * d.length;
    }

    public static int of(long[] l) {
        return 4 + 8 * l.length;
    }

    public static int of(String s) {
        return 4 + s.length();
    }

}
