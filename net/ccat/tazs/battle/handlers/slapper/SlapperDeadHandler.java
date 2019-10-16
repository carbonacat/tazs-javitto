package net.ccat.tazs.battle.handlers.slapper;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


/**
 * Handles the Dead state of a Slapper.
 * - Pretty much renders a corpse.
 */
public class SlapperDeadHandler
    extends BaseSlapperHandler
{
    static final SlapperDeadHandler instance = new SlapperDeadHandler();
    
    
    /***** EVENTS *****/
    
    public boolean onPlayerControl(UnitsSystem system, int unitIdentifier)
    {
        return false;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        if (unitTimer == 0)
            unitTimer = TICKS_TO_GROUND;
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
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        drawDeadSlapper(unitX, unitY, unitAngle, system.slapperBodySpriteByTeam[unitTeam],
                        unitTimer, TICKS_TO_GROUND,
                        screen);
    }
    
    
    private static final int TICKS_TO_GROUND = 64;
}