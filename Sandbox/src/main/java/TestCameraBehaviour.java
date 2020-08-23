import com.quartzy.engine.ecs.components.Behaviour;
import com.quartzy.engine.ecs.components.CameraComponent;
import com.quartzy.engine.input.Input;
import com.quartzy.engine.input.Keyboard;
import org.lwjgl.glfw.GLFW;

public class TestCameraBehaviour extends Behaviour{
    private CameraComponent camera;
    private double speed = 100f;
    
    @Override
    public void start(){
        this.camera = getComponent(CameraComponent.class);
    }
    
    @Override
    public void update(float delta){
        Keyboard keyboard = Input.getKeyboard();
        
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_W)){
            this.camera.translateView(0, (float) (-speed*delta), 0);
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_S)){
            this.camera.translateView(0, (float) (speed*delta), 0);
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_A)){
            this.camera.translateView((float) (speed*delta), 0, 0);
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_D)){
            this.camera.translateView((float) (-speed*delta), 0, 0);
        }
    }
}
