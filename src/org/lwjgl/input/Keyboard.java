package org.lwjgl.input;
import pz.shim.N;
public class Keyboard {
    // st layout: 0 mx,1 my,2 buttons,3 wheel,4 active,5 w,6 h,7 nEvents, 8..263 key down flags, 264.. events (key,state,char)*
    static final int[] st = new int[264 + 3 * 256];
    private static int evCount = 0, evIdx = 0, curKey, curChar; private static boolean curState;
    public static void poll() {
        N.input(st);
        evCount = st[7]; evIdx = 0;
    }
    public static boolean isKeyDown(int k) { return k >= 0 && k < 256 && st[8 + k] != 0; }
    public static int getNumKeyboardEvents() { return evCount - evIdx; }
    public static boolean next() {
        if (evIdx >= evCount) return false;
        int b = 264 + 3 * evIdx++;
        curKey = st[b]; curState = st[b + 1] != 0; curChar = st[b + 2];
        return true;
    }
    public static int getEventKey() { return curKey; }
    public static boolean getEventKeyState() { return curState; }
    public static char getEventCharacter() { return (char) curChar; }
    public static void create() { }
    public static boolean isCreated() { return true; }
    public static void enableRepeatEvents(boolean b) { }
}
