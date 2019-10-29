package net.ccat.tazs.battle.handlers.archer;

import femto.Sprite;

import net.ccat.tazs.battle.handlers.slapper.BaseSlapperHandler;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.NonAnimatedSprite;
import net.ccat.tazs.resources.texts.UNIT_ARCHER;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;
import net.ccat.tazs.ui.UITools;


/**
 * Base Handler for all Handlers related to the Archer.
 */
public class BaseArcherHandler
    implements UnitHandler
{
    public static final short HEALTH_INITIAL = 50;
    public static final float WALK_SPEED = 0.200f;
    public static final float ANGLE_ROTATION_BY_TICK = 24.f / 256.f;
    public static final float HAND_DISTANCE = 2.f;
    
    public static final float ATTACK_RANGE_MIN = 0.f;
    public static final float ATTACK_RANGE_MAX = 100.f;
    
    public static final int ATTACK_TIMER_START = 1;
    public static final int ATTACK_TIMER_PREPARED = ATTACK_TIMER_START + 8;
    public static final int ATTACK_TIMER_CHARGING_MIN = ATTACK_TIMER_PREPARED; // If released here, the arrow will travel ATTACK_RANGE_MIN pixels.
    public static final int ATTACK_TIMER_CHARGING_MAX = ATTACK_TIMER_CHARGING_MIN + 64; // If released here, the arrow will travel ATTACK_RANGE_MAX pixels.
    public static final int ATTACK_TIMER_DECHARGING_MAX = ATTACK_TIMER_CHARGING_MAX; // If released here, the arrow will travel ATTACK_RANGE_MAX pixels.
    public static final int ATTACK_TIMER_DECHARGING_MIN = ATTACK_TIMER_DECHARGING_MAX + 64; // If released here, the arrow will travel ATTACK_RANGE_MIN pixels.
    public static final int ATTACK_TIMER_FIRING_START = ATTACK_TIMER_DECHARGING_MIN + 1;
    public static final int ATTACK_TIMER_FIRING_END = ATTACK_TIMER_FIRING_START + 32;
    public static final int ATTACK_TIMER_RECOVERED_HALF = ATTACK_TIMER_FIRING_END + 32;
    public static final int ATTACK_TIMER_RECOVERED = ATTACK_TIMER_RECOVERED_HALF + 32;
    public static final float ATTACK_ANGLE_MAX = Math.PI * 0.125f;
    public static final float ATTACK_RADIUS = 3.f;
    public static final float ATTACK_POWER = 20.f;
    
    public static final float CLOSE_DISTANCE = HAND_DISTANCE + ATTACK_RANGE_MAX + HandlersTools.UNIT_RADIUS - 2;
    public static final float CLOSE_DISTANCE_SQUARED = CLOSE_DISTANCE * CLOSE_DISTANCE;
    
    public static final int COST = 20;
    public static final float INVERSE_WEIGHT = 2.5;
    public static final int RECONSIDER_TICKS = 128;
    
    
    /***** INFORMATION *****/
    
    public int unitType()
    {
        return UnitTypes.ARCHER;
    }
    
    public pointer name()
    {
        return UNIT_ARCHER.bin();
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
                         AdvancedHiRes16Color screen)
    {
        drawStandingArcher(unitX, unitY, unitAngle,
                           system.everythingSprite, BaseSlapperHandler.baseFrameForTeam(unitTeam),
                           VideoConstants.EVERYTHING_BOW_FRAME + VideoConstants.BOW_IDLE_FRAME,
                           VideoConstants.SLAPPERBODY_WEAPON_OFFSET_Y,
                           screen);
    }
    
    public void drawControlUI(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        HandlersTools.drawControlCircle(system, unitIdentifier, screen);
    }
    
    
    /***** RENDERING TOOLS *****/
    
    /**
     * Renders an Idle Archer.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen
     */
    public static void drawIdleArcherUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
        
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
    public static void drawDyingArcherUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
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
    
    public static void drawAttackingArcherUnit(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        byte unitTeam = system.unitsTeams[unitIdentifier];
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
                                           AdvancedHiRes16Color screen)
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
                                 AdvancedHiRes16Color screen)
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
     * @param keepCharging When charging only - If false, immediately release.
     * @return False if the attack ended.
     */
    public static boolean handleAttack(UnitsSystem system, int unitIdentifier, boolean keepCharging)
    {
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        if ((unitTimer >= ATTACK_TIMER_CHARGING_MIN) && (unitTimer <= ATTACK_TIMER_DECHARGING_MIN))
        {
            if (keepCharging)
            {
                unitTimer++;
                if (unitTimer >= ATTACK_TIMER_DECHARGING_MIN)
                    unitTimer = ATTACK_TIMER_CHARGING_MIN;
            }
            else
            {
                float targetDistance = targetDistanceWhenCharging(unitTimer);
                float unitX = system.unitsXs[unitIdentifier];
                float unitY = system.unitsYs[unitIdentifier];
                float unitAngle = system.unitsAngles[unitIdentifier];
                byte unitTeam = system.unitsTeams[unitIdentifier];
                float targetX = unitX + Math.cos(unitAngle) * targetDistance;
                float targetY = unitY + Math.sin(unitAngle) * targetDistance;
                int hitUnitIdentifier = system.findClosestLivingUnit(targetX, targetY, Teams.oppositeTeam(unitTeam),
                                                                     ATTACK_RADIUS + HandlersTools.UNIT_RADIUS);
                
                // TODO: Throw an actual arrow instead of instant hit.
                if (hitUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
                    system.unitsHandlers[hitUnitIdentifier].onHit(system, hitUnitIdentifier,
                                                                  ATTACK_POWER * Math.cos(unitAngle), ATTACK_POWER * Math.sin(unitAngle),
                                                                  ATTACK_POWER);
                unitTimer = ATTACK_TIMER_FIRING_START;
            }
        }
        else
            unitTimer++;
        if (unitTimer == ATTACK_TIMER_RECOVERED)
            unitTimer = 0;
        system.unitsTimers[unitIdentifier] = unitTimer;
        return unitTimer != 0;
    }
    
    /**
     * @param unitTimer
     * @return The distance from the Unit of the target, when the Archer is charging.
     */
    public static float targetDistanceWhenCharging(int unitTimer)
    {
        if ((unitTimer >= ATTACK_TIMER_CHARGING_MIN) && (unitTimer <= ATTACK_TIMER_CHARGING_MAX))
            return MathTools.lerp(unitTimer, ATTACK_TIMER_CHARGING_MIN, ATTACK_RANGE_MIN, ATTACK_TIMER_CHARGING_MAX, ATTACK_RANGE_MAX);
        if ((unitTimer >= ATTACK_TIMER_DECHARGING_MAX) && (unitTimer <= ATTACK_TIMER_DECHARGING_MIN))
            return MathTools.lerp(unitTimer, ATTACK_TIMER_DECHARGING_MIN, ATTACK_RANGE_MIN, ATTACK_TIMER_DECHARGING_MAX, ATTACK_RANGE_MAX);
        return ATTACK_RANGE_MIN;
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
        if (unitTimer <= ATTACK_TIMER_DECHARGING_MIN)
            return MathTools.lerpi(unitTimer,
                                   ATTACK_TIMER_DECHARGING_MIN, VideoConstants.BOW_LOAD_FRAMES_START,
                                   ATTACK_TIMER_DECHARGING_MAX, VideoConstants.BOW_LOAD_FRAMES_LAST);
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