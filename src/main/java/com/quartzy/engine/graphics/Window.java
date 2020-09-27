package com.quartzy.engine.graphics;

import com.quartzy.engine.Client;
import com.quartzy.engine.events.impl.*;
import com.quartzy.engine.input.Mods;
import lombok.CustomLog;
import lombok.Getter;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import java.io.File;

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
        
        glfwSetWindowPosCallback(id, new GLFWWindowPosCallback(){
            @Override
            public void invoke(long window, int xpos, int ypos){
                Client.getInstance().getEventManager().triggerEvent(new WindowMoveEvent(xpos, ypos));
            }
        });
        
        glfwSetDropCallback(id, new GLFWDropCallback(){
            @Override
            public void invoke(long window, int count, long names){
                File[] files = new File[count];
                for(int i = 0; i < files.length; i++){
                    files[i] = new File(getName(names, i));
                }
                Client.getInstance().getEventManager().triggerEvent(new FileDropEvent(files));
            }
        });
        
        glfwSetCursorEnterCallback(id, new GLFWCursorEnterCallback(){
            @Override
            public void invoke(long window, boolean entered){
                if(entered){
                    Client.getInstance().getEventManager().triggerEvent(new CursorEnterEvent());
                }else {
                    Client.getInstance().getEventManager().triggerEvent(new CursorLeaveEvent());
                }
            }
        });
    
        glfwSetKeyCallback(id, new GLFWKeyCallback(){
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods){
                Mods mods1 = new Mods((mods & GLFW_MOD_CONTROL)==GLFW_MOD_CONTROL, (mods & GLFW_MOD_ALT)==GLFW_MOD_ALT, (mods & GLFW_MOD_SHIFT)==GLFW_MOD_SHIFT, (mods & GLFW_MOD_NUM_LOCK)==GLFW_MOD_NUM_LOCK, (mods & GLFW_MOD_SUPER)==GLFW_MOD_SUPER, (mods & GLFW_MOD_CAPS_LOCK)==GLFW_MOD_CAPS_LOCK);
                if(action == GLFW_PRESS){
                    Client.getInstance().getEventManager().triggerEvent(new KeyPressedEvent(key, mods1));
                }else if(action == GLFW_RELEASE){
                    Client.getInstance().getEventManager().triggerEvent(new KeyReleasedEvent(key, mods1));
                }
            }
        });
        glfwSetCharCallback(id, new GLFWCharCallback(){
            @Override
            public void invoke(long window, int codepoint){
                Client.getInstance().getEventManager().triggerEvent(new KeyTypedEvent(codepoint));
            }
        });
    
        glfwSetMouseButtonCallback(id, new GLFWMouseButtonCallback(){
            @Override
            public void invoke(long window, int button, int action, int mods){
                Mods mods1 = new Mods((mods & GLFW_MOD_CONTROL)==GLFW_MOD_CONTROL, (mods & GLFW_MOD_ALT)==GLFW_MOD_ALT, (mods & GLFW_MOD_SHIFT)==GLFW_MOD_SHIFT, (mods & GLFW_MOD_NUM_LOCK)==GLFW_MOD_NUM_LOCK, (mods & GLFW_MOD_SUPER)==GLFW_MOD_SUPER, (mods & GLFW_MOD_CAPS_LOCK)==GLFW_MOD_CAPS_LOCK);
                if(action==GLFW_PRESS){
                    Client.getInstance().getEventManager().triggerEvent(new MouseButtonPressedEvent(button, mods1));
                }else if(action==GLFW_RELEASE){
                    Client.getInstance().getEventManager().triggerEvent(new MouseButtonReleasedEvent(button, mods1));
                }
            }
        });
        glfwSetCursorPosCallback(id, new GLFWCursorPosCallback(){
            @Override
            public void invoke(long window, double xpos, double ypos){
                Client.getInstance().getEventManager().triggerEvent(new CursorMoveEvent(xpos, ypos));
            }
        });
    
        glfwSetScrollCallback(id, new GLFWScrollCallback(){
            @Override
            public void invoke(long window, double xoffset, double yoffset){
                Client.getInstance().getEventManager().triggerEvent(new MouseScrollEvent(xoffset, yoffset));
            }
        });
        
        glfwSetWindowSizeCallback(id, new GLFWWindowSizeCallback(){
            @Override
            public void invoke(long window, int width1, int height1){
                Client.getInstance().getEventManager().triggerEvent(new WindowResizeEvent(width1, height1, width, height));
            }
        });
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
