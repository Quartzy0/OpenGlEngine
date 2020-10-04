package com.quartzy.engine;

import com.quartzy.engine.audio.SoundManager;
import com.quartzy.engine.ecs.components.CustomRenderComponent;
import com.quartzy.engine.layers.LayerStack;
import com.quartzy.engine.layers.events.RenderEvent;
import com.quartzy.engine.layers.events.TickEvent;
import com.quartzy.engine.layers.layerimpl.WorldLayer;
import com.quartzy.engine.input.Input;
import com.quartzy.engine.graphics.Renderer;
import com.quartzy.engine.graphics.TextureManager;
import com.quartzy.engine.graphics.Window;
import com.quartzy.engine.network.NetworkManager;
import com.quartzy.engine.network.Side;
import com.quartzy.engine.network.client.NetworkManagerClient;
import com.quartzy.engine.utils.Logger;
import com.quartzy.engine.utils.Resource;
import com.quartzy.engine.utils.ResourceManager;
import com.quartzy.engine.world.World;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import org.atteo.classindex.ClassIndex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;

@CustomLog
public class Client{
    
    @Getter
    private static Client instance;
    private ApplicationClient applicationClient;
    
    private int tickInSecond;
    @Getter
    private Renderer renderer;
    private ResourceManager resourceManager;
    private TextureManager textureManager;
    @Getter
    @Setter
    private boolean running;
    @Getter
    private int fps;
    
    @Getter
    private LayerStack layerStack;
    
    private Window window;
    private NetworkManager networkManager;
    
    @Getter
    @Setter
    private String vertexShader, fragmentShader;
    
    @Getter
    @Setter
    private String host = null;
    @Getter
    @Setter
    private int port;
    
    /**
     * Creates the game object
     */
    public Client(ApplicationClient client){
        Client.instance = this;
        this.applicationClient = client;
    }
    
    /**
     * Initializes the game. This means creating the resource managers and loading all of the tiles from the file. It also initializes the renderer and opens the game window
     * @param args Game console arguments
     */
    public void init(String... args){
        Thread.currentThread().setName("Main game thread");
        if(Arrays.asList(args).contains("debug"))Logger.setEnabled(true);
        window = applicationClient.preInit(args, this);
        if(host!=null){
            networkManager = new NetworkManagerClient(port, host);
        }else {
            networkManager = new NetworkManager(Side.CLIENT);
        }
        WorldLayer layer = new WorldLayer();
        Input.init(window.getId(), layer);
        layerStack = new LayerStack();
        layerStack.pushLayer(layer);
        textureManager = new TextureManager();
        resourceManager = new ResourceManager(true, true, SoundManager.getInstance(), textureManager);
        renderer = new Renderer();
        Resource resource = null;
        Resource resource1 = null;
        if(vertexShader!=null && fragmentShader!=null){
            resource = resourceManager.addResource(vertexShader);
            resource1 = resourceManager.addResource(fragmentShader);
        }
        renderer.init(resource, resource1, window);
        applicationClient.init(this);
    }
    
    /**
     * Starts the game loop
     */
    public void startGame(){
        if(running){
            throw new IllegalStateException("The game is already running");
        }
        running = true;
        applicationClient.preStart(this);
        try{
            networkManager.run();
        } catch(Exception e){
            e.printStackTrace();
        }
        log.info("Game is now running!");
        gameLoop();
        dispose();
    }
    
    /**
     * Closes the window and clears all of the buffers
     */
    public void dispose(){
        running = false;
        if(NetworkManager.INSTANCE.getSide()==Side.CLIENT && World.getCurrentWorld()!=null){
            HashMap<Short, CustomRenderComponent> allCustomRenderers = World.getCurrentWorld().getEcsManager().getAllEntitiesWithComponent(CustomRenderComponent.class);
            if(allCustomRenderers != null && !allCustomRenderers.isEmpty()){
                for(CustomRenderComponent value : allCustomRenderers.values()){
                    value.dispose();
                }
            }
        }
        applicationClient.dispose(this);
        networkManager.close();
        SoundManager.getInstance().dispose();
        renderer.dispose();
        window.dispose();
        log.info("Goodbye!");
    }
    
    private long lastTime = System.currentTimeMillis();
    private long lastS = System.currentTimeMillis();
    
    private void gameLoop(){
        glfwSwapInterval(1);
        while (!window.shouldClose() && running) {
            float delta = (System.currentTimeMillis()-lastTime)/1000F;
            lastTime = System.currentTimeMillis();
            update(delta);
            render();
            
            tickInSecond++;
            if(System.currentTimeMillis()-lastS>=1000){
                lastS = System.currentTimeMillis();
                fps = tickInSecond;
                tickInSecond = 0;
            }
            
            glfwSwapBuffers(window.getId());
            glfwPollEvents();
        }
    }
    
    private void render(){
        renderer.clear();
    
        layerStack.triggerEvent(new RenderEvent(window.getId(), renderer));
    }
    
    private void update(float delta){
        layerStack.triggerEvent(new TickEvent(window.getId(), delta));
    }
    
    public ResourceManager getResourceManager(){
        return resourceManager;
    }
    
    public Window getWindow(){
        return window;
    }
    
    public TextureManager getTextureManager(){
        return textureManager;
    }
    
    public NetworkManager getNetworkManager(){
        return networkManager;
    }
    
    public static void main(String[] args){
        Iterable<Class<? extends ApplicationClient>> subclasses = ClassIndex.getSubclasses(ApplicationClient.class);
        if(subclasses==null)return;
        Iterator<Class<? extends ApplicationClient>> iterator = subclasses.iterator();
        if(!iterator.hasNext())return;
        Class<? extends ApplicationClient> next = iterator.next();
        try{
            ApplicationClient client1 = next.newInstance();
            Client client = new Client(client1);
            client.init(args);
            client.startGame();
        } catch(InstantiationException | IllegalAccessException e){
            log.severe("Something went wrong while trying to get the application class", e);
        }
    }
}
