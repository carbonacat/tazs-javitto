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
    
    public void drawControlUI(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        super.drawControlUI(system, unitIdentifier, screen);
        
        int unitTimer = system.unitsTimers[unitIdentifier];

        if (((unitTimer >= ATTACK_TIMER_CHARGING_MIN) && (unitTimer <= ATTACK_TIMER_CHARGING_MAX))
            || ((unitTimer >= ATTACK_TIMER_DECHARGING_MAX) && (unitTimer <= ATTACK_TIMER_DECHARGING_MIN)))
        {
            float unitX = system.unitsXs[unitIdentifier];
            float unitY = system.unitsYs[unitIdentifier];
            float unitAngle = system.unitsAngles[unitIdentifier];
            float distance = targetDistanceWhenCharging(unitTimer);
            float targetX = unitX + Math.cos(unitAngle) * distance;
            float targetY = unitY + Math.sin(unitAngle) * distance;
            
            HandlersTools.drawControlTarget(system.everyUISprite, targetX, targetY, screen);
        }
    }
}