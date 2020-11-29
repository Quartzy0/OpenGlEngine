package com.quartzy.engine.ecs.components;

import com.google.gson.JsonObject;
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
import org.dyn4j.geometry.Vector3;
import org.joml.Matrix4d;

import java.util.List;

public class CameraComponent extends Component{
    @Getter
    private Matrix4f modelMatrix, viewMatrix, projectionMatrix;
    
    @Getter
    private Vector3f cameraPos;
    
    @Getter
    private float scale = 1;
    
    @Getter
    @Setter
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
            this.projectionMatrix = Renderer.makeProjectionMatrix(window.getWidth(), window.getHeight());
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
            Client.getInstance().getRenderer().setUniforms(this.modelMatrix, this.viewMatrix, this.projectionMatrix, this.scale);
            changed = false;
        }
    }
    
    public void updateViewport(int newWidth, int newHeight, int x, int y){
        this.projectionMatrix = Renderer.makeProjectionMatrix(newWidth, newHeight);
        if(NetworkManager.INSTANCE.getSide()== Side.CLIENT){
//            Client.getInstance().getRenderer().setUniformsUI(this.modelMatrix, this.viewMatrix, this.projectionMatrix, this.scale);
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
    
    public void setScale(float scale){
        if(scale<=0)return;
        this.scale = scale;
        this.changed = true;
    }
    
    public void addScale(float scale){
        this.setScale(this.scale + scale);
    }
    
    @Override
    public List<Class<? extends Component>> requiredComponents(){
        return null;
    }
    
    @Override
    public boolean canHaveMultiple(){
        return false;
    }
    
    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("camera_pos_x", this.cameraPos.x);
        jsonObject.addProperty("camera_pos_y", this.cameraPos.y);
        jsonObject.addProperty("camera_pos_z", this.cameraPos.z);
    
        jsonObject.addProperty("main_on_startup", this.mainOnStartup);
        
        return jsonObject;
    }
    
    @Override
    public void fromJson(JsonObject in){
        float camera_pos_x = in.get("camera_pos_x").getAsFloat();
        float camera_pos_y = in.get("camera_pos_y").getAsFloat();
        float camera_pos_z = in.get("camera_pos_z").getAsFloat();
        
        this.mainOnStartup = in.get("main_on_startup").getAsBoolean();
        
        this.cameraPos = new Vector3f(camera_pos_x, camera_pos_y, camera_pos_z);
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
