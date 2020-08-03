package com.quartzy.engine.utils;

import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger{
    
    private Level level;
    private static boolean enabled;
    
    private DateTimeFormatter FORMATTER;
    
    private static Logger instance;
    
    /**
     * @param level The minimum level that will be logged
     */
    public Logger(Level level){
        this.level = level;
        this.FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy h:mm");
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
        String finalMessage = "[" + dateString + "]" + "[" + Thread.currentThread().getName() + "] " + className + ":" + methodName + " : " + String.format(message, args);
        return finalMessage;
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
