import com.quartzy.engine.ApplicationClient;
import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.ECSManager;
import com.quartzy.engine.ecs.components.*;
import com.quartzy.engine.graphics.Window;
import com.quartzy.engine.world.World;
import lombok.CustomLog;
import org.dyn4j.geometry.Transform;

@CustomLog
public class Game implements ApplicationClient{
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
    
        short objectToIgnore = ecsManager.createObject();
        Transform transform1 = new Transform();
        transform1.translate(300, 300);
        ecsManager.addComponentToEntity(objectToIgnore, new TransformComponent(transform1));
        ecsManager.addComponentToEntity(objectToIgnore, new AudioSourceComponent());
        ecsManager.setEntityTag(objectToIgnore, "tag#1");
        ecsManager.addEntityToLayer(objectToIgnore, 20);
        
        World.saveToFile("worlds/testing", world, objectToIgnore);
    
        World world1 = World.loadWorld("worlds/testing/Tester_man.wrld");
        System.out.println(world);
        System.out.println(world1);
        World.setCurrentWorld(world1);
    }
    
    @Override
    public void preStart(Client client){
    
    }
    
    @Override
    public void dispose(Client client){
    
    }
}
