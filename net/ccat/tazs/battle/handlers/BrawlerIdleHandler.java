package net.ccat.tazs.battle.handlers;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


/**
 * Handles the Idle state of a Brawler.
 * - Seeks the closest Enemy.
 * - Switch to BrawlerPunch when close enough to punch them.
 * TODO: Actually not being Idle, as it seeks Enemies.
 */
public class BrawlerIdleHandler
    extends BaseBrawlerHandler
{
    static final BrawlerIdleHandler instance = new BrawlerIdleHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int unitTimer = system.unitsTimers[unitIdentifier];
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        
        // Random walk
        unitTimer--;
        if (unitTimer <= 0)
        {
        
            // TODO: Not the right way to find another team.
            targetIdentifier = system.findClosestUnit(unitX, unitY, 1 - unitTeam, SEEK_DISTANCE_MAX, true);
            system.unitsTargetIdentifiers[unitIdentifier] = targetIdentifier;
            unitTimer = 128;
        }
        if (targetIdentifier != UnitsSystem.IDENTIFIER_NONE)
        {
            float relativeX = system.unitsXs[targetIdentifier] - unitX;
            float relativeY = system.unitsYs[targetIdentifier] - unitY;
            float squaredDistance = relativeX * relativeX + relativeY * relativeY;
            
            // Updating the angle
            if (squaredDistance > 0)
            {
                float targetAngle = Math.atan2(relativeY, relativeX);
                float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -ANGLE_ROTATION_BY_TICK, ANGLE_ROTATION_BY_TICK);
                
                unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
            }
            if (squaredDistance > CLOSE_DISTANCE_SQUARED)
            {
                unitX += Math.cos(unitAngle) * WALK_SPEED;
                unitY += Math.sin(unitAngle) * WALK_SPEED;
            }
            else
            {
                 // Let's punch them!
                unitTimer = 0;
                system.unitsHandlers[unitIdentifier] = BrawlerPunchHandler.instance;
            }
        }

        // Updating the changed state.
        system.unitsTimers[unitIdentifier] = unitTimer;
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
        
        drawBrawler(unitX, unitY, unitAngle, HAND_IDLE_DISTANCE, system.brawlerBodySpriteByTeam[unitTeam], system.handSprite, screen);
    }
}