package com.quartzy.engine.layers.events;

import com.quartzy.engine.layers.Event;
import com.quartzy.engine.math.Vector2f;
import lombok.Getter;

import java.util.Objects;

public class MouseDragEvent extends Event{
    
    public static float dragDebounce = 0.1f;
    
    @Getter
    private int button;
    @Getter
    private Vector2f clickOrigin;
    
    public MouseDragEvent(int button, Vector2f clickOrigin, long window){
        super(EventType.MOUSE, window);
        this.button = button;
        this.clickOrigin = clickOrigin;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        MouseDragEvent that = (MouseDragEvent) o;
        return button == that.button &&
                Objects.equals(clickOrigin, that.clickOrigin);
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(super.hashCode(), button, clickOrigin);
    }
    
    @Override
    public String toString(){
        return "MouseDragEvent{" +
                "button=" + button +
                ", clickOrigin=" + clickOrigin +
                '}';
    }
}
