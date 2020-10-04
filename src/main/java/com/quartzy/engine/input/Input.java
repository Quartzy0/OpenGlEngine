package com.quartzy.engine.input;

import com.quartzy.engine.layers.layerimpl.WorldLayer;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class Input{
    private static final HashMap<Integer, Boolean> prevKeyStates = new HashMap<>();
    
    private static long window;
    private static WorldLayer worldLayer;
    
    public static void init(long windowId, WorldLayer layer){
        window = windowId;
        worldLayer = layer;
    }
    
    public static boolean isKeyPressed(int key){
        return worldLayer.getKeyStates().getOrDefault(key, false);
    }
    
    public static boolean isKeyDown(int key){
        boolean currentState = isKeyPressed(key);
        boolean b = prevKeyStates.containsKey(key) ? (!prevKeyStates.get(key)) && currentState : currentState;
        prevKeyStates.put(key, currentState);
        return b;
    }
    
    public static boolean isButtonDown(int button){
        return worldLayer.getMouseButtonStates().getOrDefault(button, false);
    }
    
    public static Vector2f getCursorPos(){
        return new Vector2f(worldLayer.getMouseX(), worldLayer.getMouseY());
    }
}
