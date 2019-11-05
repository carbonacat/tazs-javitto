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

import net.ccat.tazs.battle.BattleMode;
import net.ccat.tazs.battle.Teams;
import net.ccat.tazs.battle.UnitHandler;
import net.ccat.tazs.battle.UnitsSystem;
import net.ccat.tazs.battle.UnitTypes;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.Dimensions;
import net.ccat.tazs.resources.sprites.CursorSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.texts.BUTTON_A;
import net.ccat.tazs.resources.texts.BUTTON_B;
import net.ccat.tazs.resources.texts.BUTTON_PAD;
import net.ccat.tazs.resources.texts.MENU_COMMANDS_HELP;
import net.ccat.tazs.resources.texts.MISC_BIG_SEPARATOR;
import net.ccat.tazs.resources.texts.MISC_ERROR;
import net.ccat.tazs.resources.texts.MISC_SEPARATOR;
import net.ccat.tazs.resources.texts.MISC_UNKNOWN;
import net.ccat.tazs.resources.texts.PREPARATION_COMMANDS_CANNOT_REMOVE;
import net.ccat.tazs.resources.texts.PREPARATION_COMMANDS_CONTROL;
import net.ccat.tazs.resources.texts.PREPARATION_COMMANDS_PLACE_INVALID_NO_MORE_FREE_UNITS;
import net.ccat.tazs.resources.texts.PREPARATION_COMMANDS_PLACE_INVALID_TOO_EXPENSIVE;
import net.ccat.tazs.resources.texts.PREPARATION_COMMANDS_PLACE_UNIT_K;
import net.ccat.tazs.resources.texts.PREPARATION_COMMANDS_REMOVE;
import net.ccat.tazs.resources.texts.PREPARATION_ENEMY_SIDE;
import net.ccat.tazs.resources.texts.PREPARATION_MENU_EXIT;
import net.ccat.tazs.resources.texts.PREPARATION_MENU_LAUNCH;
import net.ccat.tazs.resources.texts.PREPARATION_NO_MANS_LAND;
import net.ccat.tazs.resources.texts.TEAMS_ENEMY;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.tools.Performances;
import net.ccat.tazs.ui.AdvancedHiRes16Color;
import net.ccat.tazs.ui.PadMenuUI;
import net.ccat.tazs.ui.UIModes;
import net.ccat.tazs.ui.UITools;


/**
 * Handles the Battle Preparation Phase, where the Player sets up their armies.
 */
public class BattlePreparationPhaseState
    extends State
{
    public BattlePreparationPhaseState(TAZSGame game, boolean fromRetry)
    {
        mGame = game;
        mFromRetry = fromRetry;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void init()
    {
        Performances.onInit();
        
        TAZSGame game = mGame;
        
        game.unitsSystem.clear();
        game.centerCameraOn(0, 0);
        game.cursorX = 0;
        game.cursorY = 0;
        game.cursorSprite.playInvalid();
        game.padMenuUI.setPosition(Dimensions.PADMENU_X, Dimensions.PADMENU_Y);
        game.padMenuUI.clearChoices();
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_UP, PREPARATION_MENU_LAUNCH.bin());
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_DOWN, PREPARATION_MENU_EXIT.bin());
        
        if (mFromRetry)
            game.battleMode.onPreparationRetry(game);
        else
            game.battleMode.onPreparationInit(game);
        // Adjusts the type so it's valid.
        game.currentUnitType = game.battleMode.placeableUnitType(game, 0);
        updatePadMenuUnitChoices();
    }
    
    public void update()
    {
        TAZSGame game = mGame;
        
        Performances.onUpdateStart();
        
        AdvancedHiRes16Color screen = game.screen;
        
        updateInput();
        
        game.drawSceneBackground();
        renderArea(Teams.PLAYER, Colors.TEAM_PLAYER_AREA_BORDER, Colors.TEAM_PLAYER_AREA_INSIDE);
        renderArea(Teams.ENEMY, Colors.TEAM_ENEMY_AREA_BORDER, Colors.TEAM_ENEMY_AREA_INSIDE);
        game.unitsSystem.controlledUnitIdentifier = game.unitsSystem.findControlledUnitIdentifier();
        game.unitsSystem.draw(screen);
        game.unitsSystem.controlledUnitIdentifier = UnitsSystem.IDENTIFIER_NONE;
        renderUI();
        
        screen.flush();
        Performances.onFlushedScreen();
        
        Performances.onUpdateEnd();
    }
    
    public void shutdown()
    {
        mGame = null;
        
        Performances.onShutdown();
    }
 
 
    /***** PRIVATE *****/
    
    private void updateInput()
    {
        TAZSGame game = mGame;
        int oldUIMode = game.uiMode;
        
        // For animating the units inside the Unit Box.
        mUnitTimer++;
        
        // Updating the mode.
        game.uiMode = UIModes.INVALID;
        
        game.padMenuUI.setEnabledChoice(PadMenuUI.CHOICE_UP, (game.unitsSystem.unitsCount(Teams.PLAYER) > 0) && (game.unitsSystem.unitsCount(Teams.ENEMY) > 0));
        if (game.padMenuUI.update())
        {
            int selectedChoice = game.padMenuUI.selectedChoice();
            
            game.uiMode = UIModes.MENU;
            if (selectedChoice == PadMenuUI.CHOICE_UP)
            {
                game.cursorSelectSound.play();
                game.battleMode.onPreparationFinished(game);
                game.padMenuUI.hideUntilNextPress();
            }
            else if (selectedChoice == PadMenuUI.CHOICE_DOWN)
            {
                game.cursorCancelSound.play();
                game.battleMode.onPreparationExit(game);
                game.padMenuUI.hideUntilNextPress();
            }
            else if (selectedChoice == PadMenuUI.CHOICE_RIGHT)
            {
                game.cursorMoveSound.play();
                mGame.currentUnitType = mGame.battleMode.placeableUnitType(mGame, 1);
                mUnitTimer = 0;
                updatePadMenuUnitChoices();
            }
            else if (selectedChoice == PadMenuUI.CHOICE_LEFT)
            {
                game.cursorMoveSound.play();
                mGame.currentUnitType = mGame.battleMode.placeableUnitType(mGame, -1);
                mUnitTimer = 0;
                updatePadMenuUnitChoices();
            }
            game.battleMode.onPreparationMenuUpdate(game);
        }
        else
        {
            float cursorX = game.cursorX;
            float cursorY = game.cursorY;
            
            // Moving the Cursor.
            if (Button.Up.isPressed())
                cursorY = Math.max(cursorY - CURSOR_PIXELS_PER_TICK, Dimensions.PREPARATION_CURSOR_MIN_Y);
            if (Button.Down.isPressed())
                cursorY = Math.min(cursorY + CURSOR_PIXELS_PER_TICK, Dimensions.PREPARATION_CURSOR_MAX_Y);
            if (Button.Left.isPressed())
                cursorX = Math.max(cursorX - CURSOR_PIXELS_PER_TICK, Dimensions.PREPARATION_CURSOR_MIN_X);
            if (Button.Right.isPressed())
                cursorX = Math.min(cursorX + CURSOR_PIXELS_PER_TICK, Dimensions.PREPARATION_CURSOR_MAX_X);
            
            game.cursorX = cursorX;
            game.cursorY = cursorY;
            game.battleMode.onPreparationCursorUpdate(game);
        }
        // Changing the Cursor's animation.
        if (game.uiMode != oldUIMode)
        {
            if (game.uiMode == UIModes.PLACE)
                game.cursorSprite.playPlace();
            else if (game.uiMode == UIModes.REMOVE)
                game.cursorSprite.playDelete();
            else
                game.cursorSprite.playInvalid();
        }
        UITools.resetJustPressed();
    }
    
    private void renderArea(int team, int borderColor, int insideColor)
    {
        TAZSGame game = mGame;
        AdvancedHiRes16Color screen = game.screen;
        int minX = game.areaMinX(team) + 1;
        int minY = game.areaMinY(team) + 1;
        int maxX = game.areaMaxX(team) - 1;
        int maxY = game.areaMaxY(team) - 1;
        
        
        // Let's render a nice area for both team.
        
        int areaOffset = (System.currentTimeMillis() >> AREA_OFFSET_SHIFT) % Dimensions.PREPARATION_AREA_LINE_SPACE;
        int x1 = minX - 1;
        int x0 = x1 + areaOffset;
        int y0 = minY - 1;
        int y1 = y0 + areaOffset;
        
        while ((x0 < maxX) || (y0 < maxY))
        {
            screen.drawLine(x0, y0, x1, y1, insideColor, false);
            x0 += Dimensions.PREPARATION_AREA_LINE_SPACE;
            if (x0 >= maxX)
            {
                y0 += x0 - maxX;
                x0 = maxX;
            }
            y1 += Dimensions.PREPARATION_AREA_LINE_SPACE;
            if (y1 >= maxY)
            {
                x1 += y1 - maxY;
                y1 = maxY;
            }
        }
        game.screen.drawRect(minX - 1, minY - 1, maxX - minX + 2, maxY - minY + 2, borderColor, false);
    }
    
    private void renderUI()
    {
        TAZSGame game = mGame;
        AdvancedHiRes16Color screen = game.screen;
        boolean hasHoveredUnit = (game.focusedUnitIdentifier != UnitsSystem.IDENTIFIER_NONE);
        pointer unitName = MISC_UNKNOWN.bin();
        
        if (hasHoveredUnit)
            unitName =  game.unitsSystem.unitsHandlers[game.focusedUnitIdentifier].name();

        if (game.uiMode != UIModes.MENU)
            game.cursorSprite.draw(screen, game.cursorX - VideoConstants.CURSOR_ORIGIN_X, game.cursorY - VideoConstants.CURSOR_ORIGIN_Y);
        
        screen.fillRect(0, Dimensions.HELPBAR_BOX_MIN_Y, Dimensions.SCREEN_WIDTH, Dimensions.SCREEN_HEIGHT - Dimensions.HELPBAR_BOX_MIN_Y, Colors.HELP_BG);
        screen.setTextPosition(Dimensions.HELPBAR_X, Dimensions.HELPBAR_Y);
        
        if (game.uiMode == UIModes.MENU)
        {
            screen.setTextColor(Colors.HELP_ACTIVE);
            screen.printPText(BUTTON_PAD.bin());
            screen.printPText(MISC_SEPARATOR.bin());
            screen.printPText(MENU_COMMANDS_HELP.bin());
        }
        else if (game.uiMode == UIModes.REMOVE)
        {
            screen.setTextColor(Colors.HELP_ACTIVE);
            screen.printPText(BUTTON_B.bin());
            screen.printPText(MISC_SEPARATOR.bin());
            screen.printPText(PREPARATION_COMMANDS_REMOVE.bin());
            screen.printPText(MISC_BIG_SEPARATOR.bin());
            screen.printPText(BUTTON_A.bin());
            screen.printPText(MISC_SEPARATOR.bin());
            screen.printPText(PREPARATION_COMMANDS_CONTROL.bin());
        }
        else if (game.uiMode == UIModes.CANNOT_REMOVE)
        {
            screen.setTextColor(Colors.HELP_INACTIVE);
            screen.printPText(PREPARATION_COMMANDS_CANNOT_REMOVE.bin());
            screen.printPText(MISC_BIG_SEPARATOR.bin());
            screen.setTextColor(Colors.HELP_ACTIVE);
            screen.printPText(BUTTON_A.bin());
            screen.printPText(MISC_SEPARATOR.bin());
            screen.printPText(PREPARATION_COMMANDS_CONTROL.bin());
        }
        else if ((game.uiMode == UIModes.PLACE) || (game.uiMode == UIModes.NO_MORE_UNITS) || (game.uiMode == UIModes.TOO_EXPENSIVE))
        {
            screen.setTextColor((game.uiMode == UIModes.PLACE) ? Colors.HELP_ACTIVE : Colors.HELP_INACTIVE);
            screen.printPText(BUTTON_A.bin());
            screen.printPText(MISC_SEPARATOR.bin());
            screen.printPText(PREPARATION_COMMANDS_PLACE_UNIT_K.bin());
            if (game.uiMode == UIModes.NO_MORE_UNITS)
                screen.printPText(PREPARATION_COMMANDS_PLACE_INVALID_NO_MORE_FREE_UNITS.bin());
            else if (game.uiMode == UIModes.TOO_EXPENSIVE)
                screen.printPText(PREPARATION_COMMANDS_PLACE_INVALID_TOO_EXPENSIVE.bin());
            else
                screen.printPText(UnitTypes.idleHandlerForType(game.currentUnitType).name());
        }
        else
        {
            screen.setTextColor(Colors.HELP_INACTIVE);
            if (game.uiMode == UIModes.NOMANSLAND)
                screen.printPText(PREPARATION_NO_MANS_LAND.bin());
            else if (game.uiMode == UIModes.ENEMY_TERRITORY)
            {
                if (hasHoveredUnit)
                {
                    screen.printPText(TEAMS_ENEMY.bin());
                    screen.printPText(MISC_SEPARATOR.bin());
                    screen.printPText(unitName);
                }
                else
                    screen.printPText(PREPARATION_ENEMY_SIDE.bin());
            }
            else
                screen.printPText(MISC_ERROR.bin());
        }
        
        int unitTeam = game.battleMode.teamForUnitBox(game);
        
        UnitHandler unitHandler = UnitTypes.idleHandlerForType(game.currentUnitType);
        
        screen.drawWindow(Dimensions.UNITBOX_X, Dimensions.UNITBOX_Y, Dimensions.UNITBOX_WIDTH, Dimensions.UNITBOX_HEIGHT);
        if (game.uiMode == UIModes.TOO_EXPENSIVE)
            screen.setTextColor(Colors.WINDOW_TEXT_ERROR);
        else if (game.uiMode == UIModes.PLACE)
            screen.setTextColor(Colors.WINDOW_TEXT);
        else
            screen.setTextColor(Colors.WINDOW_TEXT_DISABLED);
        screen.setTextPosition(Dimensions.UNITBOX_X + 2, Dimensions.UNITBOX_Y + 2);
        screen.print(unitHandler.cost());
        screen.printBean(game.everyUISprite);
        unitHandler.drawAsUI(game.unitsSystem,
                             screen.cameraX + Dimensions.UNITBOX_UNIT_X, screen.cameraY + Dimensions.UNITBOX_UNIT_Y, Math.PI, unitTeam,
                             mUnitTimer,
                             screen);
        
        game.topBarUI.draw(game.everyUISprite, screen);
        game.padMenuUI.draw(screen);
        game.drawUnitUI(game.unitsSystem.findControlledUnitIdentifier());
    }
    
    private void updatePadMenuUnitChoices()
    {
        int previousUnitType = mGame.battleMode.placeableUnitType(mGame, -1);
        int nextUnitType = mGame.battleMode.placeableUnitType(mGame, 1);
        
        mGame.padMenuUI.setChoice(PadMenuUI.CHOICE_RIGHT, UnitTypes.idleHandlerForType(nextUnitType).name());
        mGame.padMenuUI.setChoice(PadMenuUI.CHOICE_LEFT, UnitTypes.idleHandlerForType(previousUnitType).name());
    }
    
    
    private TAZSGame mGame;
    private boolean mFromRetry;
    private int mUnitTimer;

    private static final float CURSOR_PIXELS_PER_TICK = 2.f;
    private static final int AREA_OFFSET_SHIFT = 7;
}