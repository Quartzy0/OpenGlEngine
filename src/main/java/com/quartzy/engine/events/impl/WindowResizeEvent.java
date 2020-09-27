package com.quartzy.engine.events.impl;

import com.quartzy.engine.events.Event;
import lombok.Getter;

public class WindowResizeEvent extends Event{
    @Getter
    private int newWidth, newHeight, oldWidth, oldHeight;
    
    public WindowResizeEvent(int newWidth, int newHeight, int oldWidth, int oldHeight){
        super(EventType.WINDOW);
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        
        WindowResizeEvent that = (WindowResizeEvent) o;
        
        if(newWidth != that.newWidth) return false;
        if(newHeight != that.newHeight) return false;
        if(oldWidth != that.oldWidth) return false;
        return oldHeight == that.oldHeight;
    }
    
    @Override
    public int hashCode(){
        int result = super.hashCode();
        result = 31 * result + newWidth;
        result = 31 * result + newHeight;
        result = 31 * result + oldWidth;
        result = 31 * result + oldHeight;
        return result;
    }
    
    @Override
    public String toString(){
        return "WindowResizeEvent{" +
                "newWidth=" + newWidth +
                ", newHeight=" + newHeight +
                ", oldWidth=" + oldWidth +
                ", oldHeight=" + oldHeight +
                "} " + super.toString();
    }
}
