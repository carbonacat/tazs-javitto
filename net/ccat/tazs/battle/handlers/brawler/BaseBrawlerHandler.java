package net.ccat.tazs.battle.handlers.brawler;

import femto.mode.HiRes16Color;
import femto.Sprite;

import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


/**
 * Base Handler for all Handlers related to the Brawler.
 */
public class BaseBrawlerHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 100;
    public static final float WALK_SPEED = 0.250f;
    public static final float ANGLE_ROTATION_BY_TICK = 24.f / 256.f;
    public static final float HAND_IDLE_DISTANCE = 2.0625f;
    public static final float HAND_MAX_DISTANCE = 6.f;
    public static final float HAND_RADIUS = 1.f;
    public static final float HAND_POWER = 5.f;
    public static final int ATTACK_TIMER_INIT = 0;
    public static final int ATTACK_TIMER_MAX = 4;
    public static final int ATTACK_TIMER_RETREATED = 8;
    public static final int ATTACK_TIMER_RESTED = 16;
    public static final float ATTACK_ANGLE_MAX = Math.PI * 0.25f;
    
    public static final float CLOSE_DISTANCE = HAND_MAX_DISTANCE + HAND_RADIUS + HandlersTools.UNIT_RADIUS - 2;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    
    public static final int COST = 10;
    public static final float INVERSE_WEIGHT = 2.00;
    public static final int DEATH_TICKS = 30;
    public static final int RECONSIDER_TICKS = 128;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.BRAWLER;
    }
    
    public String name()
    {
        return Texts.UNIT_BRAWLER;
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
            system.unitsHandlers[unitIdentifier] = BrawlerControlledHandler.instance;
            return true;
        }
        return false;
    }
    
    public void onHit(UnitsSystem system, int unitIdentifier,
                      float powerX, float powerY, float power)
    {
        if (HandlersTools.hitAndCheckIfBecameDead(system, unitIdentifier, powerX, powerY, power))
            system.unitsHandlers[unitIdentifier] = BrawlerDeadHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         HiRes16Color screen)
    {
        drawStandingBrawler(unitX, unitY, unitAngle, HAND_IDLE_DISTANCE,
                            system.everythingSprite, baseFrameForTeam(unitTeam),
                            screen);
    }
    
    public void drawControlUI(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
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
            return VideoConstants.EVERYTHING_BRAWLERBODY_A_FRAME;
        if (unitTeam == Teams.ENEMY)
            return VideoConstants.EVERYTHING_BRAWLERBODY_B_FRAME;
        // Shouldn't happen!
        while (true);
        return VideoConstants.EVERYTHING_BRAWLERBODY_A_FRAME;
    }
    
    /**
     * Renders an Idle Brawler using its information inside system.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawIdleBrawlerUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        
        drawStandingBrawler(unitX, unitY, unitAngle, HAND_IDLE_DISTANCE,
                            system.everythingSprite, baseFrameForTeam(unitTeam),
                            screen);
    }
    
    /**
     * Renders a Dying Brawler using its information inside system.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawDyingBrawlerUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];

        drawDyingBrawlerBody(unitX, unitY, unitAngle,
                             unitTimer,
                             system.everythingSprite, baseFrameForTeam(unitTeam),
                             screen);
    }
    
    /**
     * Renders an Attacking Brawler using its information inside system.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawAttackingBrawlerUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        float handDistance = handDistanceForAttackTimer(unitTimer);
        
        drawStandingBrawler(unitX, unitY, unitAngle, handDistance,
                            system.everythingSprite, baseFrameForTeam(unitTeam),
                            screen);
    }
    
    /**
     * Renders a Brawler with its weapon at a given distance.
     * 
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param handDistance
     * @param everythingSprite
     * @param baseFrame
     * @param screen
     */
    public static void drawStandingBrawler(float unitX, float unitY, float unitAngle,
                                           float handDistance,
                                           NonAnimatedSprite everythingSprite, int baseFrame,
                                           HiRes16Color screen)
    {
        // Is the hand above?
        if (unitAngle < 0)
            drawHand(unitX, unitY, unitAngle, handDistance, everythingSprite, screen);
        drawStandingBrawlerBody(unitX, unitY, unitAngle, everythingSprite, baseFrame, screen);
        // Is the hand below?
        if (unitAngle >= 0)
            drawHand(unitX, unitY, unitAngle, handDistance, everythingSprite, screen);
    }
    
    /**
     * Renders the Brawler Weapon.
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
                                HiRes16Color screen)
    {
        everythingSprite.setMirrored(false);
        everythingSprite.selectFrame(VideoConstants.EVERYTHING_HAND_FRAME);
        everythingSprite.setPosition(handX(unitX, unitAngle, handDistance) - VideoConstants.EVERYTHING_ORIGIN_X,
                                     handY(unitY, unitAngle, handDistance) - VideoConstants.EVERYTHING_ORIGIN_Y - VideoConstants.BRAWLERBODY_HAND_OFFSET_Y);
        everythingSprite.draw(screen);
    }
    
    /**
     * Renders an Idle Brawler Body.
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param everythingSprite
     * @param baseFrame
     * @param screen
     */
    public static void drawStandingBrawlerBody(float unitX, float unitY, float unitAngle,
                                               NonAnimatedSprite everythingSprite, int baseFrame,
                                               HiRes16Color screen)
    {
        everythingSprite.selectFrame(baseFrame + VideoConstants.BRAWLERBODY_IDLE_FRAME);
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
    public static void drawDyingBrawlerBody(float unitX, float unitY, float unitAngle,
                                            int unitTimer,
                                            NonAnimatedSprite everythingSprite, int baseFrame,
                                            HiRes16Color screen)
    {
        int rawFrame = MathTools.lerpi(unitTimer, 0, VideoConstants.BRAWLERBODY_DEAD_FRAMES_LAST, DEATH_TICKS, VideoConstants.BRAWLERBODY_DEAD_FRAMES_START);
        int frame = baseFrame + MathTools.clampi(rawFrame, VideoConstants.BRAWLERBODY_DEAD_FRAMES_START, VideoConstants.BRAWLERBODY_DEAD_FRAMES_LAST);
        
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
}