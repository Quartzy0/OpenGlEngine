package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.math.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

public class LightSourceComponent extends Component{
    
    private static boolean anyChanged;
    
    @Getter
    private Vector3f color;
    private TransformComponent transform;
    
    @Getter
    private float z;
    
    public LightSourceComponent(Vector3f color, float z){
        this.color = color;
        this.z = z;
    }
    
    public LightSourceComponent(Vector3f color){
        this(color, 50.0f);
    }
    
    public LightSourceComponent(){
        this(new Vector3f(1, 1, 1));
    }
    
    @Override
    public void init(){
        this.transform = world.getEcsManager().getComponent(entityId, TransformComponent.class);
        anyChanged = true;
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return Collections.singletonList(TransformComponent.class);
    }
    
    @Override
    public void toBytes(ByteBuf out){
        out.writeFloat(color.x);
        out.writeFloat(color.y);
        out.writeFloat(color.z);
        
        out.writeFloat(z);
    }
    
    @Override
    public void fromBytes(ByteBuf in){
        float x = in.readFloat();
        float y = in.readFloat();
        float z = in.readFloat();
        this.color = new Vector3f(x, y, z);
        this.z = in.readFloat();
    }
    
    public Vector3f getPosition(){
        return new Vector3f((float) this.transform.getX(), (float) this.transform.getY(), z);
    }
    
    public void setColor(Vector3f color){
        this.color = color;
        anyChanged = true;
    }
    
    public void setZ(float z){
        this.z = z;
        anyChanged = true;
    }
    
    public static boolean isAnyChanged(){
        return anyChanged || TransformComponent.anyChanged;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        LightSourceComponent that = (LightSourceComponent) o;
        
        if(Float.compare(that.z, z) != 0) return false;
        return color != null ? color.equals(that.color) : that.color == null;
    }
    
    @Override
    public int hashCode(){
        int result = color != null ? color.hashCode() : 0;
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        return result;
    }
}
