package com.quartzy.engine.world;

import com.quartzy.engine.ecs.ECSManager;
import com.quartzy.engine.ecs.components.Behaviour;
import com.quartzy.engine.ecs.components.BehaviourComponent;
import com.quartzy.engine.ecs.components.RigidBodyComponent;
import com.quartzy.engine.ecs.components.TextureComponent;
import com.quartzy.engine.graphics.Renderer;
import com.quartzy.engine.graphics.Texture;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class World{
    
//    @Getter
//    private static HashMap<Integer, Entity> allTiles = new HashMap<>();
//    public static final float TILE_WIDTH = 64f, TILE_HEIGHT = 64f;
    
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
//        Texture texture = tiles[0][0].getTexture();
//        Texture texture1 = tiles[1][1].getTexture();
//        renderer.begin();
//        texture.bind(0);
//        texture1.bind(1);
//        renderer.drawTextureRegion(200, 200, 100, 100, 0);
//        renderer.drawTextureRegion(100, 0, 100, 100, 1);
//        renderer.end();
        
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
        
//
//
//        for(int i = 0; i < entities.size(); i++){
//            renderer.begin();
//            entities.get(i).render(renderer);
//            renderer.end();
//        }
    }
    
    /**
     * used for updating all of the entities and tiles in the world
     * @param delta Time in seconds since last frame
     */
    public void update(float delta){
        if(this.firstRun){
            for(BehaviourComponent value : ecsManager.getAllEntitiesWithComponent(BehaviourComponent.class).values()){
                value.start();
            }
            this.firstRun = false;
        }else {
            for(BehaviourComponent value : ecsManager.getAllEntitiesWithComponent(BehaviourComponent.class).values()){
                value.update(delta);
            }
        }
        this.physicsWorld.update(delta);
    }
    
//    /**
//     * Registers a new tile that can be used in all of the worlds
//     * @param id Tile id
//     * @param texture Texture of the tile
//     * @param name Name of the tile
//     */
//    public static void registerNewTile(int id, Texture texture, String name){
//        if(allTiles.containsKey(id)){
//            throw new IllegalArgumentException("Tile by this id already exists");
//        }
//        allTiles.put(id, new Entity(TILE_WIDTH, TILE_HEIGHT, new Vector2f(0, 0), texture, name).addComponent(new PhysicsBody()));
//    }
    
//    /**
//     * @param id Id of the tiles
//     * @return The entity object of that tile
//     */
//    public static Entity getTileById(int id){
//        Entity entity = allTiles.get(id);
//        if(entity==null)return null;
//        return entity.clone();
//    }
    
//    /**
//     * Loads all tiles from a .tdata file
//     * @param resourceManager Resource manager object
//     */
//    public static void loadTilesFromFile(ResourceManager resourceManager){
//        File file = resourceManager.addResource("data/.tdata").getFile();
//        try{
//            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
//            String line;
//            while((line = bufferedReader.readLine())!=null){
//                String[] values = line.split(",");
//                int id = Integer.parseInt(values[0]);
//                String name = values[1];
//                String texturePath = values[2];
//                Resource resource = resourceManager.addResource(texturePath);
//                registerNewTile(id, resourceManager.getTextureManager().getTexture(resource.getName()), name);
//            }
//        } catch(IOException e){
//            e.printStackTrace();
//        }
//    }
    
//    /**
//     * Loads all tiles from a .tdata file without loading the textures. This is used on the server
//     * @param resourceManager Resource manager object
//     */
//    public static void loadTilesFromFileNoTex(ResourceManager resourceManager){
//        File file = resourceManager.addResource("data/.tdata").getFile();
//        try{
//            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
//            String line;
//            while((line = bufferedReader.readLine())!=null){
//                String[] values = line.split(",");
//                int id = Integer.parseInt(values[0]);
//                String name = values[1];
//                registerNewTile(id, null, name);
//            }
//        } catch(IOException e){
//            e.printStackTrace();
//        }
//    }
    
//    /**
//     * @param tile Entity object of a tile
//     * @return The id of the entity tile object
//     */
//    public static int getTileId(Entity tile){
//        for(Map.Entry<Integer, Entity> entry : allTiles.entrySet()){
//            if(entry.getValue().equals(tile)){
//                return entry.getKey();
//            }
//        }
//        return -1;
//    }
}
