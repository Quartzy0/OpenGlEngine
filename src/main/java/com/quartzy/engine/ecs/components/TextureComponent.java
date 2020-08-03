package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.graphics.Color;
import com.quartzy.engine.graphics.Texture;
import com.quartzy.engine.world.World;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

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
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return Collections.singletonList(RigidBodyComponent.class);
    }
}
