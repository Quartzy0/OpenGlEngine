package com.quartzy.engine.ecs.components;

import com.google.gson.JsonObject;
import com.quartzy.engine.ecs.Component;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.dyn4j.geometry.Rotation;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

import java.util.Collections;
import java.util.List;

public class TransformComponent extends Component{
    public static boolean anyChanged;
    
    @Setter
    @Getter
    private Transform transform;
    
    private RigidBodyComponent bodyComponent = null;
    
    public TransformComponent(Transform transform){
        this.transform = transform;
    }
    
    public TransformComponent(){
    }
    
    @Override
    public void init(){
        RigidBodyComponent component = world.getEcsManager().getComponent(entityId, RigidBodyComponent.class);
        if(component!=null){
            bodyComponent = component;
        }
        anyChanged = true;
    }
    
    public void setX(double x){
        this.transform.setTranslationX(x);
        if(this.bodyComponent!=null){
            this.bodyComponent.getBody().getTransform().setTranslationX(x);
        }
        anyChanged = true;
    }
    
    public void setY(double y){
        this.transform.setTranslationY(y);
        if(this.bodyComponent!=null){
            this.bodyComponent.getBody().getTransform().setTranslationY(y);
        }
        anyChanged = true;
    }
    
    public void setPosition(double x, double y){
        this.transform.setTranslation(x, y);
        if(this.bodyComponent!=null){
            this.bodyComponent.getBody().getTransform().setTranslation(x,y);
        }
        anyChanged = true;
    }
    
    public void setRotation(double theta){
        this.transform.setRotation(theta);
        if(this.bodyComponent!=null){
            this.bodyComponent.getBody().getTransform().setRotation(theta);
        }
        anyChanged = true;
    }
    
    public void setRotation(Rotation rotation){
        this.transform.setRotation(rotation);
        if(this.bodyComponent!=null){
            this.bodyComponent.getBody().getTransform().setRotation(rotation);
        }
        anyChanged = true;
    }
    
    public double getX(){
        return this.transform.getTranslationX();
    }
    
    public double getY(){
        return this.transform.getTranslationY();
    }
    
    public void translate(double x, double y){
        this.transform.translate(x, y);
        anyChanged = true;
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return Collections.emptyList();
    }
    
    @Override
    public boolean canHaveMultiple(){
        return false;
    }
    
    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("x", this.transform.getTranslationX());
        jsonObject.addProperty("y", this.transform.getTranslationY());
    
        jsonObject.addProperty("angle", this.transform.getRotationAngle());
        
        return jsonObject;
    }
    
    @Override
    public void fromJson(JsonObject in){
        double x = in.get("x").getAsDouble();
        double y = in.get("y").getAsDouble();
        double angle = in.get("angle").getAsDouble();
        
        this.transform = new Transform();
        this.transform.translate(x, y);
        this.transform.setRotation(angle);
        
        anyChanged = true;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        TransformComponent that = (TransformComponent) o;
    
        return transform != null ? transform.equals(that.transform) : that.transform == null;
    }
    
    @Override
    public int hashCode(){
        return transform != null ? transform.hashCode() : 0;
    }
}
