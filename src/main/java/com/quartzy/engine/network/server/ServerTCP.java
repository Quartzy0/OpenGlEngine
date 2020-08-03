package com.quartzy.engine.network.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.CustomLog;

@CustomLog
public class ServerTCP extends Thread{
    
    private int port;
    private TCPServerHandler serverHandler;
    
    private ChannelFuture f;
    
    public ServerTCP(int port){
        super("Server TCP");
        this.setDaemon(true);
        this.port = port;
    }
    
    @Override
    public void run(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            serverHandler = new TCPServerHandler();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception{
                            ch.pipeline().addLast(new DecoderServer(),
                                    new EncoderServer(),
                                    serverHandler);
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
    
            f = b.bind(port).sync();
            log.info("Listening on port %d", port);
            f.channel().closeFuture().sync();
        } catch(InterruptedException e){
            e.printStackTrace();
        } finally{
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    
    public TCPServerHandler getServerHandler(){
        return serverHandler;
    }
    
    public void close(){
        log.info("Closing TCP server");
        f.channel().close();
    }
}
