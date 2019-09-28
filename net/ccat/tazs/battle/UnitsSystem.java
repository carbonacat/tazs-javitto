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
        mBrawlerBodySprite = new BrawlerBodySprite();
        mHandSprite = new HandSprite();
    }
    
    
    /***** UNITS *****/
    
    /**
     * Clears any stored Units.
     */
    void clear()
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
    int addUnit(float x, float y, float angle)
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
    
    
    /***** LIFECYCLE *****/
    
    /**
     * Called when a tick happens.
     */
    void onTick()
    {
        
    }
    
    
    /***** RENDERING *****/
    
    /**
     * Renders all the Units.
     * @param screen The screen to render into.
     */
    void draw(HiRes16Color screen)
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
    private int mCounts = 0;
    
    public BrawlerBodySprite mBrawlerBodySprite;
    public HandSprite mHandSprite;
}