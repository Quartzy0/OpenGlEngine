package com.quartzy.engine.audio;

import lombok.Getter;

public class Sound{
    
    @Getter
    private int bufferPointer;
    @Getter
    private String name;
    
    /**
     * @param bufferPointer The pointer to the sound buffer
     * @param name The name of the sound. This is used in the SoundManager to search for a specific sound
     */
    public Sound(int bufferPointer, String name){
        this.bufferPointer = bufferPointer;
        this.name = name;
    }
}
