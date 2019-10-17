package net.ccat.tazs.battle.handlers;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.tools.MathTools;


/**
 * A collection of Tools related to Handlers.
 */
public class HandlersTools
{
    public static final float POWER_HP_RATIO = 3.f;
    
    
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
        char unitTeam = system.unitsTeams[unitIdentifier];
        
        screen.drawCircle(unitX, unitY, Dimensions.UNIT_CONTROL_RADIUS, Teams.colorForTeam(unitTeam), false);
    }
    
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
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        float targetAngle = system.playerPadAngle;
        float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -rotationSpeed, rotationSpeed);
        
        unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
        system.unitsAngles[unitIdentifier] = unitAngle;
        if (system.playerPadLength > 0)
        {
            float unitSpeed = walkSpeed * system.playerPadLength;
            
            unitX += Math.cos(targetAngle) * unitSpeed;
            unitY += Math.sin(targetAngle) * unitSpeed;
            system.unitsXs[unitIdentifier] = unitX;
            system.unitsYs[unitIdentifier] = unitY;
        }
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
        
        // TODO: Do a proper pushback. [011]
        system.unitsXs[unitIdentifier] += powerX;
        system.unitsYs[unitIdentifier] += powerY;
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