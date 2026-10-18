package pz.shim;
public class Rectangle implements Cloneable {
    public int x, y, width, height;
    public Rectangle() { }
    public Rectangle(int x, int y, int w, int h) { this.x = x; this.y = y; width = w; height = h; }
    public Rectangle(int w, int h) { width = w; height = h; }
    public Object clone() { return new Rectangle(x, y, width, height); }
    public void add(Rectangle r) {
        int x1 = Math.min(x, r.x), y1 = Math.min(y, r.y), x2 = Math.max(x + width, r.x + r.width), y2 = Math.max(y + height, r.y + r.height);
        x = x1; y = y1; width = x2 - x1; height = y2 - y1;
    }
    public boolean contains(int px, int py) { return px >= x && py >= y && px < x + width && py < y + height; }
}
