package com.quartzy.engine.events;

import lombok.CustomLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@CustomLog
public class EventManager{
    
    private LinkedHashMap<Layer, HashMap<Class<? extends Event>, Method>> layerStack = new LinkedHashMap<>();
    private List<Class<? extends Event>> usedEvents = new ArrayList<>();
    
    public void triggerEvent(Event event){
        if(!usedEvents.contains(event.getClass()))return;
        for(Map.Entry<Layer, HashMap<Class<? extends Event>, Method>> entry : layerStack.entrySet()){
            Layer layer = entry.getKey();
            Method method = entry.getValue().get(event.getClass());
            if(method==null)continue;
            try{
                method.invoke(layer, event);
            } catch(IllegalAccessException | InvocationTargetException e){
                log.crash("Could not invoke method %s from layer %s", e, method.getName(), layer.getClass());
                entry.getValue().remove(event.getClass());
                continue;
            }
            if(event.isHandled()) break;
        }
    }
    
    public Layer popLayer(){
        Layer[] layers = layerStack.keySet().toArray(new Layer[layerStack.size()]);
        Layer layerToRemove = layers[layers.length-1];
        HashMap<Class<? extends Event>, Method> remove = layerStack.remove(layerToRemove);
        return remove!=null ? layerToRemove : null;
    }
    
    public Layer popLayer(int index){
        Layer[] layers = layerStack.keySet().toArray(new Layer[layerStack.size()]);
        if(index>=layers.length)return null;
        Layer layerToRemove = layers[index];
        HashMap<Class<? extends Event>, Method> remove = layerStack.remove(layerToRemove);
        return remove!=null ? layerToRemove : null;
    }
    
    public void pushLayer(Layer layer){
        Class<? extends Layer> clazz = layer.getClass();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        
        HashMap<Class<? extends Event>, Method> methodMap = new HashMap<>();
        for(int i = 0; i < declaredMethods.length; i++){
            if(declaredMethods[i].isAnnotationPresent(SubscribeEvent.class)){
                declaredMethods[i].setAccessible(true);
                Parameter[] parameters = declaredMethods[i].getParameters();
                if(parameters.length==1){
                    Class<?> type = parameters[0].getType();
                    if(Event.class.isAssignableFrom(type)){
                        Class<? extends Event> type1 = (Class<? extends Event>) type;
                        methodMap.put(type1, declaredMethods[i]);
                        if(!usedEvents.contains(type1)){
                            usedEvents.add(type1);
                        }
                    }
                }
            }
        }
        if(!methodMap.isEmpty())
            layerStack.put(layer, methodMap);
    }
}
