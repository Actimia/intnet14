package se.edstrompartners.net.command;

import java.nio.charset.Charset;

public class CommandEncoder {

    private static final Charset latin1 = Charset.forName("ISO-8859-1");

    private byte[] bytes;
    private int offset;

    /**
     * Creates a new CommandEncoder for a command. It will encode all the common
     * fields (size and ID).
     * 
     * @param com
     *            The command to encode.
     */
    public CommandEncoder(Command com) {
        this.bytes = new byte[com.datasize() + 8];
        encodeCommon(com);
    }

    /**
     * Fetches the bytes for the command. Dont use this reference until encoding
     * is done unless you know what you are doing.
     * 
     * @return
     */
    public byte[] getBytes() {
        return bytes;
    }

    private CommandEncoder encodeCommon(Command com) {
        encode(com.datasize() + 8);
        encode(com.getType().getID());
        return this;
    }

    /**
     * Encodes an object using a custom handler, registered with
     * Command.registerTypeHandler(). Will throw exception if no handler is
     * registered for the class.
     * 
     * @param cls
     *            The class of the object.
     * @param val
     *            The object.
     * @return This CommandEncoder, for chaining operations.
     */
    public <T> CommandEncoder encode(Class<T> cls, T val) {
        TypeHandler<T> th = Command.getTypeHandler(cls);
        return th.encode(this, val);
    }

    /**
     * Encodes a single byte.
     * 
     * @param bits
     * @return
     */
    public CommandEncoder encode(byte bits) {
        bytes[offset++] = bits;
        return this;
    }

    /**
     * Encodes multiple bytes.
     * 
     * @param bits
     * @return
     */
    public CommandEncoder encode(byte[] bits) {
        encode(bits.length);
        for (int i = 0; i < bits.length; i++) {
            encode(bits[i]);
        }
        return this;
    }

    /**
     * Encodes a single short.
     * 
     * @param bits
     * @return
     */
    public CommandEncoder encode(short bits) {
        bytes[offset++] = (byte) (bits >>> 8);
        bytes[offset++] = (byte) bits;
        return this;
    }

    /**
     * Encodes multiple shorts.
     * 
     * @param bits
     * @return
     */
    public CommandEncoder encode(short[] bits) {
        encode(bits.length);
        for (int i = 0; i < bits.length; i++) {
            encode(bits[i]);
        }
        return this;
    }

    /**
     * Encodes a single int.
     * 
     * @param bits
     * @return
     */
    public CommandEncoder encode(int bits) {
        bytes[offset++] = (byte) (bits >>> 24);
        bytes[offset++] = (byte) (bits >>> 16);
        bytes[offset++] = (byte) (bits >>> 8);
        bytes[offset++] = (byte) bits;
        return this;
    }

    /**
     * Encodes multiple ints.
     * 
     * @param bits
     * @return
     */
    public CommandEncoder encode(int[] bits) {
        encode(bits.length);
        for (int i = 0; i < bits.length; i++) {
            encode(bits[i]);
        }
        return this;
    }

    /**
     * Encodes a single long.
     * 
     * @param bits
     * @return
     */
    public CommandEncoder encode(long bits) {
        bytes[offset++] = (byte) (bits >>> 56);
        bytes[offset++] = (byte) (bits >>> 48);
        bytes[offset++] = (byte) (bits >>> 40);
        bytes[offset++] = (byte) (bits >>> 32);
        bytes[offset++] = (byte) (bits >>> 24);
        bytes[offset++] = (byte) (bits >>> 16);
        bytes[offset++] = (byte) (bits >>> 8);
        bytes[offset++] = (byte) bits;
        return this;
    }

    /**
     * Encodes multiple longs.
     * 
     * @param bits
     * @return
     */
    public CommandEncoder encode(long[] bits) {
        encode(bits.length);
        for (int i = 0; i < bits.length; i++) {
            encode(bits[i]);
        }
        return this;
    }

    /**
     * Encodes a single boolean.
     * 
     * @param b
     * @return
     */
    public CommandEncoder encode(boolean b) {
        if (b) {
            return encode((byte) 1);
        } else {
            return encode((byte) 0);
        }
    }

    /**
     * Encodes multiple booleans.
     * 
     * @param bits
     * @return
     */
    public CommandEncoder encode(boolean[] bits) {
        encode(bits.length);
        for (int i = 0; i < bits.length; i++) {
            encode(bits[i]);
        }
        return this;
    }

    /**
     * Encodes a single float.
     * 
     * @param x
     * @return
     */
    public CommandEncoder encode(float x) {
        return encode(Float.floatToRawIntBits(x));
    }

    /**
     * Encodes multiple floats.
     * 
     * @param bits
     * @return
     */
    public CommandEncoder encode(float[] bits) {
        encode(bits.length);
        for (int i = 0; i < bits.length; i++) {
            encode(bits[i]);
        }
        return this;
    }

    /**
     * Encodes a single long.
     * 
     * @param x
     * @return
     */
    public CommandEncoder encode(double x) {
        return encode(Double.doubleToRawLongBits(x));
    }

    /**
     * Encodes multiple doubles.
     * 
     * @param bits
     * @return
     */
    public CommandEncoder encode(double[] bits) {
        encode(bits.length);
        for (int i = 0; i < bits.length; i++) {
            encode(bits[i]);
        }
        return this;
    }

    /**
     * Encodes the given string with a 4-byte length field before. Use the
     * <code>getEncodedLength()</code> method to find encoded string length for
     * the Command.size() method. <bold>Only ISO-8859-1 characters (code points
     * less than 256) are supported at this time.</bold>
     * 
     * @param text
     *            The string to encode.
     * @return
     */
    public CommandEncoder encode(String text) {
        byte[] sbytes = text.getBytes(latin1);
        encode(sbytes.length);
        System.arraycopy(sbytes, 0, bytes, offset, sbytes.length);
        offset += text.length();
        return this;
    }

    /**
     * 
     * @param text
     * @return
     */
    public static int getEncodedLength(String text) {
        return 4 + text.length();
    }
}
