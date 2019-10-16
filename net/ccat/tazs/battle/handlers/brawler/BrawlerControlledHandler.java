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
    extends BrawlerPunchHandler
{
    static final BrawlerControlledHandler instance = new BrawlerControlledHandler();
    
    
    /***** INFORMATION *****/
    
    public boolean isControlled()
    {
        return true;
    }
    
    public boolean onPlayerControl(UnitsSystem system, int unitIdentifier, boolean control)
    {
        if (control)
            return false;
        system.unitsHandlers[unitIdentifier] = BrawlerIdleHandler.instance;
        return true;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];

        {
            float targetAngle = system.playerPadAngle;
            float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -ANGLE_ROTATION_BY_TICK, ANGLE_ROTATION_BY_TICK);
            
            unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
            system.unitsAngles[unitIdentifier] = unitAngle;
            if (system.playerPadLength > 0)
            {
                float unitSpeed = WALK_SPEED * system.playerPadLength;
                
                unitX += Math.cos(targetAngle) * unitSpeed;
                unitY += Math.sin(targetAngle) * unitSpeed;
                system.unitsXs[unitIdentifier] = unitX;
                system.unitsYs[unitIdentifier] = unitY;
            }
        }
        
        // TODO: Extremely similar to BrawlerPunchHandler.
        if (unitTimer == 0)
        {
            if (system.playerAction)
                unitTimer = 1;
        }
        else
        {
            unitTimer++;
            if (unitTimer < TIMER_PUNCH_MAX)
            {
                float handDistance = MathTools.lerp(unitTimer,
                                                    TIMER_INIT, HAND_IDLE_DISTANCE,
                                                    TIMER_PUNCH_MAX, HAND_MAX_DISTANCE);
                char unitTeam = system.unitsTeams[unitIdentifier];
                float weaponX = handX(unitX, unitAngle, handDistance);
                float weaponY = handY(unitY, unitAngle, handDistance);
                
                // TODO: 1-team isn't really a good way to find the other team.
                int hitUnitIdentifier = system.findClosestUnit(weaponX, weaponY, 1 - unitTeam, HAND_RADIUS + UNIT_RADIUS, false);
                
                if (hitUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
                {
                    system.unitsHandlers[hitUnitIdentifier].onHit(system, hitUnitIdentifier,
                                                                  HAND_POWER * Math.cos(unitAngle), HAND_POWER * Math.sin(unitAngle),
                                                                  HAND_POWER);
                    // Interpolating to find the equivalent withdrawal position.
                    unitTimer = MathTools.lerpi(unitTimer, 0, TIMER_PUNCH_MAX, TIMER_PUNCH_MAX, TIMER_PUNCH_REST);
                }
            }
            if (unitTimer == TIMER_PUNCH_REST)
                unitTimer = 0;
        }
        system.unitsTimers[unitIdentifier] = unitTimer;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        float handDistance = handDistanceForPunchTimer(unitTimer);
        
        // TODO: Standard stuff?
        screen.drawCircle(unitX, unitY, Dimensions.UNIT_CONTROL_RADIUS, Teams.colorForTeam(unitTeam), false);
        drawBrawler(unitX, unitY, unitAngle, handDistance, system.brawlerBodySpriteByTeam[unitTeam], system.handSprite, screen);
    }
}