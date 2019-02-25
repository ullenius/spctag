package se.anosh.spctag.emulator.factory;

import java.util.Objects;

/**
 * 
 * This package uses Factory method pattern (Gang Of Four)
 * 
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public abstract class EmulatorFactory {
    
    public enum Type {
        JAPANESE,
        LEGACY
    }
    
    public Emulator orderEmulator(int magicNumber,Type style) {
        Objects.requireNonNull(style);
        Emulator emulator = createEmulator(magicNumber,style);
        return emulator;
        
    }
    
    // Method overloading
    // uses Japanese-type unless specified
    public Emulator orderEmulator(int magicNumber) {
        return orderEmulator(magicNumber,Type.JAPANESE);
    }
    
    
    // this method act as a factory
    protected abstract Emulator createEmulator(int magicNumber, Type style);
    
    
}
