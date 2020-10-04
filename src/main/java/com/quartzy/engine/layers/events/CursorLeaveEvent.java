package com.quartzy.engine.layers.events;

import com.quartzy.engine.layers.Event;

public class CursorLeaveEvent extends Event{
    public CursorLeaveEvent(long windowId){
        super(EventType.WINDOW, windowId);
    }
    
    @Override
    public String toString(){
        return "CursorLeaveEvent{} " + super.toString();
    }
}
