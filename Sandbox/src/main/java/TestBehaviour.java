import com.quartzy.engine.ecs.components.Behaviour;
import com.quartzy.engine.events.Input;
import lombok.CustomLog;
import org.lwjgl.glfw.GLFW;

@CustomLog
public class TestBehaviour extends Behaviour{
    @Override
    public void start(){
        log.debug("Entered start method");
    }
    
    @Override
    public void update(float delta){
        if(Input.getKeyboard().isKeyDown(GLFW.GLFW_KEY_H)){
            log.debug("Works = yes");
        }
    }
}
