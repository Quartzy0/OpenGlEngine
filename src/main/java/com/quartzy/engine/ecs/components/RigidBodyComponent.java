package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.world.World;
import lombok.Getter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;

import java.util.Collections;
import java.util.List;

public class RigidBodyComponent extends Component{
    @Getter
    private Body body;
    
    public RigidBodyComponent(double width, double height){
        body = new Body();
        body.addFixture(Geometry.createRectangle(width, height));
        body.setMass(MassType.NORMAL);
        world.getPhysicsWorld().addBody(body);
    }
    
    public RigidBodyComponent(double width, double height, double x, double y){
        body = new Body();
        body.addFixture(Geometry.createRectangle(width, height));
        body.setMass(MassType.NORMAL);
        body.translate(x, y);
        world.getPhysicsWorld().addBody(body);
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return Collections.emptyList();
    }
}
