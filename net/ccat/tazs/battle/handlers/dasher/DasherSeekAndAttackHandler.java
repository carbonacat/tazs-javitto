//
// Copyright (C) 2019 Carbonacat
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

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
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        if ((unitTimer >= DASH_TIMER_INIT) && (unitTimer <= DASH_TIMER_END))
            drawDashingDasherUnit(system, unitIdentifier, screen);
        else
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
            unitTimer = READY_TICKS;
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
            if (unitTimer >= READY_TICKS)
            {
                if (system.unitsHealths[targetIdentifier] == 0)
                {
                    // Target is already dead - time to reconsider things!.
                    unitTimer = RECONSIDER_TICKS;
                }
                else if ((squaredDistance < CLOSE_DISTANCE_SQUARED) && (MathTools.abs(MathTools.wrapAngle(targetAngle - unitAngle)) <= DASH_ANGLE_MAX))
                {
                    // Let's start dashing!
                    unitTimer = DASH_TIMER_INIT;
                }
            }
        }
        else if (((MathTools.abs(unitX) >= HandlersTools.FAR_DISTANCE) || (MathTools.abs(unitY) >= HandlersTools.FAR_DISTANCE))
                 || (unitX * unitX + unitY * unitY > HandlersTools.FAR_DISTANCE_SQUARED))
        {
            // Make them return to the center.
            float targetAngle = Math.atan2(-unitY, -unitX);
            float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -ANGLE_ROTATION_BY_TICK, ANGLE_ROTATION_BY_TICK);
                
            unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
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
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        
        unitTimer++;
        if (unitTimer < DASH_TIMER_END)
        {
            byte unitTeam = system.unitsTeams[unitIdentifier];
            
            // Can't stop!
            unitX += Math.cos(unitAngle) * DASH_SPEED;
            unitY += Math.sin(unitAngle) * DASH_SPEED;
            
            // Dash attack will hit the closest unit with a pushback.
            
            for (int hitI = 0; hitI < DASH_HITS; hitI++)
            {
                int enemyIdentifier = system.findClosestLivingUnit(unitX, unitY, Teams.oppositeTeam(unitTeam), DASH_RADIUS);
                
                if (enemyIdentifier != UnitsSystem.IDENTIFIER_NONE)
                {
                    float unitToEnemyX = system.unitsXs[enemyIdentifier] - unitX;
                    float unitToEnemyY = system.unitsYs[enemyIdentifier] - unitY;
                    float unitToEnemySquared = unitToEnemyX * unitToEnemyX + unitToEnemyY * unitToEnemyY;
                    float unitToEnemy = 0;
                    
                    if (unitToEnemySquared == 0)
                    {
                        unitToEnemyX = 1;
                        unitToEnemyY = 0;
                        unitToEnemy = 1;
                    }
                    else
                        unitToEnemy = Math.sqrt(unitToEnemySquared);
                    system.unitsHandlers[enemyIdentifier].onHit(system, enemyIdentifier,
                                                                unitToEnemyX * DASH_POWER, unitToEnemyY * DASH_POWER, DASH_POWER);
                }
                else
                    hitI = DASH_HITS;
            }
        }
        else
        {
            unitTimer = DASH_TIMER_RECOVER;
            
            // Can't stop!
            unitX += Math.cos(unitAngle) * WALK_SPEED;
            unitY += Math.sin(unitAngle) * WALK_SPEED;
        }
        
        // Updating the changed state.
        system.unitsTimers[unitIdentifier] = unitTimer;
        system.unitsXs[unitIdentifier] = unitX;
        system.unitsYs[unitIdentifier] = unitY;
        system.unitsAngles[unitIdentifier] = unitAngle;
    }
}