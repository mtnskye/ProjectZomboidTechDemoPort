package org.lwjgl.opengl;
import java.nio.*;
import pz.shim.N;
/** Records fixed-function GL calls into a float command stream executed by WebGL in JS. */
public class GL11 {
    public static final int GL_COLOR_BUFFER_BIT = 0x4000, GL_DEPTH_BUFFER_BIT = 0x100, GL_TEXTURE_2D = 0x0DE1,
        GL_BLEND = 0x0BE2, GL_QUADS = 7, GL_TRIANGLES = 4, GL_LINES = 1, GL_PROJECTION = 0x1701, GL_MODELVIEW = 0x1700,
        GL_RGBA = 0x1908, GL_RGB = 0x1907, GL_UNSIGNED_BYTE = 0x1401, GL_SRC_ALPHA = 0x302, GL_ONE_MINUS_SRC_ALPHA = 0x303,
        GL_LINEAR = 0x2601, GL_NEAREST = 0x2600;
    static float[] c = new float[1 << 16];
    static int n = 0;
    static int bound = 0, nextTex = 1;

    static void ensure(int k) { if (n + k > c.length) flush(); }
    public static void flush() { if (n > 0) { int m = n; n = 0; N.gl(c, m); } }
    static void op(int o, float a, float b, float cc, float d) { ensure(5); c[n++] = o; c[n++] = a; c[n++] = b; c[n++] = cc; c[n++] = d; }

    public static void glBegin(int m) { op(1, m, 0, 0, 0); }
    public static void glEnd() { op(2, 0, 0, 0, 0); }
    public static void glVertex2f(float x, float y) { ensure(3); c[n++] = 3; c[n++] = x; c[n++] = y; }
    public static void glVertex2i(int x, int y) { glVertex2f(x, y); }
    public static void glTexCoord2f(float s, float t) { ensure(3); c[n++] = 4; c[n++] = s; c[n++] = t; }
    public static void glColor4f(float r, float g, float b, float a) { op(5, r, g, b, a); }
    public static void glBindTexture(int target, int tex) { bound = tex; op(6, tex, 0, 0, 0); }
    public static void glEnable(int cap) { op(7, cap, 0, 0, 0); }
    public static void glDisable(int cap) { op(8, cap, 0, 0, 0); }
    public static void glMatrixMode(int m) { op(9, m, 0, 0, 0); }
    public static void glLoadIdentity() { op(10, 0, 0, 0, 0); }
    public static void glOrtho(double l, double r, double b, double t, double zn, double zf) {
        ensure(7); c[n++] = 11; c[n++] = (float) l; c[n++] = (float) r; c[n++] = (float) b; c[n++] = (float) t; c[n++] = (float) zn; c[n++] = (float) zf; }
    public static void glTranslatef(float x, float y, float z) { op(12, x, y, z, 0); }
    public static void glPushMatrix() { op(13, 0, 0, 0, 0); }
    public static void glPopMatrix() { op(14, 0, 0, 0, 0); }
    public static void glBlendFunc(int s, int d) { op(15, s, d, 0, 0); }
    public static void glViewport(int x, int y, int w, int h) { op(16, x, y, w, h); }
    public static void glClear(int mask) { op(17, mask, 0, 0, 0); }
    public static void glClearColor(float r, float g, float b, float a) { op(18, r, g, b, a); }
    public static void glTexParameteri(int target, int pname, int param) { op(19, pname, param, 0, 0); }
    public static void glTexEnvf(int target, int pname, float param) { op(20, pname, param, 0, 0); }
    public static void glPushAttrib(int mask) { }
    public static void glPopAttrib() { }
    public static void glScalef(float x, float y, float z) { op(24, x, y, z, 0); }
    public static void glRotatef(float a, float x, float y, float z) { op(25, a, x, y, z); }
    public static void glLineWidth(float w) { }
    public static void glDepthMask(boolean b) { }
    public static void glFlush() { }
    public static void glFinish() { }
    public static int glGetError() { return 0; }
    public static int glGenTextures() { return nextTex++; }
    public static void glGenTextures(IntBuffer b) { b.put(b.position(), nextTex++); }
    public static void glDeleteTextures(IntBuffer b) {
        for (int i = b.position(); i < b.limit(); i++) op(21, b.get(i), 0, 0, 0);
    }
    public static void glDeleteTextures(int t) { op(21, t, 0, 0, 0); }

    private static byte[] arr(ByteBuffer p, int[] off) {
        if (p.hasArray()) { off[0] = p.arrayOffset() + p.position(); return p.array(); }
        byte[] t = new byte[p.remaining()]; p.duplicate().get(t); off[0] = 0; return t;
    }
    public static void glTexImage2D(int target, int level, int ifmt, int w, int h, int border, int fmt, int type, ByteBuffer p) {
        flush();
        if (p == null) { N.texImage(bound, w, h, null, 0); return; }
        int[] off = new int[1]; byte[] a = arr(p, off);
        N.texImage(bound, w, h, a, off[0]);
    }
    public static void glTexSubImage2D(int target, int level, int x, int y, int w, int h, int fmt, int type, ByteBuffer p) {
        flush();
        int[] off = new int[1]; byte[] a = arr(p, off);
        N.texSub(bound, x, y, w, h, a, off[0]);
    }
    public static void glGetTexImage(int target, int level, int fmt, int type, ByteBuffer p) {
        flush();
        int[] off = new int[1]; byte[] a = arr(p, off);
        N.texRead(bound, a, off[0]);
    }
}
