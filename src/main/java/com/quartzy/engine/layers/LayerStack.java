package com.quartzy.engine.layers;

import lombok.CustomLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@CustomLog
public class LayerStack{
    
    private ArrayList<Map.Entry<Layer, HashMap<Class<? extends Event>, Method>>> layerStack = new ArrayList<>();
    private List<Class<? extends Event>> usedEvents = new ArrayList<>();
    
    private int layerInsertIndex = 0;
    
    public void triggerEvent(Event event){
        if(!usedEvents.contains(event.getClass()))return;
        for(int i = layerStack.size() - 1; i >= 0; i--){
            Map.Entry<Layer, HashMap<Class<? extends Event>, Method>> entry = layerStack.get(i);
            Layer layer = entry.getKey();
            Method method = entry.getValue().get(event.getClass());
            if(method==null)continue;
            try{
                method.invoke(layer, event);
            } catch(IllegalAccessException | InvocationTargetException e){
                log.severe("Could not invoke method %s in layer %s", e, method.getName(), layer.getClass().getName());
            }
            if(event.isHandled())break;
        }
    }
    
    public Layer popOverlay(){
        if(layerInsertIndex==(layerStack.size()-1))return null;
        Map.Entry<Layer, HashMap<Class<? extends Event>, Method>> entry = layerStack.remove(layerStack.size() - 1);
        if(entry==null)return null;
        return entry.getKey();
    }
    
    public Layer popLayer(){
        if(layerInsertIndex==0)return null;
        layerInsertIndex--;
        Map.Entry<Layer, HashMap<Class<? extends Event>, Method>> entry = layerStack.remove(layerInsertIndex);
        if(entry==null)return null;
        return entry.getKey();
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
        if(!methodMap.isEmpty()){
            layerStack.add(layerInsertIndex, new DefaultEntry<>(layer, methodMap));
            layerInsertIndex++;
        }
    }
    
    public void pushOverlay(Layer layer){
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
            layerStack.add(new DefaultEntry<>(layer, methodMap));
    }
    
    private static final class DefaultEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private final V value;
        
        DefaultEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public K getKey() {
            return key;
        }
        
        @Override
        public V getValue() {
            return value;
        }
        
        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException("read-only");
        }
    }
}
