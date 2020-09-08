import com.quartzy.engine.ecs.components.Behaviour;
import com.quartzy.engine.input.Input;
import com.quartzy.engine.world.World;
import lombok.CustomLog;
import org.lwjgl.glfw.GLFW;

@CustomLog
public class SaveTestBehaviour extends Behaviour{
    @Override
    public void start(){
        log.info("Initialized " + entityId);
    }
    
    @Override
    public void update(float delta){
        if(Input.getKeyboard().isKeyDown(GLFW.GLFW_KEY_H)){
            World world1 = World.loadWorld("worlds/testing/Tester_man.wrld", Game.yes);
            World.setCurrentWorld(world1);
        }
    }
}
