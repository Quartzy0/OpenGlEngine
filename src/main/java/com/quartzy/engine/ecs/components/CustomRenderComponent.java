package com.quartzy.engine.ecs.components;

import com.google.gson.JsonObject;
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
    public boolean canHaveMultiple(){
        return true;
    }
    
    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("class_name", renderer.getClass().getName());
        jsonObject.addProperty("active", active);
        
        return jsonObject;
    }
    
    @Override
    public void fromJson(JsonObject in){
        this.active = in.get("active").getAsBoolean();
        String className = in.get("class_name").getAsString();
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
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        CustomRenderComponent that = (CustomRenderComponent) o;
    
        return renderer != null ? renderer.equals(that.renderer) : that.renderer == null;
    }
    
    @Override
    public int hashCode(){
        return renderer != null ? renderer.hashCode() : 0;
    }
}
