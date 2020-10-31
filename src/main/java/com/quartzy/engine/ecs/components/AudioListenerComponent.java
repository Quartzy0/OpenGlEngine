package com.quartzy.engine.ecs.components;

import com.google.gson.JsonObject;
import com.quartzy.engine.ecs.Component;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Vector3;

import java.util.Collections;
import java.util.List;

import static org.lwjgl.openal.AL10.*;

public class AudioListenerComponent extends Component{
    @Getter
    @Setter
    private Vector3 up, at;
    
    @Getter
    @Setter
    private boolean mainAtStartup;
    
    public AudioListenerComponent(boolean main){
        this.mainAtStartup = main;
        this.at = new Vector3(0, 0, -1);
        this.up = new Vector3(0, 1, 0);
    }
    
    public AudioListenerComponent(){
        this(false);
    }
    
    @Override
    public void init(){
        if(this.mainAtStartup)setAsMain();
    }
    
    public void setAsMain(){
        Transform component = this.world.getEcsManager().getComponent(entityId, TransformComponent.class).getTransform();
        alListener3f(AL_POSITION, (float) component.getTranslationX(), (float) component.getTranslationY(), 0);
        RigidBodyComponent component1 = this.world.getEcsManager().getComponent(entityId, RigidBodyComponent.class);
        if(component1!=null){
            Vector2 linearVelocity = component1.getBody().getLinearVelocity();
            alListener3f(AL_VELOCITY, (float) linearVelocity.x, (float) linearVelocity.y, 0);
        }else {
            alListener3f(AL_VELOCITY, 0f, 0f, 0f);
        }
        float[] orientationData = new float[6];
        orientationData[0] = (float) at.x;
        orientationData[1] = (float) at.y;
        orientationData[2] = (float) at.z;
        orientationData[3] = (float) up.x;
        orientationData[4] = (float) up.y;
        orientationData[5] = (float) up.z;
        alListenerfv(AL_ORIENTATION, orientationData);
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
        jsonObject.addProperty("up_x", up.x);
        jsonObject.addProperty("up_y", up.y);
        jsonObject.addProperty("up_z", up.z);
    
        jsonObject.addProperty("at_x", at.x);
        jsonObject.addProperty("at_y", at.y);
        jsonObject.addProperty("at_z", at.z);
        return jsonObject;
    }
    
    @Override
    public void fromJson(JsonObject in){
        double up_x = in.get("up_x").getAsDouble();
        double up_y = in.get("up_y").getAsDouble();
        double up_z = in.get("up_z").getAsDouble();
    
        double at_x = in.get("at_x").getAsDouble();
        double at_y = in.get("at_y").getAsDouble();
        double at_z = in.get("at_z").getAsDouble();
        
        this.up = new Vector3(up_x, up_y, up_z);
        this.at = new Vector3(at_x, at_y, at_z);
    }
    
    public void setAt(double x, double y, double z){
        this.at.set(x, y, z);
    }
    
    public void setUp(double x, double y, double z){
        this.up.set(x, y, z);
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        AudioListenerComponent that = (AudioListenerComponent) o;
        
        if(up != null ? !up.equals(that.up) : that.up != null) return false;
        return at != null ? at.equals(that.at) : that.at == null;
    }
    
    @Override
    public int hashCode(){
        int result = up != null ? up.hashCode() : 0;
        result = 31 * result + (at != null ? at.hashCode() : 0);
        return result;
    }
}
