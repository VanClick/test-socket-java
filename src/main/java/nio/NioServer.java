package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {
    public static final int BUF_SIZE = 1024;
    public static final int PORT = 8080;
    public static final int TIMEOUT = 3000;

    public static void main(String[] args) {
        Selector selector = null;
        ServerSocketChannel channel = null;
        try {
            selector = Selector.open();
            channel = ServerSocketChannel.open();
            channel.socket().bind(new InetSocketAddress(PORT));  // 监听端口
            channel.configureBlocking(false);  // 必须为非阻塞

            // 将Channel注册到Selector上， 监听接收新连接
            SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                if (selector.select(TIMEOUT) == 0) {  // 执行 select， 返回已就绪 channel 个数
                    System.out.println("select == 0， 3000ms");
                    continue;
                }
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();  // 已就绪 channel 集合
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isAcceptable()) {    // 有新连接
                        try {
                            handleAccept(key);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (key.isReadable()) {   // 有数据可读
                        try {
                            handleRead(key);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (key.isWritable() && key.isValid()) {   // 网络不阻塞则一直可写,一般不注册
                        try {
                            handleWrite(key);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (key.isConnectable()) {
                        System.out.println("isConnectable = true");
                    }
                    iter.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (selector != null) {
                    selector.close();
                }
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void handleAccept(SelectionKey key) throws IOException {
        System.out.println("handleAccept");

        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssChannel.accept();
        sc.configureBlocking(false);  // 必须为非阻塞

        // 将Channel注册到Selector上， 监听读事件
        sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(BUF_SIZE));
    }

    public static void handleRead(SelectionKey key) throws IOException {
        System.out.println("handleRead");

        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buf = (ByteBuffer) key.attachment();
        long bytesRead = channel.read(buf);  // 从channel读数据
        while (bytesRead > 0) {
            buf.flip();
            while (buf.hasRemaining()) {
                System.out.print((char) buf.get());
            }
            System.out.println();
            buf.clear();
            bytesRead = channel.read(buf);
        }
        if (bytesRead == -1) {
            channel.close();
        }

        ByteBuffer sendBuffer = ByteBuffer.allocate(128);
        sendBuffer.put("msg from server".getBytes());
        sendBuffer.flip();
        channel.write(sendBuffer);  // 向channel写数据
    }

    public static void handleWrite(SelectionKey key) throws IOException {
        System.out.println("handleWrite");

        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.flip();
        SocketChannel channel = (SocketChannel) key.channel();
        while (buf.hasRemaining()) {
            channel.write(buf);  // 向channel写数据
        }
        buf.compact();
    }
}
