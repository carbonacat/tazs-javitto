package net.ccat.tazs.battle.handlers.slapper;

import femto.mode.HiRes16Color;


/**
 * Handles when a Slapper slaps a target.
 * Goes back to SlapperIdle when it's done.
 */
public class SlapperSlapHandler
    extends BaseSlapperHandler
{
    static final SlapperSlapHandler instance = new SlapperSlapHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        
        if (targetIdentifier == UnitsSystem.IDENTIFIER_NONE)
        {
            system.unitsTimers[unitIdentifier] = 0;
            system.unitsHandlers[unitIdentifier] = SlapperSeekHandler.instance;
        }
        else if (system.unitsTimers[unitIdentifier] == 0)
            startAttack(system, unitIdentifier);
        else if (!handleAttack(system, unitIdentifier))
            system.unitsHandlers[unitIdentifier] = SlapperSeekHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        drawAttackingSlapperUnit(system, unitIdentifier, screen);
    }
}