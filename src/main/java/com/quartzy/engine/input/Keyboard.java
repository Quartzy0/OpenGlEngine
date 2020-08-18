package com.quartzy.engine.input;

import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard{
    
    private final long window;
    private final HashMap<Integer, KeyPressed> keyEvents = new HashMap<>();
    
    private final GLFWKeyCallback callback;
    
    private final HashMap<Integer, Boolean> prevKeyStates = new HashMap<>();
    
    /**
     * Creates the object and sets all of the appropriate GLFW event callbacks
     * @param window The GLFW window id
     */
    public Keyboard(long window){
        this.window = window;
        glfwSetKeyCallback(window, callback = new GLFWKeyCallback(){
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods){
                KeyPressed keyPressed = keyEvents.get(key);
                if(keyPressed!=null){
                    Mods mods1 = new Mods((mods & GLFW_MOD_CONTROL)==GLFW_MOD_CONTROL, (mods & GLFW_MOD_ALT)==GLFW_MOD_ALT, (mods & GLFW_MOD_SHIFT)==GLFW_MOD_SHIFT, (mods & GLFW_MOD_NUM_LOCK)==GLFW_MOD_NUM_LOCK, (mods & GLFW_MOD_SUPER)==GLFW_MOD_SUPER, (mods & GLFW_MOD_CAPS_LOCK)==GLFW_MOD_CAPS_LOCK);
                    keyPressed.pressed(scancode, action, mods1);
                }
            }
        });
    }
    
    /**
     * Checks the state of the provided key
     * @param key The GLFW key code
     * @return <code>true</code> if key is pressed, and <code>false</code> if not pressed
     */
    public boolean isKeyPressed(int key){
        return glfwGetKey(window, key)==GLFW_PRESS;
    }
    
    /**
     * Checks if a key is pressed and was't pressed last time it was checked
     * @param key The key you want to check for
     * @return Is the key newly pressed
     */
    public boolean isKeyDown(int key){
        boolean currentState = isKeyPressed(key);
        boolean b = prevKeyStates.containsKey(key) ? (!prevKeyStates.get(key)) && currentState : currentState;
        prevKeyStates.put(key, currentState);
        return b;
    }
    
    /**
     * Adds a listener for the specific key that was specified
     * @param key GLFW key code
     * @param keyPressed Event listener
     */
    public void addListener(int key, KeyPressed keyPressed){
        keyEvents.put(key, keyPressed);
    }
}
