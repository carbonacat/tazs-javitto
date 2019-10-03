package net.ccat.tazs.battle.handlers;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


/**
 * Handles when a Brawler punches a target.
 * Goes back to BrawlerIdle when it's done.
 */
public class BrawlerPunchHandler
    extends BaseBrawlerHandler
{
    static final BrawlerPunchHandler alliedInstance = new BrawlerPunchHandler(true);
    static final BrawlerPunchHandler enemyInstance = new BrawlerPunchHandler(false);
    
    static BrawlerPunchHandler instance(boolean isAllied)
    {
        return isAllied ? alliedInstance : enemyInstance;
    }
    
    
    public BrawlerPunchHandler(boolean isAllied)
    {
        super(isAllied);
    }
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int unitTimer = system.unitsTimers[unitIdentifier];
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        
        if (targetIdentifier == UnitsSystem.IDENTIFIER_NONE)
            system.unitsHandlers[unitIdentifier] = BrawlerIdleHandler.instance(mIsAllied);
        else
        {
            if (unitTimer == 0)
                unitTimer = 1;
            else
            {
                unitTimer++;
                if (unitTimer < TIMER_PUNCH_MAX)
                {
                    float handDistance = MathTools.lerp(unitTimer,
                                                        TIMER_INIT, HAND_IDLE_DISTANCE,
                                                        TIMER_PUNCH_MAX, HAND_MAX_DISTANCE);
                    float unitX = system.unitsXs[unitIdentifier];
                    float unitY = system.unitsYs[unitIdentifier];
                    float unitAngle = system.unitsAngles[unitIdentifier];
                    float weaponX = handX(unitX, unitAngle, handDistance);
                    float weaponY = handY(unitY, unitAngle, handDistance);
                    int hitUnitIdentifier = system.findClosestUnit(weaponX, weaponY, !mIsAllied, HAND_RADIUS + UNIT_RADIUS);
                    
                    if (hitUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
                        system.unitsHandlers[hitUnitIdentifier].onHit(system, hitUnitIdentifier,
                                                                      HAND_POWER * Math.cos(unitAngle), HAND_POWER * Math.sin(unitAngle));
                }
                if (unitTimer == TIMER_PUNCH_REST)
                    system.unitsHandlers[unitIdentifier] = BrawlerIdleHandler.instance(mIsAllied);
            }
        }
        // Updating the changed state.
        system.unitsTimers[unitIdentifier] = unitTimer;
    }
    
    
    /***** RENDERING *****/
    
    public void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        int unitTimer = system.unitsTimers[unitIdentifier];
        float handDistance;
        
        // Calculating the distance.
        if (unitTimer < TIMER_PUNCH_MAX)
            handDistance = MathTools.lerp(unitTimer,
                                          TIMER_INIT, HAND_IDLE_DISTANCE,
                                          TIMER_PUNCH_MAX, HAND_MAX_DISTANCE);
        else if (unitTimer < TIMER_PUNCH_REST)
            handDistance = MathTools.lerp(unitTimer,
                                          TIMER_PUNCH_MAX, HAND_MAX_DISTANCE,
                                          TIMER_PUNCH_REST, HAND_IDLE_DISTANCE);
        else
            handDistance = HAND_IDLE_DISTANCE;
        
        drawBrawler(unitX, unitY, unitAngle, handDistance, system.brawlerBodySprite, system.handSprite, screen);
    }
    
    
    /***** PRIVATE *****/
    
    public static final int TIMER_INIT = 0;
    public static final int TIMER_PUNCH_MAX = 8;
    public static final int TIMER_PUNCH_REST = 32;
}