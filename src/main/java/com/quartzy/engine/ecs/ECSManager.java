package com.quartzy.engine.ecs;

import com.quartzy.engine.ecs.components.TransformComponent;
import com.quartzy.engine.world.World;
import lombok.CustomLog;
import lombok.Getter;
import org.dyn4j.geometry.Transform;

import java.util.*;

@CustomLog
public class ECSManager{
    
    private final Random r = new Random();
    
    @Getter
    private HashMap<Class<? extends Component>, ComponentManager> components = new HashMap<>();
    
    @Getter
    private HashMap<Integer, List<Short>> layers = new HashMap<>();
    @Getter
    private HashMap<String, Short> tags = new HashMap<>();
    
    private World parent;
    
    private boolean inited = false;
    
    public ECSManager(World parent){
        this.parent = parent;
    }
    
    public <T extends Component> void addComponentToEntityNoCheck(short entityId, T component){
        if(components.containsKey(component.getClass())){
            components.get(component.getClass()).addComponent(component, entityId, parent);
        }else {
            ComponentManager<T> value = new ComponentManager<>(component.getClass());
            value.addComponent(component, entityId, parent);
            components.put(component.getClass(), value);
        }
        if(inited){
            component.init();
        }
    }
    
    public <T extends Component> void addComponentToEntity(short entityId, T component){
        List<Class<? extends Component>> classes = component.requiredComponents();
        if(classes!=null){
            for(Class<? extends Component> requiredComponent : classes){
                ComponentManager componentManager = components.get(requiredComponent);
                if(componentManager == null || !componentManager.hasComponent(entityId)){
                    log.severe("Component %s was attempted to be added onto an entity without a %s component", new RuntimeException("Missing component"), component.getClass().getName(), requiredComponent.getName());
                }
            }
        }
        if(components.containsKey(component.getClass())){
            components.get(component.getClass()).addComponent(component, entityId, parent);
        }else {
            ComponentManager<T> value = new ComponentManager<>(component.getClass());
            value.addComponent(component, entityId, parent);
            components.put(component.getClass(), value);
        }
        if(inited){
            component.init();
        }
    }
    
    public void initComponents(){
        for(ComponentManager value : components.values()){
            for(Object o : value.getComponents().values()){
                Component component = (Component) o;
                component.init();
            }
        }
        this.inited = true;
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
    
    public short getEntityByTag(String tag){
        return this.tags.get(tag);
    }
    
    public List<Short> getEntitiesOnLayer(int layerId){
        return layers.get(layerId);
    }
    
    public void setEntityTag(short entityId, String tag){
        this.tags.put(tag, entityId);
    }
    
    public void addEntityToLayer(short entityId, int layerId){
        if(layers.containsKey(layerId)){
            layers.get(layerId).add(entityId);
        }else {
            layers.put(layerId, new ArrayList<>(Collections.singleton(entityId)));
        }
    }
    
    public void removeEntity(short entityId){
        for(ComponentManager value : this.components.values()){
            value.removeComponent(entityId);
        }
    }
    
    public <T extends Component> HashMap<Short, T> getAllEntitiesWithComponent(Class<T> clazz){
        if(!components.containsKey(clazz))return new HashMap<>();
        return components.get(clazz).getComponents();
    }
    
    public short createObject(String tag, int layer){
        short s = (short) r.nextInt(Short.MAX_VALUE + 1);
        while(hasEntity(s)){
            s = (short) r.nextInt(Short.MAX_VALUE + 1);
        }
        if(tag!=null){
            this.tags.put(tag, s);
        }
        if(this.layers.containsKey(layer)){
            this.layers.get(layer).add(s);
        }else {
            this.layers.put(layer, new ArrayList<>(s));
        }
        return s;
    }
    
    public short createObject(){
        return this.createObject(null, 0);
    }
    
    public short createObject(String tag){
        return this.createObject(tag, 0);
    }
    
    public short createObject(int layer){
        return this.createObject(null, layer);
    }
}
