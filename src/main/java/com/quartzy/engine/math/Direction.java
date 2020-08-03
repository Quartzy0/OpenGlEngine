package com.quartzy.engine.math;

public enum Direction{
    UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0);
    
    public final int xMod, yMod;
    
    Direction(int xMod, int yMod){
        this.xMod = xMod;
        this.yMod = yMod;
    }
}
