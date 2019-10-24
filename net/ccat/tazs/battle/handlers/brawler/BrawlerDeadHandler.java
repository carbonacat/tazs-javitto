package net.ccat.tazs.battle.handlers.brawler;

import femto.mode.HiRes16Color;


/**
 * Handles the Dead state of a Brawler.
 * - Pretty much renders a corpse.
 */
public class BrawlerDeadHandler
    extends BaseBrawlerHandler
{
    static final BrawlerDeadHandler instance = new BrawlerDeadHandler();
    
    
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
            unitTimer = DEATH_TICKS;
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
        drawDyingBrawlerUnit(system, unitIdentifier, screen);
    }
}