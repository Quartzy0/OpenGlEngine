import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.components.Behaviour;
import com.quartzy.engine.ecs.components.TransformComponent;
import com.quartzy.engine.input.Input;
import com.quartzy.engine.input.Keyboard;
import lombok.CustomLog;
import org.lwjgl.glfw.GLFW;

@CustomLog
public class TestBehaviour extends Behaviour{
    private TransformComponent transform;
    private double speed = 100;
    
    @Override
    public void start(){
        log.debug("Entered start method");
        this.transform = getComponent(TransformComponent.class);
    }
    
    @Override
    public void update(float delta){
        Keyboard keyboard = Input.getKeyboard();
        if(keyboard.isKeyDown(GLFW.GLFW_KEY_I)){
            this.transform.setPosition(200, 200);
        }
    
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_W)){
            this.transform.translate(0, speed*delta);
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_S)){
            this.transform.translate(0, -speed*delta);
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_A)){
            this.transform.translate(-speed*delta, 0);
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_D)){
            this.transform.translate(speed*delta, 0);
        }
        
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_UP)){
            Client.getInstance().getWindow().setWidth(Client.getInstance().getWindow().getWidth()+1);
        }
        if(keyboard.isKeyPressed(GLFW.GLFW_KEY_DOWN)){
            Client.getInstance().getWindow().setWidth(Client.getInstance().getWindow().getWidth()-1);
        }
    }
}
