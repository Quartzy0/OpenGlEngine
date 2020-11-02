package com.quartzy.engine.world;

import com.google.gson.*;
import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.ecs.ComponentManager;
import com.quartzy.engine.ecs.ECSManager;
import com.quartzy.engine.ecs.Particle;
import com.quartzy.engine.ecs.components.*;
import com.quartzy.engine.graphics.Color;
import com.quartzy.engine.graphics.Renderer;
import com.quartzy.engine.graphics.Texture;
import com.quartzy.engine.math.Vector2f;
import com.quartzy.engine.math.Vector3f;
import com.quartzy.engine.utils.Resource;
import com.quartzy.engine.utils.ResourceType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@CustomLog
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
        HashMap<Short, List<TextureComponent>> allTextures = ecsManager.getAllEntitiesWithComponent(TextureComponent.class);
        if(allTextures!=null && !allTextures.isEmpty()){
            int k = 0;
            Texture prevTexture = null;
            renderer.begin();
            for(Map.Entry<Short, List<TextureComponent>> shortComponentEntry : allTextures.entrySet()){
                for(TextureComponent textureComponent : shortComponentEntry.getValue()){
                    TransformComponent component = ecsManager.getComponent(shortComponentEntry.getKey(), TransformComponent.class);
                    if(component == null) continue;
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
                    renderer.drawTextureRegion(((float) component.getTransform().getTranslationX()) + textureComponent.getOffset().x, ((float) component.getTransform().getTranslationY()) + textureComponent.getOffset().y, textureComponent.getTexture().getWidth(), textureComponent.getTexture().getHeight(), textureComponent.getColor(), k - 1);
                }
            }
            renderer.end();
        }
    
        HashMap<Short, List<ParticleEmitterComponent>> allParticleEmitters = this.ecsManager.getAllEntitiesWithComponent(ParticleEmitterComponent.class);
        if(allParticleEmitters!=null && !allParticleEmitters.isEmpty()){
            int k = 0;
            Texture prevTexture = null;
            renderer.begin();
            for(List<ParticleEmitterComponent> emitters : allParticleEmitters.values()){
                for(ParticleEmitterComponent emitter : emitters){
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
            }
            renderer.end();
        }
        if(LightSourceComponent.isAnyChanged()){
            HashMap<Short, List<LightSourceComponent>> allLights = this.ecsManager.getAllEntitiesWithComponent(LightSourceComponent.class);
            if(allLights != null && !allLights.isEmpty()){
                int i = 0;
                Vector3f[] positions = new Vector3f[renderer.getMaxLightsPerDrawCall()];
                Vector3f[] colors = new Vector3f[renderer.getMaxLightsPerDrawCall()];
                for(List<LightSourceComponent> lights : allLights.values()){
                    for(LightSourceComponent light : lights){
                        if(i < renderer.getMaxLightsPerDrawCall()){
                            positions[i] = light.getPosition();
                            colors[i] = light.getColorVec();
                            i++;
                        } else{
                            break;
                        }
                        if(i >= renderer.getMaxLightsPerDrawCall())break;
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
    
        HashMap<Short, List<CustomRenderComponent>> allCustomRenderers = ecsManager.getAllEntitiesWithComponent(CustomRenderComponent.class);
        if(allCustomRenderers!=null && !allCustomRenderers.isEmpty()){
            for(List<CustomRenderComponent> values : allCustomRenderers.values()){
                for(CustomRenderComponent value : values){
                    value.render(renderer);
                }
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
            HashMap<Short, List<BehaviourComponent>> allBehaviours = ecsManager.getAllEntitiesWithComponent(BehaviourComponent.class);
            if(allBehaviours!=null && !allBehaviours.isEmpty()){
                for(List<BehaviourComponent> values : allBehaviours.values()){
                    for(BehaviourComponent value : values){
                        value.update(delta);
                    }
                }
            }
        }
        this.physicsWorld.update(delta);
        HashMap<Short, List<RigidBodyComponent>> allEntitiesWithComponent = ecsManager.getAllEntitiesWithComponent(RigidBodyComponent.class);
        if(allEntitiesWithComponent!=null && !allEntitiesWithComponent.isEmpty()){
            for(List<RigidBodyComponent> values : allEntitiesWithComponent.values()){
                for(RigidBodyComponent value : values){
                    value.updateTransform();
                }
            }
        }
        HashMap<Short, List<ParticleEmitterComponent>> allParticleEmitters = this.ecsManager.getAllEntitiesWithComponent(ParticleEmitterComponent.class);
        if(allParticleEmitters!=null && !allParticleEmitters.isEmpty()){
            for(List<ParticleEmitterComponent> emitters : allParticleEmitters.values()){
                for(ParticleEmitterComponent emitter : emitters){
                    emitter.update(delta);
                }
            }
        }
        HashMap<Short, List<CustomRenderComponent>> allCustomRenderers = ecsManager.getAllEntitiesWithComponent(CustomRenderComponent.class);
        if(allCustomRenderers!=null && !allCustomRenderers.isEmpty()){
            for(List<CustomRenderComponent> values : allCustomRenderers.values()){
                for(CustomRenderComponent value : values){
                    value.update(delta);
                }
            }
        }
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        World world = (World) o;
        
        if(!name.equals(world.name)) return false;
        return ecsManager.equals(world.ecsManager);
    }
    
    @Override
    public int hashCode(){
        int result = name.hashCode();
        result = 31 * result + ecsManager.hashCode();
        return result;
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
        JsonObject worldObject = new JsonObject();
        worldObject.addProperty("name", world.name);
        
        JsonArray componentsArray = new JsonArray();
        ECSManager ecsManager = world.ecsManager;
        for(ComponentManager value : ecsManager.getComponents().values()){
            HashMap<Short, List<Component>> components = value.getComponents();
            JsonArray componentArray = new JsonArray();
            for(Map.Entry<Short, List<Component>> entry : components.entrySet()){
                if(entityBlacklist!=null && entityBlacklist.length!=0){
                    boolean skipEntity = false;
                    for(int i = 0; i < entityBlacklist.length; i++){
                        if(entityBlacklist[i]==entry.getKey()){
                            skipEntity = true;
                            break;
                        }
                    }
                    if(skipEntity)continue;
                }
    
                for(Component component : entry.getValue()){
                    JsonObject jsonObject = component.toJson();
                    jsonObject.addProperty("__component_type__", component.getClass().getName());
                    jsonObject.addProperty("__entity_id__", entry.getKey());
                    componentArray.add(jsonObject);
                }
            }
            componentsArray.add(componentArray);
        }
        worldObject.add("components", componentsArray);
        
        JsonArray layersArray = new JsonArray();
        for(Map.Entry<Integer, List<Short>> entry : ecsManager.getLayers().entrySet()){
            JsonArray layerArray = new JsonArray();
            JsonObject layerObject = new JsonObject();
            layerObject.addProperty("layer_id", entry.getKey());
    
            for(Short aShort : entry.getValue()){
                if(entityBlacklist!=null && entityBlacklist.length!=0){
                    boolean skip = false;
                    for(int i = 0; i < entityBlacklist.length; i++){
                        if(entityBlacklist[i]==aShort){
                            skip = true;
                            break;
                        }
                    }
                    if(skip)continue;
                }
                layerArray.add(aShort);
            }
            layerObject.add("layer_array", layerArray);
            layersArray.add(layerObject);
        }
        worldObject.add("layers", layersArray);
        
        JsonArray tagsArray = new JsonArray();
        for(Map.Entry<Short, String> entry : ecsManager.getTags().entrySet()){
            if(entityBlacklist!=null && entityBlacklist.length!=0){
                boolean skip = false;
                for(int i = 0; i < entityBlacklist.length; i++){
                    if(entityBlacklist[i]==entry.getKey()){
                        skip = true;
                        break;
                    }
                }
                if(skip)continue;
            }
            
            JsonObject tagObject = new JsonObject();
            tagObject.addProperty("entity_id", entry.getKey());
            tagObject.addProperty("tag", entry.getValue());
            tagsArray.add(tagObject);
        }
        worldObject.add("tags", tagsArray);
        
        FileWriter fw = new FileWriter(file);
        fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(worldObject));
        fw.close();
        
        return new Resource(file, world.name, ResourceType.WORLD_FILE, world.getName());
    }
    
    
    public static World loadWorld(String pathname, short... entitiesToAdd){
        return loadWorld(Paths.get(pathname).toFile(), entitiesToAdd);
    }
    
    public static World loadWorld(File file, short... entitiesToAdd){
        if(!file.exists() || !file.isFile())return null;
        try{
            Scanner scanner = new Scanner(file);
            String jsonString = "";
            while(scanner.hasNextLine()){
                jsonString+=scanner.nextLine() + "\n";
            }
            scanner.close();
            JsonObject worldObject = JsonParser.parseString(jsonString).getAsJsonObject();
            World world = new World(worldObject.get("name").getAsString());
    
            ECSManager ecsManager = world.ecsManager;
            JsonArray componentsArray = worldObject.getAsJsonArray("components");
            for(JsonElement jsonElement : componentsArray){
                JsonArray component = jsonElement.getAsJsonArray();
                for(JsonElement element : component){
                    JsonObject comp = element.getAsJsonObject();
                    short entity_id = comp.get("__entity_id__").getAsShort();
                    String component_type = comp.get("__component_type__").getAsString();
                    
                    Class<? extends Component> componentClass = (Class<? extends Component>) Class.forName(component_type);
    
                    Component componentInstance = componentClass.newInstance();
                    componentInstance.fromJson(comp);
                    
                    ecsManager.addComponentToEntityNoCheck(entity_id, componentInstance);
                }
            }
    
            JsonArray layersArray = worldObject.getAsJsonArray("layers");
            for(JsonElement jsonElement : layersArray){
                JsonObject layersObject = jsonElement.getAsJsonObject();
                int layer_id = layersObject.get("layer_id").getAsInt();
                JsonArray layer_array = layersObject.get("layer_array").getAsJsonArray();
    
                for(JsonElement element : layer_array){
                    short entityId = element.getAsShort();
                    ecsManager.addEntityToLayer(entityId, layer_id);
                }
            }
    
            JsonArray tagsArray = worldObject.getAsJsonArray("tags");
            for(JsonElement jsonElement : tagsArray){
                JsonObject tagObject = jsonElement.getAsJsonObject();
                short entity_id = tagObject.get("entity_id").getAsShort();
                String tag = tagObject.get("tag").getAsString();
                
                ecsManager.setEntityTag(entity_id, tag);
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
                                    List<Component> components1 = value.getComponents(entitiesToAdd[i]);
                                    if(components1 == null || components1.isEmpty()) continue;
                                    for(Component component : components1){
                                        if(component != null){
                                            world.getEcsManager().addComponentToEntityNoCheck(entitiesToAdd[i], component);
                                            world.getEcsManager().addEntityToInitBlacklist(entitiesToAdd[i]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    HashMap<Short, String> tags = currentWorld.getEcsManager().getTags();
                    if(tags != null && !tags.isEmpty()){
                        for(Map.Entry<Short, String> entry : tags.entrySet()){
                            for(int i = 0; i < entitiesToAdd.length; i++){
                                if(entitiesToAdd[i] == entry.getKey())
                                    world.getEcsManager().setEntityTag(entitiesToAdd[i], entry.getValue());
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
        } catch(IllegalAccessException | InstantiationException | ClassNotFoundException | FileNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
}
