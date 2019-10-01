package net.ccat.tazs.battle.handlers;

import femto.mode.HiRes16Color;


/**
 * A Handler for a Unit:
 * - Updates its state
 * - Renders it.
 */
public interface UnitHandler
{
    /***** INFORMATION *****/
    
    /**
     * @Returns true if this Handler is for an Allied Unit.
     */
    public abstract boolean isAllied();
    
    
    /***** LIFECYCLE *****/
    
    /**
     * Updates a given Unit inside the UnitsSystem.
     * @param system The system the Unit belongs to.
     * @param unitIdentifier Identifies the Unit inside the system.
     */
    public abstract void onTick(UnitsSystem system, int unitIdentifier);
    
    
    /***** RENDERING *****/
    
    /**
     * Renders a given Unit inside the UnitsSystem.
     * @param system The system the Unit belongs to.
     * @param unitIdentifier Identifies the Unit inside the system.
     */
    public abstract void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen);
}
