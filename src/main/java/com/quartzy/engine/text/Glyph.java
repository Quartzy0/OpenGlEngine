package com.quartzy.engine.text;

public class Glyph {
    public final int width;
    public final int height;
    public final int x;
    public final int y;
    
    /**
     * @param width Width of the glyph
     * @param height Height of the glyph
     * @param x X position of the glyph
     * @param y Y position of the glyph
     */
    public Glyph(int width, int height, int x, int y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }
}