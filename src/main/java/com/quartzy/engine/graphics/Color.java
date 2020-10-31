package com.quartzy.engine.graphics;

import com.google.gson.JsonObject;
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
    
    public void toJson(JsonObject in){
        in.addProperty("color_red", this.red);
        in.addProperty("color_green", this.green);
        in.addProperty("color_blue", this.blue);
        in.addProperty("color_alpha", this.alpha);
    }
    
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        this.toJson(jsonObject);
        return jsonObject;
    }
    
    public boolean isPreDefined(){
        return isPreDefined(this);
    }
    
    public String getPredefinedString(){
        return getPredefinedSting(this);
    }
    
    public static Color fromJson(JsonObject jsonObject){
        float color_red = jsonObject.get("color_red").getAsFloat();
        float color_green = jsonObject.get("color_green").getAsFloat();
        float color_blue = jsonObject.get("color_blue").getAsFloat();
        float color_alpha = jsonObject.get("color_alpha").getAsFloat();
        
        return new Color(color_red, color_green, color_blue, color_alpha);
    }
    
    public static boolean isPreDefined(Color color){
        return color.equals(Color.WHITE) || color.equals(Color.BLACK) || color.equals(Color.BLUE) || color.equals(Color.GREEN) || color.equals(Color.RED) || color.equals(Color.TRANSPARENT);
    }
    
    public static String getPredefinedSting(Color color){
        if(color.equals(Color.WHITE))return "WHITE";
        if(color.equals(Color.BLACK))return "BLACK";
        if(color.equals(Color.BLUE))return "BLUE";
        if(color.equals(Color.GREEN))return "GREEN";
        if(color.equals(Color.RED))return "RED";
        if(color.equals(Color.TRANSPARENT))return "TRANSPARENT";
        return null;
    }
    
    public static Color getFromPredefinedString(String color){
        if(color.equals("WHITE"))return Color.WHITE;
        if(color.equals("BLACK"))return Color.BLACK;
        if(color.equals("BLUE"))return Color.BLUE;
        if(color.equals("GREEN"))return Color.GREEN;
        if(color.equals("RED"))return Color.RED;
        if(color.equals("TRANSPARENT"))return Color.TRANSPARENT;
        return null;
    }
}
