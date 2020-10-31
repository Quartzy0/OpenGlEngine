package com.quartzy.engine.ecs.components;

import com.google.gson.JsonObject;
import com.quartzy.engine.ecs.Component;
import io.netty.buffer.ByteBuf;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@CustomLog
public class BehaviourComponent extends Component{
    
    @Getter
    private Behaviour behaviour;
    
    @Getter
    @Setter
    private boolean active;
    
    public BehaviourComponent(Class<? extends Behaviour> behaviour){
        try{
            this.behaviour = behaviour.newInstance();
        } catch(InstantiationException | IllegalAccessException e){
            log.warning("Can't create new instance of behaviour script %s", e, behaviour.getName());
        }
        this.active = true;
    }
    
    public BehaviourComponent(){
    }
    
    public void update(float delta){
        if(active){
            behaviour.update(delta);
        }
    }
    
    @Override
    public void init(){
        try{
            Field entityId = Behaviour.class.getDeclaredField("entityId");
            entityId.setAccessible(true);
            entityId.setShort(this.behaviour, this.entityId);
            entityId.setAccessible(false);
        } catch(NoSuchFieldException | IllegalAccessException e){
            log.warning("Can't access 'entityId' field from %s class?!", e, Behaviour.class.getName());
        }
        behaviour.start();
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return Collections.emptyList();
    }
    
    @Override
    public boolean canHaveMultiple(){
        return true;
    }
    
    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("class_name", behaviour.getClass().getName());
        jsonObject.addProperty("active", active);
        
        return jsonObject;
    }
    
    @Override
    public void fromJson(JsonObject in){
        String class_name = in.get("class_name").getAsString();
        this.active = in.get("active").getAsBoolean();
    
        try{
            Class<?> aClass = Class.forName(class_name);
            if(!Behaviour.class.isAssignableFrom(aClass)){
                log.warning("Class %s does not extend the Behaviour class", class_name);
                return;
            }
            try{
                Class<? extends Behaviour> behaviourClass = (Class<? extends Behaviour>) aClass;
                this.behaviour = behaviourClass.newInstance();
            }catch(ClassCastException e){
                log.warning("Cannot cast class %s to Behaviour class", e, class_name);
            } catch(IllegalAccessException e){
                log.warning("Cannot access class %s", e, class_name);
            } catch(InstantiationException e){
                log.warning("Cannot instantiate class %s", e, class_name);
            }
        } catch(ClassNotFoundException e){
            log.warning("Cannot find behaviour class by the name %s", e, class_name);
        }
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        BehaviourComponent that = (BehaviourComponent) o;
    
        return behaviour != null ? behaviour.equals(that.behaviour) : that.behaviour == null;
    }
    
    @Override
    public int hashCode(){
        return behaviour != null ? behaviour.hashCode() : 0;
    }
}
