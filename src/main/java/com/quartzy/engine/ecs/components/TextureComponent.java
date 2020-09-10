package com.quartzy.engine.ecs.components;

import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.graphics.Color;
import com.quartzy.engine.graphics.Texture;
import com.quartzy.engine.graphics.TextureManager;
import com.quartzy.engine.utils.ResourceManager;
import com.quartzy.engine.world.World;
import io.netty.buffer.ByteBuf;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;

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
    
    public TextureComponent(Texture texture, Color color){
        this.texture = texture;
        this.color = color;
    }
    
    public TextureComponent(Texture texture){
        this.texture = texture;
        this.color = Color.WHITE;
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
    public void toBytes(ByteBuf out){
        if(texture.getResource()!=null){
            String name = texture.getResource().getName();
            out.writeInt(name.length());
            out.writeCharSequence(name, StandardCharsets.US_ASCII);
            boolean equals = color.equals(Color.WHITE);
            out.writeBoolean(equals);
            if(!equals){
                out.writeFloat(color.getRed());
                out.writeFloat(color.getGreen());
                out.writeFloat(color.getBlue());
                out.writeFloat(color.getAlpha());
            }
        }else {
            log.warning("Cannot convert texture component to bytes because it has no resource");
        }
    }
    
    @Override
    public void fromBytes(ByteBuf in){
        int len = in.readInt();
        String s = in.readCharSequence(len, StandardCharsets.US_ASCII).toString();
        if(in.readBoolean()){
            color = Color.WHITE;
        }else {
            color = new Color(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
        }
        this.texture = Client.getInstance().getTextureManager().getTexture(s);
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        TextureComponent that = (TextureComponent) o;
        
        if(texture != null ? !texture.equals(that.texture) : that.texture != null) return false;
        return color != null ? color.equals(that.color) : that.color == null;
    }
    
    @Override
    public int hashCode(){
        int result = texture != null ? texture.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }
}
