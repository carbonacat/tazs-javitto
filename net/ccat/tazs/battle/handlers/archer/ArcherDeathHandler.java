package net.ccat.tazs.battle.handlers.archer;

import femto.mode.HiRes16Color;

import net.ccat.tazs.battle.handlers.brawler.BaseBrawlerHandler;


/**
 * Handles the Death state of a Archer.
 * - Pretty much renders a corpse.
 */
public class ArcherDeathHandler
    extends BaseArcherHandler
{
    static final ArcherDeathHandler instance = new ArcherDeathHandler();
    
    
    /***** EVENTS *****/
    
    public boolean onPlayerControl(UnitsSystem system, int unitIdentifier, boolean control)
    {
        return false;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        if (unitTimer == 0)
            unitTimer = BaseBrawlerHandler.DEATH_TICKS;
        else if (unitTimer > 0)
        {
            unitTimer--;
            if (unitTimer == 1)
                unitTimer = -1;
        }
        system.unitsTimers[unitIdentifier] = unitTimer;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        drawDyingArcherUnit(system, unitIdentifier, screen);
    }
}