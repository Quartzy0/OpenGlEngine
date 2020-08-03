package com.quartzy.engine.ecs;

import com.quartzy.engine.ecs.components.RigidBodyComponent;
import com.quartzy.engine.world.World;
import lombok.CustomLog;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@CustomLog
public class ECSManager{
    
    private final Random r = new Random();
    
    private HashMap<Class<? extends Component>, ComponentManager> components = new HashMap<>();
    
    private World parent;
    
    public ECSManager(World parent){
        this.parent = parent;
    }
    
    public <T extends Component> void addComponentToEntity(short entityId, T component){
        for(Class<? extends Component> requiredComponent : component.requiredComponents()){
            ComponentManager componentManager = components.get(requiredComponent);
            if(componentManager==null || !componentManager.hasComponent(entityId)){
                log.severe("Component %s was attempted to be added onto an entity without a %s component", new RuntimeException("Missing component") ,component.getClass().getName(), requiredComponent.getName());
            }
        }
        if(components.containsKey(component.getClass())){
            components.get(component.getClass()).addComponent(component, entityId, parent);
        }else {
            ComponentManager<T> value = new ComponentManager<>();
            value.addComponent(component, entityId, parent);
            components.put(component.getClass(), value);
        }
    }
    
    public <T extends Component> T getComponent(short entityId, Class<T> clazz){
        if(!components.containsKey(clazz))return null;
        return (T) components.get(clazz).getComponent(entityId);
    }
    
    public boolean entityHasComponent(short entityId, Class<? extends Component> clazz){
        return components.get(clazz).hasComponent(entityId);
    }
    
    public boolean hasEntity(short entityId){
        for(Map.Entry<Class<? extends Component>, ComponentManager> classComponentManagerEntry : components.entrySet()){
            if(classComponentManagerEntry.getValue().hasComponent(entityId)){
                return true;
            }
        }
        return false;
    }
    
    public <T extends Component> HashMap<Short, T> getAllEntitiesWithComponent(Class<T> clazz){
        if(!components.containsKey(clazz))return null;
        return components.get(clazz).getComponents();
    }
    
    public short createEntity(){
        short s = (short) r.nextInt(Short.MAX_VALUE + 1);
        while(hasEntity(s)){
            s = (short) r.nextInt(Short.MAX_VALUE + 1);
        }
        RigidBodyComponent rigidBody = new RigidBodyComponent(64, 64, 0, 0);
        addComponentToEntity(s, rigidBody);
        return s;
    }
    
    public short createBlankObject(){
        short s = (short) r.nextInt(Short.MAX_VALUE + 1);
        while(hasEntity(s)){
            s = (short) r.nextInt(Short.MAX_VALUE + 1);
        }
        return s;
    }
}
