package com.quartzy.engine.graphics;

import com.quartzy.engine.math.*;
import com.quartzy.engine.utils.Resource;
import lombok.CustomLog;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

@CustomLog
public class ShaderProgram{
    
    private int vertexShader;
    private int fragmentShader;
    private int programId;
    
    private Resource vertexName, fragmentName;
    
    private HashMap<String, Integer> uniforms = new HashMap<>();
    
    private HashMap<String, Integer> definitions = new HashMap<>();
    
    /**
     * Compiles the shaders
     * @param vertexName Vertex shader name
     * @param fragmentName Fragment shader name
     */
    public ShaderProgram(Resource vertexName, Resource fragmentName){
        this.vertexName = vertexName;
        this.fragmentName = fragmentName;
    }
    
    /**
     * Clears all buffers and destroys the shader program
     */
    public void dispose(){
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        glDeleteProgram(programId);
    }
    
    /**
     * Binds the shader program
     */
    public void bind(){
        glUseProgram(programId);
    }
    
    public void unbind(){
        glUseProgram(0);
    }
    
    /**
     * Compiles the shaders and sets the program id
     */
    public void compileShaders(){
        log.info("Initializing shader program");
        String vertexSource = null;
        String fragmentSource = null;
        try{
            vertexSource = new String(Files.readAllBytes(this.vertexName.getFile().toPath()));
            fragmentSource = new String(Files.readAllBytes(this.fragmentName.getFile().toPath()));
        } catch(IOException e){
            log.severe("Error while loading shader files");
            e.printStackTrace();
        }
        
        if(vertexSource==null){
            log.severe("Couldn't load vertex shader file");
            return;
        }
        if(fragmentSource==null){
            log.severe("Couldn't load fragment shader file");
            return;
        }
        
        vertexSource = vertexSource.replace("\r\n", "\n");
        fragmentSource = fragmentSource.replace("\r\n", "\n");
    
        for(Map.Entry<String, Integer> entry : definitions.entrySet()){
            String definitionString = "\n#define " + entry.getKey() + " " + entry.getValue().intValue() + "\n";
            
            StringBuilder builder = new StringBuilder(vertexSource);
            builder.insert(builder.indexOf("\n"), definitionString);
            vertexSource = builder.toString();
    
            StringBuilder builder1 = new StringBuilder(fragmentSource);
            builder1.insert(builder.indexOf("\n"), definitionString);
            fragmentSource = builder1.toString();
        }
        
        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexSource);
        glCompileShader(vertexShader);
        
        int status = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            log.severe("Error while compiling shader files: " + glGetShaderInfoLog(vertexShader));
            return;
        }
        
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentSource);
        glCompileShader(fragmentShader);
        
        status = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            log.severe("Error while compiling shader files: " + glGetShaderInfoLog(fragmentShader));
            return;
        }
        
        programId = glCreateProgram();
        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);
        glBindFragDataLocation(programId, 0, "fragColor");
        glLinkProgram(programId);
        
        status = glGetProgrami(programId, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            log.severe("Error while creating shader program: " + glGetProgramInfoLog(programId));
        }
        
        this.bind();
        log.info("Done initializing shader program");
    }
    
    /**
     * @param name Name of the vertex attribute in the shader
     * @param size The amount of floats in the attribute
     * @param stride The amount of bytes between consecutive vertex attributes
     * @param offset The offset from the previous vertex attribute in bytes
     */
    public void setVertexAttribute(String name, int size, int stride, int offset){
        int loc = glGetAttribLocation(programId, name);
        glEnableVertexAttribArray(loc);
        glVertexAttribPointer(loc, size, GL_FLOAT, false, stride, offset);
    }
    
    public void addDefinition(String name, int value){
        this.definitions.put(name, value);
    }
    
    /**
     * Sets the uniform value in the shader program to the specified value
     * @param name Name of the uniform in the shader
     * @param value Value it should be set to
     */
    public void setUniform(String name, Vector2f value){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2);
            value.toBuffer(buffer);
            glUniform2fv(getUniformLocation(name), buffer);
        }
    }
    
    /**
     * Sets the uniform value in the shader program to the specified value
     * @param name Name of the uniform in the shader
     * @param value Value it should be set to
     */
    public void setUniform(String name, Vector3f value){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3);
            value.toBuffer(buffer);
            glUniform3fv(getUniformLocation(name), buffer);
        }
    }
    
    public void setUniform(String name, int[] arr){
        glUniform1iv(getUniformLocation(name), arr);
    }
    
    public void setUniform(String name, Vector3f[] arr){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3);
            for(int i = 0; i < arr.length; i++){
                arr[i].toBuffer(buffer);
                glUniform3fv(getUniformLocation(name + "[" + i + "]"), buffer);
                buffer.clear();
            }
        }
    }
    
    /**
     * Sets the uniform value in the shader program to the specified value
     * @param name Name of the uniform in the shader
     * @param value Value it should be set to
     */
    public void setUniform(String name, Vector4f value){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4);
            buffer.put(value.x).put(value.y).put(value.z).put(value.w);
            buffer.flip();
            glUniform4fv(getUniformLocation(name), buffer);
        }
    }
    
    /**
     * Sets the uniform value in the shader program to the specified value
     * @param name Name of the uniform in the shader
     * @param value Value it should be set to
     */
    public void setUniform(String name, Matrix2f value){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2*2);
            value.toBuffer(buffer);
            glUniformMatrix2fv(getUniformLocation(name), false, buffer);
        }
    }
    
    /**
     * Sets the uniform value in the shader program to the specified value
     * @param name Name of the uniform in the shader
     * @param value Value it should be set to
     */
    public void setUniform(String name, Matrix3f value){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3*3);
            value.toBuffer(buffer);
            glUniformMatrix3fv(getUniformLocation(name), false, buffer);
        }
    }
    
    /**
     * Sets the uniform value in the shader program to the specified value
     * @param name Name of the uniform in the shader
     * @param value Value it should be set to
     */
    public void setUniform(String name, Matrix4f value){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4*4);
            value.toBuffer(buffer);
            glUniformMatrix4fv(getUniformLocation(name), false, buffer);
        }
    }
    
    /**
     * Sets the uniform value in the shader program to the specified value
     * @param name Name of the uniform in the shader
     * @param value Value it should be set to
     */
    public void setUniform(String name, float value){
        glUniform1f(getUniformLocation(name), value);
    }
    
    /**
     * Sets the uniform value in the shader program to the specified value
     * @param name Name of the uniform in the shader
     * @param value Value it should be set to
     */
    public void setUniform(String name, int value){
        glUniform1i(getUniformLocation(name), value);
    }
    
    /**
     * Sets the uniform value in the shader program to the specified value
     * @param name Name of the uniform in the shader
     * @param value Value it should be set to
     */
    public void setUniform(String name, double value){
        glUniform1f(getUniformLocation(name), (float) value);
    }
    
    /**
     * Finds the location of the uniform in the shader. Once the location is found it is put in a cache and is used next time instead of looking up the location again
     * @param name Name of the uniform in the shader
     * @return The uniform location
     */
    private int getUniformLocation(String name){
        if(uniforms.containsKey(name))return uniforms.get(name);
        int value = glGetUniformLocation(programId, name);
        uniforms.put(name, value);
        return value;
    }
}
