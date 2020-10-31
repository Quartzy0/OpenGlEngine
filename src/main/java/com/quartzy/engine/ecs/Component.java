package com.quartzy.engine.ecs;

import com.google.gson.JsonObject;
import com.quartzy.engine.world.World;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.util.List;

public abstract class Component{
    @Getter
    protected short entityId;
    
    @Getter
    protected World world;
    
    public Component(){
    }
    
    public abstract void init();
    
    public abstract List<Class<? extends Component>> requiredComponents();
    
    public abstract boolean canHaveMultiple();
    
    public abstract JsonObject toJson();
    
    public abstract void fromJson(JsonObject in);
    
    public abstract boolean equals(Object obj);
    
    public abstract int hashCode();
}
