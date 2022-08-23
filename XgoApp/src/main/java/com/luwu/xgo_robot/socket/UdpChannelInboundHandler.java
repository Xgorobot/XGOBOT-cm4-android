//package com.luwu.xgo_robot.socket;
//
//import android.util.Log;
//
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.socket.DatagramPacket;
//import io.netty.util.CharsetUtil;
//import io.netty.util.concurrent.GenericFutureListener;
//
//public abstract class UdpChannelInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {
//    private ChannelHandlerContext handlerContext;
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
//        handlerContext = ctx;
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        super.channelInactive(ctx);
//        handlerContext.close();
//    }
//
//    public void send(Object o){
//        handlerContext.writeAndFlush(o).addListener(new GenericFutureListener<ChannelFuture>() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                Log.d("nettyudp","operationComplete "+future.isSuccess());
//            }
//        });
//    }
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
//        String body = datagramPacket.content().toString(CharsetUtil.UTF_8);
//        receive(body);
//    }
//
//    public abstract void receive(String data);
//}