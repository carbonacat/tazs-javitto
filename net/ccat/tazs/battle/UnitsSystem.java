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
    
    public UnitsSystem()
    {
        mXs = new float[UNITS_MAX];
        mYs = new float[UNITS_MAX];
        mAngles = new float[UNITS_MAX];
        mTimers = new short[UNITS_MAX];
        mBrawlerBodySprite = new BrawlerBodySprite();
        mHandSprite = new HandSprite();
        mUnit = new Unit();
    }
    
    
    /***** UNITS *****/
    
    /**
     * Clears any stored Units.
     */
    public void clear()
    {
        mCounts = 0;
    }
    
    /**
     * Adds a Unit inside the system, at [x, y] and looking at [angle].
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param angle Where the units is looking at, in Radiants.
     * @return the unit's identifier, or IDENTIFIER_NONE if a Unit couldn't be created.
     */
    public int addUnit(float x, float y, float angle)
    {
        if (mCounts >= UNITS_MAX)
            return IDENTIFIER_NONE;

        int unitIdentifier = mCounts;
        
        mCounts++;
        mXs[unitIdentifier] = x;
        mYs[unitIdentifier] = y;
        mAngles[unitIdentifier] = MathTools.wrapAngle(angle);
        return unitIdentifier;
    }
    
    /**
     * Reads a whole unit.
     * 
     * @param unitIdentifier Identifies which unit to read.
     * @param targetUnit Where to write the unit's properties.
     */
    public void readUnit(int unitIdentifier, Unit targetUnit)
    {
        targetUnit.x = mXs[unitIdentifier];
        targetUnit.y = mYs[unitIdentifier];
        targetUnit.angle = mAngles[unitIdentifier];
        targetUnit.timer = mTimers[unitIdentifier];
    }
    
    /**
     * Overwrites a whole unit.
     * 
     * @param unitIdentifier Identifies which unit to write.
     * @param sourceUnit From where to read unit's properties.
     */
    public void writeUnit(int unitIdentifier, Unit sourceUnit)
    {
        mXs[unitIdentifier] = sourceUnit.x;
        mYs[unitIdentifier] = sourceUnit.y;
        mAngles[unitIdentifier] = sourceUnit.angle;
        mTimers[unitIdentifier] = sourceUnit.timer;
    }
    
    
    /***** LIFECYCLE *****/
    
    /**
     * Called when a tick happens.
     */
    public void onTick()
    {
        for (int unitIdentifier = 0; unitIdentifier < mCounts; unitIdentifier++)
        {
            readUnit(unitIdentifier, mUnit);

            // Random walk
            mUnit.timer--;
            if (mUnit.timer <= 0)
            {
                mUnit.timer = 128;
                mUnit.angle = MathTools.wrapAngle(mUnit.angle + Math.random() - 0.5f);
            }
            mUnit.x += Math.cos(mUnit.angle) * 0.125f;
            mUnit.y += -Math.sin(mUnit.angle) * 0.125f;

            writeUnit(unitIdentifier, mUnit);
        }
    }
    
    
    /***** RENDERING *****/
    
    /**
     * Renders all the Units.
     * @param screen The screen to render into.
     */
    public void draw(HiRes16Color screen)
    {
        for (int unitIdentifier = 0; unitIdentifier < mCounts; unitIdentifier++)
        {
            float x = mXs[unitIdentifier];
            float y = mYs[unitIdentifier];
            float angle = mAngles[unitIdentifier];
            
            mBrawlerBodySprite.setPosition(x, y);
            mBrawlerBodySprite.setMirrored(angle > MathTools.PI_1_2 && angle < MathTools.PI_3_2);
            mBrawlerBodySprite.draw(screen);
        }
    }
    
    
    /***** PRIVATE *****/
    
    private float[] mXs;
    private float[] mYs;
    private float[] mAngles;
    private short[] mTimers;
    private int mCounts = 0;
    
    private BrawlerBodySprite mBrawlerBodySprite;
    private HandSprite mHandSprite;
    // For easing updates for a unit.
    private Unit mUnit;
}