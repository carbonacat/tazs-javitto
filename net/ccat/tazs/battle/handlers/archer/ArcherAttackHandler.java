package net.ccat.tazs.battle.handlers.archer;

import femto.mode.HiRes16Color;

import net.ccat.tazs.tools.MathTools;


/**
 * Handles when a Archer slaps a target.
 * Goes back to ArcherIdle when it's done.
 */
public class ArcherAttackHandler
    extends BaseArcherHandler
{
    static final ArcherAttackHandler instance = new ArcherAttackHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        
        if ((targetIdentifier == UnitsSystem.IDENTIFIER_NONE) || (system.unitsHealths[targetIdentifier] == 0))
        {
            system.unitsTimers[unitIdentifier] = 0;
            system.unitsHandlers[unitIdentifier] = ArcherSeekHandler.instance;
        }
        else if (system.unitsTimers[unitIdentifier] == 0)
            startAttack(system, unitIdentifier);
        else
        {
            int unitTimer = system.unitsTimers[unitIdentifier];
            float unitX = system.unitsXs[unitIdentifier];
            float unitY = system.unitsYs[unitIdentifier];
            float unitAngle = system.unitsAngles[unitIdentifier];
            char unitTeam = system.unitsTeams[unitIdentifier];
            float targetDistance = targetDistanceWhenCharging(unitTimer);
            
            // Adjusting the angle toward the target.
            float unitToTargetUnitX = system.unitsXs[targetIdentifier] - unitX;
            float unitToTargetUnitY = system.unitsYs[targetIdentifier] - unitY;
            float squaredDistance = unitToTargetUnitX * unitToTargetUnitX + unitToTargetUnitY * unitToTargetUnitY;

            // Looking at the target.
            if (squaredDistance > 0)
            {
                float targetAngle = Math.atan2(unitToTargetUnitY, unitToTargetUnitX);
                float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -ANGLE_ROTATION_BY_TICK, ANGLE_ROTATION_BY_TICK);
                
                unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
                system.unitsAngles[unitIdentifier] = unitAngle;
            }
            
            float targetX = unitX + Math.cos(unitAngle) * targetDistance;
            float targetY = unitY + Math.sin(unitAngle) * targetDistance;
            int hitUnitIdentifier = system.findClosestLivingUnit(targetX, targetY, Teams.oppositeTeam(unitTeam),
                                                                 ATTACK_RADIUS + HandlersTools.UNIT_RADIUS);

            if (!handleAttack(system, unitIdentifier, hitUnitIdentifier == UnitsSystem.IDENTIFIER_NONE))
                system.unitsHandlers[unitIdentifier] = ArcherSeekHandler.instance;
        }
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        drawAttackingArcherUnit(system, unitIdentifier, screen);
    }
}