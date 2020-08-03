package com.quartzy.engine.network.client;

import com.quartzy.engine.network.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class EncoderClient extends MessageToByteEncoder<IMessage>{
    
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IMessage message, ByteBuf out) throws Exception{
        out.writeInt(message.getId());
        message.toBytes(out);
    }
}