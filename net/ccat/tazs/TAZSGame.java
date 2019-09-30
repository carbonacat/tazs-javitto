package net.ccat.tazs;

import femto.font.TIC80;
import femto.mode.HiRes16Color;

import net.ccat.tazs.battle.UnitsSystem;
import net.ccat.tazs.resources.palettes.ModifiedNAJI16;


/**
 * Contains the game elements.
 */
class TAZSGame
{
    public TAZSGame()
    {
        screen = new HiRes16Color(ModifiedNAJI16.palette(), TIC80.font());
        unitsSystem = new UnitsSystem();
    }
    
    
    /***** RENDERING *****/
    
    public HiRes16Color screen;
    
    
    /***** GAME *****/
    
    public UnitsSystem unitsSystem;
}