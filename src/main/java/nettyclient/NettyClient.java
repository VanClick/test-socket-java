package nettyclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {

    public static void main(String[] args) throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        for (int i = 0; i < 3; i++) {
            startClient(eventLoopGroup);
        }
        Thread.sleep(1000 * 600);
    }

    public static void startClient(EventLoopGroup workerGroup) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, false); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringDecoder())
                            .addLast(new StringEncoder())
                            .addLast(new ClientHandler());
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            System.out.println("Client init success");

            // Wait until the connection is closed.
            //f.channel().closeFuture().sync();
        } finally {
            //workerGroup.shutdownGracefully();
        }
    }
}
