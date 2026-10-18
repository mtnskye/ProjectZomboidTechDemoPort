package pz.shim;
import java.io.*;
public class ImageIO {
    public static BufferedImage read(InputStream in) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream(1 << 16);
        byte[] buf = new byte[1 << 16]; int r;
        while ((r = in.read(buf)) > 0) bo.write(buf, 0, r);
        byte[] d = bo.toByteArray();
        int[] info = new int[4];
        int hd = N.decode(d, d.length, info);
        if (hd < 0) throw new IOException("image decode failed");
        return new BufferedImage(info[0], info[1], info[2] != 0, hd);
    }
}
