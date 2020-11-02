package com.quartzy.engine.graphics;

import com.quartzy.engine.Client;
import com.quartzy.engine.input.Mods;
import com.quartzy.engine.layers.events.*;
import com.quartzy.engine.math.Vector2f;
import lombok.CustomLog;
import lombok.Getter;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    
    private boolean inited;
    
    private List<MouseDragInfo> pressTimes = new ArrayList<>();
    
    @Getter
    private float mouseX = 0, mouseY = 0;
    
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
    }
    
    /**
     * Creates the GLFW window with the title, width and height from the constructor
     */
    public void init(){
        if(inited){
            log.severe("Window already %s initialized", new RuntimeException("Window already initialized"), this.id);
        }
        
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
                Client.getInstance().getLayerStack().triggerEvent(new WindowMoveEvent(xpos, ypos, id));
            }
        });
        
        glfwSetDropCallback(id, new GLFWDropCallback(){
            @Override
            public void invoke(long window, int count, long names){
                File[] files = new File[count];
                for(int i = 0; i < files.length; i++){
                    files[i] = new File(getName(names, i));
                }
                Client.getInstance().getLayerStack().triggerEvent(new FileDropEvent(files, id));
            }
        });
        
        glfwSetCursorEnterCallback(id, new GLFWCursorEnterCallback(){
            @Override
            public void invoke(long window, boolean entered){
                if(entered){
                    Client.getInstance().getLayerStack().triggerEvent(new CursorEnterEvent(id));
                }else {
                    Client.getInstance().getLayerStack().triggerEvent(new CursorLeaveEvent(id));
                }
            }
        });
    
        glfwSetKeyCallback(id, new GLFWKeyCallback(){
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods){
                Mods mods1 = new Mods((mods & GLFW_MOD_CONTROL)==GLFW_MOD_CONTROL, (mods & GLFW_MOD_ALT)==GLFW_MOD_ALT, (mods & GLFW_MOD_SHIFT)==GLFW_MOD_SHIFT, (mods & GLFW_MOD_NUM_LOCK)==GLFW_MOD_NUM_LOCK, (mods & GLFW_MOD_SUPER)==GLFW_MOD_SUPER, (mods & GLFW_MOD_CAPS_LOCK)==GLFW_MOD_CAPS_LOCK, mods);
                if(action == GLFW_PRESS){
                    Client.getInstance().getLayerStack().triggerEvent(new KeyPressedEvent(key, mods1, id));
                }else if(action == GLFW_RELEASE){
                    Client.getInstance().getLayerStack().triggerEvent(new KeyReleasedEvent(key, mods1, id));
                }
            }
        });
        glfwSetCharCallback(id, new GLFWCharCallback(){
            @Override
            public void invoke(long window, int codepoint){
                Client.getInstance().getLayerStack().triggerEvent(new KeyTypedEvent(codepoint, id));
            }
        });
    
        glfwSetMouseButtonCallback(id, new GLFWMouseButtonCallback(){
            @Override
            public void invoke(long window, int button, int action, int mods){
                Mods mods1 = new Mods((mods & GLFW_MOD_CONTROL)==GLFW_MOD_CONTROL, (mods & GLFW_MOD_ALT)==GLFW_MOD_ALT, (mods & GLFW_MOD_SHIFT)==GLFW_MOD_SHIFT, (mods & GLFW_MOD_NUM_LOCK)==GLFW_MOD_NUM_LOCK, (mods & GLFW_MOD_SUPER)==GLFW_MOD_SUPER, (mods & GLFW_MOD_CAPS_LOCK)==GLFW_MOD_CAPS_LOCK, mods);
                if(action==GLFW_PRESS){
                    Client.getInstance().getLayerStack().triggerEvent(new MouseButtonPressedEvent(button, mods1, id, new Vector2f(mouseX, mouseY)));
                    MouseDragInfo e = new MouseDragInfo(button, System.currentTimeMillis());
                    pressTimes.add(e);
                }else if(action==GLFW_RELEASE){
                    Client.getInstance().getLayerStack().triggerEvent(new MouseButtonReleasedEvent(button, mods1, id, new Vector2f(mouseX, mouseY)));
                    for(int i = 0; i < pressTimes.size(); i++){
                        if(pressTimes.get(i).button==button){
                            pressTimes.remove(i);
                            break;
                        }
                    }
                }
            }
        });
        glfwSetCursorPosCallback(id, new GLFWCursorPosCallback(){
            @Override
            public void invoke(long window, double xpos, double ypos){
                Client.getInstance().getLayerStack().triggerEvent(new CursorMoveEvent(xpos, ypos, id));
                mouseX = (float) xpos;
                mouseY = (float) ypos;
                for(int i = 0; i < pressTimes.size(); i++){
                    MouseDragInfo mouseDragInfo = pressTimes.get(i);
                    if((System.currentTimeMillis()- mouseDragInfo.start)>=MouseDragEvent.dragDebounce*1000){
                        if(mouseDragInfo.startPos==null)mouseDragInfo.startPos = new Vector2f((float) xpos, (float) ypos);
                        Client.getInstance().getLayerStack().triggerEvent(new MouseDragEvent(mouseDragInfo.button, mouseDragInfo.startPos, window));
                    }else {
                        mouseDragInfo.startPos = new Vector2f((float) xpos, (float) ypos);
                    }
                }
            }
        });
    
        glfwSetScrollCallback(id, new GLFWScrollCallback(){
            @Override
            public void invoke(long window, double xoffset, double yoffset){
                Client.getInstance().getLayerStack().triggerEvent(new MouseScrollEvent(xoffset, yoffset, id));
            }
        });
        
        glfwSetWindowSizeCallback(id, new GLFWWindowSizeCallback(){
            @Override
            public void invoke(long window, int width1, int height1){
                Client.getInstance().getLayerStack().triggerEvent(new WindowResizeEvent(width1, height1, width, height, id));
            }
        });
        
        this.inited = true;
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
    
    private class MouseDragInfo{
        public int button;
        public long start;
        public Vector2f startPos;
    
        public MouseDragInfo(int button, long start){
            this.button = button;
            this.start = start;
            this.startPos = startPos;
        }
    }
}
