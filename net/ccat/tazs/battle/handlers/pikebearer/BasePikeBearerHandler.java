package net.ccat.tazs.battle.handlers.pikebearer;

import femto.mode.HiRes16Color;
import femto.Sprite;

import net.ccat.tazs.battle.handlers.brawler.BaseBrawlerHandler;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


/**
 * Base Handler for all Handlers related to the PikeBearer.
 */
public class BasePikeBearerHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 125;
    public static final float WALK_SPEED = 0.200f;
    public static final float ANGLE_ROTATION_BY_TICK = 24.f / 256.f;
    public static final float HAND_IDLE_DISTANCE = -3.f;
    public static final float HAND_MAX_DISTANCE = 5.f;
    public static final float PIKE_RADIUS = 2.f;
    public static final float PIKE_END_RANGE = 10.f;
    public static final float PIKE_END_POWER = 7.5f;
    public static final float PIKE_MIDDLE_RANGE = 6.f;
    public static final float PIKE_MIDDLE_POWER = 5.f;
    public static final int ATTACK_TIMER_INIT = 0;
    public static final int ATTACK_TIMER_MAX = 8;
    public static final int ATTACK_TIMER_RETREATED = 16;
    public static final int ATTACK_TIMER_RESTED = 32;
    public static final float ATTACK_ANGLE_MAX = Math.PI * 0.0625f;
    
    public static final float CLOSE_DISTANCE = HAND_MAX_DISTANCE + PIKE_END_RANGE + PIKE_RADIUS + HandlersTools.UNIT_RADIUS - 2;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    
    public static final int COST = 20;
    public static final float INVERSE_WEIGHT = 1.5;
    public static final int RECONSIDER_TICKS = 128;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.PIKEBEARER;
    }
    
    public String name()
    {
        return Texts.UNIT_PIKEBEARER;
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
            system.unitsHandlers[unitIdentifier] = PikeBearerControlledHandler.instance;
            return true;
        }
        return false;
    }
    
    public void onHit(UnitsSystem system, int unitIdentifier,
                      float powerX, float powerY, float power)
    {
        if (HandlersTools.hitAndCheckIfBecameDead(system, unitIdentifier, powerX, powerY, power))
            system.unitsHandlers[unitIdentifier] = PikeBearerDeathHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         HiRes16Color screen)
    {
        drawStandingPikeBearer(unitX, unitY, unitAngle,
                            HAND_IDLE_DISTANCE,
                            system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                            screen);
    }
    
    public void drawControlUI(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        HandlersTools.drawControlCircle(system, unitIdentifier, screen);
    }
    
    
    /***** RENDERING TOOLS *****/
    
    /**
     * Renders an Idle PikeBearer.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawIdlePikeBearerUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        
        drawStandingPikeBearer(unitX, unitY, unitAngle,
                               HAND_IDLE_DISTANCE,
                               system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                               screen);
    }
    
    /**
     * Renders a Dying PikeBearer.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawDyingPikeBearerUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        // Is the hand above?
        if (unitAngle < 0)
            drawPike(unitX, unitY + pikeYOffsetForDeathTimer(unitTimer), unitAngle,
                      HAND_IDLE_DISTANCE,
                      system.everythingSprite,
                      screen);
        BaseBrawlerHandler.drawDyingBrawlerBody(unitX, unitY, unitAngle,
                                                unitTimer,
                                                system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                                                screen);
        // Is the hand below?
        if (unitAngle >= 0)
            drawPike(unitX, unitY + pikeYOffsetForDeathTimer(unitTimer), unitAngle,
                      HAND_IDLE_DISTANCE,
                      system.everythingSprite,
                      screen);
    }
    
    public static void drawAttackingPikeBearerUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        float handDistance = handDistanceForAttackTimer(unitTimer);
        
        drawStandingPikeBearer(unitX, unitY, unitAngle,
                               handDistance,
                               system.everythingSprite, BaseBrawlerHandler.baseFrameForTeam(unitTeam),
                               screen);
    }
    
    /**
     * Renders a PikeBearer with its weapon at a given distance.
     * 
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param handDistance
     * @param everythingSprite
     * @param baseFrame
     * @param pikeFrame
     * @param screen
     */
    public static void drawStandingPikeBearer(float unitX, float unitY, float unitAngle,
                                              float handDistance,
                                              NonAnimatedSprite everythingSprite, int baseFrame,
                                              HiRes16Color screen)
    {
        // Is the hand above?
        if (unitAngle < 0)
            drawPike(unitX, unitY, unitAngle,
                     handDistance,
                     everythingSprite,
                     screen);
        BaseBrawlerHandler.drawStandingBrawlerBody(unitX, unitY, unitAngle,
                                                   everythingSprite, baseFrame,
                                                   screen);
        // Is the hand below?
        if (unitAngle >= 0)
            drawPike(unitX, unitY, unitAngle,
                     handDistance,
                     everythingSprite,
                     screen);
    }
    
    /**
     * Renders the PikeBearer's Weapon.
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param handDistance
     * @param everythingSprite
     * @param pikeFrame
     * @param screen
     */
    public static void drawPike(float unitX, float unitY, float unitAngle,
                                float handDistance,
                                NonAnimatedSprite everythingSprite,
                                HiRes16Color screen)
    {
        prepareSpriteForPikeWithAngle(everythingSprite, unitAngle);

        everythingSprite.setPosition(handX(unitX, unitAngle, handDistance) - VideoConstants.EVERYTHING_ORIGIN_X,
                                     handY(unitY, unitAngle, handDistance) - VideoConstants.EVERYTHING_ORIGIN_Y - VideoConstants.BRAWLERBODY_WEAPON_OFFSET_Y);

        everythingSprite.draw(screen);
        
        // Everyone expects flipper to be reset.
        everythingSprite.setFlipped(false);
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
            float pikeDistance = handDistance + PIKE_END_RANGE;
            float weaponX = handX(unitX, unitAngle, pikeDistance);
            float weaponY = handY(unitY, unitAngle, pikeDistance);
            
            int hitUnitIdentifier = system.findClosestLivingUnit(weaponX, weaponY, Teams.oppositeTeam(unitTeam),
                                                                 PIKE_RADIUS + HandlersTools.UNIT_RADIUS);
            
            if (hitUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
            {
                system.unitsHandlers[hitUnitIdentifier].onHit(system, hitUnitIdentifier,
                                                              PIKE_END_POWER * Math.cos(unitAngle), PIKE_END_POWER * Math.sin(unitAngle),
                                                              PIKE_END_POWER);
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
    
    public static float pikeYOffsetForDeathTimer(int unitTimer)
    {
        if ((unitTimer > 0) && (unitTimer <= BaseBrawlerHandler.DEATH_TICKS))
            return MathTools.lerp(unitTimer,
                                  0, VideoConstants.BRAWLERBODY_WEAPON_OFFSET_Y,
                                  BaseBrawlerHandler.DEATH_TICKS, 0);
        return VideoConstants.BRAWLERBODY_WEAPON_OFFSET_Y;
    }
    
    public static void prepareSpriteForPikeWithAngle(NonAnimatedSprite everythingSprite, float angle)
    {
        int roughFrame = MathTools.clampi((int)Math.round((8.f * angle) / Math.PI), -8, 8);
        int subFrame = 0;
        
        if (roughFrame < -4) // && (roughFrame >= -8)
        {
            everythingSprite.setMirrored(true);
            // -8 gives 0 = HORIZONTAL
            // -4 gives 4 = VERTICAL
            subFrame = 8 + roughFrame;
        }
        else if (roughFrame > 4) // && (roughFrame <= 8)
        {
            everythingSprite.setMirrored(true);
            everythingSprite.setFlipped(true);
            // 8 gives 0 = HORIZONTAL
            // 4 gives 4 = VERTICAL
            subFrame = 8 - roughFrame;
        }
        else if (roughFrame > 0) // && (roughFrame <= 4)
        {
            everythingSprite.setMirrored(false);
            everythingSprite.setFlipped(true);
            // 0 gives 0 = HORIZONTAL
            // 4 gives 4 = VERTICAL
            subFrame = roughFrame;
        }
        else // (roughFrame <= 0) && (roughFrame >= -4)
        {
            everythingSprite.setMirrored(false);
            everythingSprite.setFlipped(false);
            // -4 gives 4 = VERTICAL
            // 0 gives -4 = HORIZONTAL
            subFrame = -roughFrame;
        }
        everythingSprite.selectFrame(VideoConstants.EVERYTHING_PIKE_FRAME + subFrame);
    }
}