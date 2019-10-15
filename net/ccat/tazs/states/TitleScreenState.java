package net.ccat.tazs.states;

import net.ccat.tazs.battle.modes.RandomBattleMode;
import net.ccat.tazs.battle.modes.SandboxBattleMode;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.MenuCursorSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.ui.UITools;

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
        mGame.menuCursorSprite.setPosition(Dimensions.TITLE_MENU_ENTRY_CURSOR_X, Dimensions.TITLE_MENU_ENTRY_Y_START - VideoConstants.MENU_CURSOR_ORIGIN_Y);
    }
    
    public void update()
    {
        HiRes16Color screen = mGame.screen;
        
        if (Button.A.justPressed())
        {
            mGame.cursorSelectSound.play();
            if (mCurrentMenuIdentifier == MENU_ENTRIES_QUICKBATTLE)
            {
                mGame.battleMode = new RandomBattleMode();
                mGame.battleMode.onLaunch(mGame);
            }
            else if (mCurrentMenuIdentifier == MENU_ENTRIES_SANDBOX)
            {
                mGame.battleMode = new SandboxBattleMode();
                mGame.battleMode.onLaunch(mGame);
            }
            else if (mCurrentMenuIdentifier == MENU_ENTRIES_CHALLENGES)
                Game.changeState(new ChallengesListState(mGame));
        }
        if (Button.Up.justPressed())
        {
            mCurrentMenuIdentifier--;
            mGame.cursorMoveSound.play();
        }
        if (Button.Down.justPressed())
        {
            mCurrentMenuIdentifier++;
            mGame.cursorMoveSound.play();
        }
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
        
        screen.setTextPosition((Dimensions.SCREEN_WIDTH - screen.textWidth(Texts.TITLE)) / 2, Dimensions.TITLE_TITLE_Y);
        screen.setTextColor(Colors.TITLE_TEXT);
        screen.print(Texts.TITLE);
        screen.setTextPosition(Dimensions.TITLE_VERSION_X, Dimensions.TITLE_VERSION_Y);
        screen.setTextColor(Colors.TITLE_VERSION);
        screen.print(Texts.TITLE_VERSION);

        drawMenuChoice(MENU_ENTRIES_QUICKBATTLE, Texts.TITLE_MENU_QUICKBATTLE, screen);
        drawMenuChoice(MENU_ENTRIES_SANDBOX, Texts.TITLE_MENU_SANDBOX, screen);
        drawMenuChoice(MENU_ENTRIES_CHALLENGES, Texts.TITLE_MENU_CHALLENGES, screen);
        
        screen.flush();
    }
    
    
    private void drawMenuChoice(int menuIdentifier, String title, HiRes16Color screen)
    {
        int y = Dimensions.TITLE_MENU_ENTRY_Y_START + menuIdentifier * Dimensions.TITLE_MENU_ENTRY_HEIGHT;
        boolean menuIsCurrent = (menuIdentifier == mCurrentMenuIdentifier);
        int entryColor = Colors.TITLE_MENU_ENTRY;
        
        if (menuIsCurrent)
        {
            MenuCursorSprite cursorSprite = mGame.menuCursorSprite;
            int targetCursorY = y - VideoConstants.MENU_CURSOR_ORIGIN_Y;
            
            if (cursorSprite.y < targetCursorY)
                cursorSprite.y = Math.min(cursorSprite.y + Dimensions.TITLE_CURSOR_Y_SPEED, targetCursorY);
            if (cursorSprite.y > targetCursorY)
                cursorSprite.y = Math.max(cursorSprite.y - Dimensions.TITLE_CURSOR_Y_SPEED, targetCursorY);
            mGame.menuCursorSprite.draw(screen);
            if (UITools.blinkingValue())
                entryColor = Colors.TITLE_MENU_ENTRY_SELECTED;
        }
        screen.setTextPosition(Dimensions.TITLE_MENU_ENTRY_X, y);
        screen.setTextColor(entryColor);
        screen.print(title);
    }
    
    private TAZSGame mGame;
    private int mCurrentMenuIdentifier = MENU_ENTRIES_QUICKBATTLE;

    private static final int MENU_ENTRIES_QUICKBATTLE = 0;
    private static final int MENU_ENTRIES_SANDBOX = MENU_ENTRIES_QUICKBATTLE + 1;
    private static final int MENU_ENTRIES_CHALLENGES = MENU_ENTRIES_SANDBOX + 1;
    private static final int MENU_ENTRIES_COUNT = MENU_ENTRIES_CHALLENGES + 1;
}