package com.quartzy.engine.events.impl;

import com.quartzy.engine.events.Event;

public class CursorLeaveEvent extends Event{
    public CursorLeaveEvent(long windowId){
        super(EventType.WINDOW, windowId);
    }
    
    @Override
    public String toString(){
        return "CursorLeaveEvent{} " + super.toString();
    }
}
