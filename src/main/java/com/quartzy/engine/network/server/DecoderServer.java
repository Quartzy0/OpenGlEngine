package com.quartzy.engine.network.server;

import com.quartzy.engine.Server;
import com.quartzy.engine.network.IMessage;
import com.quartzy.engine.network.NetworkManager;
import com.quartzy.engine.network.packets.IdentityPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.CustomLog;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

@CustomLog
public class DecoderServer extends ReplayingDecoder<IMessage>{
 
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int i = in.readInt();
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        if(i==0){
            if(NetworkManager.INSTANCE.getUsers().containsValue(socketAddress))return;
            String s = in.readCharSequence("helloHeoufnsolg".length(), StandardCharsets.US_ASCII).toString();
            if(s.equals("helloHeoufnsolg")){
                short s1 = NetworkManager.INSTANCE.addUser(socketAddress);
                IdentityPacket identityPacket = new IdentityPacket();
                identityPacket.setPlayerId(s1);
                NetworkManager.INSTANCE.sendToTcp(identityPacket, s1);
                log.info("Player with id %d was added", s1);
//                WorldTilesPacket worldTilesPacket = new WorldTilesPacket(Server.getInstance().getWorld());
//                NetworkManager.INSTANCE.sendToTcp(worldTilesPacket, s1);
            }
            return;
        }
        if(!NetworkManager.INSTANCE.getUsers().containsValue(socketAddress))return;
        IMessage message = NetworkManager.INSTANCE.getMessageTcp(i);
        if(message==null){
            log.warning("Unknown packet with id %d was recieved", i);
            return;
        }
        message.fromBytes(in);
        out.add(message);
    }
}