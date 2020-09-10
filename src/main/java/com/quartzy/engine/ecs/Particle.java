package com.quartzy.engine.ecs;

import com.quartzy.engine.graphics.Texture;
import com.quartzy.engine.math.Vector2f;
import lombok.Data;

@Data
public class Particle{
    private Texture texture;
    private long lifeTime;
    private float speed;
    private float scale;
    private Vector2f direction, position, baseSize;
    
    public Particle(Texture texture, long lifeTime, float speed, Vector2f direction, Vector2f baseSize){
        this.texture = texture;
        this.lifeTime = lifeTime;
        this.speed = speed;
        this.direction = direction;
        this.baseSize = baseSize;
    }
    
    public Particle(Particle particle){
        this.texture = particle.texture;
        this.position = particle.position;
        this.direction = particle.direction;
        this.speed = particle.speed;
        this.lifeTime = particle.lifeTime;
        this.scale = particle.scale;
        this.baseSize = particle.baseSize;
    }
    
    public void update(float delta){
        this.position = this.position.add(this.direction.scale(speed*delta));
        this.lifeTime-=(delta*1000);
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        Particle particle = (Particle) o;
        
        if(lifeTime != particle.lifeTime) return false;
        if(Float.compare(particle.speed, speed) != 0) return false;
        if(Float.compare(particle.scale, scale) != 0) return false;
        if(texture != null ? !texture.equals(particle.texture) : particle.texture != null) return false;
        if(direction != null ? !direction.equals(particle.direction) : particle.direction != null) return false;
        if(position != null ? !position.equals(particle.position) : particle.position != null) return false;
        return baseSize != null ? baseSize.equals(particle.baseSize) : particle.baseSize == null;
    }
    
    @Override
    public int hashCode(){
        int result = texture != null ? texture.hashCode() : 0;
        result = 31 * result + (int) (lifeTime ^ (lifeTime >>> 32));
        result = 31 * result + (speed != +0.0f ? Float.floatToIntBits(speed) : 0);
        result = 31 * result + (scale != +0.0f ? Float.floatToIntBits(scale) : 0);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (baseSize != null ? baseSize.hashCode() : 0);
        return result;
    }
}
