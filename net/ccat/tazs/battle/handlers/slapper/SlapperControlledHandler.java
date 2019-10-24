package net.ccat.tazs.battle.handlers.slapper;

import femto.mode.HiRes16Color;


/**
 * Handles the Controlled state of a Slapper.
 * - Reads the PAD
 * - Switch to SlapperDead when dead
 */
public class SlapperControlledHandler
    extends SlapperSlapHandler
{
    static final SlapperControlledHandler instance = new SlapperControlledHandler();
    
    
    /***** INFORMATION *****/
    
    public boolean isControlled()
    {
        return true;
    }
    
    public boolean onPlayerControl(UnitsSystem system, int unitIdentifier, boolean control)
    {
        if (control)
            return false;
        system.unitsHandlers[unitIdentifier] = SlapperSeekHandler.instance;
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
            handleAttack(system, unitIdentifier);
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        HandlersTools.drawControlCircle(system, unitIdentifier, screen);
        drawAttackingSlapperUnit(system, unitIdentifier, screen);
    }
}