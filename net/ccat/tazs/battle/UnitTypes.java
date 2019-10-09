package net.ccat.tazs.battle;

import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.battle.handlers.brawler.BrawlerIdleHandler;
import net.ccat.tazs.battle.handlers.slapper.SlapperIdleHandler;


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
        // Not supposed to happen.
        while (true);
        return Texts.MISC_UNKNOWN;
    }
    
    static UnitHandler idleHandlerForType(int unitType)
    {
        if (unitType == BRAWLER)
            return BrawlerIdleHandler.instance;
        if (unitType == SLAPPER)
            return SlapperIdleHandler.instance;
        // Not supposed to happen.
        while (true);
        return BrawlerIdleHandler.instance;
    }
}