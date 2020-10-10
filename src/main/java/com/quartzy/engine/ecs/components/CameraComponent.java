package com.quartzy.engine.ecs.components;

import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.Component;
import com.quartzy.engine.graphics.Renderer;
import com.quartzy.engine.graphics.Window;
import com.quartzy.engine.math.Matrix4f;
import com.quartzy.engine.math.Vector3f;
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
    
    @Getter
    private Vector3f cameraPos;
    
    private boolean mainOnStartup;
    private boolean changed;
    
    public CameraComponent(Matrix4f projectionMatrix){
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = new Matrix4f();
        this.modelMatrix = new Matrix4f();
        this.cameraPos = new Vector3f();
    }
    
    public CameraComponent(Matrix4f viewMatrix, Matrix4f projectionMatrix){
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.modelMatrix = new Matrix4f();
        this.cameraPos = new Vector3f();
    }
    
    public CameraComponent(Matrix4f modelMatrix, Matrix4f viewMatrix, Matrix4f projectionMatrix){
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.cameraPos = new Vector3f();
    }
    
    public CameraComponent(){
        this.viewMatrix = new Matrix4f();
        this.modelMatrix = new Matrix4f();
        this.cameraPos = new Vector3f();
    }
    
    
    public CameraComponent(Matrix4f projectionMatrix, boolean main){
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = new Matrix4f();
        this.modelMatrix = new Matrix4f();
        this.mainOnStartup = main;
        this.cameraPos = new Vector3f();
    }
    
    public CameraComponent(Matrix4f viewMatrix, Matrix4f projectionMatrix, boolean main){
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.modelMatrix = new Matrix4f();
        this.mainOnStartup = main;
        this.cameraPos = new Vector3f();
    }
    
    public CameraComponent(Matrix4f modelMatrix, Matrix4f viewMatrix, Matrix4f projectionMatrix, boolean main){
        this.modelMatrix = modelMatrix;
        this.viewMatrix = viewMatrix;
        this.projectionMatrix = projectionMatrix;
        this.mainOnStartup = main;
        this.cameraPos = new Vector3f();
    }
    
    public CameraComponent(boolean main){
        this.viewMatrix = new Matrix4f();
        this.modelMatrix = new Matrix4f();
        this.mainOnStartup = main;
        this.cameraPos = new Vector3f();
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
    
    public void updateViewport(int newWidth, int newHeight, int x, int y){
        this.projectionMatrix = Matrix4f.orthographic(x, newWidth, y, newHeight, -1f, 1f);
        if(NetworkManager.INSTANCE.getSide()== Side.CLIENT){
            Client.getInstance().getRenderer().setUniformsUI(new Matrix4f(), new Matrix4f(), this.projectionMatrix);
        }
        changed = true;
    }
    
    public void translateView(float x, float y, float z){
        this.viewMatrix = this.viewMatrix.multiply(Matrix4f.translate(x, y, z));
        cameraPos = cameraPos.add(new Vector3f(x, y, z));
        changed = true;
    }
    
    public void setCameraPos(Vector3f newPos){
        this.viewMatrix = this.viewMatrix.multiply(Matrix4f.translate(newPos.x, newPos.y, newPos.z));
        cameraPos = newPos;
        changed = true;
    }
    
    public void setCameraPos(float x, float y, float z){
        this.setCameraPos(new Vector3f(x, y ,z));
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return null;
    }
    
    @Override
    public void toBytes(ByteBuf out){
        out.writeFloat(cameraPos.x);
        out.writeFloat(cameraPos.y);
        out.writeFloat(cameraPos.z);
        out.writeBoolean(mainOnStartup);
    }
    
    @Override
    public void fromBytes(ByteBuf in){
        float x = in.readFloat();
        float y = in.readFloat();
        float z = in.readFloat();
        boolean b = in.readBoolean();
        this.cameraPos = new Vector3f(x, y, z);
        this.mainOnStartup = b;
        this.changed = true;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        CameraComponent that = (CameraComponent) o;
        
        if(modelMatrix != null ? !modelMatrix.equals(that.modelMatrix) : that.modelMatrix != null) return false;
        if(viewMatrix != null ? !viewMatrix.equals(that.viewMatrix) : that.viewMatrix != null) return false;
        if(projectionMatrix != null ? !projectionMatrix.equals(that.projectionMatrix) : that.projectionMatrix != null)
            return false;
        return cameraPos != null ? cameraPos.equals(that.cameraPos) : that.cameraPos == null;
    }
    
    @Override
    public int hashCode(){
        int result = modelMatrix != null ? modelMatrix.hashCode() : 0;
        result = 31 * result + (viewMatrix != null ? viewMatrix.hashCode() : 0);
        result = 31 * result + (projectionMatrix != null ? projectionMatrix.hashCode() : 0);
        result = 31 * result + (cameraPos != null ? cameraPos.hashCode() : 0);
        return result;
    }
}
