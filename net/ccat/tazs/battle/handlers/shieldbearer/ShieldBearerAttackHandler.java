package net.ccat.tazs.battle.handlers.shieldbearer;

import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Handles when a ShieldBearer slaps a target.
 * Goes back to ShieldBearerIdle when it's done.
 */
public class ShieldBearerAttackHandler
    extends BaseShieldBearerHandler
{
    static final ShieldBearerAttackHandler instance = new ShieldBearerAttackHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        
        if (targetIdentifier == UnitsSystem.IDENTIFIER_NONE)
        {
            system.unitsTimers[unitIdentifier] = 0;
            system.unitsHandlers[unitIdentifier] = ShieldBearerSeekHandler.instance;
        }
        else if (system.unitsTimers[unitIdentifier] == 0)
            startAttack(system, unitIdentifier);
        else if (!handleAttack(system, unitIdentifier))
            system.unitsHandlers[unitIdentifier] = ShieldBearerSeekHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        drawAttackingShieldBearerUnit(system, unitIdentifier, screen);
    }
}