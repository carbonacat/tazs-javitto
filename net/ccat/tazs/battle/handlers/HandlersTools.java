package net.ccat.tazs.battle.handlers;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.Dimensions;


/**
 * A collection of Tools related to Handlers.
 */
public class HandlersTools
{
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
}