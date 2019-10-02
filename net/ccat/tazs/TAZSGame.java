package net.ccat.tazs;

import femto.font.TIC80;
import femto.mode.HiRes16Color;

import net.ccat.tazs.battle.UnitsSystem;
import net.ccat.tazs.resources.palettes.ModifiedNAJI16;
import net.ccat.tazs.resources.sprites.CursorSprite;
import net.ccat.tazs.resources.sprites.PadMenuSprite;


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
        padMenuSprite = new PadMenuSprite();
        padMenuSprite.setStatic(true);
    }
    
    
    /***** RENDERING *****/
    
    public HiRes16Color screen;
    
    
    /***** GAME *****/
    
    public float sceneXMin = -100;
    public float sceneYMin = -70;
    public float sceneXMax = 100;
    public float sceneYMax = 70;
    public UnitsSystem unitsSystem;
    
    
    /***** COMMON RESOURCES *****/
    
    public CursorSprite cursorSprite;
    public PadMenuSprite padMenuSprite;
}