import com.quartzy.engine.ApplicationClient;
import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.ECSManager;
import com.quartzy.engine.ecs.Particle;
import com.quartzy.engine.ecs.components.*;
import com.quartzy.engine.graphics.Texture;
import com.quartzy.engine.graphics.Window;
import com.quartzy.engine.math.Vector2f;
import com.quartzy.engine.math.Vector3f;
import com.quartzy.engine.world.World;
import org.dyn4j.geometry.Transform;

import java.io.File;

public class Game implements ApplicationClient{
    @Override
    public Window preInit(String[] args, Client client){
        client.setFragmentShader("shaders/fragment.glsl");
        client.setVertexShader("shaders/vertex.glsl");
        return new Window("Sandbox", 600, 400);
    }
    
    @Override
    public void init(Client client){
//        World world = new World("Tester_man");
        client.getResourceManager().addResource("textures/tiles/stone.png");
        client.getResourceManager().addResource("textures/particles/flame.png");
        client.getResourceManager().addResource("audio/explosion.ogg");
        World world = World.loadWorld("worlds/Tester_man.wrld");
        World.setCurrentWorld(world);
    
//        ECSManager ecsManager = world.getEcsManager();
//        Texture stone = client.getTextureManager().getTexture("stone");
//        Texture flame = client.getTextureManager().getTexture("flame");
//        {
//            short entity = ecsManager.createBlankObject();
//            Transform transform = new Transform();
//            transform.setTranslation(232, 232);
//            ecsManager.addComponentToEntity(entity, new TransformComponent(transform));
//            ecsManager.addComponentToEntity(entity, new AudioSourceComponent());
//            ecsManager.addComponentToEntity(entity, new LightSourceComponent(new Vector3f(1.0f, 0.0f, 0.0f), 50.0f));
//        }
//        {
//            short entity = ecsManager.createBlankObject();
//            Transform transform = new Transform();
//            transform.setTranslation(200, 300);
//            ecsManager.addComponentToEntity(entity, new TransformComponent(transform));
//            Particle defaultParticle = new Particle(flame, 1000, 50, new Vector2f(-1f, 0f), new Vector2f(16f, 16f));
//            ecsManager.addComponentToEntity(entity, new ParticleEmitterComponent(defaultParticle, 20, 5, 2, 3, 50));
//        }
//        {
//            short entity = ecsManager.createBlankObject();
//            ecsManager.addComponentToEntity(entity, new CameraComponent(true));
//            ecsManager.addComponentToEntity(entity, new BehaviourComponent(TestCameraBehaviour.class));
//        }
//
//        for(int i = 0; i < 6; i++){
//            short entity = ecsManager.createBlankObject();
//            Transform transform = new Transform();
//            transform.setTranslation(200 + i*64, 200);
//            ecsManager.addComponentToEntity(entity, new TransformComponent(transform));
//            ecsManager.addComponentToEntity(entity, new TextureComponent(stone));
//        }
    }
    
    @Override
    public void preStart(Client client){
//        World.saveToFile("worlds", World.getCurrentWorld());
    }
    
    @Override
    public void dispose(Client client){
    
    }
}
