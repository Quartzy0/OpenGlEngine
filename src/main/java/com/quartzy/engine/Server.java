package com.quartzy.engine;

import com.quartzy.engine.network.NetworkManager;
import com.quartzy.engine.network.server.NetworkManagerServer;
import com.quartzy.engine.utils.Logger;
import com.quartzy.engine.utils.ResourceManager;
import com.quartzy.engine.world.World;
import lombok.CustomLog;
import lombok.Getter;

import java.util.Arrays;

@CustomLog
public class Server{
    
    @Getter
    private static Server instance;
    
    private int port;
    private boolean running;
    private int tickInSecond;
    private int tps;
    private String name;
    
    private World world;
    
    private ResourceManager resourceManager;
    private NetworkManager networkManager;
    
    /**
     * Initializes the object with  a port
     * @param port Port the server will listen on
     */
    public Server(int port, String name){
        this.port = port;
        this.name = name;
        instance = this;
    }
    
    public void init(String... args){
        Thread.currentThread().setName("Main server thread");
        if(Arrays.stream(args).anyMatch("debug"::equals))Logger.setEnabled(true);
        resourceManager = new ResourceManager(false, false, null, null);
        networkManager = new NetworkManagerServer(port);
    }
    
    /**
     * Starts the server with the port the object was initialized with
     */
    public void run(){
        try{
            networkManager.run();
        } catch(Exception e){
            e.printStackTrace();
        }
        running = true;
        log.info("Server is now running!");
        gameLoop();
        log.info("Goodbye!");
    }
    
    private long lastTime = System.currentTimeMillis();
    
    private void gameLoop(){
        lastTime = System.currentTimeMillis();
        while (running) {
            float delta = (System.currentTimeMillis()-lastTime)/1000F;
            lastTime = System.currentTimeMillis();
            update(delta);
        
            tickInSecond++;
            if(System.currentTimeMillis()-lastTime>=1000){
                lastTime = System.currentTimeMillis();
                tps = tickInSecond;
                tickInSecond = 0;
            }
        }
    }
    
    private void update(float delta){
        if(world!=null){
            world.update(delta);
        }
    }
    
    public World getWorld(){
        return world;
    }
    
    public Server setWorld(World world){
        this.world = world;
        return this;
    }
    
    public int getPort(){
        return port;
    }
    
    public boolean isRunning(){
        return running;
    }
    
    public int getTps(){
        return tps;
    }
    
    public ResourceManager getResourceManager(){
        return resourceManager;
    }
    
    public NetworkManager getNetworkManager(){
        return networkManager;
    }
}
