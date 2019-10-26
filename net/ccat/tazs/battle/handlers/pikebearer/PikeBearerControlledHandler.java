package net.ccat.tazs.battle.handlers.pikebearer;

import femto.mode.HiRes16Color;


/**
 * Handles the Controlled state of a PikeBearer.
 * - Reads the PAD
 * - Switch to PikeBearerDead when dead
 */
public class PikeBearerControlledHandler
    extends BasePikeBearerHandler
{
    static final PikeBearerControlledHandler instance = new PikeBearerControlledHandler();
    
    
    /***** INFORMATION *****/
    
    public boolean isControlled()
    {
        return true;
    }
    
    public boolean onPlayerControl(UnitsSystem system, int unitIdentifier, boolean control)
    {
        if (control)
            return false;
        system.unitsHandlers[unitIdentifier] = PikeBearerSeekHandler.instance;
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
        drawAttackingPikeBearerUnit(system, unitIdentifier, screen);
    }
}