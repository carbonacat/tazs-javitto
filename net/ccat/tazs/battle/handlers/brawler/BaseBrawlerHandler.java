package net.ccat.tazs.battle.handlers.brawler;

import femto.mode.HiRes16Color;
import femto.Sprite;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.HandSprite;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


public class BaseBrawlerHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 100;
    public static final float WALK_SPEED = 0.125f;
    public static final float SEEK_DISTANCE_MAX = 250.f;
    public static final float CLOSE_DISTANCE = 10.f;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    public static final float ANGLE_ROTATION_BY_TICK = 8.f / 256.f;
    public static final float HAND_IDLE_DISTANCE = 2.f;
    public static final float HAND_MAX_DISTANCE = 6.f;
    public static final float HAND_RADIUS = 1.f;
    public static final float HAND_POWER = 5.f;
    public static final float UNIT_RADIUS = 4.f;
    public static final int COST = 20;
    public static final int ATTACK_TIMER_INIT = 0;
    public static final int ATTACK_TIMER_MAX = 8;
    public static final int ATTACK_TIMER_REST = 32;
    public static final int DEATH_TICKS = 64;
    
    
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
            int hitUnitIdentifier = system.findClosestUnit(weaponX, weaponY, 1 - unitTeam, HAND_RADIUS + UNIT_RADIUS, false);
            
            if (hitUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
            {
                system.unitsHandlers[hitUnitIdentifier].onHit(system, hitUnitIdentifier,
                                                              HAND_POWER * Math.cos(unitAngle), HAND_POWER * Math.sin(unitAngle),
                                                              HAND_POWER);
                // Interpolating to find the equivalent withdrawal position.
                unitTimer = MathTools.lerpi(unitTimer, 0, ATTACK_TIMER_MAX, ATTACK_TIMER_MAX, ATTACK_TIMER_REST);
            }
        }
        if (unitTimer == ATTACK_TIMER_REST)
            unitTimer = 0;
        system.unitsTimers[unitIdentifier] = unitTimer;
        return unitTimer != 0;
    }
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         HiRes16Color screen)
    {
        drawBrawler(unitX, unitY, unitAngle, HAND_IDLE_DISTANCE,
                    system.brawlerBodySpriteByTeam[unitTeam], system.handSprite,
                    screen);
    }
    
    protected void drawUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        
        drawBrawler(unitX, unitY, unitAngle, HAND_IDLE_DISTANCE, system.brawlerBodySpriteByTeam[unitTeam], system.handSprite, screen);
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
        
        bodySprite.selectFrame(frame);
        bodySprite.setPosition(unitX - VideoConstants.BRAWLERBODY_ORIGIN_X, unitY - VideoConstants.BRAWLERBODY_ORIGIN_Y);
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
        
        drawBrawler(unitX, unitY, unitAngle, handDistance, system.brawlerBodySpriteByTeam[unitTeam], system.handSprite, screen);
    }
    
    
    protected void drawBrawler(float unitX, float unitY, float unitAngle, float handDistance,
                               NonAnimatedSprite bodySprite, HandSprite handSprite,
                               HiRes16Color screen)
    {
        handSprite.setPosition(handX(unitX, unitAngle, handDistance) - VideoConstants.HAND_ORIGIN_X,
                               handY(unitY, unitAngle, handDistance) - VideoConstants.HAND_ORIGIN_Y - VideoConstants.BRAWLERBODY_WEAPON_ORIGIN_Y);
        // Is the hand above?
        if (unitAngle < 0)
            handSprite.draw(screen);
        bodySprite.selectFrame(VideoConstants.BRAWLERBODY_FRAME_IDLE);
        bodySprite.setPosition(unitX - VideoConstants.BRAWLERBODY_ORIGIN_X, unitY - VideoConstants.BRAWLERBODY_ORIGIN_Y);
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
        else if (unitTimer < ATTACK_TIMER_REST)
            return MathTools.lerp(unitTimer,
                                  ATTACK_TIMER_MAX, HAND_MAX_DISTANCE,
                                  ATTACK_TIMER_REST, HAND_IDLE_DISTANCE);
        return HAND_IDLE_DISTANCE;
    }
}