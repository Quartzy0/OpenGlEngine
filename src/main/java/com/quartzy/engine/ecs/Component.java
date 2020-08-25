package com.quartzy.engine.ecs;

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
    
    public abstract void toBytes(ByteBuf out);
    
    public abstract void fromBytes(ByteBuf in);
}
