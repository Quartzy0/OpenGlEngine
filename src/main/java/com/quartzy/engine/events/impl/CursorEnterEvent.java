package com.quartzy.engine.events.impl;

import com.quartzy.engine.events.Event;

public class CursorEnterEvent extends Event{
    
    public CursorEnterEvent(long windowId){
        super(EventType.WINDOW, windowId);
    }
    
    @Override
    public String toString(){
        return "CursorEnterEvent{} " + super.toString();
    }
}
