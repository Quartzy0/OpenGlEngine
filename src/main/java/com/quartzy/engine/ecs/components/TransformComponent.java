package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import lombok.Getter;
import lombok.Setter;
import org.dyn4j.geometry.Rotation;
import org.dyn4j.geometry.Transform;

import java.util.Collections;
import java.util.List;

public class TransformComponent extends Component{
    @Setter
    @Getter
    private Transform transform;
    
    private RigidBodyComponent bodyComponent = null;
    
    public TransformComponent(Transform transform){
        this.transform = transform;
    }
    
    @Override
    public void init(){
        RigidBodyComponent component = world.getEcsManager().getComponent(entityId, RigidBodyComponent.class);
        if(component!=null){
            bodyComponent = component;
        }
    }
    
    public void setX(double x){
        this.transform.setTranslationX(x);
        if(this.bodyComponent!=null){
            this.bodyComponent.getBody().getTransform().setTranslationX(x);
        }
    }
    
    public void setY(double y){
        this.transform.setTranslationY(y);
        if(this.bodyComponent!=null){
            this.bodyComponent.getBody().getTransform().setTranslationY(y);
        }
    }
    
    public void setPosition(double x, double y){
        this.transform.setTranslation(x, y);
        if(this.bodyComponent!=null){
            this.bodyComponent.getBody().getTransform().setTranslation(x,y);
        }
    }
    
    public void setRotation(double theta){
        this.transform.setRotation(theta);
        if(this.bodyComponent!=null){
            this.bodyComponent.getBody().getTransform().setRotation(theta);
        }
    }
    
    public void setRotation(Rotation rotation){
        this.transform.setRotation(rotation);
        if(this.bodyComponent!=null){
            this.bodyComponent.getBody().getTransform().setRotation(rotation);
        }
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return Collections.emptyList();
    }
}
