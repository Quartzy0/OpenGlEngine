package com.quartzy.engine.events;

import lombok.Getter;
import lombok.Setter;

public class Event{
    
    @Getter
    @Setter
    private boolean handled;
    @Getter
    private EventType type;
    
    public Event(EventType type){
        this.handled = false;
        this.type = type;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        Event event = (Event) o;
    
        return type == event.type;
    }
    
    @Override
    public int hashCode(){
        return type != null ? type.hashCode() : 0;
    }
    
    @Override
    public String toString(){
        return "Event{" +
                "handled=" + handled +
                ", type=" + type.toString() +
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
            if(obj.parent==this)return true;
            if(obj==this.parent)return true;
            if(obj.parent==this.parent)return true;
            return false;
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
