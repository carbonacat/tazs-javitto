package net.ccat.tazs.battle.handlers.shieldbearer;

import femto.Sprite;

import net.ccat.tazs.battle.handlers.brawler.BaseBrawlerHandler;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.texts.UNIT_SHIELDBEARER;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Base Handler for all Handlers related to the ShieldBearer.
 */
public class BaseShieldBearerHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 150;
    public static final float WALK_SPEED = 0.200f;
    public static final float ANGLE_ROTATION_BY_TICK = 32.f / 256.f;
    public static final float HAND_IDLE_DISTANCE = 2.f;
    public static final float HAND_MAX_DISTANCE = 4.f;
    public static final float SHIELDBEARER_RADIUS = 3.f;
    public static final float SHIELDBEARER_POWER = 4.f;
    public static final int ATTACK_TIMER_INIT = 0;
    public static final int ATTACK_TIMER_MAX = 16;
    public static final int ATTACK_TIMER_RETREATED = 32;
    public static final int ATTACK_TIMER_RESTED = 40;
    public static final float ATTACK_ANGLE_MAX = Math.PI * 0.0625f;
    
    public static final float CLOSE_DISTANCE = HAND_MAX_DISTANCE + SHIELDBEARER_RADIUS + HandlersTools.UNIT_RADIUS - 2;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    
    public static final int COST = 30;
    public static final float INVERSE_WEIGHT = 1.5;
    public static final int RECONSIDER_TICKS = 128;
    
    public static final int UI_TIMER_WRAPPER = 60;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.SHIELDBEARER;
    }
    
    public pointer name()
    {
        return UNIT_SHIELDBEARER.bin();
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
            system.unitsHandlers[unitIdentifier] = ShieldBearerControlledHandler.instance;
            return true;
        }
        return false;
    }
    
    public void onHit(UnitsSystem system, int unitIdentifier,
                      float powerX, float powerY, float power)
    {
        if (power <= 0)
            return ;
        
        float hitAngle = Math.atan2(powerY, powerX);
        float deltaAngle = MathTools.wrapAngle(system.unitsAngles[unitIdentifier] - hitAngle);
        float multiplier = impactMultiplierForRelativeAngle(deltaAngle);
        
        powerX *= multiplier;
        powerY *= multiplier;
        power *= multiplier;
        if (HandlersTools.hitAndCheckIfBecameDead(system, unitIdentifier, powerX, powerY, power))
            system.unitsHandlers[unitIdentifier] = ShieldBearerDeadHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         int unitTimer,
                         AdvancedHiRes16Color screen)
    {
        boolean facingFront = unitAngle >= 0;
        
        drawStandingShieldBearer(unitX, unitY, unitAngle,
                                 handDistanceForAttackTimer(unitTimer % UI_TIMER_WRAPPER),
                                 system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                                 shieldFrameForAttackTimer(unitTimer % UI_TIMER_WRAPPER, facingFront),
                                 screen);
    }
    
    public void drawControlUI(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        HandlersTools.drawControlCircle(system, unitIdentifier, screen);
    }
    
    
    /***** RENDERING TOOLS *****/
    
    /**
     * Renders an Idle Shield Bearer Unit.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawIdleShieldBearerUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        boolean facingFront = unitAngle >= 0;
        
        drawStandingShieldBearer(unitX, unitY, unitAngle,
                                 HAND_IDLE_DISTANCE,
                                 system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                                 shieldFrameForIdle(facingFront),
                                 screen);
    }
    
    /**
     * Renders a Dying Shield Bearer.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawDyingShieldBearerUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {       
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        boolean facingFront = unitAngle >= 0;
        
        // Is the hand above?
        if (unitAngle < 0)
            drawShield(unitX, unitY + shieldYOffsetForDeathTimer(unitTimer), unitAngle,
                       HAND_IDLE_DISTANCE,
                       system.everythingSprite, shieldFrameForDeathTimer(unitTimer, facingFront),
                       screen);
        BaseBrawlerHandler.drawDyingBrawlerBody(unitX, unitY, unitAngle,
                                                unitTimer,
                                                system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                                                screen);
        // Is the hand below?
        if (unitAngle >= 0)
            drawShield(unitX, unitY + shieldYOffsetForDeathTimer(unitTimer), unitAngle,
                       HAND_IDLE_DISTANCE,
                       system.everythingSprite, shieldFrameForDeathTimer(unitTimer, facingFront),
                       screen);
    }
    
    /**
     * Renders an Attacking Shield Bearer.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawAttackingShieldBearerUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        float handDistance = handDistanceForAttackTimer(unitTimer);
        boolean facingFront = unitAngle >= 0;
        
        drawStandingShieldBearer(unitX, unitY, unitAngle,
                                 handDistance,
                                 system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                                 shieldFrameForAttackTimer(unitTimer, facingFront),
                                 screen);
    }
    
    /**
     * Renders a Shield Bearer with its weapon at a given distance.
     * 
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param handDistance
     * @param everythingSprite
     * @param baseFrame
     * @param shieldFrame
     * @param screen
     */
    public static void drawStandingShieldBearer(float unitX, float unitY, float unitAngle,
                                                float handDistance,
                                                NonAnimatedSprite everythingSprite, int baseFrame,
                                                int shieldFrame,
                                                AdvancedHiRes16Color screen)
    {
        // Is the hand above?
        if (unitAngle < 0)
            drawShield(unitX, unitY, unitAngle,
                       handDistance,
                       everythingSprite, shieldFrame,
                       screen);
        BaseBrawlerHandler.drawStandingBrawlerBody(unitX, unitY, unitAngle,
                                                   everythingSprite, baseFrame,
                                                   screen);
        // Is the hand below?
        if (unitAngle >= 0)
            drawShield(unitX, unitY, unitAngle,
                       handDistance,
                       everythingSprite, shieldFrame,
                       screen);
    }
    
    /**
     * Renders the Shield Bearer's Shield.
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param handDistance
     * @param everythingSprite
     * @param shieldFrame
     * @param screen
     */
    public static void drawShield(float unitX, float unitY, float unitAngle,
                                  float handDistance,
                                  NonAnimatedSprite everythingSprite, int shieldFrame,
                                  AdvancedHiRes16Color screen)
    {
        boolean mirrored = unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2;
        
        everythingSprite.setPosition(handX(unitX, unitAngle, handDistance) - VideoConstants.EVERYTHING_ORIGIN_X,
                                     handY(unitY, unitAngle, handDistance) - VideoConstants.EVERYTHING_ORIGIN_Y - VideoConstants.BRAWLERBODY_SHIELD_OFFSET_Y);
        everythingSprite.selectFrame(VideoConstants.EVERYTHING_SHIELD_FRAME + shieldFrame);
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
            float weaponX = handX(unitX, unitAngle, handDistance);
            float weaponY = handY(unitY, unitAngle, handDistance);
            
            int hitUnitIdentifier = system.findClosestLivingUnit(weaponX, weaponY, Teams.oppositeTeam(unitTeam),
                                                                 SHIELDBEARER_RADIUS + HandlersTools.UNIT_RADIUS);
            
            if (hitUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
            {
                system.unitsHandlers[hitUnitIdentifier].onHit(system, hitUnitIdentifier,
                                                              SHIELDBEARER_POWER * Math.cos(unitAngle), SHIELDBEARER_POWER * Math.sin(unitAngle),
                                                              SHIELDBEARER_POWER);
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
    
    public static int shieldFrameForIdle(boolean facingFront)
    {
        return facingFront ? VideoConstants.SHIELD_FRONT_FRAME : VideoConstants.SHIELD_BACK_FRAME;
    }
    
    public static int shieldFrameForAttackTimer(int unitTimer, boolean facingFront)
    {
        final int standingFrame = facingFront ? VideoConstants.SHIELD_FRONT_FRAME : VideoConstants.SHIELD_BACK_FRAME;
        final int bashingFrame = standingFrame + 1;
        
        if (unitTimer < ATTACK_TIMER_MAX)
            return MathTools.lerpi(unitTimer,
                                   ATTACK_TIMER_INIT, standingFrame,
                                   ATTACK_TIMER_MAX, bashingFrame);
        else if (unitTimer < ATTACK_TIMER_RETREATED)
            return MathTools.lerpi(unitTimer,
                                   ATTACK_TIMER_MAX, bashingFrame,
                                   ATTACK_TIMER_RETREATED, standingFrame);
        return standingFrame;
    }
    
    public static int shieldFrameForDeathTimer(int unitTimer, boolean facingFront)
    {
        final int standingFrame = facingFront ? VideoConstants.SHIELD_FRONT_FRAME : VideoConstants.SHIELD_BACK_FRAME;
        final int fallenFrame = standingFrame + VideoConstants.SHIELD_FALLEN_FRAME_INCREMENT;
        
        if ((unitTimer > 0) && (unitTimer <= BaseBrawlerHandler.DEATH_TICKS))
            return MathTools.lerpi(unitTimer,
                                   0, fallenFrame,
                                   BaseBrawlerHandler.DEATH_TICKS, standingFrame);
        return fallenFrame;
    }
    
    public static float shieldYOffsetForDeathTimer(int unitTimer)
    {
        if ((unitTimer > 0) && (unitTimer <= BaseBrawlerHandler.DEATH_TICKS))
            return MathTools.lerp(unitTimer,
                                  0, VideoConstants.BRAWLERBODY_SHIELD_OFFSET_Y,
                                  BaseBrawlerHandler.DEATH_TICKS, 0);
        return VideoConstants.BRAWLERBODY_SHIELD_OFFSET_Y;
    }
    
    public static float impactMultiplierForRelativeAngle(float deltaAngle)
    {
        if ((deltaAngle <= -MathTools.PI_7_8) || (deltaAngle >= MathTools.PI_7_8))
            return IMPACT_FRONT_PERFECT;
        if ((deltaAngle <= -MathTools.PI_3_4) || (deltaAngle >= MathTools.PI_3_4))
            return IMPACT_FRONT;
        if ((deltaAngle <= -MathTools.PI_1_2) || (deltaAngle >= MathTools.PI_1_2))
            return IMPACT_FRONT_SIDES;
        else
            return IMPACT_BACK;
    }
    
    private static float IMPACT_FRONT_PERFECT = 0.1f;
    private static float IMPACT_FRONT = 0.25f;
    private static float IMPACT_FRONT_SIDES = 0.5f;
    private static float IMPACT_BACK = 1.f;
}