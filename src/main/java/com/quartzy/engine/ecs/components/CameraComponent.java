package com.quartzy.engine.ecs.components;

import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.graphics.Renderer;
import com.quartzy.engine.graphics.Window;
import com.quartzy.engine.math.Matrix4f;
import com.quartzy.engine.network.NetworkManager;
import com.quartzy.engine.network.Side;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CameraComponent extends Component{
    @Getter
    @Setter
    private Matrix4f modelMatrix, viewMatrix, projectionMatrix;
    
    private boolean mainOnStartup;
    private boolean changed;
    
    public CameraComponent(Matrix4f projectionMatrix){
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = new Matrix4f();
        this.modelMatrix = new Matrix4f();
    }
    
    public CameraComponent(Matrix4f viewMatrix, Matrix4f projectionMatrix){
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.modelMatrix = new Matrix4f();
    }
    
    public CameraComponent(Matrix4f modelMatrix, Matrix4f viewMatrix, Matrix4f projectionMatrix){
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
    }
    
    public CameraComponent(){
        this.viewMatrix = new Matrix4f();
        this.modelMatrix = new Matrix4f();
    }
    
    
    public CameraComponent(Matrix4f projectionMatrix, boolean main){
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = new Matrix4f();
        this.modelMatrix = new Matrix4f();
        this.mainOnStartup = main;
    }
    
    public CameraComponent(Matrix4f viewMatrix, Matrix4f projectionMatrix, boolean main){
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.modelMatrix = new Matrix4f();
        this.mainOnStartup = main;
    }
    
    public CameraComponent(Matrix4f modelMatrix, Matrix4f viewMatrix, Matrix4f projectionMatrix, boolean main){
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.mainOnStartup = main;
    }
    
    public CameraComponent(boolean main){
        this.viewMatrix = new Matrix4f();
        this.modelMatrix = new Matrix4f();
        this.mainOnStartup = main;
    }
    
    @Override
    public void init(){
        if(NetworkManager.INSTANCE.getSide()==Side.CLIENT && this.projectionMatrix==null){
            Window window = Client.getInstance().getWindow();
            this.projectionMatrix = Matrix4f.orthographic(0f, window.getWidth(), 0f, window.getHeight(), -1f, 1f);
        }
        if(this.mainOnStartup) setAsMain();
    }
    
    private void setAsMain(){
        if(NetworkManager.INSTANCE.getSide()== Side.CLIENT){
            Client.getInstance().getRenderer().setMainCamera(this);
        }
    }
    
    public void update(){
        if(!changed)return;
        if(NetworkManager.INSTANCE.getSide()== Side.CLIENT){
            Client.getInstance().getRenderer().setUniforms(this.modelMatrix, this.viewMatrix, this.projectionMatrix);
            changed = false;
        }
    }
    
    public void updateViewport(int newWidth, int newHeight){
        this.projectionMatrix = Matrix4f.orthographic(0f, newWidth, 0f, newHeight, -1f, 1f);
        if(NetworkManager.INSTANCE.getSide()== Side.CLIENT){
            Client.getInstance().getRenderer().setUniformsUI(new Matrix4f(), new Matrix4f(), this.projectionMatrix);
        }
        changed = true;
    }
    
    public void translateView(float x, float y, float z){
        this.viewMatrix = this.viewMatrix.multiply(Matrix4f.translate(x, y, z));
        changed = true;
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return null;
    }
    
    @Override
    public void toBytes(ByteBuf out){
    
    }
    
    @Override
    public void fromBytes(ByteBuf in){
    
    }
}
