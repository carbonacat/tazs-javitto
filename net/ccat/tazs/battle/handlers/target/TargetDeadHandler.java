package net.ccat.tazs.battle.handlers.target;

import femto.mode.HiRes16Color;


/**
 * Handles the Dead state of a Target.
 * - Pretty much renders a corpse.
 */
public class TargetDeadHandler
    extends BaseTargetHandler
{
    static final TargetDeadHandler instance = new TargetDeadHandler();
    
    
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
        drawDyingTargetUnit(system, unitIdentifier, screen);
    }
}