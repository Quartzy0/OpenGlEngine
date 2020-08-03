package com.quartzy.engine;

import com.quartzy.engine.audio.SoundManager;
import com.quartzy.engine.events.KeyPressed;
import com.quartzy.engine.events.Keyboard;
import com.quartzy.engine.events.Mods;
import com.quartzy.engine.graphics.Color;
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
import java.util.Iterator;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;

@CustomLog
public class Client{
    
    @Getter
    private static Client instance;
    private ApplicationClient applicationClient;
    
    private int tickInSecond;
    private Renderer renderer;
    private Keyboard keyboard;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private TextureManager textureManager;
    private boolean running;
    private int fps;
    
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
        if(Arrays.stream(args).anyMatch("debug"::equals))Logger.setEnabled(true);
        window = applicationClient.preInit(args, this);
        if(host!=null){
            networkManager = new NetworkManagerClient(port, host);
        }else {
            networkManager = new NetworkManager(Side.CLIENT);
        }
        keyboard = new Keyboard(window.getId());
        soundManager = new SoundManager();
        textureManager = new TextureManager();
        keyboard.addListener(GLFW_KEY_ESCAPE, new KeyPressed(){
            @Override
            public void pressed(int scancode, int action, Mods mods){
                if(action==GLFW_PRESS){
                    running = false;
                }
            }
        });
        resourceManager = new ResourceManager(true, true, soundManager, textureManager);
        renderer = new Renderer();
        Resource resource = resourceManager.addResource(vertexShader);
        Resource resource1 = resourceManager.addResource(fragmentShader);
        if(resource==null && resource1==null){
            log.severe("No shaders were found at locations %s for vertex and %s for fragment", new RuntimeException("No shaders found"), vertexShader, fragmentShader);
            return;
        }else if(resource==null){
            log.severe("No vertex shader found at %s", new RuntimeException("No shaders found"), vertexShader);
            return;
        }else if(resource1==null){
            log.severe("No fragment shader found at %s", new RuntimeException("No shaders found"), fragmentShader);
            return;
        }
        renderer.init(resource, resource1);
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
        applicationClient.dispose(this);
        networkManager.close();
        soundManager.dispose();
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
    
        if(World.getCurrentWorld()!=null){
            World.getCurrentWorld().render(renderer);
        }
        renderer.drawString("FPS: " + fps, 10, window.getHeight()-30, Color.GREEN);
    }
    
    private void update(float delta){
        if(World.getCurrentWorld()!=null){
            World.getCurrentWorld().update(delta);
        }
    }
    
    public Keyboard getKeyboard(){
        return keyboard;
    }
    
    public SoundManager getSoundManager(){
        return soundManager;
    }
    
    public ResourceManager getResourceManager(){
        return resourceManager;
    }
    
    public Window getWindow(){
        return window;
    }
    
    public boolean isRunning(){
        return running;
    }
    
    public int getFps(){
        return fps;
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
