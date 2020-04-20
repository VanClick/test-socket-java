package nettyclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.TimeUnit;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ClientHandler registered");

        ctx.executor().scheduleAtFixedRate(() -> {
            ctx.writeAndFlush("msg from client");
        }, 1, 1, TimeUnit.SECONDS);
    }

    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("ClientHandler recv: " + msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ClientHandler inactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("ClientHandler exception");
    }
}