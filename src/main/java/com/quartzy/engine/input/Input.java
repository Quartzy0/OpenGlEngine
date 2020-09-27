package com.quartzy.engine.input;

import com.quartzy.engine.graphics.Window;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class Input{
    private static final HashMap<Integer, Boolean> prevKeyStates = new HashMap<>();
    
    private static long window;
    
    public static void init(long windowId){
        window = windowId;
    }
    
    public static boolean isKeyPressed(int key){
        return glfwGetKey(window, key)==GLFW_PRESS;
    }
    
    public static boolean isKeyDown(int key){
        boolean currentState = isKeyPressed(key);
        boolean b = prevKeyStates.containsKey(key) ? (!prevKeyStates.get(key)) && currentState : currentState;
        prevKeyStates.put(key, currentState);
        return b;
    }
    
    public static boolean isButtonDown(int button){
        return glfwGetMouseButton(window, button)==GLFW_PRESS;
    }
    
    public static Vector2f getCursorPos(){
        try(MemoryStack stack = MemoryStack.stackPush()){
            DoubleBuffer xpos = stack.mallocDouble(1);
            DoubleBuffer ypos = stack.mallocDouble(1);
            glfwGetCursorPos(window, xpos, ypos);
            return new Vector2f((float) xpos.get(), (float) ypos.get());
        }
    }
}
