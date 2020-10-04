package com.quartzy.engine.layers.events;

import com.quartzy.engine.layers.Event;
import lombok.Getter;

public class TickEvent extends Event{
    @Getter
    private float delta;
    
    public TickEvent(long windowId, float delta){
        super(EventType.ENGINE, windowId);
        this.delta = delta;
    }
}
