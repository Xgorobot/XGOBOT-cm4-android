package com.luwu.xgo_robot.socket;

import android.util.Log;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

public class UdpClient extends UdpChannelInboundHandler implements Runnable{
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;
    private UdpChannelInitializer udpChannelInitializer;
    private ExecutorService executorService;

    public UdpClient(){
        init();
    }

    private void init(){
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_RCVBUF,1024)
                .option(ChannelOption.SO_SNDBUF,1024);
        udpChannelInitializer = new UdpChannelInitializer(this);
        bootstrap.handler(udpChannelInitializer);

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(this);
    }

    @Override
    public void run() {
        try {
            ChannelFuture channelFuture = bootstrap.bind(0).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void send(){
        send(new DatagramPacket(Unpooled.copiedBuffer("echo", CharsetUtil.UTF_8),new InetSocketAddress("127.0.0.1",1112)));
    }

    @Override
    public void onMessageReceive(String data) {
        Log.d("nettyudp","receive" + data);
    }
}