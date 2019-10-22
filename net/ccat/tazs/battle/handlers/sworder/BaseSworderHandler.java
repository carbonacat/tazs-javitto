package net.ccat.tazs.battle.handlers.sworder;

import femto.mode.HiRes16Color;
import femto.Sprite;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.sprites.sword.SwordSprite;
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
    public static final float WALK_SPEED = 0.100f;
    public static final float ANGLE_ROTATION_BY_TICK = 8.f / 256.f;
    public static final float HAND_IDLE_DISTANCE = 2.f;
    public static final float HAND_MAX_DISTANCE = 6.f;
    public static final float SWORD_RADIUS = 2.f;
    public static final float SWORD_POWER = 10.f;
    public static final float SWORD_RANGE_RATIO = 1.5f;
    public static final int ATTACK_TIMER_INIT = 0;
    public static final int ATTACK_TIMER_MAX = 16;
    public static final int ATTACK_TIMER_RETREATED = 32;
    public static final int ATTACK_TIMER_RESTED = 64;
    
    public static final float CLOSE_DISTANCE = HAND_MAX_DISTANCE * SWORD_RANGE_RATIO + SWORD_RADIUS + HandlersTools.UNIT_RADIUS - 2;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    
    public static final int COST = 50;
    public static final float INVERSE_WEIGHT = 1.25;
    public static final int DEATH_TICKS = 64;
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
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         HiRes16Color screen)
    {
        drawSworder(unitX, unitY, unitAngle,
                    HAND_IDLE_DISTANCE, 0,
                    system.brawlerBodySpriteByTeam[unitTeam], system.swordSprite,
                    screen);
    }
    
    protected void drawUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        
        drawSworder(unitX, unitY, unitAngle,
                    HAND_IDLE_DISTANCE, 0,
                    system.brawlerBodySpriteByTeam[unitTeam], system.swordSprite,
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
        SwordSprite swordSprite = system.swordSprite;
        int swordFrame = swordFrameForDeathTimer(unitTimer);
        boolean mirrored = unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2;
        
        swordSprite.setPosition(handX(unitX, unitAngle, HAND_IDLE_DISTANCE) - VideoConstants.SWORD_ORIGIN_X,
                                handY(unitY, unitAngle, HAND_IDLE_DISTANCE) - VideoConstants.SWORD_ORIGIN_Y - swordYForDeathTimer(unitTimer));
        swordSprite.selectFrame(swordFrame);
        swordSprite.setMirrored(mirrored);
        
        // Is the hand above?
        if (unitAngle < 0)
            swordSprite.draw(screen);

        bodySprite.selectFrame(frame);
        bodySprite.setPosition(unitX - VideoConstants.BRAWLERBODY_ORIGIN_X, unitY - VideoConstants.BRAWLERBODY_ORIGIN_Y);
        bodySprite.setMirrored(mirrored);
        bodySprite.draw(screen);
        
        // Is the hand below?
        if (unitAngle >= 0)
            swordSprite.draw(screen);
    }
    
    protected void drawAttackingUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        float handDistance = handDistanceForAttackTimer(unitTimer);
        
        drawSworder(unitX, unitY, unitAngle,
                    handDistance, swordFrameForAttackTimer(unitTimer),
                    system.brawlerBodySpriteByTeam[unitTeam], system.swordSprite,
                    screen);
    }
    
    
    protected void drawSworder(float unitX, float unitY, float unitAngle,
                               float handDistance, int swordFrame,
                               NonAnimatedSprite bodySprite, SwordSprite swordSprite,
                               HiRes16Color screen)
    {
        boolean mirrored = unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2;
        
        swordSprite.setPosition(handX(unitX, unitAngle, handDistance) - VideoConstants.SWORD_ORIGIN_X,
                                handY(unitY, unitAngle, handDistance) - VideoConstants.SWORD_ORIGIN_Y - VideoConstants.BRAWLERBODY_WEAPON_ORIGIN_Y);
        swordSprite.selectFrame(swordFrame);
        swordSprite.setMirrored(mirrored);
        
        // Is the hand above?
        if (unitAngle < 0)
            swordSprite.draw(screen);
            
        bodySprite.selectFrame(VideoConstants.BRAWLERBODY_FRAME_IDLE);
        bodySprite.setPosition(unitX - VideoConstants.BRAWLERBODY_ORIGIN_X, unitY - VideoConstants.BRAWLERBODY_ORIGIN_Y);
        bodySprite.setMirrored(mirrored);
        bodySprite.draw(screen);

        // Is the hand below?
        if (unitAngle >= 0)
            swordSprite.draw(screen);
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
    
    private static int swordFrameForAttackTimer(int unitTimer)
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
    
    private static int swordFrameForDeathTimer(int unitTimer)
    {
        if ((unitTimer > 0) && (unitTimer <= DEATH_TICKS))
            return MathTools.lerpi(unitTimer,
                                   0, VideoConstants.SWORD_FRAME_HORIZONTAL,
                                   DEATH_TICKS, VideoConstants.SWORD_FRAME_VERTICAL);
        return VideoConstants.SWORD_FRAME_HORIZONTAL;
    }
    
    private static float swordYForDeathTimer(int unitTimer)
    {
        if ((unitTimer > 0) && (unitTimer <= DEATH_TICKS))
            return MathTools.lerp(unitTimer,
                                  0, 0,
                                  DEATH_TICKS, VideoConstants.BRAWLERBODY_WEAPON_ORIGIN_Y);
        return 0;
    }
}