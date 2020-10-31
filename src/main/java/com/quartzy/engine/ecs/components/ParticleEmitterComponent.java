package com.quartzy.engine.ecs.components;

import com.google.gson.JsonObject;
import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.ecs.Particle;
import com.quartzy.engine.graphics.Texture;
import com.quartzy.engine.math.Vector2f;
import com.quartzy.engine.network.NetworkManager;
import com.quartzy.engine.network.Side;
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
    public boolean canHaveMultiple(){
        return true;
    }
    
    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        
        JsonObject particleObject = new JsonObject();
        particleObject.addProperty("lifetime", this.defaultParticle.getLifeTime());
        particleObject.addProperty("speed", this.defaultParticle.getSpeed());
        particleObject.addProperty("scale", this.defaultParticle.getScale());
        particleObject.addProperty("texture", this.defaultParticle.getTexture().getResource().getName());
        
        particleObject.addProperty("dir_x", this.defaultParticle.getDirection().x);
        particleObject.addProperty("dir_y", this.defaultParticle.getDirection().y);
    
        particleObject.addProperty("pos_x", this.defaultParticle.getPosition().x);
        particleObject.addProperty("pos_y", this.defaultParticle.getPosition().y);
    
        particleObject.addProperty("size_x", this.defaultParticle.getBaseSize().x);
        particleObject.addProperty("size_y", this.defaultParticle.getBaseSize().y);
        
        jsonObject.add("particle", particleObject);
        
        jsonObject.addProperty("max", maxParticles);
        jsonObject.addProperty("speed", speedRange);
        jsonObject.addProperty("pos", positionRange);
        jsonObject.addProperty("scale", scaleRange);
        jsonObject.addProperty("period", particleCreationPeriod);
    
        jsonObject.addProperty("pos_x", positionOffset.x);
        jsonObject.addProperty("pos_y", positionOffset.y);
        
        jsonObject.addProperty("active", active);
        
        return jsonObject;
    }
    
    @Override
    public void fromJson(JsonObject in){
        JsonObject particle = in.getAsJsonObject("particle");
    
        Texture texture = null;
        if(NetworkManager.INSTANCE.getSide() == Side.CLIENT){
            texture = Client.getInstance().getTextureManager().getTexture(particle.get("texture").getAsString());
        }
        Vector2f dir = new Vector2f(particle.get("dir_x").getAsFloat(), particle.get("dir_y").getAsFloat());
        Vector2f size = new Vector2f(particle.get("size_x").getAsFloat(), particle.get("size_y").getAsFloat());
        this.defaultParticle = new Particle(texture, particle.get("lifetime").getAsLong(), particle.get("speed").getAsFloat(), dir, size);
        
        this.active = in.get("active").getAsBoolean();
        
        this.maxParticles = in.get("max").getAsInt();
        this.speedRange = in.get("speed").getAsFloat();
        this.positionRange = in.get("pos").getAsFloat();
        this.scaleRange = in.get("scale").getAsFloat();
        this.particleCreationPeriod = in.get("period").getAsLong();
        
        this.positionOffset = new Vector2f(in.get("pos_x").getAsFloat(), in.get("pos_y").getAsFloat());
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
