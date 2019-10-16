package net.ccat.tazs.battle.handlers.brawler;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


/**
 * Handles the Controlled state of a Brawler.
 * - Reads the PAD
 * - Switch to BrawlerDead when dead
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
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];

        {
            float targetAngle = system.padAngle;
            float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -ANGLE_ROTATION_BY_TICK, ANGLE_ROTATION_BY_TICK);
            
            unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
            if (system.padLength > 0)
            {
                unitX += Math.cos(unitAngle) * WALK_SPEED * system.padLength;
                unitY += Math.sin(unitAngle) * WALK_SPEED * system.padLength;
            }
        }
        
        system.unitsXs[unitIdentifier] = unitX;
        system.unitsYs[unitIdentifier] = unitY;
        system.unitsAngles[unitIdentifier] = unitAngle;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        
        // TODO: Standard stuff?
        screen.drawCircle(unitX, unitY, Dimensions.UNIT_CONTROL_RADIUS, Teams.colorForTeam(unitTeam), false);
        drawBrawler(unitX, unitY, unitAngle, HAND_IDLE_DISTANCE, system.brawlerBodySpriteByTeam[unitTeam], system.handSprite, screen);
    }
}