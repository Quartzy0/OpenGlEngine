package com.quartzy.engine.network.client;

import com.quartzy.engine.network.NetworkManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.CustomLog;

@CustomLog
public class ClientTCP extends Thread{
    
    private int port;
    private String host;
    
    private ChannelFuture f;
    
    public ClientTCP(int port, String host){
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
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
        
                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(new EncoderClient(),
                            new DecoderClient(), new TCPClientHandler());
                }
            });
        
            f = b.connect(host, port).sync();
            log.info("Connected over TCP to %s:%d", host, port);
            f.channel().closeFuture().sync();
        } catch(InterruptedException e){
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
    
    public void close(){
        log.info("Closing TCP client on %d", port);
        if(f!=null && f.channel()!=null){
            f.channel().close();
        }
    }
}
