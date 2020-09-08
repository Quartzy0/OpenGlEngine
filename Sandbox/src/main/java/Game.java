import com.quartzy.engine.ApplicationClient;
import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.ECSManager;
import com.quartzy.engine.ecs.components.*;
import com.quartzy.engine.graphics.Framebuffer;
import com.quartzy.engine.graphics.Window;
import com.quartzy.engine.world.World;
import lombok.CustomLog;
import org.dyn4j.geometry.Transform;

@CustomLog
public class Game implements ApplicationClient{
    public static short yes;
    
    @Override
    public Window preInit(String[] args, Client client){
        client.setFragmentShader("shaders/fragment.glsl");
        client.setVertexShader("shaders/vertex.glsl");
        return new Window("Sandbox", 1280, 720);
    }
    
    @Override
    public void init(Client client){
        World world = new World("Tester_man");
        client.getResourceManager().addResource("textures/tiles/stone.png");
    
        ECSManager ecsManager = world.getEcsManager();
        short blankObject = ecsManager.createObject("The test object", 23);
        Transform transform = new Transform();
        transform.translate(200, 200);
        ecsManager.addComponentToEntity(blankObject, new TransformComponent(transform));
        ecsManager.addComponentToEntity(blankObject, new TextureComponent(client.getTextureManager().getTexture("stone")));
        ecsManager.addComponentToEntity(blankObject, new BehaviourComponent(TestBehaviour.class));
    
        yes = ecsManager.createObject();
        Transform transform1 = new Transform();
        transform1.translate(300, 300);
        ecsManager.addComponentToEntity(yes, new TransformComponent(transform1));
        ecsManager.addComponentToEntity(yes, new AudioSourceComponent());
        ecsManager.addComponentToEntity(yes, new BehaviourComponent(SaveTestBehaviour.class));
        ecsManager.setEntityTag(yes, "tag#1");
        ecsManager.addEntityToLayer(yes, 20);
        
        World.setCurrentWorld(world);
        
        World.saveToFile("worlds/testing", world, yes);
    
        Framebuffer framebuffer = new Framebuffer(client.getWindow().getWidth(), client.getWindow().getHeight());
        client.getRenderer().setFramebuffer(framebuffer);
    }
    
    @Override
    public void preStart(Client client){
    
    }
    
    @Override
    public void dispose(Client client){
    
    }
}
