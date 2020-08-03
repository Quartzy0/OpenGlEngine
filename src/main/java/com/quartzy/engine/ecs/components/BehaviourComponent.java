package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import lombok.CustomLog;
import lombok.Getter;

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
    
    public void start(){
        behaviour.start();
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return Collections.emptyList();
    }
}
