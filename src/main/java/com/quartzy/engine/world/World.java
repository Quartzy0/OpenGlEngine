package com.quartzy.engine.world;

import com.quartzy.engine.ecs.ECSManager;
import com.quartzy.engine.ecs.components.BehaviourComponent;
import com.quartzy.engine.ecs.components.RigidBodyComponent;
import com.quartzy.engine.ecs.components.TextureComponent;
import com.quartzy.engine.graphics.Renderer;
import com.quartzy.engine.graphics.Texture;
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
        int k = 0;
        Texture prevTexture = null;
        renderer.begin();
        for(Map.Entry<Short, TextureComponent> shortComponentEntry : allEntitiesWithComponent.entrySet()){
            RigidBodyComponent component = ecsManager.getComponent(shortComponentEntry.getKey(), RigidBodyComponent.class);
            if(component==null)continue;
            TextureComponent textureComponent = shortComponentEntry.getValue();
            Texture texture = textureComponent.getTexture();
            if(!texture.equals(prevTexture)){
                prevTexture = texture;
                k++;
                if(k>=renderer.getMaxTextureSlots()){
                    k = 0;
                    renderer.end();
                    renderer.begin();
                }
                texture.bind(k);
            }
            renderer.drawTextureRegion((float) component.getBody().getTransform().getTranslationX(), (float) component.getBody().getTransform().getTranslationY(), textureComponent.getTexture().getWidth(), textureComponent.getTexture().getHeight(), k);
        }
        renderer.end();
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
    }
}
