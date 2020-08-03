import com.quartzy.engine.ApplicationClient;
import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.ECSManager;
import com.quartzy.engine.ecs.components.BehaviourComponent;
import com.quartzy.engine.ecs.components.RigidBodyComponent;
import com.quartzy.engine.ecs.components.TextureComponent;
import com.quartzy.engine.graphics.Window;
import com.quartzy.engine.world.World;

public class Game implements ApplicationClient{
    @Override
    public Window preInit(String[] args, Client client){
        client.setFragmentShader("shaders/fragment.glsl");
        client.setVertexShader("shaders/vertex.glsl");
        return new Window("Sandbox", 600, 400);
    }
    
    @Override
    public void init(Client client){
        World world = new World("Tester_man");
        client.getResourceManager().addResource("textures/tiles/stone.png");
        World.setCurrentWorld(world);
    
        ECSManager ecsManager = world.getEcsManager();
        short entity = ecsManager.createBlankObject();
        ecsManager.addComponentToEntity(entity, new RigidBodyComponent(64, 64, 200, 200));
        ecsManager.addComponentToEntity(entity, new TextureComponent(client.getTextureManager().getTexture("stone")));
        ecsManager.addComponentToEntity(entity, new BehaviourComponent(TestBehaviour.class));
    }
    
    @Override
    public void preStart(Client client){
    
    }
    
    @Override
    public void dispose(Client client){
    
    }
}
