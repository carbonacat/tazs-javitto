package net.ccat.tazs.battle.handlers.brawler;

import femto.mode.HiRes16Color;

import net.ccat.tazs.tools.MathTools;


/**
 * Handles the Seek state of a Brawler.
 * - Seeks the closest Enemy.
 * - Switch to BrawlerPunch when close enough to punch them.
 */
public class BrawlerSeekHandler
    extends BaseBrawlerHandler
{
    static final BrawlerSeekHandler instance = new BrawlerSeekHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        if (HandlersTools.seekAnEnemy(system, unitIdentifier, WALK_SPEED, ANGLE_ROTATION_BY_TICK, CLOSE_DISTANCE_SQUARED, RECONSIDER_TICKS,
                                      ATTACK_ANGLE_MAX))
            system.unitsHandlers[unitIdentifier] = BrawlerPunchHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        drawIdleBrawlerUnit(system, unitIdentifier, screen);
    }
}