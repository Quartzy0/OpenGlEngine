package com.quartzy.engine.graphics;

import com.quartzy.engine.utils.Resource;
import com.quartzy.engine.utils.ResourceType;
import lombok.CustomLog;
import lombok.Getter;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.stb.STBImage.*;

@CustomLog
public class Texture{
    
    @Getter
    private int id, width, height;
    
    @Getter
    private Resource resource;
    
    public Texture(){
        this.resource = null;
        this.id = -1;
        this.width = -1;
        this.height = -1;
    }
    
    //Used by framebuffer
    public Texture(int width, int height){
        this.width = width;
        this.height = height;
        
        this.id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.id);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }
    
    /**
     * Initializes the texture and loads it from the resource file
     * @param resource The resource for the texture file
     */
    public Texture(Resource resource){
        if(resource.getType()!= ResourceType.IMAGE){
            log.severe("Resource must be an image!", new IllegalArgumentException("Resource must be an image"));
            return;
        }
        this.resource = resource;
        this.id = glGenTextures();
        log.info("Loading texture " + resource.getName());
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
    
            stbi_set_flip_vertically_on_load(true);
            ByteBuffer image = stbi_load(resource.getFile().getAbsolutePath(), w, h, comp, 4);
            if (image == null) {
                log.severe("Failed to load a texture file %s: " + stbi_failure_reason(), resource.getName());
                return;
            }
    
            this.width = w.get();
            this.height = h.get();
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        }
    }
    
    /**
     * Loads a texture straight from a byte buffer
     * @param image ByteBuffer with the image in it
     * @param width The width of the image
     * @param height The height of the image
     */
    public Texture(ByteBuffer image, int width, int height){
        this.id = glGenTextures();
        this.width = width;
        this.height = height;
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
    }
    
    /**
     * Deletes the texture from memory
     */
    public void dispose(){
        glDeleteTextures(id);
    }
    
    /**
     * Binds the texture to the slot 0
     */
    public void bind(){
        this.bind(0);
    }
    
    /**
     * Binds the texture to a specific texture slot
     * @param slot Slot to bind it to
     */
    public void bind(int slot){
        glActiveTexture(GL_TEXTURE0+slot);
        glBindTexture(GL_TEXTURE_2D, id);
    }
    
    public void unbind(){
        glBindTexture(GL_TEXTURE_2D, -1);
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        Texture texture = (Texture) o;
        
        if(id != texture.id) return false;
        if(width != texture.width) return false;
        return height == texture.height;
    }
    
    @Override
    public int hashCode(){
        int result = id;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }
}
