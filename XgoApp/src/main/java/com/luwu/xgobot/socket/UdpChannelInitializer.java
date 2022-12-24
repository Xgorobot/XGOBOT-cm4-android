package com.luwu.xgobot.socket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class UdpChannelInitializer extends ChannelInitializer<DatagramChannel> {
    private UdpChannelInboundHandler inboundHandler;
    public UdpChannelInitializer(UdpChannelInboundHandler handler){
        inboundHandler = handler;
    }

    @Override
    protected void initChannel(DatagramChannel datagramChannel) throws Exception {
        ChannelPipeline pipeline = datagramChannel.pipeline();
        pipeline.addLast(new IdleStateHandler(12,15,0));
        pipeline.addLast(inboundHandler);
    }
}