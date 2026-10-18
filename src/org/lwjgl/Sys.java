package org.lwjgl;
public class Sys {
    public static void alert(String t, String m) { pz.shim.N.log("ALERT " + t + ": " + m); }
    public static long getTime() { return System.currentTimeMillis(); }
    public static long getTimerResolution() { return 1000; }
}
