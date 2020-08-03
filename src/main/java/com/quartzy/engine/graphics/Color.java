package com.quartzy.engine.graphics;

import lombok.Getter;
import lombok.Setter;

public class Color{
    public static final Color WHITE = new Color(1f, 1f, 1f, 1f);
    public static final Color RED = new Color(1f, 0f, 0f, 1f);
    public static final Color GREEN = new Color(0f, 1f, 0f, 1f);
    public static final Color BLUE = new Color(0f, 0f, 1f, 1f);
    public static final Color BLACK = new Color(0f, 0f, 0f, 1f);
    
    @Getter
    @Setter
    private float red, green, blue, alpha;
    
    /**
     * @param red Red channel
     * @param green Green channel
     * @param blue Blue channel
     * @param alpha Alpha channel
     */
    public Color(float red, float green, float blue, float alpha){
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    
    public Color(float red, float green, float blue){
        this(red, green, blue, 1.0f);
    }
    
    public Color(int red, int green, int blue, int alpha){
        this(red /255F, green /255F, blue /255F, alpha /255F);
    }
    
    public Color(int red, int green, int blue){
        this(red /255F, green /255F, blue /255F, 255);
    }
}
