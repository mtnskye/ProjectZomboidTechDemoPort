package pz.shim;
public class BufferedImage extends Image {
    final int w, h; boolean alpha; int handle = -1; int[] px; // px: RGBA ints, 4 per pixel (writable images)
    BufferedImage(int w, int h, boolean alpha, int handle) { this.w = w; this.h = h; this.alpha = alpha; this.handle = handle; }
    public BufferedImage(int w, int h, int type) { this.w = w; this.h = h; this.alpha = true; px = new int[w * h * 4]; }
    public int getWidth() { return w; }
    public int getHeight() { return h; }
    public ColorModel getColorModel() { return new ColorModel(alpha); }
    public WritableRaster getRaster() { return new WritableRaster(this); }
    public Graphics2D createGraphics() { return new Graphics2D(this); }
    void fill(int[] out, int bands) {
        if (px != null) { System.arraycopy(px, 0, out, 0, Math.min(out.length, px.length)); return; }
        pz.shim.N.pixels(handle, out);
    }
    void draw(BufferedImage src, int x, int y) {
        int[] s = new int[src.w * src.h * (src.alpha ? 4 : 3)];
        src.fill(s, src.alpha ? 4 : 3);
        int sb = src.alpha ? 4 : 3;
        for (int j = 0; j < src.h; j++) {
            int dy = j + y; if (dy < 0 || dy >= h) continue;
            if (sb == 4 && x == 0 && src.w == w) { System.arraycopy(s, j * src.w * 4, px, dy * w * 4, src.w * 4); continue; }
            for (int i = 0; i < src.w; i++) {
                int dx = i + x; if (dx < 0 || dx >= w) continue;
                int so = (j * src.w + i) * sb, d = (dy * w + dx) * 4;
                px[d] = s[so]; px[d + 1] = s[so + 1]; px[d + 2] = s[so + 2]; px[d + 3] = sb == 4 ? s[so + 3] : 255;
            }
        }
    }
}
