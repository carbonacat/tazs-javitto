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

package net.ccat.tazs.battle.handlers.sworder;

import femto.Sprite;

import net.ccat.tazs.battle.handlers.brawler.BaseBrawlerHandler;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.texts.UNIT_SWORDER;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Base Handler for all Handlers related to the Sworder.
 */
public class BaseSworderHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 150;
    public static final float WALK_SPEED = 0.200f;
    public static final float ANGLE_ROTATION_BY_TICK = 24.f / 256.f;
    public static final float HAND_IDLE_DISTANCE = 2.f;
    public static final float HAND_MAX_DISTANCE = 6.f;
    public static final float SWORD_RADIUS = 2.f;
    public static final float SWORD_POWER = 10.f;
    public static final float SWORD_RANGE_RATIO = 1.5f;
    public static final int ATTACK_TIMER_INIT = 0;
    public static final int ATTACK_TIMER_MAX = 8;
    public static final int ATTACK_TIMER_RETREATED = 16;
    public static final int ATTACK_TIMER_RESTED = 32;
    public static final float ATTACK_ANGLE_MAX = Math.PI * 0.125f;
    
    public static final float CLOSE_DISTANCE = HAND_MAX_DISTANCE * SWORD_RANGE_RATIO + SWORD_RADIUS + HandlersTools.UNIT_RADIUS - 2;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    
    public static final int COST = 15;
    public static final float INVERSE_WEIGHT = 1.75;
    public static final int RECONSIDER_TICKS = 128;
    
    public static final int UI_TIMER_WRAPPER = 60;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.SWORDER;
    }
    
    public pointer name()
    {
        return UNIT_SWORDER.bin();
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
            system.unitsHandlers[unitIdentifier] = SworderControlledHandler.instance;
            return true;
        }
        return false;
    }
    
    public void onHit(UnitsSystem system, int unitIdentifier,
                      float powerX, float powerY, float power)
    {
        if (HandlersTools.hitAndCheckIfBecameDead(system, unitIdentifier, powerX, powerY, power))
            system.unitsHandlers[unitIdentifier] = SworderDeadHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         int unitTimer,
                         AdvancedHiRes16Color screen)
    {
        drawStandingSworder(unitX, unitY, unitAngle,
                            handDistanceForAttackTimer(unitTimer % UI_TIMER_WRAPPER),
                            system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                            VideoConstants.EVERYTHING_SWORD_FRAME + swordFrameForAttackTimer(unitTimer % UI_TIMER_WRAPPER),
                            screen);
    }
    
    public void drawControlUI(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        HandlersTools.drawControlCircle(system, unitIdentifier, screen);
    }
    
    
    /***** RENDERING TOOLS *****/
    
    /**
     * Renders an Idle Sworder.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawIdleSworderUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        
        drawStandingSworder(unitX, unitY, unitAngle,
                            HAND_IDLE_DISTANCE,
                            system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                            VideoConstants.EVERYTHING_SWORD_FRAME + VideoConstants.SWORD_VERTICAL_FRAME,
                            screen);
    }
    
    /**
     * Renders a Dying Sworder.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawDyingSworderUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        // Is the hand above?
        if (unitAngle < 0)
            drawSword(unitX, unitY + swordYOffsetForDeathTimer(unitTimer), unitAngle,
                      HAND_IDLE_DISTANCE,
                      system.everythingSprite, VideoConstants.EVERYTHING_SWORD_FRAME + swordFrameForDeathTimer(unitTimer),
                      screen);
        BaseBrawlerHandler.drawDyingBrawlerBody(unitX, unitY, unitAngle,
                                                unitTimer,
                                                system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                                                screen);
        // Is the hand below?
        if (unitAngle >= 0)
            drawSword(unitX, unitY + swordYOffsetForDeathTimer(unitTimer), unitAngle,
                      HAND_IDLE_DISTANCE,
                      system.everythingSprite, VideoConstants.EVERYTHING_SWORD_FRAME + swordFrameForDeathTimer(unitTimer),
                      screen);
    }
    
    public static void drawAttackingSworderUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        float handDistance = handDistanceForAttackTimer(unitTimer);
        
        drawStandingSworder(unitX, unitY, unitAngle,
                            handDistance,
                            system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                            VideoConstants.EVERYTHING_SWORD_FRAME + swordFrameForAttackTimer(unitTimer),
                            screen);
    }
    
    /**
     * Renders a Sworder with its weapon at a given distance.
     * 
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param handDistance
     * @param everythingSprite
     * @param baseFrame
     * @param swordFrame
     * @param screen
     */
    public static void drawStandingSworder(float unitX, float unitY, float unitAngle,
                                           float handDistance,
                                           NonAnimatedSprite everythingSprite, int baseFrame,
                                           int swordFrame,
                                           AdvancedHiRes16Color screen)
    {
        // Is the hand above?
        if (unitAngle < 0)
            drawSword(unitX, unitY, unitAngle,
                      handDistance,
                      everythingSprite, swordFrame,
                      screen);
        BaseBrawlerHandler.drawStandingBrawlerBody(unitX, unitY, unitAngle,
                                                   everythingSprite, baseFrame,
                                                   screen);
        // Is the hand below?
        if (unitAngle >= 0)
            drawSword(unitX, unitY, unitAngle,
                      handDistance,
                      everythingSprite, swordFrame,
                      screen);
    }
    
    /**
     * Renders the Sworder's Weapon.
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param handDistance
     * @param everythingSprite
     * @param swordFrame
     * @param screen
     */
    public static void drawSword(float unitX, float unitY, float unitAngle,
                                 float handDistance,
                                 NonAnimatedSprite everythingSprite, int swordFrame,
                                 AdvancedHiRes16Color screen)
    {
        boolean mirrored = unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2;
        
        everythingSprite.setPosition(handX(unitX, unitAngle, handDistance) - VideoConstants.EVERYTHING_ORIGIN_X,
                                handY(unitY, unitAngle, handDistance) - VideoConstants.EVERYTHING_ORIGIN_Y - VideoConstants.BRAWLERBODY_WEAPON_OFFSET_Y);
        everythingSprite.selectFrame(swordFrame);
        everythingSprite.setMirrored(mirrored);
        
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
            float swordDistance = handDistance * SWORD_RANGE_RATIO;
            float weaponX = handX(unitX, unitAngle, swordDistance);
            float weaponY = handY(unitY, unitAngle, swordDistance);
            
            int hitUnitIdentifier = system.findClosestLivingUnit(weaponX, weaponY, Teams.oppositeTeam(unitTeam),
                                                                 SWORD_RADIUS + HandlersTools.UNIT_RADIUS);
            
            if (hitUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
            {
                system.unitsHandlers[hitUnitIdentifier].onHit(system, hitUnitIdentifier,
                                                              SWORD_POWER * Math.cos(unitAngle), SWORD_POWER * Math.sin(unitAngle),
                                                              SWORD_POWER);
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
    public static float handDistanceForAttackTimer(int unitTimer)
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
    
    public static int swordFrameForAttackTimer(int unitTimer)
    {
        if (unitTimer < ATTACK_TIMER_MAX)
            return MathTools.lerpi(unitTimer,
                                   ATTACK_TIMER_INIT, VideoConstants.SWORD_VERTICAL_FRAME,
                                   ATTACK_TIMER_MAX, VideoConstants.SWORD_HORIZONTAL_FRAME);
        else if (unitTimer < ATTACK_TIMER_RETREATED)
            return MathTools.lerpi(unitTimer,
                                   ATTACK_TIMER_MAX, VideoConstants.SWORD_HORIZONTAL_FRAME,
                                   ATTACK_TIMER_RETREATED, VideoConstants.SWORD_VERTICAL_FRAME);
        return VideoConstants.SWORD_VERTICAL_FRAME;
    }
    
    public static int swordFrameForDeathTimer(int unitTimer)
    {
        if ((unitTimer > 0) && (unitTimer <= BaseBrawlerHandler.DEATH_TICKS))
            return MathTools.lerpi(unitTimer,
                                   0, VideoConstants.SWORD_HORIZONTAL_FRAME,
                                   BaseBrawlerHandler.DEATH_TICKS, VideoConstants.SWORD_VERTICAL_FRAME);
        return VideoConstants.SWORD_FADED_FRAME;
    }
    
    public static float swordYOffsetForDeathTimer(int unitTimer)
    {
        if ((unitTimer > 0) && (unitTimer <= BaseBrawlerHandler.DEATH_TICKS))
            return MathTools.lerp(unitTimer,
                                  0, VideoConstants.BRAWLERBODY_WEAPON_OFFSET_Y,
                                  BaseBrawlerHandler.DEATH_TICKS, 0);
        return VideoConstants.BRAWLERBODY_WEAPON_OFFSET_Y;
    }
}