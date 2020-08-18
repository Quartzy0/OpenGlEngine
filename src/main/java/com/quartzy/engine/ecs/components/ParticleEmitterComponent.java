package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.ecs.Particle;
import com.quartzy.engine.math.Vector2f;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParticleEmitterComponent extends Component{
    
    @Getter
    @Setter
    private Particle defaultParticle;
    @Getter
    private List<Particle> particles;
    @Getter
    @Setter
    private int maxParticles;
    private float speedRange;
    private float positionRange;
    private float scaleRange;
    @Getter
    @Setter
    private long particleCreationPeriod;
    @Getter
    @Setter
    private Vector2f positionOffset;
    
    @Getter
    @Setter
    private boolean active;
    private TransformComponent transformComponent;
    
    private long lastCreatedParticle;
    
    public ParticleEmitterComponent(Particle defaultParticle, int maxParticles, float speedRange, float positionRange, float scaleRange, long particleCreationPeriod, boolean active){
        this.defaultParticle = defaultParticle;
        this.maxParticles = maxParticles;
        this.speedRange = speedRange;
        this.positionRange = positionRange;
        this.scaleRange = scaleRange;
        this.active = active;
        this.particleCreationPeriod = particleCreationPeriod;
        this.particles = new ArrayList<>();
        this.positionOffset = new Vector2f();
    }
    
    public ParticleEmitterComponent(Particle defaultParticle, int maxParticles, float speedRange, float positionRange, float scaleRange, long particleCreationPeriod){
        this(defaultParticle, maxParticles, speedRange, positionRange, scaleRange, particleCreationPeriod,true);
    }
    
    public ParticleEmitterComponent(Particle defaultParticle, int maxParticles){
        this(defaultParticle, maxParticles, 5, 3, 1, 100);
    }
    
    @Override
    public void init(){
        this.transformComponent = this.world.getEcsManager().getComponent(this.entityId, TransformComponent.class);
        this.defaultParticle.setPosition(new Vector2f((float) this.transformComponent.getX(), (float) transformComponent.getY()).add(positionOffset));
    }
    
    public void update(float delta){
        if(!active)return;
        for(int i = 0; i < particles.size(); i++){
            Particle particle = particles.get(i);
            particle.update(delta);
            if(particle.getLifeTime()<=0){
                particles.remove(i);
            }
        }
        if(particles.size()<maxParticles && System.currentTimeMillis()-lastCreatedParticle>=particleCreationPeriod){
            createParticle();
            this.lastCreatedParticle = System.currentTimeMillis();
        }
    }
    
    private void createParticle(){
        Particle particle = new Particle(this.defaultParticle);
        float sign = Math.random() > 0.5d ? -1.0f : 1.0f;
        float speedInc = sign * (float)Math.random() * this.speedRange;
        float posInc = sign * (float)Math.random() * this.positionRange;
        float scaleInc = sign * (float)Math.random() * this.scaleRange;
        
        particle.setSpeed(particle.getSpeed() + speedInc);
        particle.setPosition(particle.getPosition().add(new Vector2f(posInc, posInc)));
        particle.setScale(particle.getScale() + scaleInc);
        particles.add(particle);
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return Collections.singletonList(TransformComponent.class);
    }
    
    @Override
    public void toBytes(ByteBuf out){
    
    }
    
    @Override
    public void fromBytes(ByteBuf in){
    
    }
}
