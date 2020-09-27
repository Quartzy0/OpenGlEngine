package com.quartzy.engine.input;

public class Mods{
    public final boolean ctrl;
    public final boolean alt;
    public final boolean shift;
    public final boolean numLock;
    public final boolean Super;
    public final boolean capsLock;
    
    public final int originalMods;
    
    /**
     * @param ctrl Is Ctrl pressed
     * @param alt Is Alt pressed
     * @param shift Is Shift pressed
     * @param numLock Is Num Lock active
     * @param aSuper Is Super active
     * @param capsLock Is Caps Lock active
     */
    public Mods(boolean ctrl, boolean alt, boolean shift, boolean numLock, boolean aSuper, boolean capsLock, int originalMods){
        this.ctrl = ctrl;
        this.originalMods = originalMods;
        this.alt = alt;
        this.shift = shift;
        this.numLock = numLock;
        Super = aSuper;
        this.capsLock = capsLock;
    }
    
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        
        Mods mods = (Mods) o;
        
        if(ctrl != mods.ctrl) return false;
        if(alt != mods.alt) return false;
        if(shift != mods.shift) return false;
        if(numLock != mods.numLock) return false;
        if(Super != mods.Super) return false;
        return capsLock == mods.capsLock;
    }
    
    @Override
    public int hashCode(){
        int result = (ctrl ? 1 : 0);
        result = 31 * result + (alt ? 1 : 0);
        result = 31 * result + (shift ? 1 : 0);
        result = 31 * result + (numLock ? 1 : 0);
        result = 31 * result + (Super ? 1 : 0);
        result = 31 * result + (capsLock ? 1 : 0);
        return result;
    }
    
    @Override
    public String toString(){
        return "Mods{" +
                "ctrl=" + ctrl +
                ", alt=" + alt +
                ", shift=" + shift +
                ", numLock=" + numLock +
                ", super=" + Super +
                ", capsLock=" + capsLock +
                '}';
    }
}
