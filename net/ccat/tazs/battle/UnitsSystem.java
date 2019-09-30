package net.ccat.tazs.battle;

import femto.mode.HiRes16Color;

import net.ccat.tazs.resources.sprites.BrawlerBodySprite;
import net.ccat.tazs.resources.sprites.HandSprite;

import net.ccat.tazs.tools.MathTools;


/**
 * Manages all the Units in a Battle, from their Updates to their Rendering.
 */
class UnitsSystem
{
    public static final int UNITS_MAX = 128;
    public static final int IDENTIFIER_NONE = -1;
    
    
    /***** UNITS *****/
    
    /**
     * X coordinates for all Units.
     */
    public float[] unitsXs = new float[UNITS_MAX];
    /**
     * Y coordinates for all Units.
     */
    public float[] unitsYs = new float[UNITS_MAX];
    /**
     * Angles for all Units.
     */
    public float[] unitsAngles = new float[UNITS_MAX];
    /**
     * Timers for all Units.
     */
    public short[] unitsTimers = new short[UNITS_MAX];
    /**
     * Handlers for all Units.
     */
    public UnitHandler[] unitsHandlers = new UnitHandler[UNITS_MAX];
    
    /**
     * Clears any stored Units.
     */
    public void clear()
    {
        mCounts = 0;
    }
    
    /**
     * Adds a Unit inside the system.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param angle Where the units is looking at, in Radiants.
     * @param handler The Handler for this unit.
     * @return the unit's identifier, or IDENTIFIER_NONE if a Unit couldn't be created.
     */
    public int addUnit(float x, float y, float angle, UnitHandler handler)
    {
        if (mCounts >= UNITS_MAX)
            return IDENTIFIER_NONE;

        int unitIdentifier = mCounts;
        
        mCounts++;
        unitsXs[unitIdentifier] = x;
        unitsYs[unitIdentifier] = y;
        unitsAngles[unitIdentifier] = MathTools.wrapAngle(angle);
        unitsHandlers[unitIdentifier] = handler;
        return unitIdentifier;
    }
    
    
    /***** LIFECYCLE *****/
    
    /**
     * Called when a tick happens.
     */
    public void onTick()
    {
        for (int unitIdentifier = 0; unitIdentifier < mCounts; unitIdentifier++)
            unitsHandlers[unitIdentifier].onTick(this, unitIdentifier);
    }
    
    
    /***** RENDERING *****/
    
    public final BrawlerBodySprite brawlerBodySprite = new BrawlerBodySprite();
    public final HandSprite handSprite = new HandSprite();
    
    /**
     * Renders all the Units.
     * @param screen The screen to render into.
     */
    public void draw(HiRes16Color screen)
    {
        for (int unitIdentifier = 0; unitIdentifier < mCounts; unitIdentifier++)
            unitsHandlers[unitIdentifier].draw(this, unitIdentifier, screen);
    }
    
    
    /***** PRIVATE *****/
    
    private int mCounts = 0;
}