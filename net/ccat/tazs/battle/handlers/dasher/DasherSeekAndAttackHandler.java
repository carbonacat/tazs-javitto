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
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        if (unitTimer <= 0)
            onSeekingTick(system, unitIdentifier, unitTimer);
        else
            onDashingTick(system, unitIdentifier, unitTimer);
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        // TODO: Render the dashing mode if applicable.
        drawRunningDasherUnit(system, unitIdentifier, screen);
    }
    
    
    /***** BEHAVIORS *****/
    
    private static void onSeekingTick(UnitsSystem system, int unitIdentifier, int unitTimer)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        
        unitTimer++;
        if (unitTimer > 0)
        {
            // Might not be best location for that fast, slow-turning unit.
            // It might be better to use a fitness function.
            targetIdentifier = system.findClosestLivingUnit(unitX, unitY, Teams.oppositeTeam(unitTeam), HandlersTools.SEEK_DISTANCE_MAX);
            system.unitsTargetIdentifiers[unitIdentifier] = targetIdentifier;
            unitTimer = RECONSIDER_TICKS;
        }
        if (targetIdentifier != UnitsSystem.IDENTIFIER_NONE)
        {
            float relativeX = system.unitsXs[targetIdentifier] - unitX;
            float relativeY = system.unitsYs[targetIdentifier] - unitY;
            float squaredDistance = relativeX * relativeX + relativeY * relativeY;
            float targetAngle = 0;
            
            // Updating the angle.
            if (squaredDistance > 0)
            {
                targetAngle = Math.atan2(relativeY, relativeX);
                float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -ANGLE_ROTATION_BY_TICK, ANGLE_ROTATION_BY_TICK);
                
                unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
            }
            if ((squaredDistance > CLOSE_DISTANCE_SQUARED) && (MathTools.abs(MathTools.wrapAngle(targetAngle - unitAngle)) <= DASH_ANGLE_MAX))
            {
                // TODO: Dash mode!
            }
        }
        else
        {
            // Keep running in round. Or maybe bring out the conveyor?
            unitAngle = MathTools.wrapAngle(unitAngle + ANGLE_ROTATION_BY_TICK);
        }

        // Can't stop!
        unitX += Math.cos(unitAngle) * WALK_SPEED;
        unitY += Math.sin(unitAngle) * WALK_SPEED;
        
        // Updating the changed state.
        system.unitsTimers[unitIdentifier] = unitTimer;
        system.unitsXs[unitIdentifier] = unitX;
        system.unitsYs[unitIdentifier] = unitY;
        system.unitsAngles[unitIdentifier] = unitAngle;
    }
    
    private static void onDashingTick(UnitsSystem system, int unitIdentifier, int unitTimer)
    {
        
    }
}