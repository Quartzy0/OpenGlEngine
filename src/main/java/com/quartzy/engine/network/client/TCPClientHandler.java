package com.quartzy.engine.network.client;

import com.quartzy.engine.network.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.CustomLog;

import java.nio.charset.StandardCharsets;

@CustomLog
public class TCPClientHandler extends ChannelInboundHandlerAdapter{
  
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new IMessage(){
            @Override
            public void toBytes(ByteBuf out){
                out.writeCharSequence("helloHeoufnsolg", StandardCharsets.US_ASCII);
            }
    
            @Override
            public void fromBytes(ByteBuf in){
        
            }
    
            @Override
            public void onMessageClient(){
        
            }
    
            @Override
            public void onMessageServer(short id){
        
            }
    
            @Override
            public int getId(){
                return 0;
            }
        });
    }
 
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ((IMessage)msg).onMessageClient();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        ctx.close();
        log.warning(cause.getMessage());
    }
}