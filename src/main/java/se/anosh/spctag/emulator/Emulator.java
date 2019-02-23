 /**
  *
  * Enumeration containg the values allowed in
  * the emulator used to dump this SPC-tag defined
  * in the spec v0.31 and the Japanese specifiation
  * on drgfactory.jp
  * 
  * Note: 'Other' and 'Unknown' have different codes
  * according to the japanese spec.
  *
  */
package se.anosh.spctag.emulator;

/**
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public enum Emulator {
    
    Unknown,
    Other,
    ZSNES,
    Snes9x,
    ZST2SPC,
    SNEShout,
    ZSNES_W,
    Snes9xpp,
    SNESGT;
}
