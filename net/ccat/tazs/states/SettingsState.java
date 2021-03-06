//
// Copyright (C) 2019 Carbonacat
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.State;

import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.musics.Musics;
import net.ccat.tazs.resources.musics.MusicReader;
import net.ccat.tazs.resources.sprites.MenuCursorSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.texts.MISC_SEPARATOR;
import net.ccat.tazs.resources.texts.SETTINGS_TITLE;
import net.ccat.tazs.resources.texts.SETTINGS_DAMMIT;
import net.ccat.tazs.resources.texts.SETTINGS_ERASE_CAMPAIGN;
import net.ccat.tazs.resources.texts.SETTINGS_ERASE_CHALLENGES;
import net.ccat.tazs.resources.texts.SETTINGS_ERASE_EVERYTHING;
import net.ccat.tazs.resources.texts.SETTINGS_PLAY_MUSIC;
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
            if (mCurrentMenuIdentifier == MENU_ENTRIES_PLAY_MUSIC)
                handlePlayMusicChoice();
            else if (mCurrentMenuIdentifier == MENU_ENTRIES_DAMMIT)
                handleDammitChoice();
            else if (mCurrentMenuIdentifier == MENU_ENTRIES_ERASE_CAMPAIGN)
                handleEraseCampaignChoice();
            else if (mCurrentMenuIdentifier == MENU_ENTRIES_ERASE_CHALLENGES)
                handleEraseChallengeChoice();
            else if (mCurrentMenuIdentifier == MENU_ENTRIES_ERASE_EVERYTHING)
                handleEraseEverythingChoice();
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
    
    private void handleDammitChoice()
    {
        if (Button.A.justPressed())
            mGame.dammitSound.play();
    }
    
    private void handleEraseCampaignChoice()
    {
        if ((Button.A.justPressed()) && (Button.C.isPressed()))
        {
            mGame.cookie.clearCampaign();
            mGame.cookie.saveCookie();
            mGame.cursorSelectSound.play();
            Game.changeState(new TitleScreenState(mGame));
        }
    }
    
    private void handleEraseChallengeChoice()
    {
        if ((Button.A.justPressed()) && (Button.C.isPressed()))
        {
            mGame.cookie.clearChallenges();
            mGame.cookie.saveCookie();
            mGame.cursorSelectSound.play();
            Game.changeState(new TitleScreenState(mGame));
        }
    }
    
    private void handleEraseEverythingChoice()
    {
        if ((Button.A.justPressed()) && (Button.C.isPressed()))
        {
            mGame.cookie.clear();
            mGame.cookie.setMusicIdentifier(Musics.MUSIC00);
            mGame.music.playMusic(Musics.musicPointerForIdentifier(Musics.MUSIC00));
            mGame.cookie.saveCookie();
            mGame.cursorSelectSound.play();
            Game.changeState(new TitleScreenState(mGame));
        }
    }
    
    private void handlePlayMusicChoice()
    {
        int musicIdentifier = mGame.cookie.getMusicIdentifier();
        
        if (Button.Left.justPressed())
            musicIdentifier--;
        if (Button.Right.justPressed())
            musicIdentifier++;
        if (musicIdentifier != mGame.cookie.getMusicIdentifier())
        {
            musicIdentifier = (musicIdentifier + Musics.COUNT) % Musics.COUNT;
            mGame.cookie.setMusicIdentifier(musicIdentifier);
            mGame.cookie.saveCookie();
            mGame.music.playMusic(Musics.musicPointerForIdentifier(musicIdentifier));
        }
    }
    
    
    private void draw(AdvancedHiRes16Color screen)
    {
        screen.clear(Colors.TITLE_BG);
        
        TitleScreenState.drawTitleStuff(mGame, TitleScreenState.TIMER_END);
        
        screen.setTextPosition((Dimensions.SCREEN_WIDTH - screen.pTextWidth(SETTINGS_TITLE.bin())) / 2, Dimensions.TITLE_SUBTITLE_Y);
        screen.setTextColor(Colors.TITLE_SUBTEXT);
        screen.printPText(SETTINGS_TITLE.bin());

        int musicIdentifier = mGame.cookie.getMusicIdentifier();
        
        drawMenuChoice(MENU_ENTRIES_PLAY_MUSIC, SETTINGS_PLAY_MUSIC.bin(), MusicReader.titlePointerFromMusic(Musics.musicPointerForIdentifier(musicIdentifier)), false, screen);
        drawMenuChoice(MENU_ENTRIES_DAMMIT, SETTINGS_DAMMIT.bin(), null, false, screen);
        drawMenuChoice(MENU_ENTRIES_ERASE_CAMPAIGN, SETTINGS_ERASE_CAMPAIGN.bin(), null, true, screen);
        drawMenuChoice(MENU_ENTRIES_ERASE_CHALLENGES, SETTINGS_ERASE_CHALLENGES.bin(), null, true, screen);
        drawMenuChoice(MENU_ENTRIES_ERASE_EVERYTHING, SETTINGS_ERASE_EVERYTHING.bin(), null, true, screen);
        
        screen.flush();
        Performances.onFlushedScreen();
    }
    
    private void drawMenuChoice(int menuIdentifier, pointer title, pointer postTitle, boolean isDangerous, AdvancedHiRes16Color screen)
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
        if (postTitle != null)
            screen.printPText(postTitle);
    }
    
    private TAZSGame mGame;
    
    private int mCurrentMenuIdentifier = 0;
    
    private static final int MENU_ENTRIES_PLAY_MUSIC = 0;
    private static final int MENU_ENTRIES_DAMMIT = MENU_ENTRIES_PLAY_MUSIC + 1;
    private static final int MENU_ENTRIES_ERASE_CAMPAIGN = MENU_ENTRIES_DAMMIT + 1;
    private static final int MENU_ENTRIES_ERASE_CHALLENGES = MENU_ENTRIES_ERASE_CAMPAIGN + 1;
    private static final int MENU_ENTRIES_ERASE_EVERYTHING = MENU_ENTRIES_ERASE_CHALLENGES + 1;
    private static final int MENU_ENTRIES_COUNT = MENU_ENTRIES_ERASE_EVERYTHING + 1;
}