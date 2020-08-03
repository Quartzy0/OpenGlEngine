import com.quartzy.engine.ecs.components.Behaviour;
import com.quartzy.engine.ecs.components.RigidBodyComponent;
import com.quartzy.engine.events.Input;
import lombok.CustomLog;
import org.dyn4j.dynamics.Force;
import org.dyn4j.geometry.Vector2;
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
            getComponent(RigidBodyComponent.class).getBody().applyImpulse(new Vector2(0, 10));
        }
    }
}
