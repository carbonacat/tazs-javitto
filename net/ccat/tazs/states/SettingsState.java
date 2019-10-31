package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.State;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.sprites.MenuCursorSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.texts.MISC_SEPARATOR;
import net.ccat.tazs.resources.texts.SETTINGS_TITLE;
import net.ccat.tazs.resources.texts.SETTINGS_DAMMIT;
import net.ccat.tazs.resources.texts.SETTINGS_ERASE_CAMPAIGN;
import net.ccat.tazs.resources.texts.SETTINGS_ERASE_CHALLENGES;
import net.ccat.tazs.resources.texts.SETTINGS_ERASE_EVERYTHING;
import net.ccat.tazs.resources.texts.TITLE;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.Performances;
import net.ccat.tazs.ui.AdvancedHiRes16Color;
import net.ccat.tazs.ui.UITools;



/**
 * Allows meddling with the settings.
 */
class SettingsState
    extends State
{
    public SettingsState(TAZSGame game)
    {
        mGame = game;
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
            if (mCurrentMenuIdentifier == MENU_ENTRIES_DAMMIT)
                mGame.dammitSound.play();
            else if ((mCurrentMenuIdentifier == MENU_ENTRIES_ERASE_CAMPAIGN) && (Button.C.isPressed()))
            {
                mGame.cookie.clearCampaign();
                mGame.cookie.saveCookie();
                mGame.cursorSelectSound.play();
                Game.changeState(new TitleScreenState(mGame));
            }
            else if ((mCurrentMenuIdentifier == MENU_ENTRIES_ERASE_CHALLENGES) && (Button.C.isPressed()))
            {
                mGame.cookie.clearChallenges();
                mGame.cookie.saveCookie();
                mGame.cursorSelectSound.play();
                Game.changeState(new TitleScreenState(mGame));
            }
            
            else if ((mCurrentMenuIdentifier == MENU_ENTRIES_ERASE_EVERYTHING) && (Button.C.isPressed()))
            {
                mGame.cookie.clear();
                mGame.cookie.saveCookie();
                mGame.cursorSelectSound.play();
                Game.changeState(new TitleScreenState(mGame));
            }
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
            mCurrentMenuIdentifier = (mCurrentMenuIdentifier + MENU_ENTRIES_COUNT) % MENU_ENTRIES_COUNT;
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
        
        screen.setTextPosition((Dimensions.SCREEN_WIDTH - screen.pTextWidth(TITLE.bin())) / 2, Dimensions.TITLE_TITLE_Y);
        screen.setTextColor(Colors.TITLE_TEXT);
        screen.printPText(TITLE.bin());
        
        screen.setTextPosition((Dimensions.SCREEN_WIDTH - screen.pTextWidth(SETTINGS_TITLE.bin())) / 2, Dimensions.TITLE_SUBTITLE_Y);
        screen.setTextColor(Colors.TITLE_SUBTEXT);
        screen.printPText(SETTINGS_TITLE.bin());

        drawMenuChoice(MENU_ENTRIES_DAMMIT, SETTINGS_DAMMIT.bin(), false, screen);
        drawMenuChoice(MENU_ENTRIES_ERASE_CAMPAIGN, SETTINGS_ERASE_CAMPAIGN.bin(), true, screen);
        drawMenuChoice(MENU_ENTRIES_ERASE_CHALLENGES, SETTINGS_ERASE_CHALLENGES.bin(), true, screen);
        drawMenuChoice(MENU_ENTRIES_ERASE_EVERYTHING, SETTINGS_ERASE_EVERYTHING.bin(), true, screen);
        
        screen.flush();
        Performances.onFlushedScreen();
    }
    
    private void drawMenuChoice(int menuIdentifier, pointer title, boolean isDangerous, AdvancedHiRes16Color screen)
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
                entryColor = isDangerous ? Colors.TITLE_MENU_ENTRY_DANGEROUS : Colors.TITLE_MENU_ENTRY_SELECTED;
        }
        if ((isDangerous) && (menuIsCurrent) && (!Button.C.isPressed()))
            entryColor = Colors.TITLE_MENU_ENTRY_DISABLED;
        screen.setTextPosition(Dimensions.TITLE_MENU_ENTRY_X, y);
        screen.setTextColor(entryColor);
        screen.printPText(title);
    }
    
    private TAZSGame mGame;
    
    private int mCurrentMenuIdentifier = 0;
    
    private static final int MENU_ENTRIES_DAMMIT = 0;
    private static final int MENU_ENTRIES_ERASE_CAMPAIGN = MENU_ENTRIES_DAMMIT + 1;
    private static final int MENU_ENTRIES_ERASE_CHALLENGES = MENU_ENTRIES_ERASE_CAMPAIGN + 1;
    private static final int MENU_ENTRIES_ERASE_EVERYTHING = MENU_ENTRIES_ERASE_CHALLENGES + 1;
    private static final int MENU_ENTRIES_COUNT = MENU_ENTRIES_ERASE_EVERYTHING + 1;
}