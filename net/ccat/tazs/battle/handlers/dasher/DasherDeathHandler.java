package net.ccat.tazs.battle.handlers.dasher;

import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Handles the Dead state of a Dasher.
 * - Pretty much renders a falling corpse, finally resting.
 */
public class DasherDeathHandler
    extends BaseDasherHandler
{
    static final DasherDeathHandler instance = new DasherDeathHandler();
    
    
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
    
    public void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        drawDyingDasherUnit(system, unitIdentifier, screen);
    }
}