package pz.shim;
public class WritableRaster {
    private final BufferedImage img;
    WritableRaster(BufferedImage i) { img = i; }
    public int[] getPixels(int x, int y, int w, int h, int[] out) {
        if (x != 0 || y != 0 || w != img.getWidth() || h != img.getHeight()) throw new UnsupportedOperationException("subrect");
        int bands = img.alpha ? 4 : 3;
        if (out == null) out = new int[w * h * bands];
        img.fill(out, bands);
        return out;
    }
}
