package net.ccat.tazs.battle.handlers.archer;

import femto.mode.HiRes16Color;

import net.ccat.tazs.tools.MathTools;


/**
 * Handles the Idle state of a Archer.
 * - Seeks the closest Enemy.
 * - Switch to ArcherPunch when close enough to punch them.
 */
public class ArcherSeekHandler
    extends BaseArcherHandler
{
    static final ArcherSeekHandler instance = new ArcherSeekHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        if (HandlersTools.seekAnEnemy(system, unitIdentifier, WALK_SPEED, ANGLE_ROTATION_BY_TICK, CLOSE_DISTANCE_SQUARED, RECONSIDER_TICKS,
                                      ATTACK_ANGLE_MAX))
            system.unitsHandlers[unitIdentifier] = ArcherAttackHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        drawIdleArcherUnit(system, unitIdentifier, screen);
    }
}