package net.ccat.tazs.states;

import net.ccat.tazs.battle.modes.ChallengeBattleMode;
import net.ccat.tazs.battle.modes.challenges.Challenge01BattleMode;
import net.ccat.tazs.battle.modes.challenges.Challenge02BattleMode;
import net.ccat.tazs.battle.modes.challenges.Challenge03BattleMode;
import net.ccat.tazs.battle.modes.challenges.Challenge04BattleMode;
import net.ccat.tazs.battle.modes.challenges.Challenge05BattleMode;
import net.ccat.tazs.battle.modes.challenges.ChallengeZ01BattleMode;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.sprites.MenuCursorSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.ui.UITools;

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
            new Challenge01BattleMode(),
            new Challenge02BattleMode(),
            new Challenge03BattleMode(),
            new Challenge04BattleMode(),
            new Challenge05BattleMode(),
            new ChallengeZ01BattleMode()
        };
    }
    
    
    /***** LIFECYCLE *****/
    
    public void init()
    {
        mGame.menuCursorSprite.setPosition(Dimensions.TITLE_MENU_ENTRY_CURSOR_X, Dimensions.TITLE_MENU_ENTRY_Y_START - VideoConstants.MENU_CURSOR_ORIGIN_Y);
    }
    
    public void update()
    {
        HiRes16Color screen = mGame.screen;
        
        if (Button.A.justPressed())
        {
            mGame.cursorSelectSound.play();
            
            mGame.battleMode = mChallenges[mCurrentMenuIdentifier];
            mGame.battleMode.onLaunch(mGame);
        }
        else if (Button.B.justPressed())
        {
            mGame.cursorCancelSound.play();
            Game.changeState(new TitleScreenState(mGame));
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
        
        screen.setTextPosition((Dimensions.SCREEN_WIDTH - screen.textWidth(Texts.TITLE)) / 2, Dimensions.TITLE_TITLE_Y);
        screen.setTextColor(Colors.TITLE_TEXT);
        screen.print(Texts.TITLE);
        
        screen.setTextPosition((Dimensions.SCREEN_WIDTH - screen.textWidth(Texts.CHALLENGES_TITLE)) / 2, Dimensions.TITLE_SUBTITLE_Y);
        screen.setTextColor(Colors.TITLE_SUBTEXT);
        screen.print(Texts.CHALLENGES_TITLE);

        for (int entry = 0; entry != mChallenges.length; entry++)
            drawMenuChoice(entry, screen);
        
        screen.flush();
    }
    
    
    private void drawMenuChoice(int menuIdentifier, HiRes16Color screen)
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
        screen.print(menuIdentifier + 1);
        screen.print(Texts.MISC_SEPARATOR);
        screen.print(mChallenges[menuIdentifier].name());
    }
    
    private TAZSGame mGame;
    private int mCurrentMenuIdentifier = 0;
    private ChallengeBattleMode[] mChallenges;
}