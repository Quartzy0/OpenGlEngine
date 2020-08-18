package com.quartzy.engine.input;

import lombok.Getter;
import lombok.Setter;

public class Input{
    
    @Getter
    @Setter
    private static Keyboard keyboard;
    @Getter
    @Setter
    private static Mouse mouse;
    
    public static void init(long windowId){
        keyboard = new Keyboard(windowId);
        mouse = new Mouse(windowId);
    }
}
