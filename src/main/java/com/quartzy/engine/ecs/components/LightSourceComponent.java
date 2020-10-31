package com.quartzy.engine.ecs.components;

import com.google.gson.JsonObject;
import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.graphics.Color;
import com.quartzy.engine.math.Vector2f;
import com.quartzy.engine.math.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

public class LightSourceComponent extends Component{
    
    private static boolean anyChanged;
    
    @Getter
    private Color color;
    private Color previousColor;
    private TransformComponent transform;
    
    @Getter
    private Vector2f offset;
    private Vector2f prevOffset;
    
    @Getter
    private float z;
    private float prevZ;
    
    public LightSourceComponent(Vector3f color, float z){
        this(new Color(color.x, color.y, color.z), z);
    }
    
    public LightSourceComponent(Color color, float z){
        this.color = color;
        this.z = z;
        this.offset = new Vector2f();
        this.previousColor = color;
        this.prevZ = z;
        this.prevOffset = this.offset;
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
    public boolean canHaveMultiple(){
        return true;
    }
    
    @Override
    public JsonObject toJson(){
        JsonObject out = new JsonObject();
        
        color.toJson(out);
        out.addProperty("z", this.z);
        
        out.addProperty("offset_x", this.offset.x);
        out.addProperty("offset_y", this.offset.y);
        
        return out;
    }
    
    @Override
    public void fromJson(JsonObject in){
        this.color = Color.fromJson(in);
        this.z = in.get("z").getAsFloat();
        this.offset = new Vector2f(in.get("offset_x").getAsFloat(), in.get("offset_y").getAsFloat());
    }
    
    public Vector3f getPosition(){
        return new Vector3f((float) this.transform.getX() + this.offset.x, (float) this.transform.getY() + this.offset.y, z);
    }
    
    public void setColor(Color color){
        this.color = color;
        if(!this.previousColor.equals(this.color)){
            anyChanged = true;
            this.previousColor = this.color;
        }
    }
    
    public void setZ(float z){
        this.z = z;
        if(this.prevZ!=this.z){
            anyChanged = true;
            this.prevZ = this.z;
        }
    }
    
    public void setOffset(Vector2f offset){
        this.offset = offset;
        if(!this.prevOffset.equals(this.offset)){
            anyChanged = true;
            this.prevOffset = this.offset;
        }
    }
    
    public Vector3f getColorVec(){
        return new Vector3f(this.color.getRed(), this.color.getGreen(), this.color.getBlue());
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
