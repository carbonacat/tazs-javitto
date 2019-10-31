package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.State;

import net.ccat.tazs.battle.modes.ChallengeBattleMode;
import net.ccat.tazs.battle.modes.ChallengeFromPackBattleMode;
import net.ccat.tazs.resources.challenges.ChallengesPack;
import net.ccat.tazs.resources.challenges.ChallengePackReader;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.sprites.MenuCursorSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.texts.MISC_SEPARATOR;
import net.ccat.tazs.resources.texts.TITLE;
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
        
        mPackTitle = ChallengePackReader.titlePointerFromPack(ChallengesPack.bin());
        mPackDescription = ChallengePackReader.descriptionPointerFromPack(ChallengesPack.bin());
        mChallengesCount = ChallengePackReader.challengesCountFromPack(ChallengesPack.bin());
        
        if (identifier < mChallengesCount)
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
        
        if (Button.B.justPressed())
        {
            mGame.cursorCancelSound.play();
            Game.changeState(new TitleScreenState(mGame));
        }
        else if (Button.A.justPressed())
        {
            pointer challengePointer = ChallengePackReader.challengePointerFromPack(ChallengesPack.bin(), mCurrentMenuIdentifier);
        
            mGame.cursorSelectSound.play();
            mGame.battleMode = new ChallengeFromPackBattleMode(mCurrentMenuIdentifier, challengePointer);
            mGame.battleMode.onLaunch(mGame);
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
            mCurrentMenuIdentifier = (mCurrentMenuIdentifier + mChallengesCount) % mChallengesCount;
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
        screen.clear(Colors.TITLE_BG);
        
        // ZOMBIE
        mGame.everyUISprite.selectFrame(VideoConstants.EVERYUI_TITLE_ZOMBIE_FRAME);
        mGame.everyUISprite.setPosition(Dimensions.TITLE_LOGO_ZOMBIE_X - VideoConstants.EVERYUI_ORIGIN_X, Dimensions.TITLE_LOGO_ZOMBIE_Y - VideoConstants.EVERYUI_ORIGIN_Y);
        mGame.everyUISprite.draw(screen);
        // Totally Accurate
        mGame.everyUISprite.selectFrame(VideoConstants.EVERYUI_TITLE_TOTALLY_FRAME);
        mGame.everyUISprite.setPosition(Dimensions.TITLE_LOGO_TOTALLY_X - VideoConstants.EVERYUI_ORIGIN_X, Dimensions.TITLE_LOGO_TOTALLY_Y_FINAL - VideoConstants.EVERYUI_ORIGIN_Y);
        mGame.everyUISprite.draw(screen);
        // Simulator
        mGame.everyUISprite.selectFrame(VideoConstants.EVERYUI_TITLE_SIMULATOR_FRAME);
        mGame.everyUISprite.setPosition(Dimensions.TITLE_LOGO_SIMULATOR_X_FINAL - VideoConstants.EVERYUI_ORIGIN_X, Dimensions.TITLE_LOGO_SIMULATOR_Y - VideoConstants.EVERYUI_ORIGIN_Y);
        mGame.everyUISprite.draw(screen);

        screen.setTextPosition((Dimensions.SCREEN_WIDTH - screen.pTextWidth(mPackTitle)) / 2, Dimensions.TITLE_SUBTITLE_Y);
        screen.setTextColor(Colors.TITLE_SUBTEXT);
        screen.printPText(mPackTitle);

        for (int entry = 0; entry != mChallengesCount; entry++)
            drawMenuChoice(entry, screen);
        
        screen.flush();
        Performances.onFlushedScreen();
    }
    
    private void drawMenuChoice(int menuIdentifier, AdvancedHiRes16Color screen)
    {
        int y = Dimensions.TITLE_MENU_ENTRY_Y_START + menuIdentifier * Dimensions.TITLE_MENU_ENTRY_HEIGHT;
        boolean menuIsCurrent = (menuIdentifier == mCurrentMenuIdentifier);
        int entryColor = Colors.TITLE_MENU_ENTRY;
        pointer challengePointer = ChallengePackReader.challengePointerFromPack(ChallengesPack.bin(), menuIdentifier);
        
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
        screen.printPText(MISC_SEPARATOR.bin());
        screen.printPText(ChallengePackReader.titlePointerFromChallenge(challengePointer));
        if (mGame.cookie.isChallengeDone(ChallengePackReader.identifierFromChallenge(challengePointer)))
        {
            mGame.everyUISprite.setPosition(Dimensions.TITLE_MENU_ENTRY_CHECK_X - VideoConstants.EVERYUI_ORIGIN_X, y - VideoConstants.EVERYUI_ORIGIN_Y);
            mGame.everyUISprite.selectFrame(VideoConstants.EVERYUI_BLOOD_CHECK_FRAME);
            mGame.everyUISprite.draw(screen);
        }
    }
    
    private TAZSGame mGame;
    private pointer mPackTitle;
    private pointer mPackDescription;
    private pointer mChallenges;
    private int mChallengesCount;
    
    private int mCurrentMenuIdentifier = 0;
}