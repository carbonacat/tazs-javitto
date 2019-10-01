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
    public static final int UNITS_MAX = 16;
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
        mCount = 0;
    }
    
    /**
     * Adds a Unit inside the system.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param angle Where the units is looking at, in Radiants.
     * @param handler The Handler for this unit.
     * @return the unit's identifier, or IDENTIFIER_NONE if a Unit couldn't be created.
     */
    public int addUnit(float x, float y, float angle, UnitHandler handler)
    {
        if (mCount >= UNITS_MAX)
            return IDENTIFIER_NONE;

        int unitIdentifier = mCount;
        
        mCount++;
        unitsXs[unitIdentifier] = x;
        unitsYs[unitIdentifier] = y;
        unitsAngles[unitIdentifier] = MathTools.wrapAngle(angle);
        unitsTimers[unitIdentifier] = 0;
        unitsHandlers[unitIdentifier] = handler;
        return unitIdentifier;
    }
    
    /**
     * Removes a Unit from this System.
     * @param unitIdentifier the Unit's identifier.
     * @return true if removed, false elsewhere.
     * 
     * NOTE: Removing a Unit will change some Unit's identifiers.
     */
    public boolean removeUnit(int unitIdentifier)
    {
        if ((unitIdentifier < 0) || (unitIdentifier >= mCount))
            return false;
        mCount--;
        
        int lastUnitIdentifier = mCount;
        
        // Swaps this Unit with the last one, if there is one.
        if ((unitIdentifier != lastUnitIdentifier) && (lastUnitIdentifier >= 0))
        {
            unitsXs[unitIdentifier] = unitsXs[lastUnitIdentifier];
            unitsYs[unitIdentifier] = unitsYs[lastUnitIdentifier];
            unitsAngles[unitIdentifier] = unitsAngles[lastUnitIdentifier];
            unitsTimers[unitIdentifier] = unitsTimers[lastUnitIdentifier];
            unitsHandlers[unitIdentifier] = unitsHandlers[lastUnitIdentifier];
        }
        return true;
    }
    
    /**
     * @return The number of Units that can still be added.
     */
    public int freeUnits()
    {
        return UNITS_MAX - mCount;
    }
    
    /**
     * Finds a Unit at the given coordinates.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @return the found Unit's identifier, or IDENTIFIER_NONE if none found.
     */
    public int findUnit(float x, float y)
    {
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
        {
            float relativeX = x - unitsXs[unitIdentifier];
            float relativeY = y - unitsYs[unitIdentifier];
            
            if ((tools.MathTools.abs(relativeX) <= FIND_DISTANCE_MAX) && (tools.MathTools.abs(relativeY) < FIND_DISTANCE_MAX))
                return unitIdentifier;
        }
        return IDENTIFIER_NONE;
    }
    
    
    /***** LIFECYCLE *****/
    
    /**
     * Called when a tick happens.
     */
    public void onTick()
    {
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
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
        for (int unitIdentifier = 0; unitIdentifier < mCount; unitIdentifier++)
            unitsHandlers[unitIdentifier].draw(this, unitIdentifier, screen);
    }
    
    
    /***** PRIVATE *****/
    
    private int mCount = 0;
    
    private static final float FIND_DISTANCE_MAX = 4.f;
}