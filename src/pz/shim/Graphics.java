package pz.shim;
public class Graphics {
    final BufferedImage dst;
    Graphics(BufferedImage d) { dst = d; }
    public boolean drawImage(Image src, int x, int y, ImageObserver o) { dst.draw((BufferedImage) src, x, y); return true; }
    public void dispose() { }
}
