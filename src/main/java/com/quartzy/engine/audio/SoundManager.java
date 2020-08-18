package com.quartzy.engine.audio;

import com.quartzy.engine.ecs.components.AudioListenerComponent;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import com.quartzy.engine.utils.Resource;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.*;

@CustomLog
public class SoundManager{
    
    private static SoundManager instance;
    
    private long device, context;
    private ALCCapabilities alcCapabilities;
    private ALCapabilities alCapabilities;
    
    private HashMap<String, Sound> sounds = new HashMap<>();
    
    /**
     * Creates the SoundManager object and initializes it
     */
    public SoundManager(){
        createDeviceContext();
    }
    
    /**
     * Closes all of the devices and clears all of the buffers
     */
    public void dispose(){
        log.info("Sound manager shutting down...");
        for(Sound value : sounds.values()){
            unload(value.getName());
        }
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
    
    /**
     * Loads a sound from the provided resource and saves it to a HashMap so it can be used later
     * @param resource The resource for the sound file that needs to be loaded
     */
    public void loadSound(Resource resource){
        log.info("Loading sound %s", resource.getName());
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);
    
        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(resource.getFile().getAbsolutePath(), channelsBuffer, sampleRateBuffer);
    
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        stackPop();
        stackPop();
        
        int format = -1;
        if(channels == 1) {
            format = AL_FORMAT_MONO16;
        } else if(channels == 2) {
            format = AL_FORMAT_STEREO16;
        }

        int bufferPointer = alGenBuffers();

        alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);

        free(rawAudioBuffer);
        
        sounds.put(resource.getName(), new Sound(bufferPointer, resource.getName()));
    }
    
    /**
     * Deletes the buffer of the sound specified
     * @param name Name of the sound to be unloaded
     */
    public void unload(String name){
        Sound remove = sounds.remove(name);
        alDeleteBuffers(remove.getBufferPointer());
    }
    
    private void createDeviceContext(){
        log.info("Creating audio device context");
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        device = alcOpenDevice(defaultDeviceName);
        
        int[] attributes = {0};
        context = alcCreateContext(device, attributes);
        alcMakeContextCurrent(context);
        
        alcCapabilities = ALC.createCapabilities(device);
        alCapabilities = AL.createCapabilities(alcCapabilities);
        
        if(!alCapabilities.OpenAL10){
            throw new IllegalStateException("OpenAL is not supported on this device");
        }
    }
    
    public Sound getSound(String name){
        return sounds.get(name);
    }
    
    public static SoundManager getInstance(){
        return instance==null ? (instance = new SoundManager()) : instance;
    }
}
