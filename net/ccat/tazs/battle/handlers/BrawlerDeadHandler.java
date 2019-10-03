package net.ccat.tazs.battle.handlers;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


/**
 * Handles the Dead state of a Brawler.
 * - Pretty much renders a corpse.
 */
public class BrawlerDeadHandler
    extends BaseBrawlerHandler
{
    static final BrawlerDeadHandler alliedInstance = new BrawlerDeadHandler(true);
    static final BrawlerDeadHandler enemyInstance = new BrawlerDeadHandler(false);
    
    static BrawlerDeadHandler instance(boolean isAllied)
    {
        return isAllied ? alliedInstance : enemyInstance;
    }
    
    
    public BrawlerDeadHandler(boolean isAllied)
    {
        super(isAllied);
    }
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        // Nothing.
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        
        drawDeadBrawler(unitX, unitY, unitAngle, system.brawlerBodySprite, screen);
    }
}