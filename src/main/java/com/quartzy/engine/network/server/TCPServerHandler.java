package com.quartzy.engine.network.server;

import com.quartzy.engine.network.IMessage;
import com.quartzy.engine.network.NetworkManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.CustomLog;

import java.net.InetSocketAddress;
import java.util.HashMap;

@CustomLog
@ChannelHandler.Sharable
public class TCPServerHandler extends ChannelInboundHandlerAdapter{
    
    private HashMap<InetSocketAddress, Channel> channels = new HashMap<>();
 
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        IMessage requestData = (IMessage) msg;
        requestData.onMessageServer(NetworkManager.INSTANCE.getUser((InetSocketAddress)ctx.channel().remoteAddress()));
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        channels.put((InetSocketAddress)ctx.channel().remoteAddress(), ctx.channel());
    }
    
    public HashMap<InetSocketAddress, Channel> getChannels(){
        return channels;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        ctx.close();
        NetworkManager.INSTANCE.removeUser(NetworkManager.INSTANCE.getUser((InetSocketAddress)ctx.channel().remoteAddress()));
        log.warning(cause.getMessage());
    }
}