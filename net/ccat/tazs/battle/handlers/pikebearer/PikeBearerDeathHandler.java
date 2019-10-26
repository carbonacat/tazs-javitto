package net.ccat.tazs.battle.handlers.pikebearer;

import femto.mode.HiRes16Color;

import net.ccat.tazs.battle.handlers.brawler.BaseBrawlerHandler;


/**
 * Handles the Dead state of a PikeBearer.
 * - Pretty much renders a corpse.
 */
public class PikeBearerDeathHandler
    extends BasePikeBearerHandler
{
    static final PikeBearerDeathHandler instance = new PikeBearerDeathHandler();
    
    
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
        drawDyingPikeBearerUnit(system, unitIdentifier, screen);
    }
}