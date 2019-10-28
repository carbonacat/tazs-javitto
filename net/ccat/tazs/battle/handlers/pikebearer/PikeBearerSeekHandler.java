package net.ccat.tazs.battle.handlers.pikebearer;

import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


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
        if (HandlersTools.seekAnEnemy(system, unitIdentifier, WALK_SPEED, ANGLE_ROTATION_BY_TICK, CLOSE_DISTANCE_SQUARED, RECONSIDER_TICKS,
                                      ATTACK_ANGLE_MAX))
            system.unitsHandlers[unitIdentifier] = PikeBearerAttackHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        drawIdlePikeBearerUnit(system, unitIdentifier, screen);
    }
}