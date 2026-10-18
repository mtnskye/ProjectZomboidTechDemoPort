package org.lwjgl.input;
import pz.shim.N;
public class Mouse {
    static int x, y, buttons, wheel, w = 800, h = 600;
    public static void poll() {
        int[] st = Keyboard.st; // shared snapshot filled by Keyboard.poll()
        x = st[0]; y = st[1]; buttons = st[2]; wheel += st[3]; w = st[5]; h = st[6];
    }
    public static boolean isCreated() { return true; }
    public static int getX() { return x; }
    public static int getY() { return h - 1 - y; }
    public static boolean isButtonDown(int b) { return (buttons & (1 << b)) != 0; }
    public static int getDWheel() { int v = wheel; wheel = 0; return v; }
    public static Cursor setNativeCursor(Cursor c) { return null; }
    public static void setGrabbed(boolean g) { }
}
