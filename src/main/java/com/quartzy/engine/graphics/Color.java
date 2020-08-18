package com.quartzy.engine.graphics;

import lombok.Getter;
import lombok.Setter;

public class Color{
    public static final Color WHITE = new Color(1f, 1f, 1f, 1f);
    public static final Color RED = new Color(1f, 0f, 0f, 1f);
    public static final Color GREEN = new Color(0f, 1f, 0f, 1f);
    public static final Color BLUE = new Color(0f, 0f, 1f, 1f);
    public static final Color BLACK = new Color(0f, 0f, 0f, 1f);
    public static final Color TRANSPARENT = new Color(0f, 0f, 0f, 0f);
    
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
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        Color color = (Color) o;
        
        if(Float.compare(color.red, red) != 0) return false;
        if(Float.compare(color.green, green) != 0) return false;
        if(Float.compare(color.blue, blue) != 0) return false;
        return Float.compare(color.alpha, alpha) == 0;
    }
    
    @Override
    public int hashCode(){
        int result = (red != +0.0f ? Float.floatToIntBits(red) : 0);
        result = 31 * result + (green != +0.0f ? Float.floatToIntBits(green) : 0);
        result = 31 * result + (blue != +0.0f ? Float.floatToIntBits(blue) : 0);
        result = 31 * result + (alpha != +0.0f ? Float.floatToIntBits(alpha) : 0);
        return result;
    }
}
