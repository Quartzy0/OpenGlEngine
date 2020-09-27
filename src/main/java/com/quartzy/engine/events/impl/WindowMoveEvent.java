package com.quartzy.engine.events.impl;

import com.quartzy.engine.events.Event;
import lombok.Getter;

public class WindowMoveEvent extends Event{
    
    @Getter
    private int newX, newY;
    
    public WindowMoveEvent(int newX, int newY){
        super(EventType.WINDOW);
        this.newX = newX;
        this.newY = newY;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        
        WindowMoveEvent that = (WindowMoveEvent) o;
        
        if(newX != that.newX) return false;
        return newY == that.newY;
    }
    
    @Override
    public int hashCode(){
        int result = super.hashCode();
        result = 31 * result + newX;
        result = 31 * result + newY;
        return result;
    }
    
    @Override
    public String toString(){
        return "WindowMoveEvent{" +
                "newX=" + newX +
                ", newY=" + newY +
                "} " + super.toString();
    }
}
