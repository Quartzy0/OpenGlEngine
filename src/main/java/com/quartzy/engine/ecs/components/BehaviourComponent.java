package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import lombok.CustomLog;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

@CustomLog
public class BehaviourComponent extends Component{
    
    @Getter
    private Behaviour behaviour;
    
    public BehaviourComponent(Class<? extends Behaviour> behaviour){
        try{
            this.behaviour = behaviour.newInstance();
        } catch(InstantiationException | IllegalAccessException e){
            log.warning("Can't create new instance of behaviour script %s", e, behaviour.getName());
        }
    }
    
    public void update(float delta){
        behaviour.update(delta);
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
}
