package net.ccat.tazs.battle.handlers;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.UITools;


/**
 * A collection of Tools related to Handlers.
 */
public class HandlersTools
{
    public static final float POWER_HP_RATIO = 3.f;
    public static final float SEEK_DISTANCE_MAX = 250.f;
    public static final float UNIT_RADIUS = 3.f;
    public static final float UNIT_ATTACK_ANGLE = Math.PI * 0.125f;
    
    
    /***** RENDERING *****/
    
    /**
     * Renders the Control Circle for the given Unit.
     * 
     * @param system
     * @param unitIdentifier
     * @param screen.
     */
    public static void drawControlCircle(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        float radius = MathTools.lerp(System.currentTimeMillis() % 512, 0, Dimensions.UNIT_CONTROL_DIRECTION_LENGTH, 512, Dimensions.UNIT_CONTROL_RADIUS);
        
        screen.drawCircle(unitX, unitY, radius + 1.f, Teams.colorForTeam(unitTeam), false);
        screen.drawCircle(unitX, unitY, radius, Teams.darkerColorForTeam(unitTeam), false);
        screen.drawLine(unitX, unitY,
                        unitX + Math.cos(unitAngle) * Dimensions.UNIT_CONTROL_DIRECTION_LENGTH, unitY + Math.sin(unitAngle) * Dimensions.UNIT_CONTROL_DIRECTION_LENGTH,
                        Teams.colorForTeam(unitTeam), false);
    }
    
    
    /***** STANDARD BEHAVIOR *****/
    
    /**
     * Handles a movement using the player controls.
     * 
     * @param system
     * @param unitIdentifier
     * @param rotationSpeed How fast the angle will change in one tick.
     * @param walkSpeed How fast a unit is moving.
     */
    public static void moveUnitWithPad(UnitsSystem system, int unitIdentifier, float rotationSpeed, float walkSpeed)
    {
        if (system.playerPadLength > 0)
        {
            float unitX = system.unitsXs[unitIdentifier];
            float unitY = system.unitsYs[unitIdentifier];
            float unitSpeed = walkSpeed * system.playerPadLength;
            float unitAngle = system.unitsAngles[unitIdentifier];
            float targetAngle = system.playerPadAngle;
            
            if (!system.playerSecondaryAction)
            {
                float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -rotationSpeed, rotationSpeed);
                
                unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
                system.unitsAngles[unitIdentifier] = unitAngle;
            }
            unitX += Math.cos(targetAngle) * unitSpeed;
            unitY += Math.sin(targetAngle) * unitSpeed;
            system.unitsXs[unitIdentifier] = unitX;
            system.unitsYs[unitIdentifier] = unitY;
        }
    }
    
    /**
     * Seeks an enemy, walk toward it until at range.
     * 
     * @param system
     * @param unitIdentifier
     * @param walkSpeed How fast per tick the Unit moves.
     * @param rotationSpeed How fast per tick the Unit rotates.
     * @param closeEnoughDistanceSquared The distance where the Unit is close enough to attack, but squared.
     * @param ticksUntilChangingTarget Ticks before the Unit consider another target.
     * @return True if an enemy is close enough -
     */
    public static boolean seekAnEnemy(UnitsSystem system, int unitIdentifier,
                                      float walkSpeed, float rotationSpeed,
                                      float closeEnoughDistanceSquared,
                                      int ticksUntilChangingTarget)
    {
        int unitTimer = system.unitsTimers[unitIdentifier];
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        char unitTeam = system.unitsTeams[unitIdentifier];
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        boolean closeEnough = false;
        
        // Random walk
        unitTimer--;
        if (unitTimer <= 0)
        {
            targetIdentifier = system.findClosestLivingUnit(unitX, unitY, Teams.oppositeTeam(unitTeam), SEEK_DISTANCE_MAX);
            system.unitsTargetIdentifiers[unitIdentifier] = targetIdentifier;
            unitTimer = ticksUntilChangingTarget;
        }
        if (targetIdentifier != UnitsSystem.IDENTIFIER_NONE)
        {
            float relativeX = system.unitsXs[targetIdentifier] - unitX;
            float relativeY = system.unitsYs[targetIdentifier] - unitY;
            float squaredDistance = relativeX * relativeX + relativeY * relativeY;
            float targetAngle = 0;
            
            // Updating the angle.
            if (squaredDistance > 0)
            {
                targetAngle = Math.atan2(relativeY, relativeX);
                float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -rotationSpeed, rotationSpeed);
                
                unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
            }
            if (squaredDistance > closeEnoughDistanceSquared)
            {
                unitX += Math.cos(unitAngle) * walkSpeed;
                unitY += Math.sin(unitAngle) * walkSpeed;
            }
            else if (MathTools.abs(MathTools.wrapAngle(targetAngle - unitAngle)) < UNIT_ATTACK_ANGLE)
            {
                 // Let's punch them!
                unitTimer = 0;
                closeEnough = true;
            }
        }

        // Updating the changed state.
        system.unitsTimers[unitIdentifier] = unitTimer;
        system.unitsXs[unitIdentifier] = unitX;
        system.unitsYs[unitIdentifier] = unitY;
        system.unitsAngles[unitIdentifier] = unitAngle;
        return closeEnough;
    }
    
    /**
     * Hits a Unit with a power.
     * 
     * @param system
     * @param unitIdentifier
     * @param powerX
     * @param powerY
     * @param power
     * @return True if the Unit just died because of the hit, false elsewhere.
     */
    public static boolean hitAndCheckIfBecameDead(UnitsSystem system, int unitIdentifier,
                                                  float powerX, float powerY, float power)
    {
        short health = system.unitsHealths[unitIdentifier];
        float inverseWeight = system.unitsHandlers[unitIdentifier].inverseWeight();
        
        // TODO: Do a proper pushback. [011]
        system.unitsXs[unitIdentifier] += powerX * inverseWeight;
        system.unitsYs[unitIdentifier] += powerY * inverseWeight;
        if (health > 0)
        {
            float lostHealth = power * POWER_HP_RATIO;
            
            if (health > lostHealth)
                health -= (short)(int)lostHealth;
            else
            {
                system.unitsTimers[unitIdentifier] = 0;
                health = 0;
            }
            system.unitsHealths[unitIdentifier] = health;
            return health == 0;
        }
        return false;
    }
}