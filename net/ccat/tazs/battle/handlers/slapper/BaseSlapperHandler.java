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

package net.ccat.tazs.battle.handlers.slapper;

import femto.Sprite;

import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.texts.UNIT_SLAPPER;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Base Handler for all Handlers related to the Slapper.
 */
public class BaseSlapperHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 100;
    public static final float WALK_SPEED = 0.250f;
    public static final float ANGLE_ROTATION_BY_TICK = 32.f / 256.f;
    public static final float HAND_IDLE_DISTANCE = 2.f;
    public static final float HAND_MAX_DISTANCE = 6.f;
    public static final float HAND_RADIUS = 1.f;
    public static final float HAND_POWER = 5.f;
    public static final float HAND_ANGLE_INCREMENT = Math.PI * 0.25f;
    public static final int ATTACK_TIMER_INIT = 0;
    public static final int ATTACK_TIMER_MAX = 4;
    public static final int ATTACK_TIMER_RETREATED = 8;
    public static final int ATTACK_TIMER_RESTED = 16;
    public static final float ATTACK_ANGLE_MAX = Math.PI * 0.03125f;
    
    public static final float CLOSE_DISTANCE = HAND_MAX_DISTANCE + HAND_RADIUS + HandlersTools.UNIT_RADIUS - 2;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    
    public static final int COST = 15;
    public static final float INVERSE_WEIGHT = 2.00;
    public static final int DEATH_TICKS = 50;
    public static final int RECONSIDER_TICKS = 128;
    
    public static final int UI_TIMER_WRAPPER = 60;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.SLAPPER;
    }
    
    public pointer name()
    {
        return UNIT_SLAPPER.bin();
    }
    
    public int startingHealth()
    {
        return HEALTH_INITIAL;
    }
    
    public int cost()
    {
        return COST;
    }
    
    public boolean isControlled()
    {
        return false;
    }
    
    public float inverseWeight()
    {
        return INVERSE_WEIGHT;
    }
    
    public boolean isReadyToAttack(UnitsSystem system, int unitIdentifier)
    {
        return false;
    }
    
    
    /***** EVENTS *****/
    
    public boolean onPlayerControl(UnitsSystem system, int unitIdentifier, boolean control)
    {
        if (control)
        {
            system.unitsHandlers[unitIdentifier] = SlapperControlledHandler.instance;
            return true;
        }
        return false;
    }
    
    public void onHit(UnitsSystem system, int unitIdentifier,
                      float powerX, float powerY, float power)
    {
        if (HandlersTools.hitAndCheckIfBecameDead(system, unitIdentifier, powerX, powerY, power))
            system.unitsHandlers[unitIdentifier] = SlapperDeadHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         int unitTimer,
                         AdvancedHiRes16Color screen)
    {
        drawStandingSlapper(unitX, unitY, unitAngle, handDistanceForAttackTimer(unitTimer % UI_TIMER_WRAPPER),
                            system.everythingSprite, baseFrameForTeam(unitTeam),
                            screen);
    }
    
    public void drawControlUI(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        HandlersTools.drawControlCircle(system, unitIdentifier, screen);
    }
    
    
    /***** RENDERING TOOLS *****/
    
    /**
     * @param unitTeam
     * @return The base frame for a given team.
     */
    public static int baseFrameForTeam(int unitTeam)
    {
        if (unitTeam == Teams.PLAYER)
            return VideoConstants.EVERYTHING_SLAPPERBODY_A_FRAME;
        if (unitTeam == Teams.ENEMY)
            return VideoConstants.EVERYTHING_SLAPPERBODY_B_FRAME;
        // Shouldn't happen!
        while (true);
        return VideoConstants.EVERYTHING_SLAPPERBODY_A_FRAME;
    }
    
    
    /**
     * Renders an Idle Slapper using its information inside system.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawIdleSlapperUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        
        drawStandingSlapper(unitX, unitY, unitAngle, HAND_IDLE_DISTANCE,
                            system.everythingSprite, baseFrameForTeam(unitTeam),
                            screen);
    }
    
    
    /**
     * Renders a Dying Slapper using its information inside system.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawDyingSlapperUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];

        drawDyingSlapperBody(unitX, unitY, unitAngle,
                             unitTimer,
                             system.everythingSprite, baseFrameForTeam(unitTeam),
                             screen);
    }
    
    /**
     * Renders an Attacking Slapper using its information inside system.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawAttackingSlapperUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        float handDistance = handDistanceForAttackTimer(unitTimer);
        
        drawStandingSlapper(unitX, unitY, unitAngle, handDistance,
                            system.everythingSprite, baseFrameForTeam(unitTeam),
                            screen);
    }
    
    /**
     * Renders a Slapper with its weapon at a given distance.
     * 
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param handDistance
     * @param everythingSprite
     * @param baseFrame
     * @param screen
     */
    public static void drawStandingSlapper(float unitX, float unitY, float unitAngle,
                                           float handDistance,
                                           NonAnimatedSprite everythingSprite, int baseFrame,
                                           AdvancedHiRes16Color screen)
    {
        // Is the hand above?
        if (unitAngle < 0)
            drawHand(unitX, unitY, unitAngle, handDistance, everythingSprite, screen);
        drawStandingSlapperBody(unitX, unitY, unitAngle, everythingSprite, baseFrame, screen);
        // Is the hand below?
        if (unitAngle >= 0)
            drawHand(unitX, unitY, unitAngle, handDistance, everythingSprite, screen);
    }
    
    /**
     * Renders the Slapper's Weapon.
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param handDistance
     * @param everythingSprite
     * @param screen
     */
    public static void drawHand(float unitX, float unitY, float unitAngle,
                                float handDistance,
                                NonAnimatedSprite everythingSprite,
                                AdvancedHiRes16Color screen)
    {
        everythingSprite.setMirrored(false);
        everythingSprite.selectFrame(VideoConstants.EVERYTHING_HAND_FRAME);
        everythingSprite.setPosition(handX(unitX, unitAngle, handDistance) - VideoConstants.EVERYTHING_ORIGIN_X,
                                     handY(unitY, unitAngle, handDistance) - VideoConstants.EVERYTHING_ORIGIN_Y - VideoConstants.SLAPPERBODY_HAND_OFFSET_Y);
        everythingSprite.draw(screen);
    }
    
    /**
     * Renders an Idle Slapper Body.
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param everythingSprite
     * @param baseFrame
     * @param screen
     */
    public static void drawStandingSlapperBody(float unitX, float unitY, float unitAngle,
                                               NonAnimatedSprite everythingSprite, int baseFrame,
                                               AdvancedHiRes16Color screen)
    {
        everythingSprite.selectFrame(baseFrame + VideoConstants.SLAPPERBODY_IDLE_FRAME);
        everythingSprite.setPosition(unitX - VideoConstants.EVERYTHING_ORIGIN_X, unitY - VideoConstants.EVERYTHING_ORIGIN_Y);
        everythingSprite.setMirrored(unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2);
        everythingSprite.draw(screen);
    }
    
    /**
     * Renders a Dying Brawler Body.
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param unitTimer
     * @param everythingSprite
     * @param baseFrame
     * @param screen
     */
    public static void drawDyingSlapperBody(float unitX, float unitY, float unitAngle,
                                            int unitTimer,
                                            NonAnimatedSprite everythingSprite, int baseFrame,
                                            AdvancedHiRes16Color screen)
    {
        int rawFrame = MathTools.lerpi(unitTimer, 0, VideoConstants.SLAPPERBODY_DEAD_FRAMES_LAST, DEATH_TICKS, VideoConstants.SLAPPERBODY_DEAD_FRAMES_START);
        int frame = baseFrame + MathTools.clampi(rawFrame, VideoConstants.SLAPPERBODY_DEAD_FRAMES_START, VideoConstants.SLAPPERBODY_DEAD_FRAMES_LAST);
        
        everythingSprite.selectFrame(frame);
        everythingSprite.setPosition(unitX - VideoConstants.EVERYTHING_ORIGIN_X, unitY - VideoConstants.EVERYTHING_ORIGIN_Y);
        everythingSprite.setMirrored(unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2);
        everythingSprite.draw(screen);
    }
    
    
    /***** ATTACKING *****/
    
    /**
     * Starts the Attack.
     * 
     * @param system
     * @param unitIdentifier
     */
    public static void startAttack(UnitsSystem system, int unitIdentifier)
    {
        system.unitsTimers[unitIdentifier] = 1;
    }
    
    /**
     * Handles a started attack.
     * 
     * @param system
     * @param unitIdentifier
     * @return False if the attack ended.
     */
    public static boolean handleAttack(UnitsSystem system, int unitIdentifier)
    {
        int unitTimer = system.unitsTimers[unitIdentifier] + 1;
        
        if (unitTimer < ATTACK_TIMER_MAX)
        {
            float handDistance = MathTools.lerp(unitTimer,
                                                ATTACK_TIMER_INIT, HAND_IDLE_DISTANCE,
                                                ATTACK_TIMER_MAX, HAND_MAX_DISTANCE);
            float unitX = system.unitsXs[unitIdentifier];
            float unitY = system.unitsYs[unitIdentifier];
            float unitAngle = system.unitsAngles[unitIdentifier];
            byte unitTeam = system.unitsTeams[unitIdentifier];
            float weaponX = handX(unitX, unitAngle, handDistance);
            float weaponY = handY(unitY, unitAngle, handDistance);
            
            int hitUnitIdentifier = system.findClosestLivingUnit(weaponX, weaponY, Teams.oppositeTeam(unitTeam),
                                                                 HAND_RADIUS + HandlersTools.UNIT_RADIUS);
            
            if (hitUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
            {
                system.unitsHandlers[hitUnitIdentifier].onHit(system, hitUnitIdentifier,
                                                              HAND_POWER * Math.cos(unitAngle), HAND_POWER * Math.sin(unitAngle),
                                                              HAND_POWER);
                
                // Rotates the Enemy a bit, depending on its (inverse) weight.
                float enemyAngle = system.unitsAngles[hitUnitIdentifier];
                float enemyInverseWeight = system.unitsHandlers[hitUnitIdentifier].inverseWeight();
                
                enemyAngle = MathTools.wrapAngle(enemyAngle + HAND_ANGLE_INCREMENT * enemyInverseWeight);
                system.unitsAngles[hitUnitIdentifier] = enemyAngle;
                
                // Interpolating to find the equivalent withdrawal position.
                unitTimer = MathTools.lerpi(unitTimer, ATTACK_TIMER_INIT, ATTACK_TIMER_RETREATED, ATTACK_TIMER_MAX, ATTACK_TIMER_MAX);
            }
        }
        if (unitTimer == ATTACK_TIMER_RESTED)
            unitTimer = 0;
        system.unitsTimers[unitIdentifier] = unitTimer;
        return unitTimer != 0;
    }
    
    
    /***** TOOLS *****/
    
    
    public static float handX(float unitX, float unitAngle, float handDistance)
    {
        return unitX + handDistance * Math.cos(unitAngle);
    }
    public static float handY(float unitY, float unitAngle, float handDistance)
    {
        return unitY + handDistance * Math.sin(unitAngle);
    }
    
    /**
     * @param unitTimer The Unit's timer value.
     * @return The distance for the hand.
     */
    public static static float handDistanceForAttackTimer(int unitTimer)
    {
        if (unitTimer < ATTACK_TIMER_MAX)
            return MathTools.lerp(unitTimer,
                                  ATTACK_TIMER_INIT, HAND_IDLE_DISTANCE,
                                  ATTACK_TIMER_MAX, HAND_MAX_DISTANCE);
        else if (unitTimer < ATTACK_TIMER_RETREATED)
            return MathTools.lerp(unitTimer,
                                  ATTACK_TIMER_MAX, HAND_MAX_DISTANCE,
                                  ATTACK_TIMER_RETREATED, HAND_IDLE_DISTANCE);
        return HAND_IDLE_DISTANCE;
    }
}