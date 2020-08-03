package com.quartzy.engine.events;

public class Mods{
    public final boolean ctrl;
    public final boolean alt;
    public final boolean shift;
    public final boolean numLock;
    public final boolean Super;
    public final boolean capsLock;
    
    /**
     * @param ctrl Is Ctrl pressed
     * @param alt Is Alt pressed
     * @param shift Is Shift pressed
     * @param numLock Is Num Lock active
     * @param aSuper Is Super active
     * @param capsLock Is Caps Lock active
     */
    public Mods(boolean ctrl, boolean alt, boolean shift, boolean numLock, boolean aSuper, boolean capsLock){
        this.ctrl = ctrl;
        this.alt = alt;
        this.shift = shift;
        this.numLock = numLock;
        Super = aSuper;
        this.capsLock = capsLock;
    }
}
