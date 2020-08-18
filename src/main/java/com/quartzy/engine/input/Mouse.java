package com.quartzy.engine.input;

import com.quartzy.engine.math.Vector2f;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse{
    private final long window;
    private final HashMap<Integer, ButtonPressed> mouseEvents = new HashMap<>();
    
    private GLFWMouseButtonCallback callback;
    
    /**
     * Creates the object and sets all of the appropriate GLFW event callbacks
     * @param window The GLFW window id
     */
    public Mouse(long window){
        this.window = window;
        glfwSetMouseButtonCallback(window, callback = new GLFWMouseButtonCallback(){
            @Override
            public void invoke(long window, int button, int action, int mods){
                ButtonPressed buttonPressed = mouseEvents.get(button);
                if(buttonPressed!=null){
                    Mods mods1 = new Mods((mods & GLFW_MOD_CONTROL)==GLFW_MOD_CONTROL, (mods & GLFW_MOD_ALT)==GLFW_MOD_ALT, (mods & GLFW_MOD_SHIFT)==GLFW_MOD_SHIFT, (mods & GLFW_MOD_NUM_LOCK)==GLFW_MOD_NUM_LOCK, (mods & GLFW_MOD_SUPER)==GLFW_MOD_SUPER, (mods & GLFW_MOD_CAPS_LOCK)==GLFW_MOD_CAPS_LOCK);
                    buttonPressed.pressed(action, mods1);
                }
            }
        });
    }
    
    /**
     * Checks the state of the button specified
     * @param button GLFW button id
     * @return The state of the button
     */
    public boolean isButtonDown(int button){
        return glfwGetMouseButton(window, button)==GLFW_PRESS;
    }
    
    /**
     * Adds the event listener and calls it every time the button is pressed
     * @param button GLFW button id
     * @param buttonPressed The event listener for the button specified
     */
    public void addListener(int button, ButtonPressed buttonPressed){
        mouseEvents.put(button, buttonPressed);
    }
    
    /**
     * @return Current cursor position
     */
    public Vector2f getCursorPos(){
        try(MemoryStack stack = MemoryStack.stackPush()){
            DoubleBuffer xpos = stack.mallocDouble(1);
            DoubleBuffer ypos = stack.mallocDouble(1);
            glfwGetCursorPos(window, xpos, ypos);
            return new Vector2f((float) xpos.get(), (float) ypos.get());
        }
    }
}
