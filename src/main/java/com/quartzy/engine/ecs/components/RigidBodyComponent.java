package com.quartzy.engine.ecs.components;

import com.google.gson.JsonObject;
import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.math.Vector2f;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rotation;
import org.dyn4j.geometry.Vector2;

import java.util.Collections;
import java.util.List;

public class RigidBodyComponent extends Component{
    @Getter
    @Setter
    private Body body;
    
    /**
     * Can be null
     */
    @Getter
    private double width,height;
    
    public RigidBodyComponent(double width, double height){
        this.width = width;
        this.height = height;
    }
    
    public RigidBodyComponent(Body customBody){
        this.body = customBody;
    }
    
    public RigidBodyComponent(){
    }
    
    @Override
    public void init(){
        TransformComponent component = world.getEcsManager().getComponent(entityId, TransformComponent.class);
        if(this.body==null){
            this.body = new Body();
            body.setTransform(component.getTransform());
            body.addFixture(Geometry.createRectangle(width, height));
            body.setMass(new Mass(new Vector2(0, 0), 30, 30));
            world.getPhysicsWorld().addBody(body);
        }
    }
    
    public void updateTransform(){
        TransformComponent component = world.getEcsManager().getComponent(entityId, TransformComponent.class);
        component.setTransform(this.body.getTransform());
        TransformComponent.anyChanged = true;
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return Collections.singletonList(TransformComponent.class);
    }
    
    @Override
    public boolean canHaveMultiple(){
        return false;
    }
    
    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("mass", body.getMass().getMass());
        jsonObject.addProperty("inertia", body.getMass().getInertia());
    
        jsonObject.addProperty("centre_x", body.getMass().getCenter().x);
        jsonObject.addProperty("centre_y", body.getMass().getCenter().y);
    
        jsonObject.addProperty("pos_x", body.getTransform().getTranslationX());
        jsonObject.addProperty("pos_y", body.getTransform().getTranslationY());
    
        jsonObject.addProperty("rot", body.getTransform().getRotationAngle());
        
        return jsonObject;
    }
    
    @Override
    public void fromJson(JsonObject in){
        Vector2 centre = new Vector2(in.get("centre_x").getAsDouble(), in.get("centre_y").getAsDouble());
        body.setMass(new Mass(centre, in.get("mass").getAsDouble(), in.get("inertia").getAsDouble()));
        
        body.translate(in.get("pos_x").getAsDouble(), in.get("pos_y").getAsDouble());
        body.rotate(in.get("rot").getAsDouble());
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        RigidBodyComponent that = (RigidBodyComponent) o;
        
        if(Double.compare(that.width, width) != 0) return false;
        if(Double.compare(that.height, height) != 0) return false;
        return body != null ? body.equals(that.body) : that.body == null;
    }
    
    @Override
    public int hashCode(){
        int result;
        long temp;
        result = body != null ? body.hashCode() : 0;
        temp = Double.doubleToLongBits(width);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(height);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
