package org.lwjgl.opengl;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import pz.shim.N;
public class Display {
    private static int w = 800, h = 600;
    private static boolean created = false;
    private static long last = 0;
    public static void setParent(pz.shim.Canvas c) { }
    public static void setParent(Object c) { }
    public static void setTitle(String t) { }
    public static void setFullscreen(boolean f) { }
    public static void setVSyncEnabled(boolean v) { }
    public static void setDisplayMode(DisplayMode m) { w = m.getWidth(); h = m.getHeight(); N.display(w, h); }
    public static DisplayMode getDesktopDisplayMode() { return new DisplayMode(w, h); }
    public static DisplayMode getDisplayMode() { return new DisplayMode(w, h); }
    public static int getWidth() { return w; }
    public static int getHeight() { return h; }
    public static void create() throws LWJGLException { created = true; N.display(w, h); }
    public static boolean isCreated() { return created; }
    public static void update() {
        GL11.flush();
        Keyboard.poll();
        Mouse.poll();
    }
    public static void sync(int fps) {
        long now = System.nanoTime();
        long target = last + 1000000000L / fps;
        if (now < target) {
            try { Thread.sleep((target - now) / 1000000L); } catch (InterruptedException e) { }
            last = target;
        } else last = now;
        // Always yield once per frame so the browser can paint / deliver input.
        try { Thread.sleep(0); } catch (InterruptedException e) { }
    }
    public static boolean isCloseRequested() { return false; }
    public static boolean isActive() { return true; }
    public static boolean isVisible() { return true; }
    public static boolean isDirty() { return true; }
    public static void destroy() { created = false; }
}
