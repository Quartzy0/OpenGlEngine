package com.quartzy.engine.layers.events;

import com.quartzy.engine.layers.Event;
import com.quartzy.engine.input.Mods;
import com.quartzy.engine.math.Vector2f;
import lombok.Getter;

public class MouseButtonReleasedEvent extends Event{
    
    @Getter
    private int button;
    @Getter
    private Mods mods;
    
    @Getter
    private Vector2f mousePos;
    
    public MouseButtonReleasedEvent(int button, Mods mods, long windowId, Vector2f mousePos){
        super(EventType.MOUSE, windowId);
        this.button = button;
        this.mods = mods;
        this.mousePos = mousePos;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        
        MouseButtonReleasedEvent that = (MouseButtonReleasedEvent) o;
        
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
        return "MouseButtonReleasedEvent{" +
                "button=" + button +
                ", mods=" + mods +
                "} " + super.toString();
    }
    
}
