package com.quartzy.engine.graphics;

import com.quartzy.engine.math.Vector2f;
import lombok.Getter;
import lombok.Setter;

import static org.lwjgl.opengl.GL30.*;

public class Framebuffer{
    
    @Getter
    @Setter
    private int fboID;
    @Getter
    @Setter
    private Texture texture;
    
    @Getter
    private Vector2f dimensions;
    
    public Framebuffer(int width, int height){
        this.dimensions = new Vector2f(width, height);
        this.init();
    }
    
    public Framebuffer(int width, int height, boolean init){
        this.dimensions = new Vector2f(width, height);
        if(init){
            this.init();
        }
    }
    
    public void init(){
        int width = (int) this.dimensions.x;
        int height = (int) this.dimensions.y;
        
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    
        this.texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getId(), 0);
    
        int rboId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboId);
    
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            throw new RuntimeException("Framebuffer could not be constructed!");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    public void bind(){
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    }
    
    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
