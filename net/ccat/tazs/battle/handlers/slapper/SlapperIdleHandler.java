package net.ccat.tazs.battle.handlers.slapper;

import femto.mode.HiRes16Color;

import net.ccat.tazs.tools.MathTools;


/**
 * Handles the Idle state of a Slapper.
 * - Seeks the closest Enemy.
 * - Switch to SlapperPunch when close enough to punch them.
 * TODO: Actually not being Idle, as it seeks Enemies.
 */
public class SlapperIdleHandler
    extends BaseSlapperHandler
{
    static final SlapperIdleHandler instance = new SlapperIdleHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        if (HandlersTools.seekAnEnemy(system, unitIdentifier, WALK_SPEED, ANGLE_ROTATION_BY_TICK, CLOSE_DISTANCE_SQUARED, RECONSIDER_TICKS))
            system.unitsHandlers[unitIdentifier] = SlapperSlapHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        drawUnit(system, unitIdentifier, screen);
    }
}