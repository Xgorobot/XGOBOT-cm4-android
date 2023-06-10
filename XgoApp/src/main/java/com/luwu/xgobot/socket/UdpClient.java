package com.luwu.xgobot.socket;

import android.util.Log;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

public class UdpClient {
    public static void sendBroadCast(String data,int port) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioDatagramChannel.class)
                .handler(new
                                 ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception { }
                        });
                    }
                })
                .option(ChannelOption.SO_BROADCAST, true);

        Channel channel = bootstrap.bind(0).sync().channel();
        InetSocketAddress recipient = new InetSocketAddress("255.255.255.255", port);
        channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(data, CharsetUtil.UTF_8), recipient)).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                System.out.println("UDP broadcast message sent.");
            } else {
                System.err.println("UDP broadcast message failed to send: " + future.cause());
            }
            channel.close();
        }).await();
    }
}

