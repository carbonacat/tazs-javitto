package net.ccat.tazs.battle.handlers.dasher;

import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Handles the Seek & Dash state of a Dasher.
 * - Seeks the closest Enemy.
 * - Dashes through them.
 */
public class DasherSeekAndAttackHandler
    extends BaseDasherHandler
{
    static final DasherSeekAndAttackHandler instance = new DasherSeekAndAttackHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        // TODO: Might need a custom solution since this guy is always running anyway.
        if (HandlersTools.seekAnEnemy(system, unitIdentifier, WALK_SPEED, ANGLE_ROTATION_BY_TICK, CLOSE_DISTANCE_SQUARED, RECONSIDER_TICKS,
                                      DASH_ANGLE_MAX))
        {
            // TODO: Switch to dashing.
        }
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        // TODO: Render the dashing mode if applicable.
        drawRunningDasherUnit(system, unitIdentifier, screen);
    }
}