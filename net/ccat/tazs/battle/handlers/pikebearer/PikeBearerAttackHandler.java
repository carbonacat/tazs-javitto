package net.ccat.tazs.battle.handlers.pikebearer;

import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Handles when a PikeBearer slaps a target.
 * Goes back to PikeBearerIdle when it's done.
 */
public class PikeBearerAttackHandler
    extends BasePikeBearerHandler
{
    static final PikeBearerAttackHandler instance = new PikeBearerAttackHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        
        if (targetIdentifier == UnitsSystem.IDENTIFIER_NONE)
        {
            system.unitsTimers[unitIdentifier] = 0;
            system.unitsHandlers[unitIdentifier] = PikeBearerSeekHandler.instance;
        }
        else if (system.unitsTimers[unitIdentifier] == 0)
            startAttack(system, unitIdentifier);
        else if (!handleAttack(system, unitIdentifier))
            system.unitsHandlers[unitIdentifier] = PikeBearerSeekHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        drawAttackingPikeBearerUnit(system, unitIdentifier, screen);
    }
}