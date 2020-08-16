package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import lombok.Getter;
import lombok.Setter;
import org.dyn4j.dynamics.Body;
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
    
    public void setAt(double x, double y, double z){
        this.at.set(x, y, z);
    }
    
    public void setUp(double x, double y, double z){
        this.up.set(x, y, z);
    }
}
