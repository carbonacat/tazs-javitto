package net.ccat.tazs.battle.handlers.shieldbearer;

import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Handles the Idle state of a ShieldBearer.
 * - Seeks the closest Enemy.
 * - Switch to ShieldBearerPunch when close enough to punch them.
 */
public class ShieldBearerSeekHandler
    extends BaseShieldBearerHandler
{
    static final ShieldBearerSeekHandler instance = new ShieldBearerSeekHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        if (HandlersTools.seekAnEnemy(system, unitIdentifier, WALK_SPEED, ANGLE_ROTATION_BY_TICK, CLOSE_DISTANCE_SQUARED, RECONSIDER_TICKS,
                                      ATTACK_ANGLE_MAX))
            system.unitsHandlers[unitIdentifier] = ShieldBearerAttackHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        drawIdleShieldBearerUnit(system, unitIdentifier, screen);
    }
}