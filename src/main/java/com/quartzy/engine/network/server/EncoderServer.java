package com.quartzy.engine.network.server;

import com.quartzy.engine.network.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.CustomLog;

@CustomLog
public class EncoderServer extends MessageToByteEncoder<IMessage>{
    
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IMessage message, ByteBuf out) throws Exception{
        out.writeInt(message.getId());
        message.toBytes(out);
        log.info("Sending packet with id %d, that has the size of %d bytes", message.getId(), out.readableBytes());
    }
}