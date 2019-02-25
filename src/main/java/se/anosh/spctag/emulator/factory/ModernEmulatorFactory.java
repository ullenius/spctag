package se.anosh.spctag.emulator.factory;


/**
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class ModernEmulatorFactory extends EmulatorFactory {
    
    /**
     * 
     * TODO: Add String parameter (or enum?) to decide which
     * type to create. At the moment there is only one type of
     * class that the Japanese factory creates :)
     * 
     * @param magicNumber
     * @param style
     * @return 
     */
    @Override
    protected Emulator createEmulator(final int magicNumber,Type style) {
        
        if (style == Type.JAPANESE)
            return new JapaneseEmulator(magicNumber);
        
        else if (style == Type.LEGACY)
            return new LegacyEmulator(magicNumber);
        
        else
            throw new IllegalArgumentException("this should never happen. Unless you modified the enum");
        
    }

    
}
