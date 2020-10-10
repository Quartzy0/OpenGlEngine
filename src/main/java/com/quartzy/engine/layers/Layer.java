package com.quartzy.engine.layers;

import com.quartzy.engine.graphics.Renderer;

public abstract class Layer{
    public abstract void update(float delta);
    public abstract void render(Renderer renderer);
}
