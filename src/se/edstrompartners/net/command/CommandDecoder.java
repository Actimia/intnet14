package se.edstrompartners.net.command;

import java.nio.charset.Charset;

public class CommandDecoder {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private int offset = 0;
    private byte[] bytes;
    private int commandID;
    private int length;

    public CommandDecoder(byte[] bytes) {
        this.bytes = bytes;
        commandID = decodeInt();
    }

    public int decodedLength() {
        return offset;
    }

    public int decodedID() {
        return commandID;
    }

    public <T> T decode(Class<T> cls) {
        TypeHandler<T> th = Command.getTypeHandler(cls);
        return th.decode(this);

    }

    public byte decodeByte() {
        return bytes[offset++];
    }

    public byte[] decodeBytes() {
        byte[] res = new byte[decodeInt()];
        for (int i = 0; i < res.length; i++) {
            res[i] = decodeByte();
        }
        return res;
    }

    public short decodeShort() {
        short bits = (short) ((bytes[offset++] & 0xff) << 8);
        bits |= (short) (bytes[offset++] & 0xff);
        return bits;
    }

    public short[] decodeShorts() {
        short[] res = new short[decodeInt()];
        for (int i = 0; i < res.length; i++) {
            res[i] = decodeShort();
        }
        return res;
    }

    public int decodeInt() {
        int bits = (bytes[offset++] & 0xff) << 24;
        bits |= (bytes[offset++] & 0xff) << 16;
        bits |= (bytes[offset++] & 0xff) << 8;
        bits |= (bytes[offset++] & 0xff);
        return bits;
    }

    public int[] decodeInts() {
        int[] res = new int[decodeInt()];
        for (int i = 0; i < res.length; i++) {
            res[i] = decodeInt();
        }
        return res;
    }

    public long decodeLong() {
        long bits = (bytes[offset++] & 0xff) << 56;
        bits |= (bytes[offset++] & 0xff) << 48;
        bits |= (bytes[offset++] & 0xff) << 40;
        bits |= (bytes[offset++] & 0xff) << 32;
        bits |= (bytes[offset++] & 0xff) << 24;
        bits |= (bytes[offset++] & 0xff) << 16;
        bits |= (bytes[offset++] & 0xff) << 8;
        bits |= (bytes[offset++] & 0xff);
        return bits;
    }

    public long[] decodeLongs() {
        long[] res = new long[decodeInt()];
        for (int i = 0; i < res.length; i++) {
            res[i] = decodeLong();
        }
        return res;
    }

    public boolean decodeBoolean() {
        return decodeByte() == (byte) 1;
    }

    public boolean[] decodeBooleans() {
        boolean[] res = new boolean[decodeInt()];
        for (int i = 0; i < res.length; i++) {
            res[i] = decodeBoolean();
        }
        return res;
    }

    public float decodeFloat() {
        return Float.intBitsToFloat(decodeInt());
    }

    public float[] decodeFloats() {
        float[] res = new float[decodeInt()];
        for (int i = 0; i < res.length; i++) {
            res[i] = decodeFloat();
        }
        return res;
    }

    public double decodeDouble() {
        return Double.longBitsToDouble(decodeLong());
    }

    public double[] decodeDoubles() {
        double[] res = new double[decodeInt()];
        for (int i = 0; i < res.length; i++) {
            res[i] = decodeDouble();
        }
        return res;
    }

    public String decodeString() {
        int len = decodeInt();
        String res = new String(bytes, offset, len, UTF8);
        offset = offset + len;
        return res;
    }

    public String[] decodeStrings() {
        int len = decodeInt();
        String[] res = new String[len];
        for (int i = 0; i < res.length; i++) {
            res[i] = decodeString();
        }
        return res;
    }

}
