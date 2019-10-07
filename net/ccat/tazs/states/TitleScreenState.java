package net.ccat.tazs.states;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.MenuCursorSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;

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
        mGame.menuCursorSprite.setPosition(MENU_ENTRY_CURSOR_X, MENU_ENTRY_Y_START - VideoConstants.MENU_CURSOR_ORIGIN_Y);
    }
    
    public void update()
    {
        HiRes16Color screen = mGame.screen;
        
        if (Button.A.justPressed())
        {
            if (mCurrentMenuIdentifier == MENU_ENTRIES_QUICKBATTLE_IDENTIFIER)
                Game.changeState(new BattlePreparationPhaseState(mGame, BattlePreparationPhaseState.GAMEMODE_QUICKBATTLE));
            else if (mCurrentMenuIdentifier == MENU_ENTRIES_SANDBOX_IDENTIFIER)
                Game.changeState(new BattlePreparationPhaseState(mGame, BattlePreparationPhaseState.GAMEMODE_SANDBOX));
        }
        if (Button.Up.justPressed())
            mCurrentMenuIdentifier--;
        if (Button.Down.justPressed())
            mCurrentMenuIdentifier++;
        mCurrentMenuIdentifier = (mCurrentMenuIdentifier + MENU_ENTRIES_COUNT) % MENU_ENTRIES_COUNT;
        
        draw(screen);
    }
    
    public void shutdown()
    {
        mGame = null;
    }
    
    
    /***** PRIVATE STUFF *****/
    
    private void draw(HiRes16Color screen)
    {
        // TODO: Proper title screen. [015]
        screen.clear(Colors.TITLE_BG);
        
        screen.setTextPosition((screen.width() - screen.textWidth(Texts.TITLE)) / 2, TITLE_Y);
        screen.setTextColor(Colors.TITLE_TEXT);
        screen.print(Texts.TITLE);
        screen.setTextPosition(VERSION_X, VERSION_Y);
        screen.setTextColor(Colors.TITLE_VERSION);
        screen.print(Texts.TITLE_VERSION);

        drawMenuChoice(MENU_ENTRIES_QUICKBATTLE_IDENTIFIER, Texts.TITLE_MENU_QUICKBATTLE, screen);
        drawMenuChoice(MENU_ENTRIES_SANDBOX_IDENTIFIER, Texts.TITLE_MENU_SANDBOX, screen);
        
        screen.flush();
    }
    
    
    private void drawMenuChoice(int menuIdentifier, String title, HiRes16Color screen)
    {
        int y = MENU_ENTRY_Y_START + menuIdentifier * MENU_ENTRY_HEIGHT;
        boolean menuIsCurrent = (menuIdentifier == mCurrentMenuIdentifier);
        int entryColor = Colors.TITLE_MENU_ENTRY;
        
        if (menuIsCurrent)
        {
            MenuCursorSprite cursorSprite = mGame.menuCursorSprite;
            int targetCursorY = y - VideoConstants.MENU_CURSOR_ORIGIN_Y;
            
            if (cursorSprite.y < targetCursorY)
                cursorSprite.y = Math.min(cursorSprite.y + CURSOR_Y_SPEED, targetCursorY);
            if (cursorSprite.y > targetCursorY)
                cursorSprite.y = Math.max(cursorSprite.y - CURSOR_Y_SPEED, targetCursorY);
            mGame.menuCursorSprite.draw(screen);
            if ((System.currentTimeMillis() & BLINK_MASK) == BLINK_MASK)
                entryColor = Colors.TITLE_MENU_ENTRY_SELECTED;
        }
        screen.setTextPosition(MENU_ENTRY_X, y);
        screen.setTextColor(entryColor);
        screen.print(title);
    }
    
    private TAZSGame mGame;
    private int mCurrentMenuIdentifier = MENU_ENTRIES_QUICKBATTLE_IDENTIFIER;
    
    private static final int TITLE_Y = 32;
    private static final int VERSION_X = 1;
    private static final int VERSION_Y = 176 - 6;
    private static final int MENU_ENTRY_X = 32;
    private static final int MENU_ENTRY_CURSOR_X = MENU_ENTRY_X - VideoConstants.MENU_CURSOR_ORIGIN_X;
    private static final int MENU_ENTRY_Y_START = 64;
    private static final int MENU_ENTRY_HEIGHT = 8;
    private static final int CURSOR_Y_SPEED = 2;
    private static final int BLINK_MASK = 0x80;

    private static final int MENU_ENTRIES_QUICKBATTLE_IDENTIFIER = 0;
    private static final int MENU_ENTRIES_SANDBOX_IDENTIFIER = MENU_ENTRIES_QUICKBATTLE_IDENTIFIER + 1;
    private static final int MENU_ENTRIES_COUNT = MENU_ENTRIES_SANDBOX_IDENTIFIER + 1;
}