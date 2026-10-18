package org.lwjgl.opengl;
import java.nio.IntBuffer;
public class EXTFramebufferObject {
    public static final int GL_FRAMEBUFFER_EXT = 0x8D40, GL_COLOR_ATTACHMENT0_EXT = 0x8CE0;
    private static int next = 1;
    public static void glGenFramebuffersEXT(IntBuffer b) { b.put(b.position(), next++); }
    public static int glGenFramebuffersEXT() { return next++; }
    public static void glBindFramebufferEXT(int target, int fbo) { GL11.op(22, fbo, 0, 0, 0); }
    public static void glFramebufferTexture2DEXT(int target, int attach, int textarget, int tex, int level) { GL11.op(23, tex, 0, 0, 0); }
    public static int glCheckFramebufferStatusEXT(int target) { return 0x8CD5; }
}
