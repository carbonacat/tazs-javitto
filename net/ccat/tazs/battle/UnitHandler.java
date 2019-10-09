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
     * @return The UnitType of this Unit.
     * @see net.ccat.tazs.battle.UnitTypes.
     */
    public abstract int unitType();
    
    /**
     * @return the usual name.
     */
    public abstract String name();
    
    /**
     * @return How many health a Unit of this type should have.
     */
    public abstract int startingHealth();
    
    /**
     * @return How many does a Unit of this type costs.
     */
    public abstract int cost();
    
    
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
     * @param power the power.
     */
    public abstract void onHit(UnitsSystem system, int unitIdentifier,
                               float powerX, float powerY, float power);
    
    
    /***** RENDERING *****/
    
    /**
     * Renders a given Unit inside the UnitsSystem.
     * @param system The system the Unit belongs to.
     * @param unitIdentifier Identifies the Unit inside the system.
     */
    public abstract void draw(UnitsSystem system, int unitIdentifier, HiRes16Color screen);
    
    /**
     * Renders the given Unit inside the UnitsSystem.
     * @param system The system the Unit belongs to.
     * @param x
     * @param y
     * @param angle
     * @param team The team.
     * @param unitIdentifier Identifies the Unit inside the system.
     */
    public abstract void drawAsUI(UnitsSystem system,
                                  float x, float y, float angle, int team,
                                  HiRes16Color screen);
}
