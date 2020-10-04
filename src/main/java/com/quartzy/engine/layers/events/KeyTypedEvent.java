package com.quartzy.engine.layers.events;

import com.quartzy.engine.layers.Event;
import lombok.Getter;

public class KeyTypedEvent extends Event{
    
    @Getter
    private int codepoint;
    
    public KeyTypedEvent(int codepoint, long windowId){
        super(EventType.KEYBOARD, windowId);
        this.codepoint = codepoint;
    }
    
    @Override
    public String toString(){
        return "KeyTypedEvent{" +
                "codepoint=" + codepoint +
                "} " + super.toString();
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        
        KeyTypedEvent that = (KeyTypedEvent) o;
    
        return codepoint == that.codepoint;
    }
    
    @Override
    public int hashCode(){
        int result = super.hashCode();
        result = 31 * result + codepoint;
        return result;
    }
}
