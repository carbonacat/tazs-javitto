package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.State;

import net.ccat.tazs.battle.modes.RandomBattleMode;
import net.ccat.tazs.battle.modes.SandboxBattleMode;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.musics.Musics;
import net.ccat.tazs.resources.sprites.MenuCursorSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.texts.MISC_ERROR;
import net.ccat.tazs.resources.texts.SAVE_COMMANDS_DAMMIT;
import net.ccat.tazs.resources.texts.SAVE_COMMANDS_ERASE;
import net.ccat.tazs.resources.texts.SAVE_ERROR_CORRUPTED;
import net.ccat.tazs.resources.texts.SAVE_ERROR_EMPTY;
import net.ccat.tazs.resources.texts.SAVE_ERROR_VERSION;
import net.ccat.tazs.resources.texts.TITLE_VERSION;
import net.ccat.tazs.resources.texts.TITLE_MENU_CHALLENGES;
import net.ccat.tazs.resources.texts.TITLE_MENU_QUICKBATTLE;
import net.ccat.tazs.resources.texts.TITLE_MENU_SANDBOX;
import net.ccat.tazs.resources.texts.TITLE_MENU_SETTINGS;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.save.SaveStatus;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.tools.Performances;
import net.ccat.tazs.ui.AdvancedHiRes16Color;
import net.ccat.tazs.ui.UITools;


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
        Performances.onInit();
        
        if (mGame == null)
            mGame = new TAZSGame();
        else // Don't need to show the animation again.
            mTimer = TIMER_END;
        mGame.menuCursorSprite.setPosition(Dimensions.TITLE_MENU_ENTRY_CURSOR_X, Dimensions.TITLE_MENU_ENTRY_Y_START - VideoConstants.MENU_CURSOR_ORIGIN_Y);
        mCookieStatus = mGame.cookie.getStatus();
    }
    
    public void update()
    {
        Performances.onUpdateStart();
        
        AdvancedHiRes16Color screen = mGame.screen;
        
        if (mCookieStatus != SaveStatus.OK)
            handleCookieDialog();
        else
        {
            mTimer++;
            
            if (mTimer < TIMER_END)
            {
                // Skips to the menu.
                if (Button.A.justPressed())
                    mTimer = TIMER_END;
            }
            else
            {
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
                        Game.changeState(new ChallengesListState(mGame, 0));
                    else if (mCurrentMenuIdentifier == MENU_ENTRIES_SETTINGS)
                        Game.changeState(new SettingsState(mGame));
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
            }
        } 
        mCurrentMenuIdentifier = (mCurrentMenuIdentifier + MENU_ENTRIES_COUNT) % MENU_ENTRIES_COUNT;
        
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
        
        if (mCookieStatus != SaveStatus.OK)
            drawCookieDialog(screen);
        else
        {
            drawTitleStuff(mGame, mTimer);
            if (mTimer >= TIMER_END)
            {
                drawMenuChoice(MENU_ENTRIES_QUICKBATTLE, TITLE_MENU_QUICKBATTLE.bin(), screen);
                drawMenuChoice(MENU_ENTRIES_SANDBOX, TITLE_MENU_SANDBOX.bin(), screen);
                drawMenuChoice(MENU_ENTRIES_CHALLENGES, TITLE_MENU_CHALLENGES.bin(), screen);
                drawMenuChoice(MENU_ENTRIES_SETTINGS, TITLE_MENU_SETTINGS.bin(), screen);
            }
        }
        
        screen.flush();
        Performances.onFlushedScreen();
    }
    
    private void drawMenuChoice(int menuIdentifier, pointer title, AdvancedHiRes16Color screen)
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
        screen.printPText(title);
    }
    
    private void handleCookieDialog()
    {
        if (Button.A.justPressed())
        {
            // Theorically unnecessary.
            if ((mCookieStatus == SaveStatus.EMPTY) || (mCookieStatus == SaveStatus.CORRUPTED) || (mCookieStatus == SaveStatus.VERSION_MISMATCH))
            {
                mGame.cursorSelectSound.play();
                mGame.cookie.clear();
                mGame.cookie.setMusicIdentifier(Musics.MUSIC00);
                mGame.cookie.saveCookie();
                mCookieStatus = mGame.cookie.getStatus();
            }
        }
        if (Button.B.justPressed())
            mGame.dammitSound.play();
    }
    
    private void drawCookieDialog(AdvancedHiRes16Color screen)
    {
        screen.fillRectBlended(0, 0, Dimensions.SCREEN_WIDTH, Dimensions.SCREEN_HEIGHT,
                               Colors.WINDOW_OVERLAY, 0, AdvancedHiRes16Color.PATTERN_25_75_HEX);
        
        pointer textPointer;
        
        if (mCookieStatus == SaveStatus.CORRUPTED)
            textPointer = SAVE_ERROR_CORRUPTED.bin();
        else if (mCookieStatus == SaveStatus.EMPTY)
            textPointer = SAVE_ERROR_EMPTY.bin();
        else if (mCookieStatus == SaveStatus.VERSION_MISMATCH)
            textPointer = SAVE_ERROR_VERSION.bin();
        else
            textPointer = MISC_ERROR.bin();
        screen.setTextColor(Colors.WINDOW_TEXT);
        screen.drawLabelP(textPointer,
                          Colors.WINDOW_BACKGROUND, Colors.WINDOW_BACKGROUND,
                          Dimensions.WINDOW_PADDING,
                          Dimensions.SCREEN_WIDTH_2, Dimensions.SCREEN_HEIGHT_2 - Dimensions.WINDOW_PADDING,
                          UITools.ALIGNMENT_CENTER, UITools.ALIGNMENT_END);
        screen.drawLabelP(SAVE_COMMANDS_ERASE.bin(),
                          Colors.WINDOW_BORDER, Colors.WINDOW_BACKGROUND,
                          Dimensions.WINDOW_PADDING,
                          Dimensions.SCREEN_WIDTH_2, Dimensions.SCREEN_HEIGHT_2 + Dimensions.WINDOW_PADDING,
                          UITools.ALIGNMENT_CENTER, UITools.ALIGNMENT_START);
        screen.drawLabelP(SAVE_COMMANDS_DAMMIT.bin(),
                          Colors.WINDOW_BORDER, Colors.WINDOW_BACKGROUND,
                          Dimensions.WINDOW_PADDING,
                          Dimensions.SCREEN_WIDTH_2, Dimensions.SCREEN_HEIGHT_2 + Dimensions.WINDOW_PADDING + 10 + Dimensions.WINDOW_PADDING,
                          UITools.ALIGNMENT_CENTER, UITools.ALIGNMENT_START);
    }
    
    static public void drawTitleStuff(TAZSGame game, int timer)
    {
        AdvancedHiRes16Color screen = game.screen;
        
        if (timer >= TIMER_INIT)
        {
            game.everyUISprite.selectFrame(VideoConstants.EVERYUI_TITLE_ZOMBIE_FRAME);
            game.everyUISprite.setPosition(Dimensions.TITLE_LOGO_ZOMBIE_X - VideoConstants.EVERYUI_ORIGIN_X, Dimensions.TITLE_LOGO_ZOMBIE_Y - VideoConstants.EVERYUI_ORIGIN_Y);
            game.everyUISprite.draw(screen);
            if (timer <= TIMER_STEP1)
                screen.fillRectBlended(Dimensions.TITLE_LOGO_ZOMBIE_XMIN, Dimensions.TITLE_LOGO_ZOMBIE_YMIN, Dimensions.TITLE_LOGO_ZOMBIE_WIDTH, Dimensions.TITLE_LOGO_ZOMBIE_HEIGHT,
                                       0, Colors.TITLE_BG, patternForFadein(timer, TIMER_INIT, TIMER_STEP1));
        }
        if (timer >= TIMER_STEP1)
        {
            // Totally Accurate
            game.everyUISprite.selectFrame(VideoConstants.EVERYUI_TITLE_TOTALLY_FRAME);
            if (timer <= TIMER_STEP2)
            {
                int totallyY = MathTools.lerpi(timer, TIMER_STEP1, Dimensions.TITLE_LOGO_TOTALLY_Y_START, TIMER_STEP2, Dimensions.TITLE_LOGO_TOTALLY_Y_FINAL);
                
                game.everyUISprite.setPosition(Dimensions.TITLE_LOGO_TOTALLY_X - VideoConstants.EVERYUI_ORIGIN_X,
                                               totallyY - VideoConstants.EVERYUI_ORIGIN_Y);
            }
            else
                game.everyUISprite.setPosition(Dimensions.TITLE_LOGO_TOTALLY_X - VideoConstants.EVERYUI_ORIGIN_X, Dimensions.TITLE_LOGO_TOTALLY_Y_FINAL - VideoConstants.EVERYUI_ORIGIN_Y);
            game.everyUISprite.draw(screen);
            
            // Simulator
            game.everyUISprite.selectFrame(VideoConstants.EVERYUI_TITLE_SIMULATOR_FRAME);
            if (timer <= TIMER_STEP2)
            {
                int simulatorX = MathTools.lerpi(timer, TIMER_STEP1, Dimensions.TITLE_LOGO_SIMULATOR_X_START, TIMER_STEP2, Dimensions.TITLE_LOGO_SIMULATOR_X_FINAL);
                
                game.everyUISprite.setPosition(simulatorX - VideoConstants.EVERYUI_ORIGIN_X,
                                                Dimensions.TITLE_LOGO_SIMULATOR_Y - VideoConstants.EVERYUI_ORIGIN_Y);
            }
            else
                game.everyUISprite.setPosition(Dimensions.TITLE_LOGO_SIMULATOR_X_FINAL - VideoConstants.EVERYUI_ORIGIN_X, Dimensions.TITLE_LOGO_SIMULATOR_Y - VideoConstants.EVERYUI_ORIGIN_Y);
            game.everyUISprite.draw(screen);
            
            int shadowsY;
            
            if (timer < TIMER_END)
                shadowsY = MathTools.lerpi(timer, TIMER_STEP1, Dimensions.TITLE_LOGO_SHADOWS_Y_START, TIMER_END, Dimensions.TITLE_LOGO_SHADOWS_Y_FINAL);
            else
                shadowsY = Dimensions.TITLE_LOGO_SHADOWS_Y_FINAL;

            for (int frame = VideoConstants.EVERYUI_TITLE_SHADOWS_FRAMES_FIRST; frame <= VideoConstants.EVERYUI_TITLE_SHADOWS_FRAMES_LAST; frame++)
            {
                int shadowsX = MathTools.lerpi(frame, VideoConstants.EVERYUI_TITLE_SHADOWS_FRAMES_FIRST, Dimensions.TITLE_LOGO_SHADOWS_X_MIN,
                                               VideoConstants.EVERYUI_TITLE_SHADOWS_FRAMES_LAST, Dimensions.TITLE_LOGO_SHADOWS_X_MAX)
                    + Math.random(-1, 1);
                
                game.everyUISprite.selectFrame(frame);
                game.everyUISprite.setPosition(shadowsX - VideoConstants.EVERYUI_ORIGIN_X, shadowsY - VideoConstants.EVERYUI_ORIGIN_Y);
                game.everyUISprite.draw(screen);
            }
        }
        screen.setTextPosition(Dimensions.TITLE_VERSION_X, Dimensions.TITLE_VERSION_Y);
        screen.setTextColor(Colors.TITLE_VERSION);
        screen.printPText(TITLE_VERSION.bin());
    }
    
    static private int patternForFadein(int timer, int timerMin, int timerMax)
    {
        switch (MathTools.lerpi(timer, timerMin, 0, timerMax, 8))
        {
        case 0:
            return AdvancedHiRes16Color.PATTERN_100_0;
        case 1:
            return AdvancedHiRes16Color.PATTERN_93_6;
        case 2:
            return AdvancedHiRes16Color.PATTERN_87_12;
        case 3:
            return AdvancedHiRes16Color.PATTERN_75_25_GRID;
        case 4:
            return AdvancedHiRes16Color.PATTERN_50_50;
        case 5:
            return AdvancedHiRes16Color.PATTERN_25_75_GRID;
        case 6:
            return AdvancedHiRes16Color.PATTERN_12_87;
        case 7:
            return AdvancedHiRes16Color.PATTERN_6_93;
        case 8:
            return AdvancedHiRes16Color.PATTERN_0_100;
        }
        return AdvancedHiRes16Color.PATTERN_0_100;
    }
    
    private TAZSGame mGame;
    private int mCurrentMenuIdentifier = MENU_ENTRIES_QUICKBATTLE;
    private int mCookieStatus = SaveStatus.EMPTY;
    private int mTimer;

    private static final int MENU_ENTRIES_QUICKBATTLE = 0;
    private static final int MENU_ENTRIES_SANDBOX = MENU_ENTRIES_QUICKBATTLE + 1;
    private static final int MENU_ENTRIES_CHALLENGES = MENU_ENTRIES_SANDBOX + 1;
    private static final int MENU_ENTRIES_SETTINGS = MENU_ENTRIES_CHALLENGES + 1;
    private static final int MENU_ENTRIES_COUNT = MENU_ENTRIES_SETTINGS + 1;
    
    private static final int TIMER_INIT = 0;
    private static final int TIMER_STEP1 = 30;
    private static final int TIMER_STEP2 = 60;
    private static final int TIMER_END = 90;
}