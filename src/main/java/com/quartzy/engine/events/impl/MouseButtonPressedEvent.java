package com.quartzy.engine.events.impl;

import com.quartzy.engine.events.Event;
import com.quartzy.engine.input.Mods;
import lombok.Getter;

public class MouseButtonPressedEvent extends Event{
    
    @Getter
    private int button;
    @Getter
    private Mods mods;
    
    public MouseButtonPressedEvent(int button, Mods mods, long windowId){
        super(EventType.MOUSE, windowId);
        this.button = button;
        this.mods = mods;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        
        MouseButtonPressedEvent that = (MouseButtonPressedEvent) o;
        
        if(button != that.button) return false;
        return mods != null ? mods.equals(that.mods) : that.mods == null;
    }
    
    @Override
    public int hashCode(){
        int result = super.hashCode();
        result = 31 * result + button;
        result = 31 * result + (mods != null ? mods.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString(){
        return "MouseButtonPressedEvent{" +
                "button=" + button +
                ", mods=" + mods +
                "} " + super.toString();
    }
}
