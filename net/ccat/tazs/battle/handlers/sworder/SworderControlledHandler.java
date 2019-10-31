package net.ccat.tazs.battle.handlers.sworder;

import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Handles the Controlled state of a Sworder.
 * - Reads the PAD
 * - Switch to SworderDead when dead
 */
public class SworderControlledHandler
    extends BaseSworderHandler
{
    static final SworderControlledHandler instance = new SworderControlledHandler();
    
    
    /***** INFORMATION *****/
    
    public boolean isControlled()
    {
        return true;
    }
    
    public boolean onPlayerControl(UnitsSystem system, int unitIdentifier, boolean control)
    {
        if (control)
            return false;
        system.unitsHandlers[unitIdentifier] = SworderSeekHandler.instance;
        return true;
    }
    
    public boolean isReadyToAttack(UnitsSystem system, int unitIdentifier)
    {
        return system.unitsTimers[unitIdentifier] == 0;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        system.controlledUnitIdentifier = unitIdentifier;
        HandlersTools.moveUnitWithPad(system, unitIdentifier, ANGLE_ROTATION_BY_TICK, WALK_SPEED);
        if (system.unitsTimers[unitIdentifier] == 0)
        {
            if (system.playerPrimaryAction)
                startAttack(system, unitIdentifier);
        }
        else
            handleAttack(system, unitIdentifier);
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        drawAttackingSworderUnit(system, unitIdentifier, screen);
    }
}