package com.quartzy.engine.ecs;

import com.quartzy.engine.world.World;
import lombok.CustomLog;
import lombok.Getter;

import java.util.List;

public abstract class Component{
    @Getter
    protected short entityId;
    
    @Getter
    protected World world;
    
    public abstract List<Class<? extends Component>> requiredComponents();
}
