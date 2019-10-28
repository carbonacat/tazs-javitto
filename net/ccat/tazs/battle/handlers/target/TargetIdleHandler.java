package net.ccat.tazs.battle.handlers.target;

import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


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
    
    public void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        drawTargetUnit(system, unitIdentifier, screen);
    }
}