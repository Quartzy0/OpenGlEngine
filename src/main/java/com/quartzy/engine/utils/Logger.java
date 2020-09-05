package com.quartzy.engine.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.lwjgl.opengl.GL43;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.lwjgl.opengl.GL43.*;

public class Logger{
    
    private Level level;
    @Getter
    @Setter
    private int openGlLogLevel;
    private static boolean enabled;
    
    private DateTimeFormatter FORMATTER;
    
    private static Logger instance;
    
    /**
     * @param level The minimum level that will be logged
     */
    public Logger(Level level){
        this.level = level;
        this.FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    }
    
    private String constructMessage(int stackOffset, String message, Object... args){
        String dateString = FORMATTER.format(LocalDateTime.now());
        String className = "";
        String methodName = "";
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if(stackTrace!=null && stackTrace.length>0){
            methodName = stackTrace[stackOffset+2].getMethodName();
            className = stackTrace[stackOffset+2].getClassName();
        }
        return "[" + dateString + "]" + "[" + Thread.currentThread().getName() + "] " + className + ":" + methodName + " : " + String.format(message, args);
    }
    
    private String constructMessage(String message, Object... args){
        return constructMessage(2, message, args);
    }
    
    private String constructMessage(String message, Exception e, Object... args){
        String finalMessage = constructMessage(2, message, args);
        finalMessage+="\n";
        finalMessage+=constructExceptionString(e);
        return finalMessage;
    }
    
    @SneakyThrows
    private String constructExceptionString(Exception exception){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        exception.printStackTrace(printStream);
        printStream.flush();
        String s = baos.toString(StandardCharsets.UTF_8.name());
        printStream.close();
        baos.close();
        return s;
    }
    
    /**
     * @param message Message to be logged
     * @param args Arguments for formatting
     */
    public void severe(String message, Object... args){
        if(!enabled)return;
        if(Integer.compare(level.getLevel(), this.level.getLevel())==-1)return;
        String finalMessage = constructMessage(message, args);
        StringBuilder sb = new StringBuilder();
        sb.color16(StringBuilder.Color16.FG_RED, finalMessage);
        System.out.println(sb.toString());
    }
    
    /**
     * @param message Message to be logged
     * @param args Arguments for formatting
     */
    public void severe(String message, Exception e, Object... args){
        if(!enabled)return;
        if(Integer.compare(level.getLevel(), this.level.getLevel())==-1)return;
        String finalMessage = constructMessage(message, e, args);
        StringBuilder sb = new StringBuilder();
        sb.color16(StringBuilder.Color16.FG_RED, finalMessage);
        System.out.println(sb.toString());
    }
    
    /**
     * @param message Message to be logged
     * @param args Arguments for formatting
     */
    public void warning(String message,  Object... args){
        if(!enabled)return;
        if(Integer.compare(level.getLevel(), this.level.getLevel())==-1)return;
        String finalMessage = constructMessage(message, args);
        StringBuilder sb = new StringBuilder();
        sb.color16(StringBuilder.Color16.FG_YELLOW, finalMessage);
        System.out.println(sb.toString());
    }
    
    /**
     * @param message Message to be logged
     * @param args Arguments for formatting
     */
    public void warning(String message, Exception e, Object... args){
        if(!enabled)return;
        if(Integer.compare(level.getLevel(), this.level.getLevel())==-1)return;
        String finalMessage = constructMessage(message, e, args);
        StringBuilder sb = new StringBuilder();
        sb.color16(StringBuilder.Color16.FG_YELLOW, finalMessage);
        System.out.println(sb.toString());
    }
    
    /**
     * @param message Message to be logged
     * @param args Arguments for formatting
     */
    public void info(String message,  Object... args){
        if(!enabled)return;
        if(Integer.compare(level.getLevel(), this.level.getLevel())==-1)return;
        String finalMessage = constructMessage(message, args);
        StringBuilder sb = new StringBuilder();
        sb.color16(StringBuilder.Color16.FG_GREEN, finalMessage);
        System.out.println(sb.toString());
    }
    
    /**
     * @param message Message to be logged
     * @param args Arguments for formatting
     */
    public void debug(String message,  Object... args){
        if(!enabled)return;
        if(Integer.compare(level.getLevel(), this.level.getLevel())==-1)return;
        String finalMessage = constructMessage(message, args);
        StringBuilder sb = new StringBuilder();
        sb.color16(StringBuilder.Color16.FG_LIGHT_GREEN, finalMessage);
        System.out.println(sb.toString());
    }
    
    public void openGLLog(int source, int type, int id, int severity, String message, long userParam){
        if(!enabled)return;
        if(this.openGlLogLevel != GL_DEBUG_SEVERITY_NOTIFICATION){
            if(severity > this.openGlLogLevel) return;
        }
        if(severity == GL_DEBUG_SEVERITY_NOTIFICATION && this.openGlLogLevel!=GL_DEBUG_SEVERITY_NOTIFICATION)return;
        String sourceString = "GL_DEBUG_SOURCE_OTHER";
        String typeString = "GL_DEBUG_TYPE_OTHER";
        switch(source){
            case GL_DEBUG_SOURCE_API:
                sourceString = "GL_DEBUG_SOURCE_API";
                break;
            case GL_DEBUG_SOURCE_WINDOW_SYSTEM:
                sourceString = "GL_DEBUG_SOURCE_WINDOW_SYSTEM";
                break;
            case GL_DEBUG_SOURCE_SHADER_COMPILER:
                sourceString = "GL_DEBUG_SOURCE_SHADER_COMPILER";
                break;
            case GL_DEBUG_SOURCE_THIRD_PARTY:
                sourceString = "GL_DEBUG_SOURCE_THIRD_PARTY";
                break;
            case GL_DEBUG_SOURCE_APPLICATION:
                sourceString = "GL_DEBUG_SOURCE_APPLICATION";
                break;
            case GL_DEBUG_SOURCE_OTHER:
                sourceString = "GL_DEBUG_SOURCE_OTHER";
                break;
        }
        switch(type){
            case GL_DEBUG_TYPE_ERROR:
                typeString = "GL_DEBUG_TYPE_ERROR";
                break;
            case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR:
                typeString = "GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR";
                break;
            case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:
                typeString = "GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR";
                break;
            case GL_DEBUG_TYPE_PORTABILITY:
                typeString = "GL_DEBUG_TYPE_PORTABILITY";
                break;
            case GL_DEBUG_TYPE_PERFORMANCE:
                typeString = "GL_DEBUG_TYPE_PERFORMANCE";
                break;
            case GL_DEBUG_TYPE_OTHER:
                typeString = "GL_DEBUG_TYPE_OTHER";
                break;
            case GL_DEBUG_TYPE_MARKER:
                typeString = "GL_DEBUG_TYPE_MARKER";
                break;
        }
        String dateString = FORMATTER.format(LocalDateTime.now());
        StringBuilder sb;
        String finalMessage;
        switch(severity){
            case GL_DEBUG_SEVERITY_LOW:
                finalMessage = "[" + dateString + "]" + ": " + String.format("OpenGL Log: Severity: LOW, Type: %s, Source: %s, Message: %s", typeString, sourceString, message);
                sb = new StringBuilder();
                sb.color16(StringBuilder.Color16.FG_YELLOW, finalMessage);
                System.out.println(sb.toString());
                break;
            case GL_DEBUG_SEVERITY_MEDIUM:
                finalMessage = "[" + dateString + "]" + ": " + String.format("OpenGL Log: Severity: MEDIUM, Type: %s, Source: %s, Message: %s", typeString, sourceString, message);
                sb = new StringBuilder();
                sb.color16(StringBuilder.Color16.FG_YELLOW, finalMessage);
                System.out.println(sb.toString());
                break;
            case GL_DEBUG_SEVERITY_HIGH:
                finalMessage = "[" + dateString + "]" + ": " + String.format("OpenGL Log: Severity: HIGH, Type: %s, Source: %s, Message: %s", typeString, sourceString, message);
                sb = new StringBuilder();
                sb.color16(StringBuilder.Color16.FG_RED, finalMessage);
                System.out.println(sb.toString());
                break;
            case GL_DEBUG_SEVERITY_NOTIFICATION:
                finalMessage = "[" + dateString + "]" + ": " + String.format("OpenGL Log: Severity: NOTIFICATION, Type: %s, Source: %s, Message: %s", typeString, sourceString, message);
                sb = new StringBuilder();
                sb.color16(StringBuilder.Color16.FG_LIGHT_GREEN, finalMessage);
                System.out.println(sb.toString());
                break;
            default:
                finalMessage = "[" + dateString + "]" + ": " + String.format("OpenGL Log: Severity: DONT_CARE, Type: %s, Source: %s, Message: %s", typeString, sourceString, message);
                sb = new StringBuilder();
                sb.color16(StringBuilder.Color16.FG_GREEN, finalMessage);
                System.out.println(sb.toString());
                break;
        }
    }
    
    /**
     * @param level New level to be used
     * @return Return the new Logger object
     */
    public Logger setLevel(Level level){
        this.level = level;
        return this;
    }
    
    public Level getLevel(){
        return level;
    }
    
    public static boolean isEnabled(){
        return enabled;
    }
    
    public static void setEnabled(boolean enabled){
        Logger.enabled = enabled;
    }
    
    /**
     * @return Gets the logger instance
     */
    public static Logger getLogger(){
        return instance==null ? (instance = new Logger(Level.DEBUG)) : instance;
    }
}
