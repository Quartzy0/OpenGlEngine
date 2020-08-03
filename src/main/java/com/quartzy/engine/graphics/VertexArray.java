package com.quartzy.engine.graphics;

import static org.lwjgl.opengl.GL30.*;

public class VertexArray{

    /**
     * Stores the handle of the VAO.
     */
    private final int id;

    /**
     * Creates a Vertex Array Object (VAO).
     */
    public VertexArray() {
        id = glGenVertexArrays();
    }

    /**
     * Binds the VAO.
     */
    public void bind() {
        glBindVertexArray(id);
    }

    /**
     * Deletes the VAO.
     */
    public void delete() {
        glDeleteVertexArrays(id);
    }

    /**
     * Getter for the Vertex Array Object ID.
     *
     * @return Handle of the VAO
     */
    public int getID() {
        return id;
    }

}