package net.ccat.tazs.battle.handlers.sworder;

import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Handles the Idle state of a Sworder.
 * - Seeks the closest Enemy.
 * - Switch to SworderPunch when close enough to punch them.
 */
public class SworderSeekHandler
    extends BaseSworderHandler
{
    static final SworderSeekHandler instance = new SworderSeekHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        if (HandlersTools.seekAnEnemy(system, unitIdentifier, WALK_SPEED, ANGLE_ROTATION_BY_TICK, CLOSE_DISTANCE_SQUARED, RECONSIDER_TICKS,
                                      ATTACK_ANGLE_MAX))
            system.unitsHandlers[unitIdentifier] = SworderAttackHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        drawIdleSworderUnit(system, unitIdentifier, screen);
    }
}