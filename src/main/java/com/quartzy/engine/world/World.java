package com.quartzy.engine.world;

import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.ecs.ComponentManager;
import com.quartzy.engine.ecs.ECSManager;
import com.quartzy.engine.ecs.Particle;
import com.quartzy.engine.ecs.components.*;
import com.quartzy.engine.graphics.Renderer;
import com.quartzy.engine.graphics.Texture;
import com.quartzy.engine.math.Vector2f;
import com.quartzy.engine.math.Vector3f;
import com.quartzy.engine.utils.Resource;
import com.quartzy.engine.utils.ResourceType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        CameraComponent mainCamera = renderer.getMainCamera();
        if(mainCamera!=null)mainCamera.update();
        HashMap<Short, TextureComponent> allTextures = ecsManager.getAllEntitiesWithComponent(TextureComponent.class);
        if(allTextures!=null && !allTextures.isEmpty()){
            int k = 0;
            Texture prevTexture = null;
            renderer.begin();
            for(Map.Entry<Short, TextureComponent> shortComponentEntry : allTextures.entrySet()){
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
                renderer.drawTextureRegion((float) component.getTransform().getTranslationX(), (float) component.getTransform().getTranslationY(), textureComponent.getTexture().getWidth(), textureComponent.getTexture().getHeight(), k - 1);
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
        if(LightSourceComponent.isAnyChanged()){
            HashMap<Short, LightSourceComponent> allLights = this.ecsManager.getAllEntitiesWithComponent(LightSourceComponent.class);
            if(allLights != null && !allLights.isEmpty()){
                int i = 0;
                Vector3f[] positions = new Vector3f[renderer.getMaxLightsPerDrawCall()];
                Vector3f[] colors = new Vector3f[renderer.getMaxLightsPerDrawCall()];
                for(LightSourceComponent light : allLights.values()){
                    if(i < renderer.getMaxLightsPerDrawCall()){
                        positions[i] = light.getPosition();
                        colors[i] = light.getColor();
                        i++;
                    } else{
                        break;
                    }
                }
                if(i < renderer.getMaxLightsPerDrawCall()){
                    for(int j = i; j < renderer.getMaxLightsPerDrawCall(); j++){
                        positions[j] = new Vector3f(0, 0, 0);
                        colors[j] = new Vector3f(0, 0, 0);
                    }
                }
                renderer.getProgram().bind();
                renderer.getProgram().setUniform("lightPosition", positions);
                renderer.getProgram().setUniform("lightColors", colors);
            }
        }
    
        HashMap<Short, CustomRenderComponent> allCustomRenderers = ecsManager.getAllEntitiesWithComponent(CustomRenderComponent.class);
        if(allCustomRenderers!=null && !allCustomRenderers.isEmpty()){
            for(CustomRenderComponent value : allCustomRenderers.values()){
                value.render(renderer);
            }
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
            HashMap<Short, BehaviourComponent> allBehaviours = ecsManager.getAllEntitiesWithComponent(BehaviourComponent.class);
            if(allBehaviours!=null && !allBehaviours.isEmpty()){
                for(BehaviourComponent value : allBehaviours.values()){
                    value.update(delta);
                }
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
        HashMap<Short, CustomRenderComponent> allCustomRenderers = ecsManager.getAllEntitiesWithComponent(CustomRenderComponent.class);
        if(allCustomRenderers!=null && !allCustomRenderers.isEmpty()){
            for(CustomRenderComponent value : allCustomRenderers.values()){
                value.update(delta);
            }
        }
    }
    
    public static Resource saveToFile(String folderPath, World world, short... entityBlacklist){
        String name = world.name;
        Path path = Paths.get(folderPath, name + ".wrld");
        File file = path.toFile();
        return saveToFile(file, world, entityBlacklist);
    }
    
    @SneakyThrows
    public static Resource saveToFile(File file, World world, short... entityBlacklist){
        if(!file.exists()){
            file.createNewFile();
        }
        String name = world.getName();
        ByteBuf bytes = Unpooled.buffer();
        bytes.writeByte(name.length());
        bytes.writeCharSequence(name, StandardCharsets.US_ASCII);
    
        HashMap<Class<? extends Component>, ComponentManager> components = world.ecsManager.getComponents();
        int actualAmount5 = 0;
        for(ComponentManager value : components.values()){
            HashMap<Short, Component> components1 = value.getComponents();
            int left = components1.size();
            for(Short aShort : components1.keySet()){
                for(int i = 0; i < entityBlacklist.length; i++){
                    if(entityBlacklist[i] == aShort){
                        left--;
                        break;
                    }
                }
            }
            if(left!=0)actualAmount5++;
        }
        bytes.writeShort(actualAmount5);
        for(ComponentManager value : components.values()){
    
            Set<Map.Entry<Short, Component>> set = value.getComponents().entrySet();
            int actualAmount = 0;
            for(Map.Entry<Short, Component> entry : set){
                for(int i = 0; i < entityBlacklist.length; i++){
                    if(entityBlacklist[i] == entry.getKey())break;
                    if(i== entityBlacklist.length-1){
                        actualAmount++;
                    }
                }
            }
            if(actualAmount==0)continue;
            String name1 = value.getType().getName();
            bytes.writeByte(name1.length());
            bytes.writeCharSequence(name1, StandardCharsets.US_ASCII);
            bytes.writeInt(actualAmount);
            
            for(Map.Entry<Short, Component> entry : set){
    
                boolean canPass = true;
                for(int i = 0; i < entityBlacklist.length; i++){
                    if(entityBlacklist[i] == entry.getKey()){
                        canPass = false;
                    }
                    if(!canPass)break;
                }
                if(!canPass)continue;
                
                bytes.writeShort(entry.getKey());
    
                entry.getValue().toBytes(bytes);
            }
        }
    
        HashMap<Integer, List<Short>> layers = world.getEcsManager().getLayers();
        int actualAmount1 = 0;
        for(Map.Entry<Integer, List<Short>> entry : layers.entrySet()){
            for(Short entry1 : entry.getValue()){
                boolean next = false;
                for(int i = 0; i < entityBlacklist.length; i++){
                    if(entityBlacklist[i] != entry1){
                        actualAmount1++;
                        next = true;
                        break;
                    }
                }
                if(next)break;
            }
        }
        bytes.writeShort(actualAmount1);
        for(Map.Entry<Integer, List<Short>> entry : layers.entrySet()){
            int actualAmount = 0;
            for(Short entry1 : entry.getValue()){
                for(int i = 0; i < entityBlacklist.length; i++){
                    if(entityBlacklist[i] == entry1)break;
                    if(i == entityBlacklist.length-1){
                        actualAmount++;
                    }
                }
            }
            bytes.writeShort(entry.getKey());
            bytes.writeInt(actualAmount);
    
            for(Short aShort : entry.getValue()){
                boolean canContinue = true;
                for(int i = 0; i < entityBlacklist.length; i++){
                    if(entityBlacklist[i] == aShort){
                        canContinue = false;
                        break;
                    }
                }
                if(!canContinue)continue;
                bytes.writeShort(aShort);
            }
        }
    
        HashMap<String, Short> tags = world.getEcsManager().getTags();
        int actualAmount2 = 0;
        for(Short entry1 : tags.values()){
            for(int i = 0; i < entityBlacklist.length; i++){
                if(entityBlacklist[i] == entry1)break;
                if(i == entityBlacklist.length-1){
                    actualAmount2++;
                }
            }
        }
        bytes.writeInt(actualAmount2);
        for(Map.Entry<String, Short> entry : tags.entrySet()){
            boolean canPass = true;
            for(int i = 0; i < entityBlacklist.length; i++){
                if(entityBlacklist[i] == entry.getValue()){
                    canPass = false;
                }
                if(!canPass)break;
            }
            if(!canPass)continue;
            
            bytes.writeByte(entry.getKey().length());
            bytes.writeCharSequence(entry.getKey(), StandardCharsets.US_ASCII);
            bytes.writeShort(entry.getValue());
        }
    
        byte[] compBytes = new byte[bytes.readableBytes()];
        bytes.readBytes(compBytes);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(compBytes);
        }
        
        return new Resource(file, world.name, ResourceType.WORLD_FILE, world.getName());
    }
    
    
    public static World loadWorld(String pathname, short... entitiesToAdd){
        return loadWorld(Paths.get(pathname).toFile(), entitiesToAdd);
    }
    
    public static World loadWorld(File file, short... entitiesToAdd){
        if(!file.exists())return null;
        byte[] bytes;
        try{
            bytes = Files.readAllBytes(file.toPath());
        } catch(IOException e){
            return null;
        }
        if(bytes.length == 0)return null;
        try{
            ByteBuf in = Unpooled.wrappedBuffer(bytes);
    
            byte worldNameLen = in.readByte();
            String worldName = in.readCharSequence(worldNameLen, StandardCharsets.US_ASCII).toString();
            World world = new World(worldName);
    
            short componentCount = in.readShort();
    
            ECSManager ecsManager = world.getEcsManager();
            for(int i = 0; i < componentCount; i++){
                byte componentNameLen = in.readByte();
                String componentName = in.readCharSequence(componentNameLen, StandardCharsets.US_ASCII).toString();
        
                Class<? extends Component> componentClass = (Class<? extends Component>) Class.forName(componentName);
        
                int valAmount = in.readInt();
                for(int j = 0; j < valAmount; j++){
                    Component component = componentClass.newInstance();
                    short entityId = in.readShort();
                    component.fromBytes(in);
                    ecsManager.addComponentToEntityNoCheck(entityId, component);
                }
            }
    
            short layerAmount = in.readShort();
            for(int i = 0; i < layerAmount; i++){
                short layerId = in.readShort();
        
                int layerEntityCount = in.readInt();
                for(int i1 = 0; i1 < layerEntityCount; i1++){
                    short entityId = in.readShort();
                    ecsManager.addEntityToLayer(entityId, layerId);
                }
            }
    
            int tayAmount = in.readInt();
            for(int i = 0; i < tayAmount; i++){
                byte tagNameLen = in.readByte();
                String tagName = in.readCharSequence(tagNameLen, StandardCharsets.US_ASCII).toString();
                short entityId = in.readShort();
                ecsManager.setEntityTag(entityId, tagName);
            }
    
            if(entitiesToAdd!=null && entitiesToAdd.length!=0){
                World currentWorld = World.getCurrentWorld();
                if(currentWorld != null){
                    HashMap<Class<? extends Component>, ComponentManager> components = currentWorld.getEcsManager().getComponents();
                    if(components != null && !components.isEmpty()){
                        Collection<ComponentManager> values = components.values();
                        if(values != null && !values.isEmpty()){
                            for(ComponentManager value : values){
                                for(int i = 0; i < entitiesToAdd.length; i++){
                                    Component component = value.getComponent(entitiesToAdd[i]);
                                    if(component != null){
                                        world.getEcsManager().addComponentToEntityNoCheck(entitiesToAdd[i], component);
                                        world.getEcsManager().addEntityToInitBlacklist(entitiesToAdd[i]);
                                    }
                                }
                            }
                        }
                    }
                    HashMap<String, Short> tags = currentWorld.getEcsManager().getTags();
                    if(tags != null && !tags.isEmpty()){
                        for(Map.Entry<String, Short> entry : tags.entrySet()){
                            for(int i = 0; i < entitiesToAdd.length; i++){
                                if(entitiesToAdd[i] == entry.getValue())
                                    world.getEcsManager().setEntityTag(entitiesToAdd[i], entry.getKey());
                            }
                        }
                    }
                    HashMap<Integer, List<Short>> layers = currentWorld.getEcsManager().getLayers();
                    if(layers != null && !layers.isEmpty()){
                        for(Map.Entry<Integer, List<Short>> entry : layers.entrySet()){
                            for(Short aShort : entry.getValue()){
                                for(int i = 0; i < entitiesToAdd.length; i++){
                                    if(aShort == entitiesToAdd[i])
                                        world.getEcsManager().addEntityToLayer(entitiesToAdd[i], entry.getKey());
                                }
                            }
                        }
                    }
                }
            }
    
            return world;
        }catch(IndexOutOfBoundsException e){
            return null;
        } catch(IllegalAccessException | InstantiationException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
}
