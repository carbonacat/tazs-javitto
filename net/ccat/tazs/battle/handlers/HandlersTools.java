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

package net.ccat.tazs.battle.handlers;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;
import net.ccat.tazs.ui.UITools;


/**
 * A collection of Tools related to Handlers.
 */
public class HandlersTools
{
    public static final float POWER_HP_RATIO = 3.f;
    public static final float SEEK_DISTANCE_MAX = 250.f;
    public static final float UNIT_RADIUS = 3.f;
    public static final int RADIUS_FREQUENCY = 256;
    public static final float POWER_TO_FORCE = 0.25f;
    public static final float FAR_DISTANCE = SEEK_DISTANCE_MAX / 2.f;
    public static final float FAR_DISTANCE_SQUARED = FAR_DISTANCE * FAR_DISTANCE;
    
    
    /***** RENDERING *****/
    
    /**
     * Renders the Control Circle for the given Unit.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen.
     */
    public static void drawControlCircle(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        
        if (system.unitsHealths[unitIdentifier] == 0)
            screen.drawCircle(unitX, unitY, Dimensions.UNIT_CONTROL_RADIUS, Teams.darkerColorForTeam(unitTeam), false);
        else
        {
            float radius = MathTools.lerp(System.currentTimeMillis() % RADIUS_FREQUENCY, 0, Dimensions.UNIT_CONTROL_DIRECTION_LENGTH, RADIUS_FREQUENCY, Dimensions.UNIT_CONTROL_RADIUS);
            
            screen.drawCircle(unitX, unitY, radius + 1.f, Teams.colorForTeam(unitTeam), false);
            screen.drawCircle(unitX, unitY, radius, Teams.darkerColorForTeam(unitTeam), false);
            screen.drawLine(unitX + Math.cos(unitAngle) * Dimensions.UNIT_CONTROL_RADIUS, unitY + Math.sin(unitAngle) * Dimensions.UNIT_CONTROL_RADIUS,
                            unitX + Math.cos(unitAngle) * Dimensions.UNIT_CONTROL_DIRECTION_LENGTH, unitY + Math.sin(unitAngle) * Dimensions.UNIT_CONTROL_DIRECTION_LENGTH,
                            Teams.colorForTeam(unitTeam), false);
        }
    }
    
    /**
     * Renders the Control Target at the given position.
     * @param everyUISprite
     * @param targetX
     * @param targetY
     * @param screen
     */
    public static void drawControlTarget(NonAnimatedSprite everyUISprite, float targetX, float targetY,
                                         AdvancedHiRes16Color screen)
    {
        everyUISprite.setMirrored(false);
        everyUISprite.setPosition(targetX - VideoConstants.EVERYUI_ORIGIN_X - screen.cameraX, targetY - VideoConstants.EVERYUI_ORIGIN_Y - screen.cameraY);
        everyUISprite.selectFrame(UITools.blinkingValue() ? VideoConstants.EVERYUI_TARGET_FRAMES_START : VideoConstants.EVERYUI_TARGET_FRAMES_END);
        everyUISprite.draw(screen);
    }   
    
    /***** STANDARD BEHAVIOR *****/
    
    /**
     * Handles a movement using the player controls.
     * 
     * @param system
     * @param unitIdentifier
     * @param rotationSpeed How fast the angle will change in one tick.
     * @param walkSpeed How fast a unit is moving.
     */
    public static void moveUnitWithPad(UnitsSystem system, int unitIdentifier, float rotationSpeed, float walkSpeed)
    {
        if (system.playerPadLength > 0)
        {
            float unitX = system.unitsXs[unitIdentifier];
            float unitY = system.unitsYs[unitIdentifier];
            float unitSpeed = walkSpeed * system.playerPadLength;
            float unitAngle = system.unitsAngles[unitIdentifier];
            float targetAngle = system.playerPadAngle;
            
            if (!system.playerSecondaryAction)
            {
                float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -rotationSpeed, rotationSpeed);
                
                unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
                system.unitsAngles[unitIdentifier] = unitAngle;
            }
            unitX += Math.cos(targetAngle) * unitSpeed;
            unitY += Math.sin(targetAngle) * unitSpeed;
            system.unitsXs[unitIdentifier] = unitX;
            system.unitsYs[unitIdentifier] = unitY;
        }
    }
    
    /**
     * Seeks an enemy, walk toward it until at range.
     * 
     * @param system
     * @param unitIdentifier
     * @param walkSpeed How fast per tick the Unit moves.
     * @param rotationSpeed How fast per tick the Unit rotates.
     * @param closeEnoughDistanceSquared The distance where the Unit is close enough to attack, but squared.
     * @param ticksUntilChangingTarget Ticks before the Unit consider another target.
     * @param attackAngleMax The maximum difference between the current Unit's angle and it's Opponent relative one.
     * @return True if an enemy is close enough -
     */
    public static boolean seekAnEnemy(UnitsSystem system, int unitIdentifier,
                                      float walkSpeed, float rotationSpeed,
                                      float closeEnoughDistanceSquared,
                                      int ticksUntilChangingTarget,
                                      float attackAngleMax)
    {
        int unitTimer = system.unitsTimers[unitIdentifier];
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        boolean closeEnough = false;
        
        // Random walk
        unitTimer--;
        if (unitTimer <= 0)
        {
            targetIdentifier = system.findClosestLivingUnit(unitX, unitY, Teams.oppositeTeam(unitTeam), SEEK_DISTANCE_MAX);
            system.unitsTargetIdentifiers[unitIdentifier] = targetIdentifier;
            unitTimer = ticksUntilChangingTarget;
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
                float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -rotationSpeed, rotationSpeed);
                
                unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
            }
            if (squaredDistance > closeEnoughDistanceSquared)
            {
                unitX += Math.cos(unitAngle) * walkSpeed;
                unitY += Math.sin(unitAngle) * walkSpeed;
            }
            else if (MathTools.abs(MathTools.wrapAngle(targetAngle - unitAngle)) <= attackAngleMax)
            {
                 // Let's punch them!
                unitTimer = 0;
                closeEnough = true;
            }
        }
        else if (((MathTools.abs(unitX) >= FAR_DISTANCE) || (MathTools.abs(unitY) >= FAR_DISTANCE))
                 || (unitX * unitX + unitY * unitY > FAR_DISTANCE_SQUARED))
        {
            // Make them return to the center.
            float targetAngle = Math.atan2(-unitY, -unitX);
            float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -rotationSpeed, rotationSpeed);
                
            unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
            unitX += Math.cos(unitAngle) * walkSpeed;
            unitY += Math.sin(unitAngle) * walkSpeed;
        }

        // Updating the changed state.
        system.unitsTimers[unitIdentifier] = unitTimer;
        system.unitsXs[unitIdentifier] = unitX;
        system.unitsYs[unitIdentifier] = unitY;
        system.unitsAngles[unitIdentifier] = unitAngle;
        return closeEnough;
    }
    
    /**
     * Hits a Unit with a power.
     * 
     * @param system
     * @param unitIdentifier
     * @param powerX
     * @param powerY
     * @param power
     * @return True if the Unit just died because of the hit, false elsewhere.
     */
    public static boolean hitAndCheckIfBecameDead(UnitsSystem system, int unitIdentifier,
                                                  float powerX, float powerY, float power)
    {
        short health = system.unitsHealths[unitIdentifier];
        float inverseWeight = system.unitsHandlers[unitIdentifier].inverseWeight();
        
        // TODO: Do a proper pushback. [011]
        system.unitsXs[unitIdentifier] += powerX * POWER_TO_FORCE * inverseWeight;
        system.unitsYs[unitIdentifier] += powerY * POWER_TO_FORCE * inverseWeight;
        if (health > 0)
        {
            float lostHealth = power * POWER_HP_RATIO;
            
            if (health > lostHealth)
                health -= (short)(int)lostHealth;
            else
            {
                system.unitsTimers[unitIdentifier] = 0;
                health = 0;
            }
            system.unitsHealths[unitIdentifier] = health;
            return health == 0;
        }
        return false;
    }
}