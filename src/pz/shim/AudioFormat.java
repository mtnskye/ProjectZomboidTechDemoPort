package pz.shim;
public class AudioFormat {
    public static class Encoding { public static final Encoding PCM_SIGNED = new Encoding(); }
    final int ch, bits; final float rate;
    public AudioFormat(float rate, int bits, int ch) { this.rate = rate; this.bits = bits; this.ch = ch; }
    public int getChannels() { return ch; }
    public int getSampleSizeInBits() { return bits; }
    public float getSampleRate() { return rate; }
    public Encoding getEncoding() { return Encoding.PCM_SIGNED; }
}
