package org.lwjgl.openal;
import java.nio.*;
import pz.shim.N;
public class AL10 {
    public static final int AL_FORMAT_MONO8 = 0x1100, AL_FORMAT_MONO16 = 0x1101, AL_FORMAT_STEREO8 = 0x1102, AL_FORMAT_STEREO16 = 0x1103,
        AL_SOURCE_STATE = 0x1010, AL_PLAYING = 0x1012, AL_STOPPED = 0x1014, AL_INITIAL = 0x1011, AL_BUFFER = 0x1009,
        AL_NO_ERROR = 0, AL_SIZE = 0x2004, AL_FREQUENCY = 0x2001, AL_BITS = 0x2002, AL_CHANNELS = 0x2003,
        AL_LOOPING = 0x1007, AL_GAIN = 0x100A, AL_PITCH = 0x1003, AL_POSITION = 0x1004, AL_VELOCITY = 0x1006, AL_ORIENTATION = 0x100F;
    private static int nextBuf = 1, nextSrc = 1;
    private static final int[] bsize = new int[4096], bfreq = new int[4096], bbits = new int[4096], bch = new int[4096];
    public static void alGenBuffers(IntBuffer b) { b.put(b.position(), nextBuf++); }
    public static void alGenSources(IntBuffer b) { b.put(b.position(), nextSrc++); }
    public static int alGetError() { return 0; }
    public static void alBufferData(int buf, int fmt, ByteBuffer data, int freq) {
        int ch = (fmt == AL_FORMAT_MONO8 || fmt == AL_FORMAT_MONO16) ? 1 : 2;
        int bits = (fmt == AL_FORMAT_MONO8 || fmt == AL_FORMAT_STEREO8) ? 8 : 16;
        byte[] t; int len = data.remaining();
        if (data.hasArray()) { t = data.array(); int off = data.arrayOffset() + data.position(); if (off != 0) { byte[] c = new byte[len]; System.arraycopy(t, off, c, 0, len); t = c; } }
        else { t = new byte[len]; data.duplicate().get(t); }
        bsize[buf] = len; bfreq[buf] = freq; bbits[buf] = bits; bch[buf] = ch;
        N.alData(buf, ch, bits, freq, t, len);
    }
    public static int alGetBufferi(int buf, int p) {
        switch (p) { case AL_SIZE: return bsize[buf]; case AL_FREQUENCY: return bfreq[buf]; case AL_BITS: return bbits[buf]; case AL_CHANNELS: return bch[buf]; }
        return 0;
    }
    public static void alSourcei(int s, int p, int v) { N.al(1, s, p, v, 0); }
    public static void alSourcef(int s, int p, float v) { N.al(2, s, p, 0, v); }
    public static void alSource(int s, int p, FloatBuffer v) { int i = v.position(); N.al(3, s, p, 0, v.get(i)); if (v.remaining() >= 3) { N.al(4, s, p, 0, v.get(i + 1)); N.al(5, s, p, 0, v.get(i + 2)); } }
    public static void alSource3f(int s, int p, float x, float y, float z) { N.al(3, s, p, 0, x); N.al(4, s, p, 0, y); N.al(5, s, p, 0, z); }
    public static void alSourcePlay(int s) { N.al(6, s, 0, 0, 0); }
    public static void alSourceStop(int s) { N.al(7, s, 0, 0, 0); }
    public static int alGetSourcei(int s, int p) { return N.alQuery(p, s); }
    public static void alListener(int p, FloatBuffer v) { int i = v.position(); N.al(8, p, 0, 0, v.get(i)); if (v.remaining() >= 3) { N.al(9, p, 0, 0, v.get(i + 1)); N.al(10, p, 0, 0, v.get(i + 2)); } }
    public static void alListenerf(int p, float v) { N.al(8, p, 0, 0, v); }
}
