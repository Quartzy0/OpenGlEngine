package com.quartzy.engine.ecs.components;

import com.quartzy.engine.ecs.Component;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rotation;
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
    
    @Override
    public void toBytes(ByteBuf out){
        out.writeDouble(body.getMass().getMass());
        out.writeDouble(body.getMass().getInertia());
        out.writeDouble(body.getMass().getCenter().x);
        out.writeDouble(body.getMass().getCenter().y);
        out.writeDouble(body.getTransform().getTranslationX());
        out.writeDouble(body.getTransform().getTranslationY());
        out.writeDouble(body.getTransform().getRotationAngle());
    }
    
    @Override
    public void fromBytes(ByteBuf in){
        double mass = in.readDouble();
        double inertia = in.readDouble();
        double centreX = in.readDouble();
        double centreY = in.readDouble();
        double x = in.readDouble();
        double y = in.readDouble();
        double rotAngle = in.readDouble();
        this.body.setMass(new Mass(new Vector2(centreX, centreY), mass, inertia));
        this.body.getTransform().setTranslation(x, y);
        this.body.getTransform().setRotation(new Rotation(rotAngle));
    }
}
