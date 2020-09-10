package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.world.World;

public class Behaviour{
    
    protected short entityId;
    
    public void start(){
    
    }
    
    public void update(float delta){
    
    }
    
    protected final <T extends Component> T getComponent(Class<T> component){
        return World.getCurrentWorld().getEcsManager().getComponent(this.entityId, component);
    }
    
    protected final <T extends Component> T getComponent(short entityId, Class<T> component){
        return World.getCurrentWorld().getEcsManager().getComponent(entityId, component);
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        Behaviour behaviour = (Behaviour) o;
    
        return entityId == behaviour.entityId;
    }
    
    @Override
    public int hashCode(){
        return entityId;
    }
}
