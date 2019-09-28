package net.ccat.tazs;

import femto.font.TIC80;
import femto.mode.HiRes16Color;
import femto.palette.Naji16;

import net.ccat.tazs.battle.UnitsSystem;


/**
 * Contains the game elements.
 */
class TAZSGame
{
    public TAZSGame()
    {
        screen = new HiRes16Color(Naji16.palette(), TIC80.font());
        unitsSystem = new UnitsSystem();
    }
    
    
    /***** RENDERING *****/
    
    public HiRes16Color screen;
    
    
    /***** GAME *****/
    
    public UnitsSystem unitsSystem;
}