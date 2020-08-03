/*
package com.quartzy.engine.world;

import com.quartzy.engine.Client;
import com.quartzy.engine.entities.Entity;
import com.quartzy.engine.utils.Resource;
import com.quartzy.engine.utils.ResourceManager;
import lombok.CustomLog;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@CustomLog
public class WorldLoader{
    
    */
/**
     * Saves the world object to a file which can
     * later be loaded with WorldLoader#loadWorld
     *
     * @param world The world that will get saved
     * @param resourceManager The Resource Manager object that is used to create the world file resource
     *
     * @return A Resource object for the world file that was created
     * *//*

    public static Resource saveWorld(World world, ResourceManager resourceManager){
        try{
            Resource resource = resourceManager.addResource("worlds/" + world.getName() + ".wrld");
            File file = resource.getFile();
            if(!file.exists())file.createNewFile();
            log.info("Saving world '%s' to %s", world.getName(), file.getAbsolutePath());
            DataOutputStream os = new DataOutputStream(new FileOutputStream(file));
            byte[] bytes = world.getName().getBytes(StandardCharsets.US_ASCII);
            os.writeInt(bytes.length);
            os.write(bytes);
            os.writeInt(world.getWidth());
            os.writeInt(world.getHeight());
            Entity[][] tiles = world.getTiles();
            byte[] tilesB = new byte[tiles.length * tiles[0].length];
            int j = 0;
            for(int i = 0; i < tiles.length; i++){
                for(int i1 = 0; i1 < tiles[i].length; i1++){
                    tilesB[j] = (byte) World.getTileId(tiles[i][i1]);
                    j++;
                }
            }
            os.write(tilesB);
            os.close();
            return resource;
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    
    */
/**
     * Saves the world object to a file which can
     * later be loaded with WorldLoader#loadWorld
     *
     * @param world The world that will get saved
     * @param clientObj A Game object that is used to create the world file resource
     *
     * @return A Resource object for the world file that was created
    * *//*

    public static Resource saveWorld(World world, Client clientObj){
        try{
            Resource resource = clientObj.getResourceManager().addResource("worlds/" + world.getName() + ".wrld");
            File file = resource.getFile();
            if(!file.exists())file.createNewFile();
            log.info("Saving world '%s' to %s", world.getName(), file.getAbsolutePath());
            DataOutputStream os = new DataOutputStream(new FileOutputStream(file));
            byte[] bytes = world.getName().getBytes(StandardCharsets.US_ASCII);
            os.writeInt(bytes.length);
            os.write(bytes);
            os.writeInt(world.getWidth());
            os.writeInt(world.getHeight());
            Entity[][] tiles = world.getTiles();
            byte[] tilesB = new byte[tiles.length * tiles[0].length];
            int j = 0;
            for(int i = 0; i < tiles.length; i++){
                for(int i1 = 0; i1 < tiles[i].length; i1++){
                    tilesB[j] = (byte) World.getTileId(tiles[i][i1]);
                    j++;
                }
            }
            os.write(tilesB);
            os.close();
            return resource;
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    
    
    */
/**
     * Loads a world object form the file it was saved to
     *
     * @param worldRes The Resource object for the world file
     *
     * @return The world object that was loaded for the file
    * *//*

    public static World loadWorld(Resource worldRes){
        try{
            log.info("Loading world from %s", worldRes.getFile().getAbsolutePath());
            byte[] bytes = Files.readAllBytes(worldRes.getFile().toPath());
            int b1 = bytes[0], b2 = bytes[1], b3 = bytes[2], b4 = bytes[3];
            int nameLen = ((b1 << 24) + (b2 << 16) + (b3 << 8) + (b4 << 0));
            byte[] nameB = new byte[nameLen];
            for(int i = 0; i < nameB.length; i++){
                nameB[i] = bytes[i+4];
            }
            String name = new String(nameB);
            int b11 = bytes[nameLen+4], b21 = bytes[nameLen+4+1], b31 = bytes[nameLen+4+2], b41 = bytes[nameLen+4+3];
            int width = ((b11 << 24) + (b21 << 16) + (b31 << 8) + (b41 << 0));
            int b12 = bytes[nameLen+8], b22 = bytes[nameLen+8+1], b32 = bytes[nameLen+8+2], b42 = bytes[nameLen+8+3];
            int height  = ((b12 << 24) + (b22 << 16) + (b32 << 8) + (b42 << 0));
            Entity[][] tiles = new Entity[height][width];
            for(int i = 0; i < tiles.length; i++){
                for(int j = 0; j < tiles[i].length; j++){
                    byte b = bytes[12+nameLen+j+i*tiles.length];
                    tiles[i][j] = World.getTileById(b<0 ? b+256 : b);
                }
            }
            return new World(name, tiles);
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
    
    public static World loadWorldFromByteTiles(byte[] tiles, int width, int height, String name){
        log.info("Loading world %s from bytes", name);
        Entity[][] tilesE = new Entity[width][height];
        int i = 0;
        for(int i1 = 0; i1 < tilesE.length; i1++){
            for(int i2 = 0; i2 < tilesE[i1].length; i2++){
                tilesE[i1][i2] = World.getTileById(tiles[i]);
                i++;
            }
        }
        return new World(name, tilesE);
    }
}
*/
