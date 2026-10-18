package pz.shim;
import java.io.*;
public class AudioSystem {
    public static AudioInputStream getAudioInputStream(InputStream in) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream(1 << 16);
        byte[] buf = new byte[1 << 16]; int r;
        while ((r = in.read(buf)) > 0) bo.write(buf, 0, r);
        byte[] d = bo.toByteArray();
        int[] info = new int[4];
        int hd = N.audioDecode(d, d.length, info);
        if (hd < 0) throw new IOException("audio decode failed");
        byte[] pcm = new byte[info[3]];
        N.audioPcm(hd, pcm);
        return new AudioInputStream(pcm, new AudioFormat(info[1], 16, info[0]));
    }
}
