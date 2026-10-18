package pz.shim;
public class Component {
    public int getWidth() { int[] s = Dim.get(); return s[0]; }
    public int getHeight() { int[] s = Dim.get(); return s[1]; }
    public Dimension getSize() { int[] s = Dim.get(); return new Dimension(s[0], s[1]); }
    public void setSize(int w, int h) { }
    public void setIgnoreRepaint(boolean b) { }
    public void setFocusable(boolean b) { }
    public void requestFocus() { }
    public boolean requestFocusInWindow() { return true; }
    public void setCursor(Cursor c) { }
    public void setVisible(boolean b) { }
    public void addNotify() { }
    public void removeNotify() { }
}
