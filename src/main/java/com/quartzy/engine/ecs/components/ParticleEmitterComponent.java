package com.quartzy.engine.ecs.components;

import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.ecs.Particle;
import com.quartzy.engine.graphics.Texture;
import com.quartzy.engine.math.Vector2f;
import com.quartzy.engine.utils.Resource;
import io.netty.buffer.ByteBuf;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CustomLog
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
    
    public ParticleEmitterComponent(){
    }
    
    @Override
    public void init(){
        this.particles = new ArrayList<>();
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
        out.writeFloat(speedRange);
        out.writeFloat(positionRange);
        out.writeFloat(scaleRange);
        out.writeInt(maxParticles);
        
        out.writeLong(particleCreationPeriod);
        
        out.writeFloat(positionOffset.x);
        out.writeFloat(positionOffset.y);
        
        out.writeBoolean(active);
        
        out.writeFloat(defaultParticle.getSpeed());
        out.writeLong(defaultParticle.getLifeTime());
        out.writeFloat(defaultParticle.getBaseSize().x);
        out.writeFloat(defaultParticle.getBaseSize().y);
        out.writeFloat(defaultParticle.getDirection().x);
        out.writeFloat(defaultParticle.getDirection().y);
    
        Resource resource = defaultParticle.getTexture().getResource();
        if(resource==null){
            log.warning("Texture has no resource attached and therefore can't be saved. This will cause problems later when loading!");
            return;
        }
        String name = resource.getName();
        out.writeInt(name.length());
        out.writeCharSequence(name, StandardCharsets.US_ASCII);
    }
    
    @Override
    public void fromBytes(ByteBuf in){
        this.speedRange = in.readFloat();
        this.positionRange = in.readFloat();
        this.scaleRange = in.readFloat();
        this.maxParticles = in.readInt();
        
        this.particleCreationPeriod = in.readLong();
    
        float x = in.readFloat();
        float y = in.readFloat();
        this.positionOffset = new Vector2f(x, y);
        
        this.active = in.readBoolean();
    
        float speed = in.readFloat();
        long lifetime = in.readLong();
        float sizeX = in.readFloat();
        float sizeY = in.readFloat();
        float directionX = in.readFloat();
        float directionY = in.readFloat();
        int textureNameLen = in.readInt();
        String textureName = in.readCharSequence(textureNameLen, StandardCharsets.US_ASCII).toString();
        Texture texture = Client.getInstance().getTextureManager().getTexture(textureName);
        this.defaultParticle = new Particle(texture, lifetime, speed, new Vector2f(directionX, directionY), new Vector2f(sizeX, sizeY));
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        ParticleEmitterComponent that = (ParticleEmitterComponent) o;
        
        if(maxParticles != that.maxParticles) return false;
        if(Float.compare(that.speedRange, speedRange) != 0) return false;
        if(Float.compare(that.positionRange, positionRange) != 0) return false;
        if(Float.compare(that.scaleRange, scaleRange) != 0) return false;
        if(particleCreationPeriod != that.particleCreationPeriod) return false;
        if(defaultParticle != null ? !defaultParticle.equals(that.defaultParticle) : that.defaultParticle != null)
            return false;
        return positionOffset != null ? positionOffset.equals(that.positionOffset) : that.positionOffset == null;
    }
    
    @Override
    public int hashCode(){
        int result = defaultParticle != null ? defaultParticle.hashCode() : 0;
        result = 31 * result + maxParticles;
        result = 31 * result + (speedRange != +0.0f ? Float.floatToIntBits(speedRange) : 0);
        result = 31 * result + (positionRange != +0.0f ? Float.floatToIntBits(positionRange) : 0);
        result = 31 * result + (scaleRange != +0.0f ? Float.floatToIntBits(scaleRange) : 0);
        result = 31 * result + (int) (particleCreationPeriod ^ (particleCreationPeriod >>> 32));
        result = 31 * result + (positionOffset != null ? positionOffset.hashCode() : 0);
        return result;
    }
}
