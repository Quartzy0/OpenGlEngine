package com.quartzy.engine.events.impl;

import com.quartzy.engine.events.Event;
import com.quartzy.engine.input.Mods;
import lombok.Getter;

public class KeyReleasedEvent extends Event{
    
    @Getter
    private int keyCode;
    
    @Getter
    private Mods mods;
    
    public KeyReleasedEvent(int keyCode, Mods mods, long windowId){
        super(EventType.KEYBOARD, windowId);
        this.keyCode = keyCode;
        this.mods = mods;
    }
    
    @Override
    public String toString(){
        return "KeyReleasedEvent{" +
                "keyCode=" + keyCode +
                ", mods=" + mods +
                "} " + super.toString();
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        
        KeyReleasedEvent that = (KeyReleasedEvent) o;
        
        if(keyCode != that.keyCode) return false;
        return mods != null ? mods.equals(that.mods) : that.mods == null;
    }
    
    @Override
    public int hashCode(){
        int result = super.hashCode();
        result = 31 * result + keyCode;
        result = 31 * result + (mods != null ? mods.hashCode() : 0);
        return result;
    }
}
