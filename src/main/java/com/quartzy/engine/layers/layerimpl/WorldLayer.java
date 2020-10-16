package com.quartzy.engine.layers.layerimpl;

import com.quartzy.engine.graphics.Renderer;
import com.quartzy.engine.layers.Layer;
import com.quartzy.engine.layers.SubscribeEvent;
import com.quartzy.engine.layers.events.*;
import com.quartzy.engine.network.NetworkManager;
import com.quartzy.engine.network.Side;
import com.quartzy.engine.world.World;
import lombok.Getter;

import java.util.HashMap;

public class WorldLayer extends Layer{
    
    @Getter
    private final HashMap<Integer, Boolean> keyStates = new HashMap<>();
    @Getter
    private final HashMap<Integer, Boolean> mouseButtonStates = new HashMap<>();
    @Getter
    private float mouseX = 0, mouseY = 0;
    
    @SubscribeEvent
    public void keyPressed(KeyPressedEvent event){
        keyStates.put(event.getKeyCode(), true);
    }
    
    @SubscribeEvent
    public void keyReleased(KeyReleasedEvent event){
        keyStates.put(event.getKeyCode(), false);
    }
    
    @SubscribeEvent
    public void mouseButtonPressedEvent(MouseButtonPressedEvent event){
        mouseButtonStates.put(event.getButton(), true);
    }
    
    @SubscribeEvent
    public void mouseButtonPressedEvent(MouseButtonReleasedEvent event){
        mouseButtonStates.put(event.getButton(), false);
    }
    
    @SubscribeEvent
    public void mouseMoved(CursorMoveEvent event){
        mouseX = (float) event.getX();
        mouseY = (float) event.getY();
    }
    
    @Override
    public void update(float delta){
        World currentWorld = World.getCurrentWorld();
        if(currentWorld!=null){
            currentWorld.update(delta);
        }
    }
    
    @Override
    public void render(Renderer renderer){
        World currentWorld = World.getCurrentWorld();
        if(currentWorld!=null && NetworkManager.INSTANCE.getSide()==Side.CLIENT){
            currentWorld.render(renderer);
        }
    }
    
    @Override
    public void onDetach(){
    
    }
    
    @Override
    public void onAttach(){
    
    }
}
