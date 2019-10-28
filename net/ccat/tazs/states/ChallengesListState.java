package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.State;

import net.ccat.tazs.battle.modes.ChallengeBattleMode;
import net.ccat.tazs.battle.modes.challenges.Challenge01BattleMode;
import net.ccat.tazs.battle.modes.challenges.Challenge02BattleMode;
import net.ccat.tazs.battle.modes.challenges.Challenge03BattleMode;
import net.ccat.tazs.battle.modes.challenges.Challenge04BattleMode;
import net.ccat.tazs.battle.modes.challenges.Challenge05BattleMode;
import net.ccat.tazs.battle.modes.challenges.ChallengeZ01BattleMode;
import net.ccat.tazs.resources.challenges.Challenge01;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.sprites.MenuCursorSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.texts.CHALLENGES_TITLE;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.Performances;
import net.ccat.tazs.ui.AdvancedHiRes16Color;
import net.ccat.tazs.ui.UITools;



/**
 * Allows listing all the available Challenges and launching them.
 */
class ChallengesListState
    extends State
{
    public ChallengesListState(TAZSGame game, int identifier)
    {
        mGame = game;
        mChallenges = new ChallengeBattleMode[]
        {
            new Challenge01BattleMode(0),
            new Challenge02BattleMode(1),
            new Challenge03BattleMode(2),
            new Challenge04BattleMode(3),
            new Challenge05BattleMode(4),
            new ChallengeZ01BattleMode(5)
        };
        if (identifier < mChallenges.length)
            mCurrentMenuIdentifier = identifier;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void init()
    {
        Performances.onInit();
        
        int cursorY = Dimensions.TITLE_MENU_ENTRY_Y_START + mCurrentMenuIdentifier * Dimensions.TITLE_MENU_ENTRY_HEIGHT;
        
        mGame.menuCursorSprite.setPosition(Dimensions.TITLE_MENU_ENTRY_CURSOR_X, cursorY - VideoConstants.MENU_CURSOR_ORIGIN_Y);
    }
    
    public void update()
    {
        Performances.onUpdateStart();
        
        AdvancedHiRes16Color screen = mGame.screen;
        
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
        
        Performances.onUpdateEnd();
    }
    
    public void shutdown()
    {
        mGame = null;
        
        Performances.onShutdown();
    }
    
    
    /***** PRIVATE STUFF *****/
    
    private void draw(AdvancedHiRes16Color screen)
    {
        // TODO: Proper title screen. [015]
        screen.clear(Colors.TITLE_BG);
        
        screen.setTextPosition((Dimensions.SCREEN_WIDTH - screen.textWidth(Texts.TITLE)) / 2, Dimensions.TITLE_TITLE_Y);
        screen.setTextColor(Colors.TITLE_TEXT);
        screen.print(Texts.TITLE);
        
        screen.setTextPosition((Dimensions.SCREEN_WIDTH - screen.pTextWidth(CHALLENGES_TITLE.bin())) / 2, Dimensions.TITLE_SUBTITLE_Y);
        screen.setTextColor(Colors.TITLE_SUBTEXT);
        screen.printPText(CHALLENGES_TITLE.bin());

        for (int entry = 0; entry != mChallenges.length; entry++)
            drawMenuChoice(entry, screen);
        
        screen.flush();
        Performances.onFlushedScreen();
    }
    
    
    private void drawMenuChoice(int menuIdentifier, AdvancedHiRes16Color screen)
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