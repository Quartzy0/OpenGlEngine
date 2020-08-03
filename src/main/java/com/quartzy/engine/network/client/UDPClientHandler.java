package com.quartzy.engine.network.client;

import com.quartzy.engine.network.IMessage;
import com.quartzy.engine.network.NetworkManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.CustomLog;

import java.net.InetSocketAddress;

@CustomLog
public class UDPClientHandler extends SimpleChannelInboundHandler<DatagramPacket>{
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception{
        if(!NetworkManager.INSTANCE.getUsers().containsValue(ctx.channel().remoteAddress()))return;
        ByteBuf in = msg.content();
        int id = in.readInt();
        IMessage message = NetworkManager.INSTANCE.getMessageUdp(id);
        if(message==null){
            log.warning("Received unknown packet with id %d", id);
            return;
        }
        message.fromBytes(in);
        message.onMessageClient();
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        if(!NetworkManager.INSTANCE.getUsers().containsValue(ctx.channel().remoteAddress()))return;
        ByteBuf in = ((DatagramPacket)msg).content();
        int id = in.readInt();
        IMessage message = NetworkManager.INSTANCE.getMessageUdp(id);
        if(message==null){
            log.warning("Received unknown packet with id %d", id);
            return;
        }
        message.fromBytes(in);
        message.onMessageClient();
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        ctx.flush();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        ctx.close();
        log.warning(cause.getMessage());
    }
}
