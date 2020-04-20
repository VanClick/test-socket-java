package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class NioClient {
    public static void main(String[] args) {
        ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
        ByteBuffer recvBuffer = ByteBuffer.allocate(1024);
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);  // 非阻塞
            socketChannel.connect(new InetSocketAddress("127.0.0.1", NioServer.PORT));  // 连接
            if (socketChannel.finishConnect()) {
                int i = 0;
                while (true) {
                    TimeUnit.SECONDS.sleep(1);

                    socketChannel.read(recvBuffer);  // 从channel读数据
                    recvBuffer.flip();
                    System.out.println("recv " +  recvBuffer.toString() + " " + new String(recvBuffer.array()));
                    recvBuffer.clear();

                    String info = "I'm " + i++ + "-th information from client";
                    sendBuffer.clear();
                    sendBuffer.put(info.getBytes());
                    sendBuffer.flip();
                    while (sendBuffer.hasRemaining()) {   // position < limit
                        System.out.println("write " + i + "  " + sendBuffer.toString());
                        socketChannel.write(sendBuffer);  // 向channel写数据
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socketChannel != null) {
                    socketChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


