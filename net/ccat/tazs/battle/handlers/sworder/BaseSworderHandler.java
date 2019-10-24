package net.ccat.tazs.battle.handlers.sworder;

import femto.mode.HiRes16Color;
import femto.Sprite;

import net.ccat.tazs.battle.handlers.brawler.BaseBrawlerHandler;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


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
    
    public static final float CLOSE_DISTANCE = HAND_MAX_DISTANCE * SWORD_RANGE_RATIO + SWORD_RADIUS + HandlersTools.UNIT_RADIUS - 2;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    
    public static final int COST = 50;
    public static final float INVERSE_WEIGHT = 1.25;
    public static final int RECONSIDER_TICKS = 128;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.SWORDER;
    }
    
    public String name()
    {
        return Texts.UNIT_SWORDER;
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
                         HiRes16Color screen)
    {
        drawStandingSworder(unitX, unitY, unitAngle,
                            HAND_IDLE_DISTANCE,
                            system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                            system.swordSprite, VideoConstants.SWORD_FRAME_VERTICAL,
                            screen);
    }
    
    
    /***** RENDERING TOOLS *****/
    
    /**
     * Renders an Idle Sworder.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawIdleSworderUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        
        drawStandingSworder(unitX, unitY, unitAngle,
                            HAND_IDLE_DISTANCE,
                            system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                            system.swordSprite, VideoConstants.SWORD_FRAME_VERTICAL,
                            screen);
    }
    
    /**
     * Renders a Dying Sworder.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawDyingSworderUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        // Is the hand above?
        if (unitAngle < 0)
            drawSword(unitX, unitY + swordYOffsetForDeathTimer(unitTimer), unitAngle,
                      HAND_IDLE_DISTANCE,
                      system.swordSprite, swordFrameForDeathTimer(unitTimer),
                      screen);
        BaseBrawlerHandler.drawDyingBrawlerBody(unitX, unitY, unitAngle,
                                                unitTimer,
                                                system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                                                screen);
        // Is the hand below?
        if (unitAngle >= 0)
            drawSword(unitX, unitY + swordYOffsetForDeathTimer(unitTimer), unitAngle,
                      HAND_IDLE_DISTANCE,
                      system.swordSprite, swordFrameForDeathTimer(unitTimer),
                      screen);
    }
    
    public static void drawAttackingSworderUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        float handDistance = handDistanceForAttackTimer(unitTimer);
        
        drawStandingSworder(unitX, unitY, unitAngle,
                            handDistance,
                            system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                            system.swordSprite, swordFrameForAttackTimer(unitTimer),
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
     * @param swordSprite
     * @param swordFrame
     * @param screen
     */
    public static void drawStandingSworder(float unitX, float unitY, float unitAngle,
                                           float handDistance,
                                           NonAnimatedSprite everythingSprite, int baseFrame,
                                           NonAnimatedSprite swordSprite, int swordFrame,
                                           HiRes16Color screen)
    {
        // Is the hand above?
        if (unitAngle < 0)
            drawSword(unitX, unitY, unitAngle,
                      handDistance,
                      swordSprite, swordFrame,
                      screen);
        BaseBrawlerHandler.drawStandingBrawlerBody(unitX, unitY, unitAngle,
                                                   everythingSprite, baseFrame,
                                                   screen);
        // Is the hand below?
        if (unitAngle >= 0)
            drawSword(unitX, unitY, unitAngle,
                      handDistance,
                      swordSprite, swordFrame,
                      screen);
    }
    
    /**
     * Renders the Sworder's Weapon.
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param handDistance
     * @param swordSprite
     * @param swordFrame
     * @param screen
     */
    public static void drawSword(float unitX, float unitY, float unitAngle,
                                 float handDistance,
                                 NonAnimatedSprite swordSprite, int swordFrame,
                                 HiRes16Color screen)
    {
        boolean mirrored = unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2;
        
        swordSprite.setPosition(handX(unitX, unitAngle, handDistance) - VideoConstants.SWORD_ORIGIN_X,
                                handY(unitY, unitAngle, handDistance) - VideoConstants.SWORD_ORIGIN_Y - VideoConstants.BRAWLERBODY_WEAPON_ORIGIN_Y);
        swordSprite.selectFrame(swordFrame);
        swordSprite.setMirrored(mirrored);
        
        swordSprite.draw(screen);
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
            char unitTeam = system.unitsTeams[unitIdentifier];
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
                                   ATTACK_TIMER_INIT, VideoConstants.SWORD_FRAME_VERTICAL,
                                   ATTACK_TIMER_MAX, VideoConstants.SWORD_FRAME_HORIZONTAL);
        else if (unitTimer < ATTACK_TIMER_RETREATED)
            return MathTools.lerpi(unitTimer,
                                   ATTACK_TIMER_MAX, VideoConstants.SWORD_FRAME_HORIZONTAL,
                                   ATTACK_TIMER_RETREATED, VideoConstants.SWORD_FRAME_VERTICAL);
        return VideoConstants.SWORD_FRAME_VERTICAL;
    }
    
    public static int swordFrameForDeathTimer(int unitTimer)
    {
        if ((unitTimer > 0) && (unitTimer <= BaseBrawlerHandler.DEATH_TICKS))
            return MathTools.lerpi(unitTimer,
                                   0, VideoConstants.SWORD_FRAME_HORIZONTAL,
                                   BaseBrawlerHandler.DEATH_TICKS, VideoConstants.SWORD_FRAME_VERTICAL);
        return VideoConstants.SWORD_FRAME_HORIZONTAL;
    }
    
    public static float swordYOffsetForDeathTimer(int unitTimer)
    {
        if ((unitTimer > 0) && (unitTimer <= BaseBrawlerHandler.DEATH_TICKS))
            return MathTools.lerp(unitTimer,
                                  0, VideoConstants.BRAWLERBODY_WEAPON_ORIGIN_Y,
                                  BaseBrawlerHandler.DEATH_TICKS, 0);
        return VideoConstants.BRAWLERBODY_WEAPON_ORIGIN_Y;
    }
}