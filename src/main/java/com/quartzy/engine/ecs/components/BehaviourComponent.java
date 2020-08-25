package com.quartzy.engine.ecs.components;

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
    public void toBytes(ByteBuf out){
        out.writeBoolean(active);
        String name = behaviour.getClass().getName();
        out.writeInt(name.length());
        out.writeCharSequence(name, StandardCharsets.US_ASCII);
    }
    
    @Override
    public void fromBytes(ByteBuf in){
        this.active = in.readBoolean();
        int len = in.readInt();
        String className = in.readCharSequence(len, StandardCharsets.US_ASCII).toString();
        try{
            Class<?> aClass = Class.forName(className);
            if(!Behaviour.class.isAssignableFrom(aClass)){
                log.warning("Class %s does not extend the Behaviour class", className);
                return;
            }
            try{
                Class<? extends Behaviour> behaviourClass = (Class<? extends Behaviour>) aClass;
                this.behaviour = behaviourClass.newInstance();
            }catch(ClassCastException e){
                log.warning("Cannot cast class %s to Behaviour class", e, className);
            } catch(IllegalAccessException e){
                log.warning("Cannot access class %s", e, className);
            } catch(InstantiationException e){
                log.warning("Cannot instantiate class %s", e, className);
            }
        } catch(ClassNotFoundException e){
            log.warning("Cannot find behaviour class by the name %s", e, className);
        }
    }
}
