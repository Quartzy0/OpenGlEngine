package com.quartzy.engine.text;

import com.quartzy.engine.graphics.Renderer;
import com.quartzy.engine.graphics.Texture;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class Font{
    
    private java.awt.Font font;
    private int fontHeight;
    private Texture fontTexture;
    private HashMap<Character, Glyph> glyphs = new HashMap<>();
    
    /**
     * @param font Java Font that will be used for rendering
     * @param antiAlias Should anti aliasing be used
     */
    public Font(java.awt.Font font, boolean antiAlias){
        this.font = font;
        this.fontTexture = generateFontTexture(antiAlias);
    }
    
    /**
     * @param fontFile File of the TrueType font
     * @param size Size of the font that will be draw
     */
    public Font(File fontFile, int size){
        try{
            font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontFile).deriveFont(size);
            this.fontTexture = generateFontTexture(false);
        } catch(FontFormatException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public Font(java.awt.Font font){
        this(font, false);
    }
    
    /**
     * Draws the text to the text onto the screen
     * @param renderer Renderer
     * @param text String to be rendered
     * @param x X position
     * @param y Y position
     * @param c Color
     */
    public void drawText(Renderer renderer, String text, float x, float y, com.quartzy.engine.graphics.Color c){
        int lines = 1;
        for(int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if(ch == '\n') {
                lines++;
            }
        }
        int textHeight = lines * fontHeight;
        
        float drawX = x;
        float drawY = y;
        if(textHeight > fontHeight) {
            drawY += textHeight - fontHeight;
        }
        
        fontTexture.bind(0);
        renderer.begin();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                drawY -= fontHeight;
                drawX = x;
                continue;
            }
            if (ch == '\r') continue;
            Glyph g = glyphs.get(ch);
            if(g!=null){
                renderer.drawTextureRegion(fontTexture, drawX, drawY, g.x, g.y, g.width, g.height, c, 0);
                drawX += g.width;
            }
        }
        renderer.end();
    }
    
    private Texture generateFontTexture(boolean antiAlias){
        int imgWidth = 0;
        int imgHeight = 0;
        
        for(int i = 32; i < 256; i++){
            if(i==127)continue;
            char c = (char) i;
            BufferedImage ch = createCharImage(font, c, antiAlias);
            
            imgWidth += ch.getWidth();
            imgHeight = Math.max(imgHeight, ch.getHeight());
        }
        fontHeight = imgHeight;
    
        BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        int x = 0;
    
        for(int i = 32; i < 256; i++){
            if(i==127)continue;
            char c = (char) i;
            BufferedImage charImage = createCharImage(font, c, antiAlias);
    
            int charWidth = charImage.getWidth();
            int charHeight = charImage.getHeight();
    
            Glyph ch = new Glyph(charWidth, charHeight, x, image.getHeight() - charHeight);
            g.drawImage(charImage, x, 0, null);
            x += ch.width;
            glyphs.put(c, ch);
        }
    
        /* Flip image Horizontal to get the origin to bottom left */
        AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
        transform.translate(0, -image.getHeight());
        AffineTransformOp operation = new AffineTransformOp(transform,
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = operation.filter(image, null);
    
        /* Get charWidth and charHeight of image */
        int width = image.getWidth();
        int height = image.getHeight();
    
        /* Get pixel data of image */
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
    
        /* Put pixel data into a ByteBuffer */
        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                /* Pixel as RGBA: 0xAARRGGBB */
                int pixel = pixels[i * width + j];
                /* Red component 0xAARRGGBB >> 16 = 0x0000AARR */
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                /* Green component 0xAARRGGBB >> 8 = 0x00AARRGG */
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                /* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
                buffer.put((byte) (pixel & 0xFF));
                /* Alpha component 0xAARRGGBB >> 24 = 0x000000AA */
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        /* Do not forget to flip the buffer! */
        buffer.flip();
        
        Texture fontTexture = new Texture(buffer, width, height);
        MemoryUtil.memFree(buffer);
        
        return fontTexture;
    }
    
    private BufferedImage createCharImage(java.awt.Font font, char ch, boolean antiAlias){
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        if(antiAlias){
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        g.dispose();
        
        int charWidth = metrics.charWidth(ch);
        int charHeight = metrics.getHeight();
        
        image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setFont(font);
        g.setPaint(Color.WHITE);
        g.drawString(String.valueOf(ch), 0, metrics.getAscent());
        g.dispose();
        return image;
    }
    
    public int getFontHeight(){
        return fontHeight;
    }
    
    public HashMap<Character, Glyph> getGlyphs(){
        return glyphs;
    }
    
    public void dispose(){
        fontTexture.dispose();
    }
}
