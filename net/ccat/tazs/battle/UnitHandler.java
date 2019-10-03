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
    
    
    /***** EVENTS *****/
    
    /**
     * Called when this Unit was hit by some power.
     * @param system The system the Unit belongs to.
     * @param unitIdentifier Identifies the Unit inside the system.
     * @param powerX the X component of the power.
     * @param powerY the Y component of the power.
     */
    public abstract void onHit(UnitsSystem system, int unitIdentifier,
                               float powerX, float powerY);
    
    
    /***** RENDERING *****/
    
    /**
     * Renders a given Unit inside the UnitsSystem.
     * @param system The system the Unit belongs to.
     * @param unitIdentifier Identifies the Unit inside the system.
     */
    public abstract void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen);
}
