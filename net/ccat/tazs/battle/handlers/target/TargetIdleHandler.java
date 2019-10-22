package net.ccat.tazs.battle.handlers.target;

import femto.mode.HiRes16Color;

import net.ccat.tazs.tools.MathTools;


/**
 * Handles the Idle state of a Target.
 * - Seeks the closest Enemy.
 * - Switch to TargetPunch when close enough to punch them.
 * TODO: Actually not being Idle, as it seeks Enemies.
 */
public class TargetIdleHandler
    extends BaseTargetHandler
{
    static final TargetIdleHandler instance = new TargetIdleHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        // Nothing to do.
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        drawUnit(system, unitIdentifier, screen);
    }
}