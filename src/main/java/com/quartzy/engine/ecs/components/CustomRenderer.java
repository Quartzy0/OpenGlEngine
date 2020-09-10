package com.quartzy.engine.ecs.components;

import com.quartzy.engine.graphics.Renderer;

public class CustomRenderer{
    
    protected short entityId;
    
    public void start(){
    
    }
    
    public void update(float delta){
    
    }
    
    public void render(Renderer renderer){
    
    }
    
    public void windowResize(int newWidth, int newHeight){
    
    }
    
    public void dispose(){
    
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        CustomRenderer that = (CustomRenderer) o;
    
        return entityId == that.entityId;
    }
    
    @Override
    public int hashCode(){
        return entityId;
    }
}
