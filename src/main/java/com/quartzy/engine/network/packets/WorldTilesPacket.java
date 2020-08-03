/*
package com.quartzy.engine.network.packets;

import com.quartzy.engine.Client;
import com.quartzy.engine.entities.Entity;
import com.quartzy.engine.network.IMessage;
import com.quartzy.engine.world.World;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

public class WorldTilesPacket extends IMessage{
    @Getter
    @Setter
    private byte[] tiles;
    @Getter
    @Setter
    private short width, height;
    @Getter
    @Setter
    private String name;
    
    public WorldTilesPacket(){
    }
    
    public WorldTilesPacket(World world){
        this.width = (short) world.getWidth();
        this.height = (short) world.getHeight();
        this.tiles = new byte[width*height];
        int i = 0;
        for(Entity[] tile : world.getTiles()){
            for(Entity entity : tile){
                this.tiles[i] = (byte) World.getTileId(entity);
                i++;
            }
        }
        this.name = world.getName();
    }
    
    public WorldTilesPacket(byte[] tiles, short width, short height, String name){
        this.tiles = tiles;
        this.width = width;
        this.height = height;
        this.name = name;
    }
    
    @Override
    public void toBytes(ByteBuf out){
        out.writeShort(width);
        out.writeShort(height);
        out.writeShort(name.length());
        out.writeCharSequence(name, StandardCharsets.US_ASCII);
        out.writeBytes(tiles);
    }
    
    @Override
    public void fromBytes(ByteBuf in){
        this.width = in.readShort();
        this.height = in.readShort();
        int i = in.readShort();
        this.name = in.readCharSequence(i, StandardCharsets.US_ASCII).toString();
        this.tiles = new byte[width*height];
        in.readBytes(this.tiles);
    }
    
    @Override
    public void onMessageClient(){
        Client.getInstance().setWorld(WorldLoader.loadWorldFromByteTiles(this.tiles, this.width, this.height, this.name));
    }
    
    @Override
    public void onMessageServer(short id){
    
    }
    
    @Override
    public int getId(){
        return 2;
    }
}
*/
