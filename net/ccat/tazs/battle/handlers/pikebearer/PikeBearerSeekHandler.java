package net.ccat.tazs.battle.handlers.pikebearer;

import femto.mode.HiRes16Color;

import net.ccat.tazs.tools.MathTools;


/**
 * Handles the Idle state of a PikeBearer.
 * - Seeks the closest Enemy.
 * - Switch to PikeBearerPunch when close enough to punch them.
 */
public class PikeBearerSeekHandler
    extends BasePikeBearerHandler
{
    static final PikeBearerSeekHandler instance = new PikeBearerSeekHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        if (HandlersTools.seekAnEnemy(system, unitIdentifier, WALK_SPEED, ANGLE_ROTATION_BY_TICK, CLOSE_DISTANCE_SQUARED, RECONSIDER_TICKS))
            system.unitsHandlers[unitIdentifier] = PikeBearerAttackHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        drawIdlePikeBearerUnit(system, unitIdentifier, screen);
    }
}