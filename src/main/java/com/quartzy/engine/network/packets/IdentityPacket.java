package com.quartzy.engine.network.packets;

import com.quartzy.engine.network.IMessage;
import io.netty.buffer.ByteBuf;
import lombok.CustomLog;

@CustomLog
public class IdentityPacket extends IMessage{
    private short playerId;
    
    @Override
    public void toBytes(ByteBuf out){
        out.writeShort(playerId);
    }
    
    @Override
    public void fromBytes(ByteBuf in){
        playerId = in.readShort();
    }
    
    @Override
    public void onMessageClient(){
        if(playerId ==0){
            log.severe("Authentication failed!");
        }else {
            log.info("Authentication succeeded! Player ID: " + playerId);
        }
    }
    
    @Override
    public void onMessageServer(short id){
    
    }
    
    @Override
    public int getId(){
        return 1;
    }
    
    public short getPlayerId(){
        return playerId;
    }
    
    public void setPlayerId(short playerId){
        this.playerId = playerId;
    }
}
