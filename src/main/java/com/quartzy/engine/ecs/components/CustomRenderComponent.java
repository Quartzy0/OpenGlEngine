package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.graphics.Renderer;
import io.netty.buffer.ByteBuf;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;

@CustomLog
public class CustomRenderComponent extends Component{
    private CustomRenderer renderer;
    
    @Getter
    @Setter
    private boolean active;
    
    public CustomRenderComponent(Class<? extends CustomRenderer> customRendererClass){
        try{
            this.renderer = customRendererClass.newInstance();
        } catch(InstantiationException | IllegalAccessException e){
            log.warning("Can't create new instance of behaviour script %s", e, customRendererClass.getName());
        }
        this.active = true;
    }
    
    public CustomRenderComponent(){
    }
    
    @Override
    public void init(){
        try{
            Field entityId = CustomRenderer.class.getDeclaredField("entityId");
            entityId.setAccessible(true);
            entityId.setShort(this.renderer, this.entityId);
            entityId.setAccessible(false);
        } catch(NoSuchFieldException | IllegalAccessException e){
            log.warning("Can't access 'entityId' field from %s class?!", e, CustomRenderer.class.getName());
        }
        renderer.start();
    }
    
    public void update(float delta){
        if(!active)return;
        renderer.update(delta);
    }
    
    public void render(Renderer renderer){
        if(!active)return;
        this.renderer.render(renderer);
    }
    
    public void resizeWindow(int newWidth, int newHeight){
        this.renderer.windowResize(newWidth, newHeight);
    }
    
    public void dispose(){
        this.renderer.dispose();
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return null;
    }
    
    @Override
    public void toBytes(ByteBuf out){
        out.writeBoolean(active);
        String name = renderer.getClass().getName();
        out.writeInt(name.length());
        out.writeCharSequence(name, StandardCharsets.US_ASCII);
    }
    
    @Override
    public void fromBytes(ByteBuf in){
        this.active = in.readBoolean();
        int len = in.readInt();
        String className = in.readCharSequence(len, StandardCharsets.US_ASCII).toString();
        try{
            Class<?> aClass = Class.forName(className);
            if(!CustomRenderer.class.isAssignableFrom(aClass)){
                log.warning("Class %s does not extend the Behaviour class", className);
                return;
            }
            try{
                Class<? extends CustomRenderer> behaviourClass = (Class<? extends CustomRenderer>) aClass;
                this.renderer = behaviourClass.newInstance();
            }catch(ClassCastException e){
                log.warning("Cannot cast class %s to Behaviour class", e, className);
            } catch(IllegalAccessException e){
                log.warning("Cannot access class %s", e, className);
            } catch(InstantiationException e){
                log.warning("Cannot instantiate class %s", e, className);
            }
        } catch(ClassNotFoundException e){
            log.warning("Cannot find behaviour class by the name %s", e, className);
        }
    }
}
