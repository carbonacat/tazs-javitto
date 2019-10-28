package net.ccat.tazs.battle.handlers.slapper;

import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Handles the Seek state of a Slapper.
 * - Seeks the closest Enemy.
 * - Switch to SlapperPunch when close enough to punch them.
 */
public class SlapperSeekHandler
    extends BaseSlapperHandler
{
    static final SlapperSeekHandler instance = new SlapperSeekHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        if (HandlersTools.seekAnEnemy(system, unitIdentifier, WALK_SPEED, ANGLE_ROTATION_BY_TICK, CLOSE_DISTANCE_SQUARED, RECONSIDER_TICKS,
                                      ATTACK_ANGLE_MAX))
            system.unitsHandlers[unitIdentifier] = SlapperSlapHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        drawIdleSlapperUnit(system, unitIdentifier, screen);
    }
}