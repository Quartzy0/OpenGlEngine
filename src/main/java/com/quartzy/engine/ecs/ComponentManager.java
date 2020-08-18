package com.quartzy.engine.ecs;

import com.quartzy.engine.world.World;
import lombok.CustomLog;

import java.lang.reflect.Field;
import java.util.HashMap;

@CustomLog
public class ComponentManager<T extends Component>{
    
    private HashMap<Short, T> components = new HashMap<>();
    
    public ComponentManager(){
    }
    
    public void addComponent(T component, short entity, World worldIn){
        try{
            Field entityId = Component.class.getDeclaredField("entityId");
            Field world = Component.class.getDeclaredField("world");
            
            entityId.setAccessible(true);
            world.setAccessible(true);
            entityId.setShort(component, entity);
            world.set(component, worldIn);
            entityId.setAccessible(false);
            world.setAccessible(false);
        } catch(NoSuchFieldException | IllegalAccessException e){
            log.severe("Couldn't find world/entityId field in component %s?!", e, component.getClass().getName());
        }
        components.put(entity, component);
    }
    
    public T getComponent(short entity){
        return components.get(entity);
    }
    
    public boolean hasComponent(short entity){
        return components.containsKey(entity);
    }
    
    public HashMap<Short, T> getComponents(){
        return components;
    }
}
