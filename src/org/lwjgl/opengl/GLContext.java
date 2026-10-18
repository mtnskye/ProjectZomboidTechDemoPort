package org.lwjgl.opengl;
public class GLContext {
    private static final ContextCapabilities CAPS = new ContextCapabilities();
    public static ContextCapabilities getCapabilities() { return CAPS; }
}
