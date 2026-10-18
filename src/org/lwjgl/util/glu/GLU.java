package org.lwjgl.util.glu;
import org.lwjgl.opengl.GL11;
public class GLU {
    public static void gluOrtho2D(float l, float r, float b, float t) { GL11.glOrtho(l, r, b, t, -1.0, 1.0); }
}
