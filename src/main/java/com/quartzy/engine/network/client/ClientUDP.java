package com.quartzy.engine.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.CustomLog;

@CustomLog
public class ClientUDP extends Thread{
    
    private int port;
    private String host;
    
    private ChannelFuture f;
    
    public ClientUDP(int port, String host){
        this.setDaemon(true);
        this.port = port;
        this.host = host;
    }
    
    @Override
    public void run(){
        EventLoopGroup workerGroup = new NioEventLoopGroup();
    
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioDatagramChannel.class);
            b.handler(new UDPClientHandler());
        
            f = b.connect(host, port).sync();
            log.info("Connected over UDP to %s:%d", host, port);
            f.channel().closeFuture().sync();
        } catch(InterruptedException e){
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
    
    public void close(){
        log.info("Closing UDP client on port %d", port);
        f.channel().close();
    }
}
