package net.ccat.tazs.battle.handlers.slapper;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


/**
 * Handles when a Slapper slaps a target.
 * Goes back to SlapperIdle when it's done.
 */
public class SlapperSlapHandler
    extends BaseSlapperHandler
{
    static final SlapperSlapHandler instance = new SlapperSlapHandler();
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int unitTimer = system.unitsTimers[unitIdentifier];
        int targetIdentifier = system.unitsTargetIdentifiers[unitIdentifier];
        
        if (targetIdentifier == UnitsSystem.IDENTIFIER_NONE)
        {
            unitTimer = 0;
            system.unitsHandlers[unitIdentifier] = SlapperIdleHandler.instance;
        }
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
                    char unitTeam = system.unitsTeams[unitIdentifier];
                    float weaponX = handX(unitX, unitAngle, handDistance);
                    float weaponY = handY(unitY, unitAngle, handDistance);
                    
                    // TODO: 1-team isn't really a good way to find the other team.
                    int hitUnitIdentifier = system.findClosestUnit(weaponX, weaponY, 1 - unitTeam, HAND_RADIUS + UNIT_RADIUS, false);
                    
                    if (hitUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
                    {
                        system.unitsHandlers[hitUnitIdentifier].onHit(system, hitUnitIdentifier,
                                                                      HAND_POWER * Math.sin(unitAngle), HAND_POWER * -Math.cos(unitAngle),
                                                                      HAND_POWER);
                        // Interpolating to find the equivalent withdrawal position.
                        unitTimer = MathTools.lerpi(unitTimer, 0, TIMER_PUNCH_MAX, TIMER_PUNCH_MAX, TIMER_PUNCH_REST);
                    }
                }
                if (unitTimer == TIMER_PUNCH_REST)
                {
                    unitTimer = 0;
                    system.unitsHandlers[unitIdentifier] = SlapperIdleHandler.instance;
                }
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
        char unitTeam = system.unitsTeams[unitIdentifier];
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
        
        drawSlapper(unitX, unitY, unitAngle, handDistance, system.slapperBodySpriteByTeam[unitTeam], system.handSprite, screen);
    }
    
    
    /***** PRIVATE *****/
    
    public static final int TIMER_INIT = 0;
    public static final int TIMER_PUNCH_MAX = 8;
    public static final int TIMER_PUNCH_REST = 32;
}