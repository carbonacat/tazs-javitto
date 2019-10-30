package net.ccat.tazs.battle;

import net.ccat.tazs.battle.handlers.archer.ArcherSeekHandler;
import net.ccat.tazs.battle.handlers.brawler.BrawlerSeekHandler;
import net.ccat.tazs.battle.handlers.dasher.DasherSeekAndAttackHandler;
import net.ccat.tazs.battle.handlers.pikebearer.PikeBearerSeekHandler;
import net.ccat.tazs.battle.handlers.shieldbearer.ShieldBearerSeekHandler;
import net.ccat.tazs.battle.handlers.slapper.SlapperSeekHandler;
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
     * @see ccat.tazs.battle.handlers.pikebearer
     */
    static final int PIKEBEARER = SHIELDBEARER + 1;
    /**
     * @see ccat.tazs.battle.handlers.archer
     */
    static final int ARCHER = PIKEBEARER + 1;
    /**
     * @see ccat.tazs.battle.handlers.target
     */
    static final int TARGET = ARCHER + 1;
    /**
     * @see ccat.tazs.battle.handlers.dasher
     */
    static final int DASHER = TARGET + 1;
    static final int END = DASHER + 1;
    
    
    /***** INFORMATION *****/
    
    static UnitHandler idleHandlerForType(int unitType)
    {
        if (unitType == BRAWLER)
            return BrawlerSeekHandler.instance;
        if (unitType == SLAPPER)
            return SlapperSeekHandler.instance;
        if (unitType == SWORDER)
            return SworderSeekHandler.instance;
        if (unitType == SHIELDBEARER)
            return ShieldBearerSeekHandler.instance;
        if (unitType == PIKEBEARER)
            return PikeBearerSeekHandler.instance;
        if (unitType == ARCHER)
            return ArcherSeekHandler.instance;
        if (unitType == TARGET)
            return TargetIdleHandler.instance;
        if (unitType == DASHER)
            return DasherSeekAndAttackHandler.instance;
        // Not supposed to happen.
        while (true);
        return TargetIdleHandler.instance;
    }
}