package com.quartzy.engine.layers.events;

import com.quartzy.engine.graphics.Renderer;
import com.quartzy.engine.layers.Event;
import lombok.Getter;

public class RenderEvent extends Event{
    
    @Getter
    private Renderer renderer;
    
    public RenderEvent(long windowId, Renderer renderer){
        super(EventType.ENGINE, windowId);
        this.renderer = renderer;
    }
}
