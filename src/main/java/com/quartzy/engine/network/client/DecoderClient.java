package com.quartzy.engine.network.client;

import com.quartzy.engine.network.IMessage;
import com.quartzy.engine.network.NetworkManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.CustomLog;

import java.util.List;

@CustomLog
public class DecoderClient extends ReplayingDecoder<IMessage>{
 
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int id = in.readInt();
        IMessage message = NetworkManager.INSTANCE.getMessageTcp(id);
        if(message==null){
            log.warning("Unknown packet with id %d was received", id);
            return;
        }
        message.fromBytes(in);
        out.add(message);
    }
}