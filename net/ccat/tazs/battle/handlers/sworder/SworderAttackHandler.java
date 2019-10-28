package net.ccat.tazs.battle.handlers.sworder;

import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Handles when a Sworder slaps a target.
 * Goes back to SworderIdle when it's done.
 */
public class SworderAttackHandler
    extends BaseSworderHandler
{
    static final SworderAttackHandler instance = new SworderAttackHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        
        if (targetIdentifier == UnitsSystem.IDENTIFIER_NONE)
        {
            system.unitsTimers[unitIdentifier] = 0;
            system.unitsHandlers[unitIdentifier] = SworderSeekHandler.instance;
        }
        else if (system.unitsTimers[unitIdentifier] == 0)
            startAttack(system, unitIdentifier);
        else if (!handleAttack(system, unitIdentifier))
            system.unitsHandlers[unitIdentifier] = SworderSeekHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        drawAttackingSworderUnit(system, unitIdentifier, screen);
    }
}