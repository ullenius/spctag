package se.anosh.spctag.emulator.factory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * This class matches the code provided according to the legacy spec
 * made by creaothceann on 2006-02-27 21:48
 * 
 * Some of the values don't overlap in the Japanese spec. That's why
 * I created 2 different implementations.
 * 
 * 
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class LegacyEmulator extends Emulator {
    
    // binary and text tags share the same value
    // in this spec
	//
	// Package protected to make them accessible for JUnit-tests
    static final int UNKNOWN = 0;
    static final int ZSNES = 1;
    static final int SNES9x = 2;
    
    private static final Map<Integer,Name> emulatorMap;
    
    static {
        emulatorMap = new HashMap<>();
        emulatorMap.put(UNKNOWN,Name.Unknown);
        emulatorMap.put(ZSNES,Name.ZSNES);
        emulatorMap.put(SNES9x,Name.Snes9x);
    }
    
    /**
     * 
     * This constructor uses an internal HashMap to Map the codes (keys) to 
     * Emulator enums (values)
     * 
     * @param magicNumber 
     */
    public LegacyEmulator(Integer magicNumber) {
        
        super(emulatorMap.get(magicNumber), magicNumber);
    }
    
    
}
