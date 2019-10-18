package net.ccat.tazs.battle;

import net.ccat.tazs.battle.handlers.brawler.BrawlerIdleHandler;
import net.ccat.tazs.battle.handlers.slapper.SlapperIdleHandler;
import net.ccat.tazs.battle.handlers.sworder.SworderSeekHandler;


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
    static final int SLAPPER = BRAWLER + 1;
    /**
     * @see ccat.tazs.battle.handlers.sword
     */
    static final int SWORDER = SLAPPER + 1;
    static final int END = SWORDER + 1;
    
    
    /***** INFORMATION *****/
    
    static UnitHandler idleHandlerForType(int unitType)
    {
        if (unitType == BRAWLER)
            return BrawlerIdleHandler.instance;
        if (unitType == SLAPPER)
            return SlapperIdleHandler.instance;
        if (unitType == SWORDER)
            return SworderSeekHandler.instance;
        // Not supposed to happen.
        while (true);
        return BrawlerIdleHandler.instance;
    }
}