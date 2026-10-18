package org.lwjgl;
public class LWJGLUtil {
    public static final int PLATFORM_LINUX = 1, PLATFORM_MACOSX = 2, PLATFORM_WINDOWS = 3;
    public static int getPlatform() { return PLATFORM_LINUX; }
    public static void log(String s) { pz.shim.N.log(s); }
}
