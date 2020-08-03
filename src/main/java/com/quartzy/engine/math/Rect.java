package com.quartzy.engine.math;

import lombok.Getter;
import lombok.Setter;

public class Rect{
    
    @Getter
    @Setter
    private float x, y, width, height;
    
    public Rect(float width, float height){
        this.width = width;
        this.height = height;
        this.x = 0f;
        this.y = 0f;
    }
    
    public Rect(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void add(float x, float y){
        this.x += x;
        this.y += y;
    }
    
    public void subtract(float x, float y){
        this.x -= x;
        this.y -= y;
    }
}
