package com.quartzy.engine.graphics;

import com.quartzy.engine.utils.Resource;
import com.quartzy.engine.utils.ResourceType;

import java.util.HashMap;

public class TextureManager{
    
    private HashMap<String, Texture> textures = new HashMap<>();
    
    /**
     * Saves the texture to a cache where it can later be called from by name
     * @param name Name of the texture
     * @param texture Texture to be added
     */
    public void addTexture(String name, Texture texture){
        textures.put(name, texture);
    }
    
    /**
     * Saves the texture to a cache where it can later be called from by name
     * @param resource The resource file for the texture
     */
    public void addTexture(Resource resource){
        if(resource.getType() != ResourceType.IMAGE){
            throw new IllegalArgumentException("The resource provided is not an image");
        }
        textures.put(resource.getName(), new Texture(resource));
    }
    
    /**
     * Looks up the name of the texture in the cache and returns it
     * @param name Name of the texture
     * @return Texture that was stored in the cache by that name. Return <code>null</code> if no texture was found.
     */
    public Texture getTexture(String name){
        return textures.get(name);
    }
}
