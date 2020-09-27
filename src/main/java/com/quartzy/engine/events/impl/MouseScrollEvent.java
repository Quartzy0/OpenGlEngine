package com.quartzy.engine.events.impl;

import com.quartzy.engine.events.Event;
import lombok.Getter;

public class MouseScrollEvent extends Event{
    
    @Getter
    private double xOffset, yOffset;
    
    public MouseScrollEvent(double xOffset, double yOffset, long windowId){
        super(EventType.MOUSE, windowId);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        
        MouseScrollEvent that = (MouseScrollEvent) o;
        
        if(Double.compare(that.xOffset, xOffset) != 0) return false;
        return Double.compare(that.yOffset, yOffset) == 0;
    }
    
    @Override
    public int hashCode(){
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(xOffset);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yOffset);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    
    @Override
    public String toString(){
        return "MouseScrollEvent{" +
                "xOffset=" + xOffset +
                ", yOffset=" + yOffset +
                "} " + super.toString();
    }
    
}
