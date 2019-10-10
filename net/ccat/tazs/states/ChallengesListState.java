package net.ccat.tazs.states;

import net.ccat.tazs.battle.modes.ChallengeBattleMode;
import net.ccat.tazs.battle.modes.challenges.Challenge01BattleMode;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.MenuCursorSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;

import femto.Game;
import femto.input.Button;
import femto.mode.HiRes16Color;
import femto.State;


/**
 * Allows listing all the available Challenges and launching them.
 */
class ChallengesListState
    extends State
{
    public ChallengesListState(TAZSGame game)
    {
        mGame = game;
        mChallenges = new ChallengeBattleMode[]
        {
            // TODO: Have actual different challenges.
            new Challenge01BattleMode(),
            new Challenge01BattleMode(),
            new Challenge01BattleMode(),
            new Challenge01BattleMode(),
            new Challenge01BattleMode()
        };
    }
    
    
    /***** LIFECYCLE *****/
    
    public void init()
    {
        mGame.menuCursorSprite.setPosition(MENU_ENTRY_CURSOR_X, MENU_ENTRY_Y_START - VideoConstants.MENU_CURSOR_ORIGIN_Y);
    }
    
    public void update()
    {
        HiRes16Color screen = mGame.screen;
        
        if (Button.A.justPressed())
        {
            mGame.cursorSelectSound.play();
            
            mGame.battleMode = mChallenges[mCurrentMenuIdentifier];
            Game.changeState(new BattlePreparationPhaseState(mGame));
        }
        else
        {
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
            mCurrentMenuIdentifier = (mCurrentMenuIdentifier + mChallenges.length) % mChallenges.length;
        }
        
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
        
        screen.setTextPosition((screen.width() - screen.textWidth(Texts.CHALLENGES_TITLE)) / 2, SUBTITLE_Y);
        screen.setTextColor(Colors.TITLE_SUBTEXT);
        screen.print(Texts.CHALLENGES_TITLE);

        for (int entry = 0; entry != mChallenges.length; entry++)
            drawMenuChoice(entry, screen);
        
        screen.flush();
    }
    
    
    private void drawMenuChoice(int menuIdentifier, HiRes16Color screen)
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
        screen.print(menuIdentifier + 1);
        screen.print(Texts.MISC_SEPARATOR);
        screen.print(mChallenges[menuIdentifier].name());
    }
    
    private TAZSGame mGame;
    private int mCurrentMenuIdentifier = 0;
    private ChallengeBattleMode[] mChallenges;
    
    private static final int TITLE_Y = 32;
    private static final int SUBTITLE_Y = 48;
    private static final int MENU_ENTRY_X = 32;
    private static final int MENU_ENTRY_CURSOR_X = MENU_ENTRY_X - VideoConstants.MENU_CURSOR_ORIGIN_X;
    private static final int MENU_ENTRY_Y_START = 64;
    private static final int MENU_ENTRY_HEIGHT = 8;
    private static final int CURSOR_Y_SPEED = 2;
    private static final int BLINK_MASK = 0x80;
}