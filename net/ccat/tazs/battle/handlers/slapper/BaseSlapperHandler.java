package net.ccat.tazs.battle.handlers.slapper;

import femto.mode.HiRes16Color;
import femto.Sprite;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.sprites.HandSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


/**
 * Base Handler for all Handlers related to the Slapper.
 */
public class BaseSlapperHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 100;
    public static final float WALK_SPEED = 0.125f;
    public static final float ANGLE_ROTATION_BY_TICK = 8.f / 256.f;
    public static final float HAND_IDLE_DISTANCE = 2.f;
    public static final float HAND_MAX_DISTANCE = 6.f;
    public static final float HAND_RADIUS = 1.f;
    public static final float HAND_POWER = 5.f;
    public static final int ATTACK_TIMER_INIT = 0;
    public static final int ATTACK_TIMER_MAX = 8;
    public static final int ATTACK_TIMER_RETREATED = 16;
    public static final int ATTACK_TIMER_RESTED = 32;
    
    public static final float CLOSE_DISTANCE = HAND_MAX_DISTANCE + HAND_RADIUS + HandlersTools.UNIT_RADIUS - 1;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    
    public static final int COST = 25;
    public static final int DEATH_TICKS = 64;
    public static final int RECONSIDER_TICKS = 128;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.SLAPPER;
    }
    
    public String name()
    {
        return Texts.UNIT_SLAPPER;
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
            int hitUnitIdentifier = system.findClosestUnit(weaponX, weaponY, 1 - unitTeam, HAND_RADIUS + HandlersTools.UNIT_RADIUS, false);
            
            if (hitUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
            {
                system.unitsHandlers[hitUnitIdentifier].onHit(system, hitUnitIdentifier,
                                                              HAND_POWER * Math.sin(unitAngle), HAND_POWER * -Math.cos(unitAngle),
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
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         HiRes16Color screen)
    {
        drawSlapper(unitX, unitY, unitAngle, HAND_IDLE_DISTANCE,
                    system.slapperBodySpriteByTeam[unitTeam], system.handSprite,
                    screen);
    }
    
    protected void drawUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        
        drawSlapper(unitX, unitY, unitAngle, HAND_IDLE_DISTANCE, system.slapperBodySpriteByTeam[unitTeam], system.handSprite, screen);
    }
    
    protected void drawDeadUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        int rawFrame = MathTools.lerpi(unitTimer, 0, VideoConstants.SLAPPERBODY_FRAME_DEAD_LAST, DEATH_TICKS, VideoConstants.SLAPPERBODY_FRAME_DEAD_START);
        int frame = MathTools.clampi(rawFrame, VideoConstants.SLAPPERBODY_FRAME_DEAD_START, VideoConstants.SLAPPERBODY_FRAME_DEAD_LAST);
        NonAnimatedSprite bodySprite = system.slapperBodySpriteByTeam[unitTeam];
        
        bodySprite.selectFrame(frame);
        bodySprite.setPosition(unitX - VideoConstants.SLAPPERBODY_ORIGIN_X, unitY - VideoConstants.SLAPPERBODY_ORIGIN_Y);
        bodySprite.setMirrored(unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2);
        bodySprite.draw(screen);
    }
    
    protected void drawAttackingUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        float handDistance = handDistanceForAttackTimer(unitTimer);
        
        drawSlapper(unitX, unitY, unitAngle, handDistance, system.slapperBodySpriteByTeam[unitTeam], system.handSprite, screen);
    }
    
    
    protected void drawSlapper(float unitX, float unitY, float unitAngle, float handDistance,
                               NonAnimatedSprite bodySprite, HandSprite handSprite,
                               HiRes16Color screen)
    {
        handSprite.setPosition(handX(unitX, unitAngle, handDistance) - VideoConstants.HAND_ORIGIN_X,
                               handY(unitY, unitAngle, handDistance) - VideoConstants.HAND_ORIGIN_Y - VideoConstants.SLAPPERBODY_WEAPON_ORIGIN_Y);
        // Is the hand above?
        if (unitAngle < 0)
            handSprite.draw(screen);
        bodySprite.selectFrame(VideoConstants.SLAPPERBODY_FRAME_IDLE);
        bodySprite.setPosition(unitX - VideoConstants.SLAPPERBODY_ORIGIN_X, unitY - VideoConstants.SLAPPERBODY_ORIGIN_Y);
        bodySprite.setMirrored(unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2);
        bodySprite.draw(screen);

        // Is the hand below?
        if (unitAngle >= 0)
            handSprite.draw(screen);
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
}