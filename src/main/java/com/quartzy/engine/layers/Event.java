package com.quartzy.engine.layers;

import lombok.Getter;
import lombok.Setter;

public class Event{
    
    @Getter
    @Setter
    private boolean handled;
    @Getter
    private EventType type;
    
    @Getter
    private long windowId;
    
    public Event(EventType type, long windowId){
        this.handled = false;
        this.type = type;
        this.windowId = windowId;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        Event event = (Event) o;
        
        if(windowId != event.windowId) return false;
        return type == event.type;
    }
    
    @Override
    public int hashCode(){
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (int) (windowId ^ (windowId >>> 32));
        return result;
    }
    
    @Override
    public String toString(){
        return "Event{" +
                "handled=" + handled +
                ", type=" + type.toString() +
                ", windowId=" + windowId +
                '}';
    }
    
    public enum EventType{
        INPUT(null), KEYBOARD(INPUT), MOUSE(INPUT), ENGINE(null), WINDOW(null);
    
        public final EventType parent;
        EventType(EventType parent){
            this.parent = parent;
        }
        
        public boolean equals(EventType obj){
            if(obj==this)return true;
            return obj.parent == this;
        }
    
        @Override
        public String toString(){
            return "EventType{" +
                    "name=" + this.name() + ", " +
                    "parent=" + parent +
                    "}";
        }
    }
}
