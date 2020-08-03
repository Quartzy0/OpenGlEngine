package com.quartzy.engine.network;

import com.quartzy.engine.network.packets.IdentityPacket;
import lombok.CustomLog;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@CustomLog
public class NetworkManager{
    public static NetworkManager INSTANCE;
    @Getter
    private Side side;
    
    public NetworkManager(Side side){
        this.side = side;
        INSTANCE = this;
        registerMessageTcp(new IdentityPacket());
    }
    
    protected HashMap<Integer, IMessage> messagesTcp = new HashMap<>();
    protected HashMap<Integer, IMessage> messagesUdp = new HashMap<>();
    
    protected HashMap<Short, InetSocketAddress> users = new HashMap<>();
    protected Random r = new Random();
    
    public void registerMessageTcp(IMessage message){
        if(messagesTcp.containsKey(message.getId())){
            log.warning("A packet with the id %d already exists!", new RuntimeException("A duplicate packet was created"), message.getId());
        }
        messagesTcp.put(message.getId(), message);
    }
    
    public void registerMessageUdp(IMessage message){
        if(messagesTcp.containsKey(message.getId())){
            log.warning("A packet with the id %d already exists!", new RuntimeException("A duplicate packet was created"), message.getId());
        }
        messagesUdp.put(message.getId(), message);
    }
    
    public IMessage getMessageTcp(int id){
        return messagesTcp.get(id);
    }
    
    public void run(){
    
    }
    
    public IMessage getMessageUdp(int id){
        return messagesUdp.get(id);
    }
    
    public void sendToServerTcp(IMessage message){
    
    }
    
    public void sendToServerUdp(IMessage message){
    
    }
    
    public void sendToAllTcp(IMessage message){
    
    }
    
    public void sendToAllUdp(IMessage message){
    
    }
    
    public void sendToTcp(IMessage message, short id){
    
    }
    
    public void sendToUdp(IMessage message, short id){
    
    }
    
    public void close(){
    
    }
    
    public short addUser(InetSocketAddress socketAddress){
        short s = (short) r.nextInt(Short.MAX_VALUE + 1);
        while(users.containsKey(s) && s==0){
            s = (short) r.nextInt(Short.MAX_VALUE + 1);
        }
        users.put(s, socketAddress);
        return s;
    }
    
    public short getUser(InetSocketAddress address){
        for(Map.Entry<Short, InetSocketAddress> entry : users.entrySet()){
            if(entry.getValue().equals(address)){
                return entry.getKey();
            }
        }
        return 0;
    }
    
    public InetSocketAddress getUser(short id){
        return users.get(id);
    }
    
    public HashMap<Short, InetSocketAddress> getUsers(){
        return users;
    }
    
    public void removeUser(short id){
        users.remove(id);
    }
}
