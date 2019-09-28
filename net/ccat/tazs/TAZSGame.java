package net.ccat.tazs;

import femto.font.TIC80;
import femto.mode.HiRes16Color;
import femto.palette.Naji16;


/**
 * Contains the game elements.
 */
class TAZSGame
{
    public TAZSGame()
    {
        Naji16 sada;
        
        screen = new HiRes16Color(Naji16.palette(), TIC80.font());
    }
    
    public HiRes16Color screen; // the screenmode we want to draw with
}