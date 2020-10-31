package com.quartzy.engine.ecs;

import com.quartzy.engine.ecs.components.CustomRenderComponent;
import com.quartzy.engine.world.World;
import lombok.CustomLog;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@CustomLog
public class ComponentManager<T extends Component>{
    
    private HashMap<Short, List<T>> components = new HashMap<>();
    @Getter
    private final Class<? extends Component> type;
    
    public ComponentManager(Class<? extends Component> type){
        this.type = type;
    }
    
    public ComponentManager(HashMap<Short, List<T>> components, Class<? extends Component> type){
        this.components = components;
        this.type = type;
    }
    
    public T removeComponent(short entityId, int index){
        List<T> remove = this.components.get(entityId);
        T remove1 = remove.remove(index);
        if(remove.size()==0)this.components.remove(entityId);
        if(remove1!=null && type.equals(CustomRenderComponent.class)){
            ((CustomRenderComponent) remove1).dispose();
        }
        return remove1;
    }
    
    public List<T> removeComponent(short entityId){
        List<T> remove = this.components.remove(entityId);
        if(remove!=null && type.equals(CustomRenderComponent.class)){
            for(T t : remove){
                ((CustomRenderComponent) t).dispose();
            }
        }
        return remove;
    }
    
    public Component removeComponent(short entityId, Component component){
        List<T> ts = this.components.get(entityId);
        if(ts==null || ts.isEmpty())return null;
    
        if(ts.remove(component)){
            return component;
        }
        return null;
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
        if(!components.containsKey(entity)){
            components.put(entity, new ArrayList<>(Collections.singletonList(component)));
        }else {
            components.get(entity).add(component);
        }
    }
    
    public List<T> getComponents(short entity){
        return components.get(entity);
    }
    
    public boolean hasComponent(short entity){
        return components.containsKey(entity);
    }
    
    public HashMap<Short, List<T>> getComponents(){
        return components;
    }
}
