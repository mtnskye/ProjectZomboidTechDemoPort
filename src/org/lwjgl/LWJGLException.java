package org.lwjgl;
public class LWJGLException extends Exception {
    public LWJGLException() { super(); }
    public LWJGLException(String m) { super(m); }
    public LWJGLException(String m, Throwable c) { super(m, c); }
    public LWJGLException(Throwable c) { super(c); }
}
