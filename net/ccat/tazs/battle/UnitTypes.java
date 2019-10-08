package net.ccat.tazs.battle;

import net.ccat.tazs.resources.Texts;


/**
 * Identities a type of unit in the game.
 */
public class UnitTypes
{
    /**
     * @see ccat.tazs.battle.handlers.brawler
     */
    static final int BRAWLER = 0;
    /**
     * @see ccat.tazs.battle.handlers.slapper
     */
    static final int SLAPPER = 1;
    
    
    /***** INFORMATION *****/
    
    /**
     * @return the associated name, or a default name if unknown.
     */
    static String nameForType(int unitType)
    {
        if (unitType == BRAWLER)
            return Texts.UNIT_BRAWLER;
        if (unitType == SLAPPER)
            return Texts.UNIT_SLAPPER;
        return Texts.MISC_UNKNOWN;
    }
}