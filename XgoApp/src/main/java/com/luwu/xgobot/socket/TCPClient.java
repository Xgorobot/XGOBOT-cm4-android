package com.luwu.xgobot.socket;

import android.os.SystemClock;
import android.util.Log;


//import com.luwu.xgo_robot.mMothed.TypeUtils;

import com.luwu.xgobot.utils.ByteUtile;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Locale;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 作者：Lin on 2019/10/24 16:51
 * 邮箱：yuanwenlin2014@foxmail.com
 */
public class TCPClient {

    private static final String TAG = "NettyClient";

    private EventLoopGroup group;//Bootstrap参数

    private TCPListener listener;//写的接口用来接收服务端返回的值

    private Channel channel;//通过对象发送数据到服务端

    private boolean isConnect = false;//判断是否连接了

    private static int reconnectNum = Integer.MAX_VALUE;//定义的重连到时候用
    private boolean isNeedReconnect = true;//是否需要重连
    private boolean isConnecting = false;//是否正在连接
    private long reconnectIntervalTime = 5000;//重连的时间

    private String host;//ip
    private int tcp_port;//端口


    public void setConfig(String host, int port){
        this.host = host;
        this.tcp_port = port;
    }

    /*
    连接方法
     */
    public void connect() {
        if (isConnecting) {
            return;
        }
        //起个线程
        Thread clientThread = new Thread("client-Netty") {
            @Override
            public void run() {
                super.run();
                isNeedReconnect = true;
                reconnectNum = Integer.MAX_VALUE;
                connectServer();
            }
        };
        clientThread.start();
    }

    //连接时的具体参数设置
    private void connectServer() {
        synchronized (TCPClient.this) {
            ChannelFuture channelFuture = null;//连接管理对象
            if (!isConnect) {
                isConnecting = true;
                group = new NioEventLoopGroup();//设置的连接group
                Bootstrap bootstrap = new Bootstrap().group(group)//设置的一系列连接参数操作等
                        .option(ChannelOption.TCP_NODELAY, true)//屏蔽Nagle算法试图
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() { // 5
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
//                                ch.pipeline().addLast("framer",
//                                        new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));//服务端最后以"\n"作为结束标识
                                ch.pipeline().addLast(new StringEncoder(CharsetUtil.US_ASCII));//解码
                                ch.pipeline().addLast(new StringDecoder(CharsetUtil.US_ASCII));//解码
                                ch.pipeline().addLast(new TCPClientHandler(listener));//需要的handlerAdapter
                            }
                        });

                try {
                    //连接监听
                    channelFuture = bootstrap.connect(host, tcp_port).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if (channelFuture.isSuccess()) {
                                Log.e(TAG, "连接成功");
                                listener.onServiceStateMsgChanged("连接成功", true);
                                isConnect = true;
                                channel = channelFuture.channel();
                            } else {
                                Log.e(TAG, "连接失败");
                                listener.onServiceStateMsgChanged("连接失败", false);
                                isConnect = false;
                            }
                            isConnecting = false;
                        }
                    }).sync();

                    // 等待连接关闭
                    channelFuture.channel().closeFuture().sync();
                    Log.e(TAG, " 断开连接");
                    listener.onServiceStateMsgChanged("断开连接", false);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isConnect = false;
                    listener.onServiceStatusConnectChanged(TCPListener.STATUS_CONNECT_CLOSED);//STATUS_CONNECT_CLOSED 这我自己定义的 接口标识
                    if (null != channelFuture) {
                        if (channelFuture.channel() != null && channelFuture.channel().isOpen()) {
                            channelFuture.channel().close();
                        }
                    }
                    group.shutdownGracefully();
                    reconnect();//重新连接
                }
            }
        }
    }

    //断开连接
    public void disconnect() {
        Log.e(TAG, "disconnect");
        isNeedReconnect = false;
        group.shutdownGracefully();
    }

    //重新连接
    public void reconnect() {
        Log.e(TAG, "reconnect");
        if (isNeedReconnect && reconnectNum > 0 && !isConnect) {
            reconnectNum--;
            SystemClock.sleep(reconnectIntervalTime);
            if (isNeedReconnect && reconnectNum > 0 && !isConnect) {
                Log.e(TAG, "重新连接");
                listener.onServiceStateMsgChanged("重新连接", false);
                connectServer();
            }
        }
    }

    //发送消息到服务端。 Bootstrap设置的时候我没有设置解码，这边才转的
    public void sendFile(ArrayList<String> fileName, ArrayList<String> path){
        try {
            for (int i = 0; i < fileName.size(); i++){
//                channel.writeAndFlush(fileName.get(i));
                Log.d(TAG, "SendFile: " + fileName.get(i) + "正在发送");
                FileInputStream fileInput = new FileInputStream(path.get(i));
                int size = -1;
                byte[] buffer = new byte[1024];
                int x = 0;
                while((size = fileInput.read(buffer, 0, 1024)) != -1){
//                    Log.d(TAG, "sendFile: 发送第" + x + "    \n" + DatabaseUtils.bytesToHexString(buffer) +"\n");
                    x++;
                    channel.write(buffer);
                }
                channel.flush();
                fileInput.close();
                Log.d(TAG, "SendFile: " + fileName.get(i)+"  发送完成");
            }
            Log.d(TAG, "SendFile:  所有文件发送完成");
        } catch (Exception e) {
            Log.d(TAG, "SendFile: 发送错误:"+ e.getMessage());
        }
    }

    public void sendFile(String path, FileSendListener listener){
        try {
            FileInputStream fileInput = new FileInputStream(path);
            int size = -1;
            byte[] buffer = new byte[1024];
            int x = 0;
            while((size = fileInput.read(buffer, 0, 1024)) != -1){
                Log.d(TAG, "sendFile: size:" + size);
                Log.d(TAG, "sendFile: 发送第" + x + "    " + new String(buffer));
                x++;
                ByteBuf byteBuf = Unpooled.copiedBuffer(buffer,0,size);
                channel.write(byteBuf);
            }
            channel.flush();
            fileInput.close();
            if (listener!=null){
                listener.onFileSended();
            }

        } catch (Exception e) {
            Log.d(TAG, "SendFile: 发送错误:"+ e.getMessage());
        }
    }

    //发送消息到服务端。 Bootstrap设置的时候我没有设置解码，这边才转的
    public boolean sendMsgToServer(String data, ChannelFutureListener listener) {
        Log.d(TAG, "sendStringMsgToServer: " + data);
//        this.listener.onServiceStateMsgChanged("sendStringMsgToServer: " + data, isConnect);
        boolean flag = channel != null && isConnect;
        if (flag) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(data , //2
                    CharsetUtil.UTF_8);
            channel.writeAndFlush(byteBuf).addListener(listener);
        }
        return flag;
    }

    //发送消息到服务端。 Bootstrap设置的时候我没有设置解码，这边才转的
    public boolean sendMsgToServer(byte[] data, ChannelFutureListener listener) {
        boolean flag = channel != null && isConnect;
        if (flag) {
            Log.d(TAG, "writeAndFlush: String:" + new String(data).toUpperCase(Locale.ROOT));
            Log.d(TAG, "writeAndFlush: bytes:" + ByteUtile.byteArrayToHex(data));
//            this.listener.onServiceStateMsgChanged("writeAndFlush: String:" + new String(data), isConnect);
//            this.listener.onServiceStateMsgChanged("writeAndFlush: bytes:" + ByteUtile.byteArrayToHex(data), isConnect);
//            byte[] dataToSend = new byte[1024];
//            System.arraycopy(data,0,dataToSend,0,data.length);
            channel.writeAndFlush(new String(data).toUpperCase(Locale.ROOT)).addListener(listener);
        }
        return flag;
    }

    //重连次数
    public void setReconnectNum(int reconnectNum) {
        this.reconnectNum = reconnectNum;
    }

    public void setReconnectIntervalTime(long reconnectIntervalTime) {
        this.reconnectIntervalTime = reconnectIntervalTime;
    }
    //现在连接的状态
    public boolean isConnected() {
        return isConnect;
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    public void setConnectStatus(boolean status) {
        this.isConnect = status;
    }

    public void setListener(TCPListener listener) {
        this.listener = listener;
    }

    public interface FileSendListener{
        void onFileSended();
    }

}