package com.quartzy.engine.utils;

import lombok.Getter;

import java.io.File;

public class Resource{
    
    @Getter
    private File file;
    @Getter
    private String name;
    @Getter
    private ResourceType type;
    @Getter
    private String relativePath;
    
    /**
     * @param file File of the resource
     * @param name Name of the resource
     * @param type Type of the resource
     */
    public Resource(File file, String name, ResourceType type, String relativePath){
        this.file = file;
        this.name = name;
        this.type = type;
        this.relativePath = relativePath;
    }
}
