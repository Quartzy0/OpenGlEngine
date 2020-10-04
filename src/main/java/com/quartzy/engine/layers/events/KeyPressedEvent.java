package com.quartzy.engine.layers.events;

import com.quartzy.engine.layers.Event;
import com.quartzy.engine.input.Mods;
import lombok.Getter;

public class KeyPressedEvent extends Event{
    
    @Getter
    private int keyCode;
    @Getter
    private Mods mods;
    
    public KeyPressedEvent(int keyCode, Mods mods, long windowId){
        super(EventType.KEYBOARD, windowId);
        this.keyCode = keyCode;
        this.mods = mods;
    }
    
    @Override
    public String toString(){
        return "KeyPressedEvent{" +
                "keyCode=" + keyCode +
                ", mods=" + mods +
                "} " + super.toString();
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        
        KeyPressedEvent that = (KeyPressedEvent) o;
        
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
