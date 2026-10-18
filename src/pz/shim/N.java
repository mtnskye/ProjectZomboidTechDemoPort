package pz.shim;
/** Natives implemented in JavaScript (pzhost.js). */
public final class N {
    public static native void gl(float[] c, int n);
    public static native void texImage(int id, int w, int h, byte[] d, int off);
    public static native void texSub(int id, int x, int y, int w, int h, byte[] d, int off);
    public static native void texRead(int id, byte[] out, int off);
    public static native int decode(byte[] data, int len, int[] info);
    public static native void pixels(int handle, int[] out);
    public static native void input(int[] st);
    public static native void display(int w, int h);
    public static native void al(int op, int a, int b, int c, float f);
    public static native void alData(int buf, int channels, int bits, int rate, byte[] d, int len);
    public static native int alQuery(int op, int a);
    public static native int audioDecode(byte[] data, int len, int[] info);
    public static native void audioPcm(int handle, byte[] out);
    public static native void log(byte[] msg);
    public static void log(String s) { try { log(s.getBytes()); } catch (Throwable t) { } }
}
