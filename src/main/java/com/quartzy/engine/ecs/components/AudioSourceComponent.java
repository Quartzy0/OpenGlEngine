package com.quartzy.engine.ecs.components;

import com.quartzy.engine.audio.Sound;
import com.quartzy.engine.ecs.Component;
import io.netty.buffer.ByteBuf;
import org.dyn4j.geometry.Vector2;

import java.util.Collections;
import java.util.List;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.alSourcePlay;

public class AudioSourceComponent extends Component{
    private int sourcePointer = -1;
    
    private TransformComponent transformComponent;
    private RigidBodyComponent rigidBodyComponent;
    
    public AudioSourceComponent(){
    }
    
    @Override
    public void init(){
        this.transformComponent = world.getEcsManager().getComponent(entityId, TransformComponent.class);
        this.rigidBodyComponent = world.getEcsManager().getComponent(entityId, RigidBodyComponent.class);
    }
    
    public void play(Sound sound){
        if(sourcePointer==-1){
            sourcePointer = alGenSources();
        }
        alSourcei(sourcePointer, AL_BUFFER, sound.getBufferPointer());
        alSource3f(sourcePointer, AL_POSITION, (float) transformComponent.getTransform().getTranslationX(), (float) transformComponent.getTransform().getTranslationY(), 1f);
        if(rigidBodyComponent!=null){
            Vector2 linearVelocity = rigidBodyComponent.getBody().getLinearVelocity();
            alSource3f(sourcePointer, AL_VELOCITY, (float) linearVelocity.x, (float) linearVelocity.y, 1f);
        }
        alSourcei(sourcePointer, AL_GAIN, 200);
        alSourcePlay(sourcePointer);
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return Collections.singletonList(TransformComponent.class);
    }
    
    @Override
    public void toBytes(ByteBuf out){
    
    }
    
    @Override
    public void fromBytes(ByteBuf in){
    
    }
}
