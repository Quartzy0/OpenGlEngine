package com.quartzy.engine.events.impl;

import com.quartzy.engine.events.Event;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;

public class FileDropEvent extends Event{
    
    @Getter
    private File[] files;
    
    public FileDropEvent(File[] files){
        super(EventType.WINDOW);
        this.files = files;
    }
    
    @Override
    public String toString(){
        return "FileDropEvent{" +
                "files=" + Arrays.toString(files) +
                "} " + super.toString();
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        
        FileDropEvent that = (FileDropEvent) o;
        
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(files, that.files);
    }
    
    @Override
    public int hashCode(){
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(files);
        return result;
    }
}
