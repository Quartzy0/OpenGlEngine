package com.quartzy.engine.graphics;

import lombok.CustomLog;
import lombok.Getter;
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
    
    @Getter
    private long id;
    @Getter
    private String title;
    @Getter
    private int width, height;
    
    @Getter
    private float aspectRatio;
    
    /**
     * @param title Title of the window
     * @param width Width of the window
     * @param height Height of the window
     */
    public Window(String title, int width, int height){
        this.title = title;
        this.width = width;
        this.height = height;
        this.aspectRatio = ((float) this.width)/((float) this.height);
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
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
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
        setClearColor(new Color(0.5f, 0.5f, 0.5f, 1.0f));
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
    
    public void setWidth(int width){
        this.width = width;
        this.height = (int) (this.width/this.aspectRatio);
        glfwSetWindowSize(this.id, this.width, this.height);
    }
    
    public void setHeight(int height){
        this.height = height;
        this.width = (int) (this.height*this.aspectRatio);
        glfwSetWindowSize(this.id, this.width, this.height);
    }
}
