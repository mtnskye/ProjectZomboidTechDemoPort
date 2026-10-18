package pz.shim;
import java.io.*;
public class AudioInputStream extends InputStream {
    private final ByteArrayInputStream in; private final AudioFormat fmt; private final long frames;
    AudioInputStream(byte[] pcm, AudioFormat f) { in = new ByteArrayInputStream(pcm); fmt = f; frames = pcm.length / (f.ch * (f.bits / 8)); }
    public AudioFormat getFormat() { return fmt; }
    public long getFrameLength() { return frames; }
    public int read() { return in.read(); }
    public int read(byte[] b, int o, int l) { return in.read(b, o, l); }
    public void close() { }
}
