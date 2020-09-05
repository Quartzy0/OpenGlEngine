package com.quartzy.engine.ecs;

import com.quartzy.engine.ecs.components.CustomRenderComponent;
import com.quartzy.engine.world.World;
import lombok.CustomLog;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.HashMap;

@CustomLog
public class ComponentManager<T extends Component>{
    
    private HashMap<Short, T> components = new HashMap<>();
    @Getter
    private Class<? extends Component> type;
    
    public ComponentManager(Class<? extends Component> type){
        this.type = type;
    }
    
    public ComponentManager(HashMap<Short, T> components, Class<? extends Component> type){
        this.components = components;
        this.type = type;
    }
    
    public T removeComponent(short entityId){
        T remove = this.components.remove(entityId);
        if(remove!=null && type.equals(CustomRenderComponent.class)){
            ((CustomRenderComponent) remove).dispose();
        }
        return remove;
    }
    
    public void addComponent(T component, short entity, World worldIn){
        if(component==null)return;
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
