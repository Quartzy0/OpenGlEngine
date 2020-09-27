package com.quartzy.engine.events.impl;

import com.quartzy.engine.events.Event;
import lombok.Getter;

public class CursorMoveEvent extends Event{
    
    @Getter
    public double x,y;
    
    public CursorMoveEvent(double x, double y, long windowId){
        super(EventType.MOUSE, windowId);
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString(){
        return "CursorMoveEvent{" +
                "x=" + x +
                ", y=" + y +
                "} " + super.toString();
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        
        CursorMoveEvent that = (CursorMoveEvent) o;
        
        if(Double.compare(that.x, x) != 0) return false;
        return Double.compare(that.y, y) == 0;
    }
    
    @Override
    public int hashCode(){
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
