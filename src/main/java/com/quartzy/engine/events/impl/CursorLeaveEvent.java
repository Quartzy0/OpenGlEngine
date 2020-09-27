package com.quartzy.engine.events.impl;

import com.quartzy.engine.events.Event;

public class CursorLeaveEvent extends Event{
    public CursorLeaveEvent(){
        super(EventType.WINDOW);
    }
    
    @Override
    public String toString(){
        return "CursorLeaveEvent{} " + super.toString();
    }
}
