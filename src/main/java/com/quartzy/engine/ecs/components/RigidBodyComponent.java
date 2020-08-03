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
    
    private double x,y,width,height;
    
    public RigidBodyComponent(double width, double height){
        this.width = width;
        this.height = height;
        this.x = 0;
        this.y = 0;
    }
    
    public RigidBodyComponent(double width, double height, double x, double y){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void init(){
        this.body = new Body();
        body.addFixture(Geometry.createRectangle(width, height));
        body.setMass(new Mass(new Vector2(0, 0), 30, 30));
        body.translate(x, y);
        world.getPhysicsWorld().addBody(body);
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return Collections.emptyList();
    }
}
