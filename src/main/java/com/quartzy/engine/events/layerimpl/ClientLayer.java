package com.quartzy.engine.events.layerimpl;

import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.components.CustomRenderComponent;
import com.quartzy.engine.events.Layer;
import com.quartzy.engine.events.SubscribeEvent;
import com.quartzy.engine.events.impl.WindowResizeEvent;
import com.quartzy.engine.world.World;

import java.util.HashMap;

public class ClientLayer extends Layer{
    
    @SubscribeEvent
    public void windowResize(WindowResizeEvent event){
        Client.getInstance().getRenderer().updateViewport(event.getNewWidth(), event.getNewHeight());
        HashMap<Short, CustomRenderComponent> allCustomRenderers = World.getCurrentWorld().getEcsManager().getAllEntitiesWithComponent(CustomRenderComponent.class);
        if(allCustomRenderers != null && !allCustomRenderers.isEmpty()){
            for(CustomRenderComponent value : allCustomRenderers.values()){
                value.resizeWindow(event.getNewWidth(), event.getNewHeight());
            }
        }
    }
}
