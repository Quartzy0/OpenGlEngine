package com.quartzy.engine.network.server;

import com.quartzy.engine.network.IMessage;
import com.quartzy.engine.network.NetworkManager;
import com.quartzy.engine.network.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;

public class NetworkManagerServer extends NetworkManager{
    
    private int port;
    
    private ServerTCP serverTCP;
    private ServerUDP serverUDP;
    
    public NetworkManagerServer(int port){
        super(Side.SERVER);
        this.port = port;
    }
    
    @Override
    public void sendToAllTcp(IMessage message){
        super.sendToAllTcp(message);
    }
    
    @Override
    public void sendToAllUdp(IMessage message){
        super.sendToAllUdp(message);
    }
    
    @Override
    public void run(){
        serverTCP = new ServerTCP(port);
        serverUDP = new ServerUDP(port+1);
        serverTCP.start();
        serverUDP.start();
    }
    
    @Override
    public void sendToUdp(IMessage message, short id){
        ByteBuf out = Unpooled.buffer();
        out.writeInt(message.getId());
        message.toBytes(out);
        DatagramPacket packet = new DatagramPacket(out, users.get(id));
        serverUDP.getServerHandler().getChannel().writeAndFlush(packet);
    }
    
    @Override
    public void sendToTcp(IMessage message, short id){
        serverTCP.getServerHandler().getChannels().get(users.get(id)).writeAndFlush(message);
    }
}
