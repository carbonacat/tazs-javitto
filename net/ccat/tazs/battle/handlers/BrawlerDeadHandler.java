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
    static final BrawlerDeadHandler instance = new BrawlerDeadHandler();
    
    
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
        char unitTeam = system.unitsTeams[unitIdentifier];
        
        drawDeadBrawler(unitX, unitY, unitAngle, system.brawlerBodySpriteByTeam[unitTeam], screen);
    }
}