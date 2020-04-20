import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public class testMemory {

    public static void main(String[] args) throws  Exception{

        List<Buffer> buffers = new ArrayList<>();
        int i = 1;
        while (true) {
            if (i < 13) {
                System.out.println(i++);
                ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                buffer.putChar('a');
                buffers.add(buffer);
            } else {
                System.out.println(i++);
                ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024);
                buffer.putChar('a');
                buffers.add(buffer);
            }
        }

//        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
//        unsafeField.setAccessible(true);
//        Unsafe unsafe = (Unsafe) unsafeField.get(null);
//        int i = 1;
//        while (true) {
//            System.out.println(i++);
//            unsafe.allocateMemory( 1024 * 1024*1024);
//        }
    }
}
