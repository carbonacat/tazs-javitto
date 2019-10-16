package net.ccat.tazs.battle.handlers.brawler;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


/**
 * Handles the Controlled state of a Brawler.
 * - Reads the PAD
 * - Switch to BrawlerDead when dead
 * TODO: Actually not being Idle, as it seeks Enemies.
 */
public class BrawlerControlledHandler
    extends BaseBrawlerHandler
{
    static final BrawlerControlledHandler instance = new BrawlerControlledHandler();
    
    
    /***** INFORMATION *****/
    
    public boolean isControlled()
    {
        return true;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        
        drawBrawler(unitX, unitY, unitAngle, HAND_IDLE_DISTANCE, system.brawlerBodySpriteByTeam[unitTeam], system.handSprite, screen);
    }
}