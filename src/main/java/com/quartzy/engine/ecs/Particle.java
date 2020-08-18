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
}
