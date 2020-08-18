package com.quartzy.engine.world;

import com.quartzy.engine.ecs.ECSManager;
import com.quartzy.engine.ecs.Particle;
import com.quartzy.engine.ecs.components.*;
import com.quartzy.engine.graphics.Renderer;
import com.quartzy.engine.graphics.Texture;
import com.quartzy.engine.math.Vector2f;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class World{
    
    @Getter
    @Setter
    private static World currentWorld;
    
    @Getter
    private String name;
    @Getter
    private org.dyn4j.dynamics.World physicsWorld;
    @Getter
    private ECSManager ecsManager;
    
    private boolean firstRun;
    
    /**
     * @param name Name of the world
     */
    public World(String name){
        this.name = name;
        this.physicsWorld = new org.dyn4j.dynamics.World();
        this.ecsManager = new ECSManager(this);
        this.firstRun = true;
    }
    
    /**
     * Called once per frame. Used for rendering the world and everything in it
     * @param renderer Renderer object
     */
    public void render(Renderer renderer){
        HashMap<Short, TextureComponent> allEntitiesWithComponent = ecsManager.getAllEntitiesWithComponent(TextureComponent.class);
        if(allEntitiesWithComponent==null || allEntitiesWithComponent.isEmpty())return;
        {
            int k = 0;
            Texture prevTexture = null;
            renderer.begin();
            for(Map.Entry<Short, TextureComponent> shortComponentEntry : allEntitiesWithComponent.entrySet()){
                TransformComponent component = ecsManager.getComponent(shortComponentEntry.getKey(), TransformComponent.class);
                if(component == null) continue;
                TextureComponent textureComponent = shortComponentEntry.getValue();
                Texture texture = textureComponent.getTexture();
                if(!texture.equals(prevTexture)){
                    prevTexture = texture;
                    texture.bind(k);
                    k++;
                    if(k >= renderer.getMaxTextureSlots()){
                        k = 0;
                        renderer.end();
                        renderer.begin();
                    }
                }
                renderer.drawTextureRegion((float) component.getTransform().getTranslationX(), (float) component.getTransform().getTranslationY(), textureComponent.getTexture().getWidth(), textureComponent.getTexture().getHeight(), k-1);
            }
            renderer.end();
        }
    
        HashMap<Short, ParticleEmitterComponent> allParticleEmitters = this.ecsManager.getAllEntitiesWithComponent(ParticleEmitterComponent.class);
        if(allParticleEmitters!=null && !allParticleEmitters.isEmpty()){
            int k = 0;
            Texture prevTexture = null;
            renderer.begin();
            for(ParticleEmitterComponent emitter : allParticleEmitters.values()){
                for(Particle particle : emitter.getParticles()){
                    Texture texture = particle.getTexture();
                    if(!texture.equals(prevTexture)){
                        prevTexture = texture;
                        texture.bind(k);
                        k++;
                        if(k >= renderer.getMaxTextureSlots()){
                            k = 0;
                            renderer.end();
                            renderer.begin();
                        }
                    }
                    Vector2f size = particle.getBaseSize().add(new Vector2f(particle.getScale(), particle.getScale()));
                    renderer.drawTextureRegion(particle.getPosition().x, particle.getPosition().y, size.x, size.y, k-1);
                }
            }
            renderer.end();
        }
    }
    
    /**
     * used for updating all of the entities and tiles in the world
     * @param delta Time in seconds since last frame
     */
    public void update(float delta){
        if(this.firstRun){
            ecsManager.initComponents();
            this.firstRun = false;
        }else {
            for(BehaviourComponent value : ecsManager.getAllEntitiesWithComponent(BehaviourComponent.class).values()){
                value.update(delta);
            }
        }
        this.physicsWorld.update(delta);
        HashMap<Short, RigidBodyComponent> allEntitiesWithComponent = ecsManager.getAllEntitiesWithComponent(RigidBodyComponent.class);
        if(allEntitiesWithComponent!=null && !allEntitiesWithComponent.isEmpty()){
            Collection<RigidBodyComponent> values = allEntitiesWithComponent.values();
            for(RigidBodyComponent value : values){
                value.updateTransform();
            }
        }
        HashMap<Short, ParticleEmitterComponent> allParticleEmitters = this.ecsManager.getAllEntitiesWithComponent(ParticleEmitterComponent.class);
        if(allParticleEmitters!=null && !allParticleEmitters.isEmpty()){
            for(ParticleEmitterComponent emitter : allParticleEmitters.values()){
                emitter.update(delta);
            }
        }
    }
}
