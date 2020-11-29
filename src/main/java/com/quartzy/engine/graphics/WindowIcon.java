package com.quartzy.engine.graphics;

import com.quartzy.engine.utils.Resource;
import com.quartzy.engine.utils.ResourceType;
import lombok.CustomLog;
import lombok.Getter;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.stbi_load;

@CustomLog
public class WindowIcon{

    @Getter
    private ByteBuffer image;
    @Getter
    private int width, height;
    
    public WindowIcon(ByteBuffer image, int width, int height){
        this.image = image;
        this.width = width;
        this.height = height;
    }
    
    public GLFWImage toGLFWImage(){
        GLFWImage image = GLFWImage.malloc();
        image.set(this.width, this.height, this.image);
        return image;
    }
    
    public static GLFWImage.Buffer arrayToBuffer(WindowIcon... icons){
        if(icons==null || icons.length==0 || icons[0] == null){
            log.warning("Empty/Null array was passed into function");
            return null;
        }
        GLFWImage.Buffer imageBuffer = GLFWImage.malloc(icons.length);
        for(int i = 0; i < icons.length; i++){
            imageBuffer.put(i, icons[i].toGLFWImage());
        }
        return imageBuffer;
    }
    
    public static WindowIcon loadIcon(Resource imageRes){
        if(imageRes.getType()!= ResourceType.IMAGE){
            log.severe("Resource %s (%s) not an image", new RuntimeException("Resource not an image"), imageRes.getName(), imageRes.getFile().getAbsolutePath());
            return null;
        }
        ByteBuffer image;
        int width, heigh;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer comp = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
        
            image = stbi_load(imageRes.getFile().getAbsolutePath(), w, h, comp, 4);
            if (image == null) {
                log.severe("Couldn't load window icon %s (%s)", new RuntimeException("Error loading image"), imageRes.getName(), imageRes.getFile().getAbsolutePath());
                return null;
            }
            width = w.get();
            heigh = h.get();
        }
        
        return new WindowIcon(image, width, heigh);
    }
}
