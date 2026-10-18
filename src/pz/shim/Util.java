package pz.shim;
import java.nio.ByteBuffer;
public class Util { public static ByteBuffer allocateDirect(int n) { return ByteBuffer.allocate(n); } }
