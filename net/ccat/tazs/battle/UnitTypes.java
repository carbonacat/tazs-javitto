package net.ccat.tazs.battle;

import net.ccat.tazs.battle.handlers.brawler.BrawlerSeekHandler;
import net.ccat.tazs.battle.handlers.shieldbearer.ShieldBearerSeekHandler;
import net.ccat.tazs.battle.handlers.slapper.SlapperIdleHandler;
import net.ccat.tazs.battle.handlers.sworder.SworderSeekHandler;
import net.ccat.tazs.battle.handlers.target.TargetIdleHandler;


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
    /**
     * @see ccat.tazs.battle.handlers.shieldbearer
     */
    static final int SHIELDBEARER = SWORDER + 1;
    /**
     * @see ccat.tazs.battle.handlers.target
     */
    static final int TARGET = SHIELDBEARER + 1;
    static final int END = TARGET + 1;
    
    
    /***** INFORMATION *****/
    
    static UnitHandler idleHandlerForType(int unitType)
    {
        if (unitType == BRAWLER)
            return BrawlerSeekHandler.instance;
        if (unitType == SLAPPER)
            return SlapperIdleHandler.instance;
        if (unitType == SWORDER)
            return SworderSeekHandler.instance;
        if (unitType == SHIELDBEARER)
            return ShieldBearerSeekHandler.instance;
        if (unitType == TARGET)
            return TargetIdleHandler.instance;
        // Not supposed to happen.
        while (true);
        return TargetIdleHandler.instance;
    }
}