import com.quartzy.engine.ApplicationClient;
import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.ECSManager;
import com.quartzy.engine.ecs.Particle;
import com.quartzy.engine.ecs.components.*;
import com.quartzy.engine.graphics.Texture;
import com.quartzy.engine.graphics.Window;
import com.quartzy.engine.math.Vector2f;
import com.quartzy.engine.world.World;
import org.dyn4j.geometry.Transform;

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
        client.getResourceManager().addResource("textures/particles/flame.png");
        client.getResourceManager().addResource("audio/explosion.ogg");
        World.setCurrentWorld(world);
    
        ECSManager ecsManager = world.getEcsManager();
        Texture stone = client.getTextureManager().getTexture("stone");
        Texture flame = client.getTextureManager().getTexture("flame");
        {
            short entity = ecsManager.createBlankObject();
            Transform transform = new Transform();
            transform.setTranslation(20, 20);
            ecsManager.addComponentToEntity(entity, new TransformComponent(transform));
            ecsManager.addComponentToEntity(entity, new AudioSourceComponent());
            ecsManager.addComponentToEntity(entity, new TextureComponent(stone));
            ecsManager.addComponentToEntity(entity, new BehaviourComponent(TestBehaviour.class));
        }
        {
            short entity = ecsManager.createBlankObject();
            Transform transform = new Transform();
            transform.setTranslation(200, 200);
            ecsManager.addComponentToEntity(entity, new TransformComponent(transform));
            ecsManager.addComponentToEntity(entity, new AudioListenerComponent(true));
            ecsManager.addComponentToEntity(entity, new TextureComponent(stone));
        }
        {
            short entity = ecsManager.createBlankObject();
            Transform transform = new Transform();
            transform.setTranslation(200, 300);
            ecsManager.addComponentToEntity(entity, new TransformComponent(transform));
            Particle defaultParticle = new Particle(flame, 1000, 50, new Vector2f(-1f, 0f), new Vector2f(16f, 16f));
            ecsManager.addComponentToEntity(entity, new ParticleEmitterComponent(defaultParticle, 20, 5, 2, 3, 50));
        }
    }
    
    @Override
    public void preStart(Client client){
    
    }
    
    @Override
    public void dispose(Client client){
    
    }
}
