package com.quartzy.engine.graphics;

import lombok.CustomLog;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

@CustomLog
public class Window{
    
    private long id;
    private String title;
    private int width, height;
    
    /**
     * @param title Title of the window
     * @param width Width of the window
     * @param height Height of the window
     */
    public Window(String title, int width, int height){
        this.title = title;
        this.width = width;
        this.height = height;
        init();
    }
    
    /**
     * Creates the GLFW window with the title, width and height from the constructor
     */
    private void init(){
        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        glfwSetErrorCallback(errorCallback);
    
        if (!glfwInit()) {
            log.severe("Error initializing GLFW: ", new IllegalStateException("Unable to initialize GLFW"));
        }
    
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        id = glfwCreateWindow(width, height, title, NULL, NULL);
    
        if (id == NULL) {
            glfwTerminate();
            log.severe("Error initializing window: ", new RuntimeException("Failed to create the GLFW window"));
        }
    
        // Make the OpenGL context current
        glfwMakeContextCurrent(id);
        // Enable v-sync
        glfwSwapInterval(1);
    
        // Make the window visible
        glfwShowWindow(id);
    
        GL.createCapabilities();
        setClearColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
    }
    
    /**
     * Closes the window and terminates GLFW
     */
    public void dispose(){
        glfwSetWindowShouldClose(id, true);
        glfwDestroyWindow(id);
        glfwTerminate();
    }
    
    public void updateViewport(int newWidth, int newHeight){
        this.width = newWidth;
        this.height = newHeight;
    }
    
    /**
     * @return Should the window be closed
     */
    public boolean shouldClose(){
        return glfwWindowShouldClose(id);
    }
    
    /**
     * @param color The color that the screen will be cleard with
     */
    public void setClearColor(Color color){
        glClearColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
    
    public long getId(){
        return id;
    }
    
    public String getTitle(){
        return title;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }
}
