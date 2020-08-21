package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.math.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

public class LightSourceComponent extends Component{
    
    @Getter
    @Setter
    private Vector3f color;
    private TransformComponent transform;
    
    @Getter
    @Setter
    private float z;
    
    public LightSourceComponent(Vector3f color, float z){
        this.color = color;
        this.z = z;
    }
    
    public LightSourceComponent(Vector3f color){
        this(color, 50.0f);
    }
    
    @Override
    public void init(){
        this.transform = world.getEcsManager().getComponent(entityId, TransformComponent.class);
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
    
    public Vector3f getPosition(){
        return new Vector3f((float) this.transform.getX(), (float) this.transform.getY(), z);
    }
}
