package com.quartzy.engine.layers.events;

import com.quartzy.engine.layers.Event;

public class CursorEnterEvent extends Event{
    
    public CursorEnterEvent(long windowId){
        super(EventType.WINDOW, windowId);
    }
    
    @Override
    public String toString(){
        return "CursorEnterEvent{} " + super.toString();
    }
}
