package com.quartzy.engine.graphics;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowProperties{
    
    @Getter
    @Setter
    private int width, height;
    @Getter
    private float contentScaleX, contentScaleY;
    
    @Getter
    @Setter
    private int maxWindowWidth, maxWindowHeight, minWindowWidth, minWindowHeight;
    @Getter
    @Setter
    private int aspectRatioWidth, aspectRatioHeight;
    
    @Getter
    @Setter
    private int windowX, windowY;
    
    @Getter
    @Setter
    private String title;
    
    @Getter
    @Setter
    private WindowIcon[] icons;
    @Getter
    @Setter
    private boolean fullscreen;
    private long monitor;
    
    @Getter
    @Setter
    private boolean minimized;
    @Getter
    @Setter
    private boolean maximized;
    
    @Getter
    @Setter
    private boolean hidden;
    
    @Getter
    @Setter
    private boolean inputFocus;
    
    @Getter
    @Setter
    private float windowOpacity;
    
    @Getter
    private int maxTextureSlots;
    
    public WindowProperties(int width, int height, float contentScaleX, float contentScaleY, int maxWindowWidth, int maxWindowHeight, int minWindowWidth, int minWindowHeight, int aspectRatioWidth, int aspectRatioHeight, int windowX, int windowY, String title, WindowIcon[] icons, long monitor, boolean minimized, boolean maximized, boolean hidden, boolean inputFocus, float windowOpacity, boolean fullscreen, int maxTextureSlots){
        this.width = width;
        this.height = height;
        this.contentScaleX = contentScaleX;
        this.contentScaleY = contentScaleY;
        this.maxWindowWidth = maxWindowWidth;
        this.maxWindowHeight = maxWindowHeight;
        this.minWindowWidth = minWindowWidth;
        this.minWindowHeight = minWindowHeight;
        this.aspectRatioWidth = aspectRatioWidth;
        this.aspectRatioHeight = aspectRatioHeight;
        this.windowX = windowX;
        this.windowY = windowY;
        this.title = title;
        this.icons = icons;
        this.monitor = monitor;
        this.minimized = minimized;
        this.maximized = maximized;
        this.hidden = hidden;
        this.inputFocus = inputFocus;
        this.windowOpacity = windowOpacity;
        this.fullscreen = fullscreen;
        this.maxTextureSlots = maxTextureSlots;
    }
    
    public static WindowProperties getProperties(long windowId, String title, int maxWindowWidth, int maxWindowHeight, int minWindowWidth, int minWindowHeight, int aspectRatioWidth, int aspectRatioHeight, WindowIcon[] icons){
        int width = -1, height = -1;
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            glfwGetWindowSize(windowId, w, h);
            width = w.get(0);
            height = h.get(0);
        }
        
        float contentScaleX = -1, contentScaleY = -1;
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer w = stack.mallocFloat(1);
            FloatBuffer h = stack.mallocFloat(1);
            glfwGetWindowContentScale(windowId, w, h);
            contentScaleX = w.get(0);
            contentScaleY = h.get(0);
        }
    
        int posX = -1, posY = -1;
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            glfwGetWindowPos(windowId, w, h);
            posX = w.get(0);
            posY = h.get(0);
        }
        
        long monitor = glfwGetWindowMonitor(windowId);
        
        boolean iconofied = glfwGetWindowAttrib(windowId, GLFW_ICONIFIED) == GLFW_TRUE;
        boolean maximized = glfwGetWindowAttrib(windowId, GLFW_MAXIMIZED) == GLFW_TRUE;
        boolean visible = glfwGetWindowAttrib(windowId, GLFW_VISIBLE) == GLFW_TRUE;
        boolean inputFocus = glfwGetWindowAttrib(windowId, GLFW_FOCUSED) == GLFW_TRUE;
        
        float opacity = glfwGetWindowOpacity(windowId);
    
        int maxTextureSlots = 8;
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer buffer = stack.mallocInt(1);
            GL20.glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, buffer);
            maxTextureSlots = Math.min(buffer.get(), 32);
        }
        
        return new WindowProperties(width, height, contentScaleX, contentScaleY, maxWindowWidth, maxWindowHeight, minWindowWidth, minWindowHeight,
                aspectRatioWidth, aspectRatioHeight, posX, posY, title, icons, monitor, iconofied, maximized, visible, inputFocus, opacity, monitor!=NULL, maxTextureSlots);
    }
    
    public static WindowProperties getProperties(Window window){
        return WindowProperties.getProperties(window.getId(), window.getTitle(), GLFW_DONT_CARE, GLFW_DONT_CARE, GLFW_DONT_CARE, GLFW_DONT_CARE, GLFW_DONT_CARE, GLFW_DONT_CARE, null);
    }
    
    public static void setProperties(WindowProperties properties, long windowId){
        glfwSetWindowSize(windowId, properties.width, properties.height);
        glfwSetWindowSizeLimits(windowId, properties.minWindowWidth, properties.minWindowHeight, properties.maxWindowWidth, properties.maxWindowHeight);
        glfwSetWindowAspectRatio(windowId, properties.aspectRatioWidth, properties.aspectRatioHeight);
        glfwSetWindowPos(windowId, properties.windowX, properties.windowY);
        glfwSetWindowTitle(windowId, properties.title);
        if(properties.icons!=null){
            glfwSetWindowIcon(windowId, WindowIcon.arrayToBuffer(properties.icons));
        }else {
            glfwSetWindowIcon(windowId, null);
        }
        if(properties.fullscreen){
            GLFWVidMode glfwVidMode = glfwGetVideoMode(properties.monitor);
            glfwSetWindowMonitor(windowId, properties.monitor, 0, 0, glfwVidMode.width(), glfwVidMode.height(), glfwVidMode.refreshRate());
        }else {
            glfwSetWindowMonitor(windowId, NULL, properties.windowX, properties.windowY, properties.width, properties.height, 0);
        }
        
        if(properties.minimized){
            glfwIconifyWindow(windowId);
        }else if(properties.maximized){
            glfwMaximizeWindow(windowId);
        }else {
            glfwRestoreWindow(windowId);
        }
        
        if(properties.hidden){
            glfwHideWindow(windowId);
        }else {
            glfwShowWindow(windowId);
        }
        
        if(properties.inputFocus && glfwGetWindowAttrib(windowId, GLFW_FOCUSED)!=GLFW_TRUE){
            glfwRequestWindowAttention(windowId);
        }
        glfwSetWindowOpacity(windowId, properties.windowOpacity);
    }
}
