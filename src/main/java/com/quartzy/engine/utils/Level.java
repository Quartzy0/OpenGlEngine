package com.quartzy.engine.utils;

/**
 * Level of logging
 */
public enum Level{
    
    INFO(1), WARNING(2), SEVERE(3), DEBUG(0);
    
    private int level;
    
    Level(int level){
        this.level = level;
    }
    
    public int getLevel(){
        return level;
    }
}
