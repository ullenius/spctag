package se.anosh.spctag.emulator.factory;


/**
 * 
 * This package uses Factory method pattern (Gang Of Four)
 * 
 */
public abstract class EmulatorFactory {
    
    public enum Type {
        JAPANESE,
        LEGACY
    }
    
    public Emulator orderEmulator(int magicNumber, Type style) {
        return createEmulator(magicNumber, style);
    }
    
    // Method overloading
    // uses Japanese-type unless specified
    public Emulator orderEmulator(int magicNumber) {
        return orderEmulator(magicNumber, Type.JAPANESE);
    }
    
    
    // this method act as a factory
    protected abstract Emulator createEmulator(int magicNumber, Type style);
    
    
}
