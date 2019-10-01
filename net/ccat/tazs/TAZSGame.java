package net.ccat.tazs;

import femto.font.TIC80;
import femto.mode.HiRes16Color;

import net.ccat.tazs.battle.UnitsSystem;
import net.ccat.tazs.resources.palettes.ModifiedNAJI16;
import net.ccat.tazs.resources.sprites.CursorSprite;


/**
 * Contains the game elements.
 */
class TAZSGame
{
    public TAZSGame()
    {
        screen = new HiRes16Color(ModifiedNAJI16.palette(), TIC80.font());
        unitsSystem = new UnitsSystem();
        cursorSprite = new CursorSprite();
    }
    
    
    /***** RENDERING *****/
    
    public HiRes16Color screen;
    
    
    /***** GAME *****/
    
    public float sceneXMin = -110;
    public float sceneYMin = -88;
    public float sceneXMax = 108;
    public float sceneYMax = 87;
    public UnitsSystem unitsSystem;
    
    
    /***** COMMON RESOURCES *****/
    
    public CursorSprite cursorSprite;
}