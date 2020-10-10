package com.quartzy.engine;

import com.quartzy.engine.graphics.Window;
import com.quartzy.engine.utils.SystemInfo;
import org.atteo.classindex.IndexSubclasses;

@IndexSubclasses
public interface ApplicationClient{
    /**
     * Called before the client object is initialized. No resources can be loaded as the resource loaded was not yet initialized. This will usually be short and will set the vertex and fragment shaders by calling {@code client.setVertexShader(shader)} and {@code client.setFragmentShader(shader)}. It should also return a {@link Window} object.
     * @param args The command-line arguments that were used to start the application
     * @param client The client object
     * @return The {@link Window} object that will be used in the {@link Client} to render everything to and to listen for input
     */
    Client preInit(String[] args, SystemInfo systemInfo);
    
    /**
     * Called after the {@link Client} object has been fully initialized. You can load all resources in this method
     * @param client The client object
     */
    void init(Client client);
    
    /**
     * Called right before the game loop is started
     * @param client The client object
     */
    void preStart(Client client);
    
    /**
     * Called right before the program is stopped
     * @param client The client object
     */
    void dispose(Client client);
}
