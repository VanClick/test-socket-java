package aio;

import nio.NioServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioServer {
    private final AsynchronousServerSocketChannel server;  //此类相当一一个服务器的SOCKET，不过它的实现是异步通信形式

    public AioServer() throws IOException {
        server = AsynchronousServerSocketChannel.open().bind(
                new InetSocketAddress("127.0.0.1", NioServer.PORT));
    }

    public void startWithCompletionHandler() throws IOException {
        System.out.println("Server listen on " + NioServer.PORT);

        /*****************************************************
         * 主要功能:注册事件和事件完成后的处理器
         * 实现原理:当一有用户连接到服务器时,会调用accept方法,
         * 此方法是一个非阻塞方法并和一个完成端口(即处理器-CompletionHandler)进行绑定的方法
         * 当用户连接成功时,完成端口会自动调用completed方法,这步由操作系统完成
         * 要实现能连接多用户,必须在completed方法中在循环调用一次accept方法
         * 代码如下:server.accept(null, this);
         * 关于IOCP的详细实现原理参见C++中的IOCP
         *****************************************************/

        server.accept(null, new CompletionHandler<>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {

                try {
                    System.out.println("accept completed: " + result.getRemoteAddress().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //AsynchronousSocketChannel相当唯一标示客户的socket
                //这里需要加个final关键字才能让SocketChannel对象在方法read中可见
                final AsynchronousSocketChannel SocketChannel = result;

                //再次向处理器投递一个连接请求
                server.accept(null, this);

                try {
                    //清空缓冲区,这步不能省
                    //ByteBuffer:接收数据的缓冲区,这里初始化大小为65535
                    ByteBuffer buffer = ByteBuffer.allocate(65535);
                    result.read(buffer, null, new CompletionHandler<>() {

                        @Override
                        public void completed(Integer result, Object attachment) {
                            System.out.println("read result:" + result);
                            if (result == -1) {
                                //这里面可进行对失去连接的客户端进行删除操作
                            }
                            if (result != -1) {
                                buffer.flip();
                                //接收到的数据缓冲区转字节数,此后可对这个数组进行操作
                                while (buffer.hasRemaining()) {
                                    System.out.print((char) buffer.get());
                                }
                                System.out.println();
                            }
                            ByteBuffer sendBuffer = ByteBuffer.allocate(128);
                            sendBuffer.put("msg from server".getBytes());
                            sendBuffer.flip();
                            SocketChannel.write(sendBuffer);
                            //完成接收操作之后,必须清空缓冲区,不然会出现死循环
                            buffer.clear();
                            //再次向处理器投递一个接收数据请求
                            SocketChannel.read(buffer, null, this);
                        }

                        @Override
                        public void failed(Throwable exc, Object result2) {
                            exc.printStackTrace();
                            System.out.println("failed: " + exc);
                            //完成接收操作之后,必须清空缓冲区,不然会出现死循环
                            buffer.clear();
                            //再次向处理器投递一个接收数据请求
                            SocketChannel.read(buffer, null, this);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
                System.out.println("accept failed: " + exc);
                //再次向处理器投递一个连接请求
                server.accept(null, this);
            }
        });
        // 这里必须 保证主线程的存活

        System.in.read();
    }

    public static void main(String args[]) throws Exception {
        new AioServer().startWithCompletionHandler();
    }
}
