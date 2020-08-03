package com.quartzy.engine.network;

import io.netty.buffer.ByteBuf;

public abstract class IMessage{
    
    public abstract void toBytes(ByteBuf out);
    public abstract void fromBytes(ByteBuf in);
    
    public abstract void onMessageClient();
    public abstract void onMessageServer(short id);
    
    public abstract int getId();
}
