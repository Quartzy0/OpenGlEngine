package com.quartzy.engine.network.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.CustomLog;

@CustomLog
public class ServerUDP extends Thread{
    
    private int port;
    private UDPServerHandler serverHandler;
    
    private ChannelFuture sync;
    
    public ServerUDP(int port){
        super("Server UDP");
        this.setDaemon(true);
        this.port = port;
    }
    
    @Override
    public void run(){
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b1 = new Bootstrap();
            serverHandler = new UDPServerHandler();
            b1.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(serverHandler);
    
            sync = b1.bind(port).sync();
            log.info("Listening on port %d", port);
            sync.channel().closeFuture().sync();
        } catch(InterruptedException e){
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
    
    public UDPServerHandler getServerHandler(){
        return serverHandler;
    }
    
    public void close(){
        log.info("Closing UDP");
        sync.channel().close();
    }
}
