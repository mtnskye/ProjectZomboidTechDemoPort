package org.lwjgl;
import java.nio.*;
public final class BufferUtils {
    public static ByteBuffer createByteBuffer(int n) { return ByteBuffer.allocate(n).order(ByteOrder.nativeOrder()); }
    public static IntBuffer createIntBuffer(int n) { return createByteBuffer(n << 2).asIntBuffer(); }
    public static FloatBuffer createFloatBuffer(int n) { return createByteBuffer(n << 2).asFloatBuffer(); }
    public static ShortBuffer createShortBuffer(int n) { return createByteBuffer(n << 1).asShortBuffer(); }
}
