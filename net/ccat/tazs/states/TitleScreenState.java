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
class TitleScreenState
    extends State
{
    public TitleScreenState(TAZSGame game)
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
            Game.changeState(new BattlePreparationPhaseState(mGame));

        // TODO: Proper title screen.
        screen.clear(Colors.TITLE_BG);
        
        screen.setTextPosition(0, TITLE_HEIGHT);
        screen.setTextColor(Colors.TITLE_TEXT);
        screen.print(Texts.TITLE);
        screen.setTextPosition(0, VERSION_HEIGHT);
        screen.print(Texts.TITLE_VERSION);
        
        screen.setTextPosition(0 , COMMAND_HEIGHT);
        screen.setTextColor(Colors.TITLE_COMMAND);
        screen.print(Texts.BUTTON_A);
        screen.print(Texts.MISC_SEPARATOR);
        screen.print(Texts.TITLE_COMMANDS_QUICK_BATTLE);
        
        screen.flush();
    }
    
    public void shutdown()
    {
        mGame = null;
    }
    
    
    /***** PRIVATE STUFF *****/
    
    private TAZSGame mGame;
    
    private static final float TITLE_HEIGHT = 32;
    private static final float VERSION_HEIGHT = TITLE_HEIGHT + 7;
    private static final float COMMAND_HEIGHT = 64;
}