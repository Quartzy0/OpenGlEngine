package com.quartzy.engine.events.impl;

import com.quartzy.engine.events.Event;

public class CursorEnterEvent extends Event{
    
    public CursorEnterEvent(){
        super(EventType.WINDOW);
    }
    
    @Override
    public String toString(){
        return "CursorEnterEvent{} " + super.toString();
    }
}
