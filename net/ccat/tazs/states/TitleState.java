package net.ccat.tazs.states;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Texts;

import femto.Game;
import femto.input.Button;
import femto.mode.HiRes16Color;
import femto.State;


/**
 * First State of the game.
 * - Initializes some stuff.
 * - Display the splashscreen.
 */
class TitleState
    extends State
{
    public TitleState(TAZSGame game)
    {
        mGame = game;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void init()
    {
        if (mGame == null)
            mGame = new TAZSGame();
    }
    
    public void update()
    {
        HiRes16Color screen = mGame.screen;
        
        if (Button.A.justPressed())
            Game.changeState(new BattlePreparationState(mGame));

        // TODO: Proper title screen.
        screen.clear(Colors.TITLE_BG_COLOR);
        
        screen.setTextPosition(0, TITLE_HEIGHT);
        screen.setTextColor(Colors.TITLE_TEXT_COLOR);
        screen.print(Texts.TITLE);
        
        screen.setTextPosition(0 , COMMAND_HEIGHT);
        screen.setTextColor(Colors.TITLE_COMMAND_COLOR);
        screen.print(Texts.TITLE_COMMAND_QUICK);
        
        screen.flush();
    }
    
    public void shutdown()
    {
        mGame = null;
    }
    
    
    /***** PRIVATE STUFF *****/
    
    private TAZSGame mGame;
    
    private static final float TITLE_HEIGHT = 32;
    private static final float COMMAND_HEIGHT = 64;
}