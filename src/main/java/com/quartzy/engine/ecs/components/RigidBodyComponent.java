package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.world.World;
import lombok.Getter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import java.util.Collections;
import java.util.List;

public class RigidBodyComponent extends Component{
    @Getter
    private Body body;
    
    private double width,height;
    
    public RigidBodyComponent(double width, double height){
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void init(){
        TransformComponent component = world.getEcsManager().getComponent(entityId, TransformComponent.class);
        this.body = new Body();
        body.setTransform(component.getTransform());
        body.addFixture(Geometry.createRectangle(width, height));
        body.setMass(new Mass(new Vector2(0, 0), 30, 30));
        world.getPhysicsWorld().addBody(body);
    }
    
    public void updateTransform(){
        TransformComponent component = world.getEcsManager().getComponent(entityId, TransformComponent.class);
        component.setTransform(this.body.getTransform());
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return Collections.singletonList(TransformComponent.class);
    }
}
