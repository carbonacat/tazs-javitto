package net.ccat.tazs.battle.handlers.archer;

import femto.mode.HiRes16Color;
import femto.Sprite;

import net.ccat.tazs.battle.handlers.slapper.BaseSlapperHandler;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


/**
 * Base Handler for all Handlers related to the Archer.
 */
public class BaseArcherHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 75;
    public static final float WALK_SPEED = 0.200f;
    public static final float ANGLE_ROTATION_BY_TICK = 24.f / 256.f;
    public static final float HAND_DISTANCE = 2.f;
    
    public static final float ATTACK_RANGE_MIN = 0.f;
    public static final float ATTACK_RANGE_MAX = 100.f;
    
    public static final int ATTACK_TIMER_START = 1;
    public static final int ATTACK_TIMER_PREPARED = 8;
    public static final int ATTACK_TIMER_CHARGING_MIN = ATTACK_TIMER_PREPARED; // If released here, the arrow will travel 0 pixels.
    public static final int ATTACK_TIMER_CHARGING_MAX = 40; // If released here, the arrow will travel 50 pixels.
    public static final int ATTACK_TIMER_FIRING_START = 41;
    public static final int ATTACK_TIMER_FIRING_END = 73;
    public static final int ATTACK_TIMER_RECOVERED_HALF = 89;
    public static final int ATTACK_TIMER_RECOVERED = 105;
    public static final float ATTACK_ANGLE_MAX = Math.PI * 0.125f;
    
    public static final float CLOSE_DISTANCE = HAND_DISTANCE + ATTACK_RANGE_MAX + HandlersTools.UNIT_RADIUS - 2;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    
    public static final int COST = 10;
    public static final float INVERSE_WEIGHT = 1.5;
    public static final int RECONSIDER_TICKS = 128;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.ARCHER;
    }
    
    public String name()
    {
        return Texts.UNIT_ARCHER;
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
            system.unitsHandlers[unitIdentifier] = ArcherControlledHandler.instance;
            return true;
        }
        return false;
    }
    
    public void onHit(UnitsSystem system, int unitIdentifier,
                      float powerX, float powerY, float power)
    {
        if (HandlersTools.hitAndCheckIfBecameDead(system, unitIdentifier, powerX, powerY, power))
            system.unitsHandlers[unitIdentifier] = ArcherDeathHandler.instance;
    }
    
    
    /***** RENDERING *****/
    
    public void drawAsUI(UnitsSystem system,
                         float unitX, float unitY, float unitAngle, int unitTeam,
                         HiRes16Color screen)
    {
        drawStandingArcher(unitX, unitY, unitAngle,
                           system.everythingSprite, BaseSlapperHandler.baseFrameForTeam(unitTeam),
                           VideoConstants.EVERYTHING_BOW_FRAME + VideoConstants.BOW_IDLE_FRAME,
                           VideoConstants.SLAPPERBODY_WEAPON_OFFSET_Y,
                           screen);
    }
    
    
    /***** RENDERING TOOLS *****/
    
    /**
     * Renders an Idle Archer.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawIdleArcherUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        
        drawStandingArcher(unitX, unitY, unitAngle,
                           system.everythingSprite, BaseSlapperHandler.baseFrameForTeam(unitTeam),
                           VideoConstants.EVERYTHING_BOW_FRAME + VideoConstants.BOW_IDLE_FRAME,
                           VideoConstants.SLAPPERBODY_WEAPON_OFFSET_Y,
                           screen);
    }
    
    /**
     * Renders a Dying Archer.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawDyingArcherUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        // Is the hand above?
        if (unitAngle < 0)
            drawBow(unitX, unitY - bowYOriginForDeathTimer(unitTimer), unitAngle,
                    system.everythingSprite, VideoConstants.EVERYTHING_BOW_FRAME + bowFrameForDeathTimer(unitTimer),
                    screen);
        BaseSlapperHandler.drawDyingSlapperBody(unitX, unitY, unitAngle,
                                                unitTimer,
                                                system.everythingSprite, BaseSlapperHandler.baseFrameForTeam(unitTeam),
                                                screen);
        // Is the hand below?
        if (unitAngle >= 0)
            drawBow(unitX, unitY - bowYOriginForDeathTimer(unitTimer), unitAngle,
                    system.everythingSprite, VideoConstants.EVERYTHING_BOW_FRAME + bowFrameForDeathTimer(unitTimer),
                    screen);
    }
    
    public static void drawAttackingArcherUnit(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];

        drawStandingArcher(unitX, unitY, unitAngle,
                           system.everythingSprite, BaseSlapperHandler.baseFrameForTeam(unitTeam),
                           VideoConstants.EVERYTHING_BOW_FRAME + bowFrameForAttackTimer(unitTimer),
                           bowYOriginForAttackTimer(unitTimer),
                           screen);
    }
    
    /**
     * Renders a Archer with its weapon at a given distance.
     * 
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param everythingSprite
     * @param baseFrame
     * @param bowFrame
     * @param bowYOrigin
     * @param screen
     */
    public static void drawStandingArcher(float unitX, float unitY, float unitAngle,
                                           NonAnimatedSprite everythingSprite, int baseFrame,
                                           int bowFrame, float bowYOrigin,
                                           HiRes16Color screen)
    {
        // Is the hand above?
        if (unitAngle < 0)
            drawBow(unitX, unitY - bowYOrigin, unitAngle,
                    everythingSprite, bowFrame,
                    screen);
        BaseSlapperHandler.drawStandingSlapperBody(unitX, unitY, unitAngle,
                                                   everythingSprite, baseFrame,
                                                   screen);
        // Is the hand below?
        if (unitAngle >= 0)
            drawBow(unitX, unitY - bowYOrigin, unitAngle,
                    everythingSprite, bowFrame,
                    screen);
    }
    
    /**
     * Renders the Archer's Weapon.
     * @param unitX
     * @param unitY
     * @param unitAngle
     * @param everythingSprite
     * @param bowFrame
     * @param screen
     */
    public static void drawBow(float unitX, float unitY, float unitAngle,
                                 NonAnimatedSprite everythingSprite, int bowFrame,
                                 HiRes16Color screen)
    {
        boolean mirrored = unitAngle < -MathTools.PI_1_2 || unitAngle > MathTools.PI_1_2;
        
        everythingSprite.setPosition(handX(unitX, unitAngle, HAND_DISTANCE) - VideoConstants.EVERYTHING_ORIGIN_X,
                                     handY(unitY, unitAngle, HAND_DISTANCE) - VideoConstants.EVERYTHING_ORIGIN_Y);
        everythingSprite.selectFrame(bowFrame);
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
        system.unitsTimers[unitIdentifier] = ATTACK_TIMER_START;
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
        
        /*if (unitTimer < ATTACK_TIMER_MAX)
        {
            float unitX = system.unitsXs[unitIdentifier];
            float unitY = system.unitsYs[unitIdentifier];
            float unitAngle = system.unitsAngles[unitIdentifier];
            char unitTeam = system.unitsTeams[unitIdentifier];
            float bowDistance = HAND_DISTANCE * BOW_RANGE_RATIO;
            float weaponX = handX(unitX, unitAngle, bowDistance);
            float weaponY = handY(unitY, unitAngle, bowDistance);
            
            int hitUnitIdentifier = system.findClosestLivingUnit(weaponX, weaponY, Teams.oppositeTeam(unitTeam),
                                                                 BOW_RADIUS + HandlersTools.UNIT_RADIUS);
            
            if (hitUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
            {
                system.unitsHandlers[hitUnitIdentifier].onHit(system, hitUnitIdentifier,
                                                              BOW_POWER * Math.cos(unitAngle), BOW_POWER * Math.sin(unitAngle),
                                                              BOW_POWER);
                // Interpolating to find the equivalent withdrawal position.
                unitTimer = MathTools.lerpi(unitTimer, ATTACK_TIMER_INIT, ATTACK_TIMER_RETREATED, ATTACK_TIMER_MAX, ATTACK_TIMER_MAX);
            }
        }*/
        if (unitTimer == ATTACK_TIMER_RECOVERED)
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
    
    public static int bowFrameForAttackTimer(int unitTimer)
    {
        if (unitTimer <= ATTACK_TIMER_PREPARED)
            return MathTools.lerpi(unitTimer,
                                   ATTACK_TIMER_START, VideoConstants.BOW_IDLE_FRAME,
                                   ATTACK_TIMER_PREPARED, VideoConstants.BOW_LOAD_FRAMES_START);
        if (unitTimer <= ATTACK_TIMER_CHARGING_MAX)
            return MathTools.lerpi(unitTimer,
                                   ATTACK_TIMER_CHARGING_MIN, VideoConstants.BOW_LOAD_FRAMES_START,
                                   ATTACK_TIMER_CHARGING_MAX, VideoConstants.BOW_LOAD_FRAMES_LAST);
        if (unitTimer <= ATTACK_TIMER_FIRING_END)
            return MathTools.lerpi(unitTimer,
                                   ATTACK_TIMER_FIRING_START, VideoConstants.BOW_FIRE_FRAMES_START,
                                   ATTACK_TIMER_FIRING_END, VideoConstants.BOW_FIRE_FRAMES_LAST);
        if (unitTimer <= ATTACK_TIMER_RECOVERED_HALF)
            return MathTools.lerpi(unitTimer,
                                   ATTACK_TIMER_FIRING_END, VideoConstants.BOW_FIRE_FRAMES_LAST,
                                   ATTACK_TIMER_RECOVERED_HALF, VideoConstants.BOW_IDLE_FRAME);
        return VideoConstants.BOW_IDLE_FRAME;
    }
    
    public static float bowYOriginForAttackTimer(int unitTimer)
    {
        if (unitTimer <= ATTACK_TIMER_PREPARED)
            return MathTools.lerp(unitTimer,
                                  ATTACK_TIMER_START, VideoConstants.SLAPPERBODY_WEAPON_OFFSET_Y,
                                  ATTACK_TIMER_PREPARED, VideoConstants.SLAPPERBODY_AIM_OFFSET_Y);
        if (unitTimer <= ATTACK_TIMER_FIRING_END)
            return VideoConstants.SLAPPERBODY_AIM_OFFSET_Y;
        if (unitTimer <= ATTACK_TIMER_RECOVERED_HALF)
            return MathTools.lerp(unitTimer,
                                  ATTACK_TIMER_FIRING_END, VideoConstants.SLAPPERBODY_AIM_OFFSET_Y,
                                  ATTACK_TIMER_RECOVERED_HALF, VideoConstants.SLAPPERBODY_WEAPON_OFFSET_Y);
        return VideoConstants.SLAPPERBODY_WEAPON_OFFSET_Y;
    }
    
    public static int bowFrameForDeathTimer(int unitTimer)
    {
        if (unitTimer >= BaseSlapperHandler.DEATH_TICKS / 2)
            return VideoConstants.BOW_IDLE_FRAME;
        if (unitTimer > 0)
            return VideoConstants.BOW_FIRE_FRAMES_LAST;
        return VideoConstants.BOW_FADED_FRAME;
    }
    
    public static float bowYOriginForDeathTimer(int unitTimer)
    {
        if ((unitTimer >= 0) && (unitTimer <= BaseSlapperHandler.DEATH_TICKS))
            return MathTools.lerp(unitTimer,
                                  0, 0,
                                  BaseSlapperHandler.DEATH_TICKS, VideoConstants.SLAPPERBODY_WEAPON_OFFSET_Y);
        return 0;
    }
}