import com.quartzy.engine.ApplicationClient;
import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.ECSManager;
import com.quartzy.engine.ecs.Particle;
import com.quartzy.engine.ecs.components.*;
import com.quartzy.engine.graphics.Framebuffer;
import com.quartzy.engine.graphics.Window;
import com.quartzy.engine.math.Vector2f;
import com.quartzy.engine.math.Vector3f;
import com.quartzy.engine.world.World;
import lombok.CustomLog;
import org.dyn4j.geometry.Transform;

@CustomLog
public class Game implements ApplicationClient{
    public static short yes;
    
    @Override
    public Window preInit(String[] args, Client client){
//        client.setFragmentShader("shaders/fragment.glsl");
//        client.setVertexShader("shaders/vertex.glsl");
        return new Window("Sandbox", 1280, 720);
    }
    
    @Override
    public void init(Client client){
        World world = new World("Tester_man");
        client.getResourceManager().addResource("textures/tiles/stone.png");
        client.getResourceManager().addResource("textures/particles/flame.png");
    
        ECSManager ecsManager = world.getEcsManager();
        short blankObject = ecsManager.createObject();
        Transform transform = new Transform();
        transform.translate(200, 200);
        ecsManager.addComponentToEntity(blankObject, new TransformComponent(transform));
        ecsManager.addComponentToEntity(blankObject, new TextureComponent(client.getTextureManager().getTexture("stone")));
        ecsManager.addComponentToEntity(blankObject, new BehaviourComponent(TestBehaviour.class));
    
        yes = ecsManager.createObject();
        Transform transform1 = new Transform();
        transform1.translate(200, 200);
        ecsManager.addComponentToEntity(yes, new TransformComponent(transform1));
        ecsManager.addComponentToEntity(yes, new LightSourceComponent(new Vector3f(1f, 1f, 1f)));
    
        short entity = ecsManager.createObject();
        Transform transform2 = new Transform();
        transform2.translate(300, 300);
        ecsManager.addComponentToEntity(entity, new TransformComponent(transform2));
        Particle particle = new Particle(client.getTextureManager().getTexture("flame"), 2000, 5, new Vector2f(0f, 1f), new Vector2f(16, 16));
        ecsManager.addComponentToEntity(entity, new ParticleEmitterComponent(particle, 5));
        ecsManager.addComponentToEntity(entity, new LightSourceComponent(new Vector3f(0.5f, 0.3f, 0.3f), 30));
        
        for(int i = 0;i<100;i++){
            short blankObject1 = ecsManager.createObject();
            Transform transform5 = new Transform();
            transform5.translate(200+(i*64), 200);
            ecsManager.addComponentToEntity(blankObject1, new TransformComponent(transform5));
            ecsManager.addComponentToEntity(blankObject1, new TextureComponent(client.getTextureManager().getTexture("stone")));
        }
    
        World.setCurrentWorld(world);
    
//        Framebuffer framebuffer = new Framebuffer(client.getWindow().getWidth(), client.getWindow().getHeight());
//        client.getRenderer().setFramebuffer(framebuffer);
    }
    
    @Override
    public void preStart(Client client){
    
    }
    
    @Override
    public void dispose(Client client){
    
    }
}
