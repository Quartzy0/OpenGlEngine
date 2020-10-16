package com.quartzy.engine.utils;

import com.quartzy.engine.math.Vector2f;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

@Data
@AllArgsConstructor
public class SystemInfo{
    
    private Vector2f[] screenDimensions;
    private Vector2f mainScreen;
    private String os;
    private int cpuCoreCount;
    private long freeMemory;
    private long maxJVMMemory;
    private long totalJVMMemory;
    
    public static SystemInfo getSystemInfo(){
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = localGraphicsEnvironment.getScreenDevices();
        GraphicsDevice defaultScreenDevice = localGraphicsEnvironment.getDefaultScreenDevice();
        Vector2f mainScreenDim = new Vector2f(defaultScreenDevice.getDisplayMode().getWidth(), defaultScreenDevice.getDisplayMode().getHeight());
        Vector2f[] screenDimension = new Vector2f[screenDevices.length];
        for(int i = 0; i < screenDevices.length; i++){
            if(screenDevices[i].equals(defaultScreenDevice))
            screenDimension[i] = new Vector2f(screenDevices[i].getDisplayMode().getWidth(), screenDevices[i].getDisplayMode().getHeight());
        }
        Runtime runtime = Runtime.getRuntime();
        return new SystemInfo(screenDimension, mainScreenDim, System.getProperty("os.name"), runtime.availableProcessors(), runtime.freeMemory(), runtime.maxMemory(), runtime.totalMemory());
    }
}
