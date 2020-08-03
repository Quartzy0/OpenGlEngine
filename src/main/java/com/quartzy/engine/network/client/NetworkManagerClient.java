package com.quartzy.engine.network.client;

import com.quartzy.engine.network.IMessage;
import com.quartzy.engine.network.NetworkManager;
import com.quartzy.engine.network.Side;

public class NetworkManagerClient extends NetworkManager{
    
    private int port;
    private String host;
    
    private ClientUDP clientUDP;
    private ClientTCP clientTCP;
    
    public NetworkManagerClient(int port, String host){
        super(Side.CLIENT);
        this.port = port;
        this.host = host;
    }
    
    @Override
    public void sendToServerTcp(IMessage message){
        super.sendToServerTcp(message);
    }
    
    @Override
    public void sendToServerUdp(IMessage message){
        super.sendToServerUdp(message);
    }
    
    @Override
    public void close(){
        clientTCP.close();
        clientUDP.close();
    }
    
    @Override
    public void run(){
        clientUDP = new ClientUDP(port+1, host);
        clientTCP = new ClientTCP(port, host);
        clientUDP.setName("Client UDP");
        clientTCP.setName("Client TCP");
        clientUDP.start();
        clientTCP.start();
    }
}
