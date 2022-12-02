package com.luwu.xgo_robot.socket;

import android.util.Log;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 作者：Lin on 2019/10/24 16:53
 * 邮箱：yuanwenlin2014@foxmail.com
 */
public class TCPClientHandler extends ChannelInboundHandlerAdapter {

    private static final String TAG = "NettyClientHandler";
    private TCPListener listener;

    public TCPClientHandler(TCPListener listener) {
        this.listener = listener;
    }

    //每次给服务器发送的东西， 让服务器知道我们在连接中哎
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof IdleStateEvent) {
//            IdleStateEvent event = (IdleStateEvent) evt;
//            if (event.state() == IdleState.WRITER_IDLE) {
////                ctx.channel().writeAndFlush("Heartbeat" + System.getProperty("line.separator"));
//            }
//        }
    }

    /**
     * 连接成功
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "channelActive");
        super.channelActive(ctx);
        listener.onServiceStatusConnectChanged(TCPListener.STATUS_CONNECT_SUCCESS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.e(TAG, "channelInactive");
    }

    //接收消息的地方， 接口调用返回到activity了
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Log.d(TAG, "客户端开始读取服务端过来的信息: ");
        listener.onMessageResponse(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 当引发异常时关闭连接。
        Log.e(TAG, "exceptionCaught");
        listener.onServiceStatusConnectChanged(TCPListener.STATUS_CONNECT_ERROR);
        cause.printStackTrace();
        ctx.close();
    }

}