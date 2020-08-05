package com.quartzy.engine.utils;

import com.quartzy.engine.audio.SoundManager;
import com.quartzy.engine.graphics.TextureManager;
import lombok.CustomLog;
import lombok.Getter;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

@CustomLog
public class ResourceManager{
    
    private HashMap<String, Resource> resources = new HashMap<>();
    private boolean autoLoadSounds;
    private boolean autoLoadTextures;
    @Getter
    private SoundManager soundManager;
    @Getter
    private TextureManager textureManager;
    
    private File gameDir;
    
    /**
     * @param autoLoadSounds Should add sounds to the sound manager
     * @param autoLoadTextures Should add textures to the texture manager
     * @param soundManager The sound manager
     * @param textureManager The texture manager
     */
    public ResourceManager(boolean autoLoadSounds, boolean autoLoadTextures, SoundManager soundManager, TextureManager textureManager){
        this.autoLoadSounds = autoLoadSounds;
        this.soundManager = soundManager;
        this.autoLoadTextures = autoLoadTextures;
        this.textureManager = textureManager;
        gameDir = Paths.get(".").toFile();
    }
    
    public ResourceManager(boolean autoLoadSounds, boolean autoLoadTextures, SoundManager soundManager, TextureManager textureManager, File gameDir){
        this.autoLoadSounds = autoLoadSounds;
        this.autoLoadTextures = autoLoadTextures;
        this.soundManager = soundManager;
        this.textureManager = textureManager;
        this.gameDir = gameDir;
    }
    
    /**
     * @param autoLoadTextures Should add textures to the texture manager
     * @param textureManager The texture manager
     */
    public ResourceManager(boolean autoLoadTextures, TextureManager textureManager){
        this.autoLoadTextures = autoLoadTextures;
        this.textureManager = textureManager;
    }
    
    /**
     * @param autoLoadSounds Should add sounds to the sound manager
     * @param soundManager The sound manager
     */
    public ResourceManager(boolean autoLoadSounds, SoundManager soundManager){
        this.autoLoadSounds = autoLoadSounds;
        this.soundManager = soundManager;
    }
    
    public ResourceManager(){
        this(false,false, null, null);
    }
    
    /**
     * Loads the resource from the game directory that is made from the game name
     * @param resource Relative path to the file
     * @return The loaded resource
     */
    public Resource addResource(String resource){
        if(resource==null){
            log.warning("Resource provided was null", new NullPointerException("Resource was null"));
            return null;
        }
        String name = resource.substring(resource.lastIndexOf('/')+1, resource.lastIndexOf('.'));
        Resource resource111 = resources.get(name);
        if(resource111!=null){
            log.warning("A resource by the name %s already exists and so will not be loaded again", name);
            return resource111;
        }
        String ext = resource.substring(resource.lastIndexOf('.')+1);
        ResourceType type = ResourceType.UNKNOWN;
        for(ResourceType value : ResourceType.values()){
            if(Arrays.asList(value.extensions).contains(ext)){
                type = value;
                break;
            }
        }
        File file = new File(this.gameDir.getAbsolutePath() + File.separator + resource);
        if(!file.exists()){
            String dir = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator));
            File file1 = new File(dir);
            if(!file1.exists()) file1.mkdirs();
        }
        Resource value = new Resource(file, name, type);
        resources.put(name, value);
        if(autoLoadSounds && value.getType()==ResourceType.SOUND){
            soundManager.loadSound(value);
        }
        if(autoLoadTextures && value.getType()==ResourceType.IMAGE){
            textureManager.addTexture(value);
        }
        return value;
    }
    
    /**
     * Adds multiple resources at the same time
     * @param resources Resources
     */
    public void addResources(String... resources){
        for(String resource : resources){
            addResource(resource);
        }
    }
    
    public Resource getResource(String name){
        return resources.get(name);
    }
}
