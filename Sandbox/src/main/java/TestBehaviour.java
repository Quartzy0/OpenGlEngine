import com.quartzy.engine.Client;
import com.quartzy.engine.audio.Sound;
import com.quartzy.engine.ecs.components.AudioSourceComponent;
import com.quartzy.engine.ecs.components.Behaviour;
import com.quartzy.engine.ecs.components.RigidBodyComponent;
import com.quartzy.engine.ecs.components.TransformComponent;
import com.quartzy.engine.events.Input;
import lombok.CustomLog;
import org.dyn4j.geometry.Transform;
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
            Client.getInstance().getSoundManager().play("explosion");
        }
        if(Input.getKeyboard().isKeyDown(GLFW.GLFW_KEY_I)){
            getComponent(TransformComponent.class).setPosition(200, 200);
        }
        if(Input.getKeyboard().isKeyDown(GLFW.GLFW_KEY_L)){
            Transform transform = getComponent(TransformComponent.class).getTransform();
            log.debug("Transform x: %f, y: %f", transform.getTranslationX(), transform.getTranslationY());
            Transform transform1 = getComponent(RigidBodyComponent.class).getBody().getTransform();
            log.debug("Body x: %f, y: %f", transform1.getTranslationX(), transform1.getTranslationY());
        }
        if(Input.getKeyboard().isKeyDown(GLFW.GLFW_KEY_O)){
            AudioSourceComponent component = getComponent(AudioSourceComponent.class);
            Sound explosion = Client.getInstance().getSoundManager().getSound("explosion");
            component.play(explosion);
        }
    }
}
