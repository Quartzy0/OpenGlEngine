package com.quartzy.engine.utils;

import java.io.File;

public class Resource{
    
    private File file;
    private String name;
    private ResourceType type;
    
    /**
     * @param file File of the resource
     * @param name Name of the resource
     * @param type Type of the resource
     */
    public Resource(File file, String name, ResourceType type){
        this.file = file;
        this.name = name;
        this.type = type;
    }
    
    public File getFile(){
        return file;
    }
    
    public String getName(){
        return name;
    }
    
    public ResourceType getType(){
        return type;
    }
    
    public String getFileName(){
        return file.getName();
    }
}
