package net.ccat.tazs.battle.handlers.shieldbearer;

import femto.mode.HiRes16Color;
import femto.Sprite;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.sprites.shield.ShieldSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


/**
 * Base Handler for all Handlers related to the ShieldBearer.
 */
public class BaseShieldBearerHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 150;
    public static final float WALK_SPEED = 0.100f;
    public static final float ANGLE_ROTATION_BY_TICK = 8.f / 256.f;
    public static final float HAND_IDLE_DISTANCE = 2.f;
    public static final float HAND_MAX_DISTANCE = 4.f;
    public static final float SHIELDBEARER_RADIUS = 3.f;
    public static final float SHIELDBEARER_POWER = 5.f;
    public static final int ATTACK_TIMER_INIT = 0;
    public static final int ATTACK_TIMER_MAX = 16;
    public static final int ATTACK_TIMER_RETREATED = 32;
    public static final int ATTACK_TIMER_RESTED = 48;
    
    public static final float CLOSE_DISTANCE = HAND_MAX_DISTANCE + SHIELDBEARER_RADIUS + HandlersTools.UNIT_RADIUS - 1;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    
    public static final int COST = 50;
    public static final int DEATH_TICKS = 64;
    public static final int RECONSIDER_TICKS = 128;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.SHIELDBEARER;
    }
    
    public String name()
    {
        return Texts.UNIT_SHIELDBEARER;
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
    
    
    /***** PARTS POSITIONS *****/
    
    protected float handX(float unitX, float unitAngle, float handDistance)
    {
        return unitX + handDistance * Math.cos(unitAngle);
    }
    protected float handY(float unitY, float unitAngle, float handDistance)
    {
        return unitY + handDistance * Math.sin(unitAngle);
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
    
    
    /***** ATTACKING *****/
    
    /**
     * Starts the Attack.
     * 
     * @param system
     * @param unitIdentifier
     */
    protected void startAttack(UnitsSystem system, int unitIdentifier)
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
    protected boolean handleAttack(UnitsSystem system, int unitIdentifier)
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
            float weaponX = handX(unitX, unitAngle, handDistance);
            float weaponY = handY(unitY, unitAngle, handDistance);
            
            // TODO: 1-team isn't really a good way to find the other team.
            int hitUnitIdentifier = system.findClosestLivingUnit(weaponX, weaponY, 1 - unitTeam, SHIELDBEARER_RADIUS + HandlersTools.UNIT_RADIUS);
            
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
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         HiRes16Color screen)
    {
        drawShieldBearer(unitX, unitY, unitAngle,
                         HAND_IDLE_DISTANCE, 0,
                         system.brawlerBodySpriteByTeam[unitTeam], system.shieldSprite,
                         screen);
    }
    
    protected void drawUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        
        drawShieldBearer(unitX, unitY, unitAngle,
                         HAND_IDLE_DISTANCE, 0,
                         system.brawlerBodySpriteByTeam[unitTeam], system.shieldSprite,
                         screen);
    }
    
    protected void drawDeadUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        int rawFrame = MathTools.lerpi(unitTimer, 0, VideoConstants.BRAWLERBODY_FRAME_DEAD_LAST, DEATH_TICKS, VideoConstants.BRAWLERBODY_FRAME_DEAD_START);
        int frame = MathTools.clampi(rawFrame, VideoConstants.BRAWLERBODY_FRAME_DEAD_START, VideoConstants.BRAWLERBODY_FRAME_DEAD_LAST);
        NonAnimatedSprite bodySprite = system.brawlerBodySpriteByTeam[unitTeam];
        ShieldSprite shieldSprite = system.shieldSprite;
        boolean facingFront = unitAngle >= 0;
        int shieldFrame = shieldFrameForDeathTimer(unitTimer, facingFront);
        boolean mirrored = unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2;
        
        shieldSprite.setPosition(handX(unitX, unitAngle, HAND_IDLE_DISTANCE) - VideoConstants.SHIELD_ORIGIN_X,
                                 handY(unitY, unitAngle, HAND_IDLE_DISTANCE) - VideoConstants.SHIELD_ORIGIN_Y - shieldBearerYForDeathTimer(unitTimer));
        shieldSprite.selectFrame(shieldFrame);
        shieldSprite.setMirrored(mirrored);
        
        // Is the hand above?
        if (!facingFront)
            shieldSprite.draw(screen);

        bodySprite.selectFrame(frame);
        bodySprite.setPosition(unitX - VideoConstants.BRAWLERBODY_ORIGIN_X, unitY - VideoConstants.BRAWLERBODY_ORIGIN_Y);
        bodySprite.setMirrored(mirrored);
        bodySprite.draw(screen);
        
        // Is the hand below?
        if (facingFront)
            shieldSprite.draw(screen);
    }
    
    protected void drawAttackingUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        float handDistance = handDistanceForAttackTimer(unitTimer);
        boolean facingFront = unitAngle >= 0;
        int shieldFrame = shieldFrameForAttackTimer(unitTimer, facingFront);
        
        drawShieldBearer(unitX, unitY, unitAngle,
                         handDistance, shieldFrame,
                         system.brawlerBodySpriteByTeam[unitTeam], system.shieldSprite,
                         screen);
    }
    
    
    protected void drawShieldBearer(float unitX, float unitY, float unitAngle,
                                    float handDistance, int shieldBearerFrame,
                                    NonAnimatedSprite bodySprite, ShieldSprite shieldSprite,
                                    HiRes16Color screen)
    {
        boolean mirrored = unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2;
        boolean facingFront = unitAngle >= 0;
        
        shieldSprite.setPosition(handX(unitX, unitAngle, handDistance) - VideoConstants.SHIELD_ORIGIN_X,
                                 handY(unitY, unitAngle, handDistance) - VideoConstants.SHIELD_ORIGIN_Y - VideoConstants.BRAWLERBODY_SHIELD_ORIGIN_Y);
        shieldSprite.selectFrame(shieldBearerFrame);
        shieldSprite.setMirrored(mirrored);
        
        // Is the hand above?
        if (!facingFront)
            shieldSprite.draw(screen);
            
        bodySprite.selectFrame(VideoConstants.BRAWLERBODY_FRAME_IDLE);
        bodySprite.setPosition(unitX - VideoConstants.BRAWLERBODY_ORIGIN_X, unitY - VideoConstants.BRAWLERBODY_ORIGIN_Y);
        bodySprite.setMirrored(mirrored);
        bodySprite.draw(screen);

        // Is the hand below?
        if (facingFront)
            shieldSprite.draw(screen);
    }
    
    
    /***** TOOLS *****/
    
    /**
     * @param unitTimer The Unit's timer value.
     * @return The distance for the hand.
     */
    private static float handDistanceForAttackTimer(int unitTimer)
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
    
    private static int shieldFrameForAttackTimer(int unitTimer, boolean facingFront)
    {
        final int standingFrame = facingFront ? VideoConstants.SHIELD_FRAME_FRONT : VideoConstants.SHIELD_FRAME_BACK;
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
    
    private static int shieldFrameForDeathTimer(int unitTimer, boolean facingFront)
    {
        final int standingFrame = facingFront ? VideoConstants.SHIELD_FRAME_FRONT : VideoConstants.SHIELD_FRAME_BACK;
        final int fallenFrame = standingFrame + VideoConstants.SHIELD_FRAME_FALLEN_INCREMENT;
        
        if ((unitTimer > 0) && (unitTimer <= DEATH_TICKS))
            return MathTools.lerpi(unitTimer,
                                   0, fallenFrame,
                                   DEATH_TICKS, standingFrame);
        return fallenFrame;
    }
    
    private static float shieldBearerYForDeathTimer(int unitTimer)
    {
        if ((unitTimer > 0) && (unitTimer <= DEATH_TICKS))
            return MathTools.lerp(unitTimer,
                                  0, 0,
                                  DEATH_TICKS, VideoConstants.BRAWLERBODY_SHIELD_ORIGIN_Y);
        return 0;
    }
    
    private static float impactMultiplierForRelativeAngle(float deltaAngle)
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