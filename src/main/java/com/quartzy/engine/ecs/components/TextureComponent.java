package com.quartzy.engine.ecs.components;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.graphics.Color;
import com.quartzy.engine.graphics.Texture;
import com.quartzy.engine.graphics.TextureManager;
import com.quartzy.engine.math.Vector2f;
import com.quartzy.engine.network.NetworkManager;
import com.quartzy.engine.network.Side;
import com.quartzy.engine.utils.ResourceManager;
import com.quartzy.engine.world.World;
import io.netty.buffer.ByteBuf;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@CustomLog
public class TextureComponent extends Component{
    @Getter
    @Setter
    private Texture texture;
    @Getter
    @Setter
    private Color color;
    @Getter
    @Setter
    private Vector2f offset;
    
    public TextureComponent(Texture texture, Color color){
        this.texture = texture;
        this.color = color;
        this.offset = new Vector2f();
    }
    
    public TextureComponent(Texture texture){
        this.texture = texture;
        this.color = Color.WHITE;
        this.offset = new Vector2f();
    }
    
    public TextureComponent(Texture texture, Vector2f offset){
        this.texture = texture;
        this.offset = offset;
        this.color = Color.WHITE;
    }
    
    public TextureComponent(Texture texture, Color color, Vector2f offset){
        this.texture = texture;
        this.color = color;
        this.offset = offset;
    }
    
    public TextureComponent(){
    }
    
    @Override
    public void init(){
    
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
        JsonObject jsonObject = new JsonObject();
        if(this.texture.getResource()!=null){
            jsonObject.addProperty("texture", this.texture.getResource().getName());
            if(!color.isPreDefined()){
                JsonObject jsonObject1 = color.toJson();
                jsonObject.add("color", jsonObject1);
            }else {
                jsonObject.addProperty("color", color.getPredefinedString());
            }
            if(!this.offset.isZero()){
                jsonObject.addProperty("pos_x", this.offset.x);
                jsonObject.addProperty("pos_y", this.offset.y);
            }
        }
        return jsonObject;
    }
    
    @Override
    public void fromJson(JsonObject in){
        String texture = in.get("texture").getAsString();
        if(NetworkManager.INSTANCE.getSide() == Side.CLIENT){
            this.texture = Client.getInstance().getTextureManager().getTexture(texture);
        }
        JsonElement color = in.get("color");
        if(color.isJsonObject()){
            this.color = Color.fromJson(color.getAsJsonObject());
        }else {
            this.color = Color.getFromPredefinedString(color.getAsString());
        }
        
        if(in.has("pos_x")){
            this.offset = new Vector2f(in.get("pos_x").getAsFloat(), in.get("pos_y").getAsFloat());
        }else {
            this.offset = new Vector2f();
        }
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        TextureComponent that = (TextureComponent) o;
        
        if(texture != null ? !texture.equals(that.texture) : that.texture != null) return false;
        if(color != null ? !color.equals(that.color) : that.color != null) return false;
        return offset != null ? offset.equals(that.offset) : that.offset == null;
    }
    
    @Override
    public int hashCode(){
        int result = texture != null ? texture.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (offset != null ? offset.hashCode() : 0);
        return result;
    }
}
