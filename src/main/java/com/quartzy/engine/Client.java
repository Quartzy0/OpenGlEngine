package com.quartzy.engine;

import com.quartzy.engine.audio.SoundManager;
import com.quartzy.engine.ecs.components.CustomRenderComponent;
import com.quartzy.engine.graphics.Framebuffer;
import com.quartzy.engine.layers.LayerStack;
import com.quartzy.engine.world.WorldLayer;
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
import com.quartzy.engine.utils.SystemInfo;
import com.quartzy.engine.world.World;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import org.atteo.classindex.ClassIndex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
    
    private Framebuffer passOnFramebuffer;
    
    /**
     * Creates the game object
     */
    public Client(ApplicationClient client, ClientBuilder builder){
        Client.instance = this;
        this.applicationClient = client;
        this.window = new Window(builder.getWindowTitle(), builder.getWindowWidth(), builder.getWindowHeight());
        this.window.init();
        this.passOnFramebuffer = builder.getFramebuffer();
    }
    
    /**
     * Initializes the game. This means creating the resource managers and loading all of the tiles from the file. It also initializes the renderer and opens the game window
     */
    public void init(){
        Thread.currentThread().setName("Main game thread");
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
        renderer.setFramebuffer(this.passOnFramebuffer);
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
            HashMap<Short, List<CustomRenderComponent>> allCustomRenderers = World.getCurrentWorld().getEcsManager().getAllEntitiesWithComponent(CustomRenderComponent.class);
            if(allCustomRenderers != null && !allCustomRenderers.isEmpty()){
                for(List<CustomRenderComponent> values : allCustomRenderers.values()){
                    for(CustomRenderComponent value : values){
                        value.dispose();
                    }
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
    
        layerStack.render(renderer);
    }
    
    private void update(float delta){
        layerStack.update(delta);
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
        if(Arrays.asList(args).contains("debug"))Logger.setEnabled(true);
        Iterable<Class<? extends ApplicationClient>> subclasses = ClassIndex.getSubclasses(ApplicationClient.class);
        if(subclasses==null)return;
        Iterator<Class<? extends ApplicationClient>> iterator = subclasses.iterator();
        if(!iterator.hasNext())return;
        Class<? extends ApplicationClient> next = iterator.next();
        try{
            ApplicationClient client1 = next.newInstance();
            Client client = client1.preInit(args, SystemInfo.getSystemInfo());
            if(client==null) client = new Client.ClientBuilder().build(client1);
            client.init();
            client.startGame();
        } catch(InstantiationException | IllegalAccessException e){
            log.severe("Something went wrong while trying to get the application class", e);
        }
    }
    
    public static class ClientBuilder{
        private int windowWidth = 1280, windowHeight = 720;
        private String windowTitle;
        private String vertexShader, fragmentShader;
        private String host;
        private int port;
        private Framebuffer framebuffer;
    
        public int getWindowWidth(){
            return windowWidth==0 ? 1280 : windowWidth;
        }
    
        public ClientBuilder setWindowWidth(int windowWidth){
            this.windowWidth = windowWidth;
            return this;
        }
    
        public Framebuffer getFramebuffer(){
            return framebuffer;
        }
    
        public ClientBuilder setFramebuffer(Framebuffer framebuffer){
            this.framebuffer = framebuffer;
            return this;
        }
    
        public int getWindowHeight(){
            return windowHeight==0 ? 720 : windowHeight;
        }
    
        public ClientBuilder setWindowHeight(int windowHeight){
            this.windowHeight = windowHeight;
            return this;
        }
    
        public String getWindowTitle(){
            return windowTitle==null ? "Hello world" : windowTitle;
        }
    
        public ClientBuilder setWindowTitle(String windowTitle){
            this.windowTitle = windowTitle;
            return this;
        }
    
        public String getVertexShader(){
            return vertexShader;
        }
    
        public ClientBuilder setVertexShader(String vertexShader){
            this.vertexShader = vertexShader;
            return this;
        }
    
        public String getFragmentShader(){
            return fragmentShader;
        }
    
        public ClientBuilder setFragmentShader(String fragmentShader){
            this.fragmentShader = fragmentShader;
            return this;
        }
    
        public String getHost(){
            return host;
        }
    
        public ClientBuilder setHost(String host){
            this.host = host;
            return this;
        }
    
        public int getPort(){
            return port;
        }
    
        public ClientBuilder setPort(int port){
            this.port = port;
            return this;
        }
        
        public Client build(ApplicationClient application){
            Client client = new Client(application, this);
            client.setVertexShader(vertexShader);
            client.setFragmentShader(fragmentShader);
            client.setHost(host);
            client.setPort(port);
            return client;
        }
    }
}
