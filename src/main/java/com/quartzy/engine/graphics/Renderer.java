package com.quartzy.engine.graphics;

import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.components.CameraComponent;
import com.quartzy.engine.ecs.components.CustomRenderComponent;
import com.quartzy.engine.math.Matrix4f;
import com.quartzy.engine.math.Vector2f;
import com.quartzy.engine.network.NetworkManager;
import com.quartzy.engine.network.Side;
import com.quartzy.engine.utils.Resource;
import com.quartzy.engine.world.World;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import com.quartzy.engine.text.Font;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL45.*;

@CustomLog
public class Renderer{
    
    private VertexArray vao;
    private VertexBuffer vbo;
    @Getter
    private ShaderProgram program;
    @Getter
    private ShaderProgram uiProgram;
    
    private boolean drawingUi;
    
    private FloatBuffer vertices;
    private int numVertices;
    private boolean drawing;
    
    private Font font;
    
    private Window window;
    
    @Getter
    private int maxTextureSlots, maxLightsPerDrawCall;
    
    @Getter
    @Setter
    private CameraComponent mainCamera;
    private GLFWWindowSizeCallbackI sizeCallback;
    
    @Getter
    @Setter
    private Vector2f viewportPosition, viewportDimensions;
    
    @Getter
    @Setter
    private Framebuffer framebuffer;
    
    /**
     * Initializes the renderer. It loads the default shaders from the default resource directory
     */
    public void init(Resource vertex, Resource fragment, Window window){
        glEnable(GL_DEBUG_OUTPUT);
        GL45.glDebugMessageCallback(new GLDebugMessageCallback(){
            @Override
            public void invoke(int source, int type, int id, int severity, int length, long message, long userParam){
                String messageS = getMessage(length, message);
                log.openGLLog(source, type, id, severity, messageS, userParam);
            }
        }, 0L);
        
        maxLightsPerDrawCall = 10;
        log.info("Initializing renderer");
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer buffer = stack.mallocInt(1);
            GL20.glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, buffer);
            maxTextureSlots = Math.min(buffer.get(), 32);
        }
        log.info("Detected %d usable texture slots for fragment shader", maxTextureSlots);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        vao = new VertexArray();
        vao.bind();
        
        vbo = new VertexBuffer();
        vbo.bind(GL_ARRAY_BUFFER);
        
        vertices = MemoryUtil.memAllocFloat(4096);
    
        long size = vertices.capacity() * Float.BYTES;
        vbo.uploadData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);
    
        uiProgram = new ShaderProgram(vertex, fragment);
        uiProgram.addDefinition("MAX_TEXTURES", maxTextureSlots);
    
        uiProgram.compileShaders();
        
        program = new ShaderProgram(vertex, fragment);
        program.addDefinition("MAX_TEXTURES", maxTextureSlots);
        program.addDefinition("MAX_LIGHTS", maxLightsPerDrawCall);
        program.addDefinition("LIGHTING_ENABLED", 1);
        
        program.compileShaders();
    
        this.window = window;
        
        if(this.viewportDimensions==null){
            this.viewportDimensions = new Vector2f(1, 1);
        }
        if(this.viewportPosition==null){
            this.viewportPosition = new Vector2f(0, 0);
        }
    
        sizeCallback = (window1, width, height) -> {
            updateViewport(width, height);
            if(NetworkManager.INSTANCE.getSide()==Side.CLIENT && World.getCurrentWorld()!=null){
                HashMap<Short, CustomRenderComponent> allCustomRenderers = World.getCurrentWorld().getEcsManager().getAllEntitiesWithComponent(CustomRenderComponent.class);
                if(allCustomRenderers != null && !allCustomRenderers.isEmpty()){
                    for(CustomRenderComponent value : allCustomRenderers.values()){
                        value.resizeWindow(width, height);
                    }
                }
            }
        };
        glfwSetWindowSizeCallback(window.getId(), sizeCallback);
        updateViewport(window.getWidth(), window.getHeight());
        
        specifyVertexAttributes();
        
        Matrix4f model = new Matrix4f();
        Matrix4f view = new Matrix4f();
        Matrix4f projection = Matrix4f.orthographic(0f, window.getWidth(), 0f, window.getHeight(), -1f, 1f);
    
        uiProgram.bind();
        uiProgram.setUniform("model", model);
        uiProgram.setUniform("view", view);
        uiProgram.setUniform("projection", projection);
        int[] texturesArray = new int[maxTextureSlots];
        for(int i = 0; i < texturesArray.length; i++){
            texturesArray[i] = i;
        }
        uiProgram.setUniform("textures", texturesArray);
        
        program.bind();
        program.setUniform("model", model);
        program.setUniform("view", view);
        program.setUniform("projection", projection);
        program.setUniform("textures", texturesArray);
    
        font = new Font(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 16));
    }
    
    private void updateViewport(int windowWidth, int windowHeight){
        int newWidth = (int) (windowWidth * this.viewportDimensions.x);
        int newHeight = (int) (windowHeight * this.viewportDimensions.y);
        int x = (int) (this.viewportPosition.x * windowWidth);
        int y = (int) (this.viewportPosition.y * windowHeight);
        glViewport(x, y, newWidth, newHeight);
        CameraComponent mainCamera = getMainCamera();
        if(mainCamera!=null){
            mainCamera.updateViewport(newWidth, newHeight, 0, 0);
        }
        else{
            Matrix4f projectionMatrix = Matrix4f.orthographic(0, newWidth, 0, newHeight, -1f, 1f);
            if(NetworkManager.INSTANCE.getSide()== Side.CLIENT){
                Client.getInstance().getRenderer().setUniformsUI(new Matrix4f(), new Matrix4f(), projectionMatrix);
                Client.getInstance().getRenderer().setUniforms(new Matrix4f(), new Matrix4f(), projectionMatrix);
            }
        }
        
        this.window.updateViewport(windowWidth, windowHeight);
    }
    
    public void refreshViewport(){
        this.updateViewport(this.window.getWidth(), this.window.getHeight());
    }
    
    public void setUniforms(Matrix4f model, Matrix4f view, Matrix4f projection){
        program.bind();
        program.setUniform("model", model);
        program.setUniform("view", view);
        program.setUniform("projection", projection);
    }
    
    public void setUniformsUI(Matrix4f model, Matrix4f view, Matrix4f projection){
        uiProgram.bind();
        uiProgram.setUniform("model", model);
        uiProgram.setUniform("view", view);
        uiProgram.setUniform("projection", projection);
    }
    
    /**
     * Clears the screen with the options GL_COLOR_BUFFER_BIT and GL_DEPTH_BUFFER_BIT
     */
    public void clear(){
        if(this.framebuffer!=null)
            this.framebuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        if(this.framebuffer!=null)
            this.framebuffer.unbind();
    }
    
    /**
     * Starts the rendering batch
     */
    public void begin(){
        if(drawing){
            log.warning("Renderer is already drawing!", new IllegalStateException("Renderer is already drawing!"));
        }
        drawing = true;
        numVertices = 0;
    }
    
    /**
     * Ends and draws the current rendering batch
     */
    public void end() {
        if (!drawing) {
            log.warning("Renderer isn't drawing!", new IllegalStateException("Renderer isn't drawing!"));
        }
        drawing = false;
        flush();
    }
    
    /**
     * Draws the current rendering batch
     */
    private void flush(){
        if (numVertices > 0) {
            vertices.flip();
        
            if (vao != null) {
                vao.bind();
            } else {
                vbo.bind(GL_ARRAY_BUFFER);
                specifyVertexAttributes();
            }
            
            if(drawingUi){
                uiProgram.bind();
            }else {
                program.bind();
            }
            if(this.framebuffer!=null)
                this.framebuffer.bind();
        
            /* Upload the new vertex data */
            vbo.bind(GL_ARRAY_BUFFER);
            vbo.uploadSubData(GL_ARRAY_BUFFER, 0, vertices);
        
            /* Draw batch */
            glDrawArrays(GL_TRIANGLES, 0, numVertices);
    
            if(this.framebuffer!=null)
                this.framebuffer.unbind();
        
            /* Clear vertex data for next batch */
            vertices.clear();
            numVertices = 0;
        }
    }
    
    /**
     * Draws text on the screen
     * @param s Text to be drawn
     * @param x X position
     * @param y Y position
     * @param c Color of the text
     */
    public void drawString(String s, float x, float y, Color c){
        drawingUi = true;
        font.drawText(this, s, x, y, c);
        drawingUi = false;
    }
    
    /**
     * Draws text on the screen with the color white
     * @param s text to be drawn
     * @param x X position
     * @param y Y position
     */
    public void drawString(String s, float x, float y){
        this.drawString(s, x, y, Color.WHITE);
    }
    
    /**
     * Draws a texture on the screen
     * @param texture Texture to be drawn
     * @param x X position
     * @param y Y position
     */
    public void drawTextureRegion(Texture texture, float x, float y, int textureIndex){
        drawTextureRegion(x, y, texture.getWidth(), texture.getHeight(), Color.WHITE, textureIndex);
    }
    
    /**
     * Draws a texture on the screen tinted to the color specified
     * @param texture Texture to be drawn
     * @param x X position
     * @param y Y position
     * @param c Color of the texture
     */
    public void drawTextureRegion(Texture texture, float x, float y, Color c, int textureIndex){
        drawTextureRegion(x, y, texture.getWidth(), texture.getHeight(), c, textureIndex);
    }
    
    /**
     * Draws the bound texture to the screen
     * @param x X position
     * @param y Y position
     * @param width Width of the texture
     * @param height Height of the texture
     */
    public void drawTextureRegion(float x, float y, float width, float height, int textureIndex){
        drawTextureRegion(x, y, x+width, y+height, 0.0f, 0.0f, 1.0f, 1.0f, Color.WHITE, textureIndex);
    }
    
    /**
     * Draws the bound texture to the screen tinted with the color specified
     * @param x X position
     * @param y Y position
     * @param width Width of the texture
     * @param height Height of the texture
     * @param c Color of the texture
     */
    public void drawTextureRegion(float x, float y, float width, float height, Color c, int textureIndex){
        drawTextureRegion(x, y, x+width, y+height, 0.0f, 0.0f, 1.0f, 1.0f, c, textureIndex);
    }
    
    /**
     * Draws the bound texture onto the vertices tinted to the color specified
     * @param x1 Vertex 1
     * @param y1 Vertex 2
     * @param x2 Vertex 3
     * @param y2 Vertex 4
     * @param s1 Texture coordinate 1
     * @param t1 Texture coordinate 2
     * @param s2 Texture coordinate 3
     * @param t2 Texture coordinate 4
     * @param c Color
     */
    public void drawTextureRegion(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2, Color c, int textureIndex) {
        if (vertices.remaining() < 12 * 6) {
            flush();
        }
        
        float r = c.getRed();
        float g = c.getGreen();
        float b = c.getBlue();
        float a = c.getAlpha();
    
        vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1).put(textureIndex).put(0f).put(0f).put(1f);
        vertices.put(x1).put(y2).put(r).put(g).put(b).put(a).put(s1).put(t2).put(textureIndex).put(0f).put(0f).put(1f);
        vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2).put(textureIndex).put(0f).put(0f).put(1f);
    
        vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1).put(textureIndex).put(0f).put(0f).put(1f);
        vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2).put(textureIndex).put(0f).put(0f).put(1f);
        vertices.put(x2).put(y1).put(r).put(g).put(b).put(a).put(s2).put(t1).put(textureIndex).put(0f).put(0f).put(1f);
        
        numVertices += 6;
    }
    
    private void specifyVertexAttributes(){
        uiProgram.bind();
        uiProgram.setVertexAttribute("position", 2, 9 * Float.BYTES, 0);
        uiProgram.setVertexAttribute("color", 4, 9 * Float.BYTES, 2 * Float.BYTES);
        uiProgram.setVertexAttribute("texcoord", 2, 9 * Float.BYTES, 6 * Float.BYTES);
        uiProgram.setVertexAttribute("textureIndex", 1, 9 * Float.BYTES, 8 * Float.BYTES);
        
        program.bind();
        program.setVertexAttribute("position", 2, 12 * Float.BYTES, 0);
        program.setVertexAttribute("color", 4, 12 * Float.BYTES, 2 * Float.BYTES);
        program.setVertexAttribute("texcoord", 2, 12 * Float.BYTES, 6 * Float.BYTES);
        program.setVertexAttribute("textureIndex", 1, 12 * Float.BYTES, 8 * Float.BYTES);
        program.setVertexAttribute("normal", 3, 12 * Float.BYTES, 9 * Float.BYTES);
    }
    
    /**
     * Draws a region from the texture tinted to the color specified.
     * @param texture Texture to draw
     * @param x X position
     * @param y Y position
     * @param regX Texture region X
     * @param regY Texture region Y
     * @param width Texture region width
     * @param height Texture region height
     * @param color Color
     */
    public void drawTextureRegion(Texture texture, float x, float y, int regX, int regY, int width, int height, Color color, int textureIndex){
        drawTextureRegion(x, y, x + width, y + height, ((float) regX) / texture.getWidth(), ((float) regY) / texture.getHeight(), ((float) regX + width) / texture.getWidth(), ((float) regY + height) / texture.getHeight(), color, textureIndex);
    }
    
    /**
     * Clears all of the buffers and closes the program
     */
    public void dispose(){
        MemoryUtil.memFree(vertices);
        if (vao != null) {
            vao.delete();
        }
        vbo.delete();
        program.dispose();
        uiProgram.dispose();
        font.dispose();
    }
}
