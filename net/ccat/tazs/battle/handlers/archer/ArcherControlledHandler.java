package net.ccat.tazs.battle.handlers.archer;

import femto.mode.HiRes16Color;


/**
 * Handles the Controlled state of a Archer.
 * - Reads the PAD
 * - Switch to ArcherDead when dead
 */
public class ArcherControlledHandler
    extends BaseArcherHandler
{
    static final ArcherControlledHandler instance = new ArcherControlledHandler();
    
    
    /***** INFORMATION *****/
    
    public boolean isControlled()
    {
        return true;
    }
    
    public boolean onPlayerControl(UnitsSystem system, int unitIdentifier, boolean control)
    {
        if (control)
            return false;
        system.unitsHandlers[unitIdentifier] = ArcherSeekHandler.instance;
        return true;
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
            handleAttack(system, unitIdentifier, system.playerPrimaryAction);
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        drawAttackingArcherUnit(system, unitIdentifier, screen);
    }
}