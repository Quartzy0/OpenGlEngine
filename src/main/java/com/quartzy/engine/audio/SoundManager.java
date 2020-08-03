package com.quartzy.engine.audio;

import lombok.CustomLog;
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
    
    private long device, context;
    private ALCCapabilities alcCapabilities;
    private ALCapabilities alCapabilities;
    private int sourcePointer = -1;
    
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
        alDeleteSources(sourcePointer);
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
     * Play the sound that goes by the name provided
     * @param name The name of the sound which was specified in the load method
     */
    public void play(String name){
        Sound sound = sounds.get(name);
        if(sourcePointer==-1){
            sourcePointer = alGenSources();
        }
        alSourcei(sourcePointer, AL_BUFFER, sound.getBufferPointer());
        alSourcePlay(sourcePointer);
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
}
