package nettyserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerHandler registered");

        ctx.executor().scheduleAtFixedRate(() -> {
            ctx.channel().writeAndFlush("msg from server netty Thread");
        }, 3, 3, TimeUnit.SECONDS);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            ctx.channel().writeAndFlush("msg from server new Thread");
        }, 4, 3, TimeUnit.SECONDS);
    }

    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("ServerHandler recv: " + msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerHandler inactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("ServerHandler exceptionCaught");
    }
}
