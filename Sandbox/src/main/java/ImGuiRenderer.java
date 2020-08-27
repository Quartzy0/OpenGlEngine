import com.quartzy.engine.Client;
import com.quartzy.engine.ecs.components.CustomRenderer;
import com.quartzy.engine.graphics.Renderer;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class ImGuiRenderer extends CustomRenderer{
    private ImGuiImplGl3 implGl3;
    private ImGuiImplGlfw implGlfw;
    
    @Override
    public void start(){
        implGl3 = new ImGuiImplGl3();
        implGlfw = new ImGuiImplGlfw();
    
        ImGui.createContext();
        implGlfw.init(Client.getInstance().getWindow().getId(), true);
        
        ImGui.getIO().setDisplaySize(600, 400);
        implGl3.init("#version 150");
    }
    
    @Override
    public void render(Renderer renderer){
        implGlfw.newFrame();
        ImGui.newFrame();
        
        ImGui.setNextWindowSize(200, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(10, 10, ImGuiCond.Once);
        
        ImGui.begin("Test window");
        
        ImGui.text("Hello there!");
        
        ImGui.end();
        ImGui.render();
        
        implGl3.render(ImGui.getDrawData());
    }
    
    @Override
    public void windowResize(int newWidth, int newHeight){
        ImGui.getIO().setDisplaySize(newWidth, newHeight);
    }
    
    @Override
    public void dispose(){
        implGl3.dispose();
        implGlfw.dispose();
        
        ImGui.destroyContext();
    }
}
