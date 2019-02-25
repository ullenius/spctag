package se.anosh.spctag.emulator.factory;

import java.util.Objects;

/**
 *
 * 
 * Immutable class
 * 
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public abstract class Emulator {
    
    public Emulator(Name name, Integer offset) {
        
        if (name == null) // Map returns NULL if value is not in Map
            this.name = Name.Unknown;
        else
            this.name = name;
        
        // the model object knows if it is binary of textTag
        // so it knows where to put this value. But this object (Emulator)
        // does not need to know what it is
        this.code = Objects.requireNonNull(offset); //auto-unboxing
    }
    
    private Name name;
    final int code;

    public Name getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "Emulator{" + "name=" + name + ", offset= 0x" + Integer.toHexString(code) + '}';
    }
    
    
    
}
