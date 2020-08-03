package com.quartzy.engine.utils;

/**
 * Type of resource
 */
public enum ResourceType{
    IMAGE("png", "png", "jpg", "jpeg", "tga", "bmp", "psd", "gif", "hdr", "pic", "pnm"),
    SOUND("ogg", "ogg"),
    FONT("ttf", "ttf"),
    WORLD_FILE("wrld", "wrld"),
    SHADER("glsl", "glsl", "shader"),
    UNKNOWN("*");
    
    public final String[] extensions;
    public final String defaultExtension;
    
    ResourceType(String defaultExtension, String... extensions){
        this.extensions = extensions;
        this.defaultExtension = defaultExtension;
    }
}
