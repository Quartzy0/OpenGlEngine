package com.quartzy.engine.graphics;

import com.quartzy.engine.math.*;
import com.quartzy.engine.utils.Logger;
import com.quartzy.engine.utils.Resource;
import lombok.CustomLog;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

@CustomLog
public class ShaderProgram{
    
    private int vertexShader;
    private int fragmentShader;
    private int programId;
    
    private HashMap<String, Integer> uniforms = new HashMap<>();
    
    /**
     * Compiles the shaders
     * @param vertexName Vertex shader name
     * @param fragmentName Fragment shader name
     */
    public ShaderProgram(Resource vertexName, Resource fragmentName){
        log.info("Initializing shader program");
        compileShaders(vertexName.getFile(), fragmentName.getFile());
        this.bind();
        log.info("Done initializing shader program");
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
     * @param vertexShaderFile Vertex shader file
     * @param fragmentShaderFile Fragment shader file
     */
    private void compileShaders(File vertexShaderFile, File fragmentShaderFile){
        String vertexSource = null;
        String fragmentSource = null;
        try{
            vertexSource = new String(Files.readAllBytes(vertexShaderFile.toPath()));
            fragmentSource = new String(Files.readAllBytes(fragmentShaderFile.toPath()));
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
    
    /**
     * Sets the uniform value in the shader program to the specified value
     * @param name Name of the uniform in the shader
     * @param value Value it should be set to
     */
    public void setUniform(String name, Vector4f value){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4);
            value.toBuffer(buffer);
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
