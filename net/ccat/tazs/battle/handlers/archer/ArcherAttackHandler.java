package net.ccat.tazs.battle.handlers.archer;

import femto.mode.HiRes16Color;


/**
 * Handles when a Archer slaps a target.
 * Goes back to ArcherIdle when it's done.
 */
public class ArcherAttackHandler
    extends BaseArcherHandler
{
    static final ArcherAttackHandler instance = new ArcherAttackHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        
        if (targetIdentifier == UnitsSystem.IDENTIFIER_NONE)
        {
            system.unitsTimers[unitIdentifier] = 0;
            system.unitsHandlers[unitIdentifier] = ArcherSeekHandler.instance;
        }
        else if (system.unitsTimers[unitIdentifier] == 0)
            startAttack(system, unitIdentifier);
        else if (!handleAttack(system, unitIdentifier, true))
            system.unitsHandlers[unitIdentifier] = ArcherSeekHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        drawAttackingArcherUnit(system, unitIdentifier, screen);
    }
}