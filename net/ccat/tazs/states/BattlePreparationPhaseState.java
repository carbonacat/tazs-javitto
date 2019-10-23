package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.mode.HiRes16Color;
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
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;
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
        TAZSGame game = mGame;
        
        game.unitsSystem.clear();
        game.moveCamera(0, 0);
        game.cursorX = 0;
        game.cursorY = 0;
        game.cursorSprite.playInvalid();
        game.padMenuUI.setPosition(Dimensions.PADMENU_X, Dimensions.PADMENU_Y);
        game.padMenuUI.clearChoices();
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_UP, Texts.PREPARATION_MENU_LAUNCH);
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_DOWN, Texts.PREPARATION_MENU_EXIT);
        
        if (mFromRetry)
            game.battleMode.onPreparationRetry(game);
        else
            game.battleMode.onPreparationInit(game);
        updatePadMenuUnitChoices();
    }
    
    public void update()
    {
        HiRes16Color screen = mGame.screen;
        
        updateInput();
        
        mGame.drawSceneBackground();
        renderArea(Teams.PLAYER, Colors.TEAM_PLAYER_AREA_BORDER, Colors.TEAM_PLAYER_AREA_INSIDE);
        renderArea(Teams.ENEMY, Colors.TEAM_ENEMY_AREA_BORDER, Colors.TEAM_ENEMY_AREA_INSIDE);
        mGame.unitsSystem.draw(screen);
        renderUI();
        screen.flush();
    }
    
    public void shutdown()
    {
        mGame = null;
    }
 
 
    /***** PRIVATE *****/
    
    private void updateInput()
    {
        TAZSGame game = mGame;
        int oldUIMode = game.uiMode;
        
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
                changeCurrentUnit(1);
            }
            else if (selectedChoice == PadMenuUI.CHOICE_LEFT)
            {
                game.cursorMoveSound.play();
                changeCurrentUnit(-1);
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
        HiRes16Color screen = game.screen;
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
        HiRes16Color screen = game.screen;
        boolean hasHoveredUnit = (game.focusedUnitIdentifier != UnitsSystem.IDENTIFIER_NONE);
        String unitName = Texts.MISC_UNKNOWN;
        
        if (hasHoveredUnit)
            unitName =  game.unitsSystem.unitsHandlers[game.focusedUnitIdentifier].name();

        if (game.uiMode != UIModes.MENU)
            game.cursorSprite.draw(screen, game.cursorX - VideoConstants.CURSOR_ORIGIN_X, game.cursorY - VideoConstants.CURSOR_ORIGIN_Y);
        
        screen.fillRect(0, Dimensions.HELPBAR_BOX_MIN_Y, Dimensions.SCREEN_WIDTH, Dimensions.SCREEN_HEIGHT - Dimensions.HELPBAR_BOX_MIN_Y, Colors.HELP_BG);
        screen.setTextPosition(Dimensions.HELPBAR_X, Dimensions.HELPBAR_Y);
        
        if (game.uiMode == UIModes.MENU)
        {
            screen.setTextColor(Colors.HELP_ACTIVE);
            screen.print(Texts.BUTTON_PAD);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.MENU_COMMANDS_HELP);
        }
        else if (game.uiMode == UIModes.REMOVE)
        {
            screen.setTextColor(hasHoveredUnit ? Colors.HELP_ACTIVE : Colors.HELP_INACTIVE);
            screen.print(Texts.BUTTON_B);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.PREPARATION_COMMANDS_REMOVE);
            screen.print(Texts.MISC_BIG_SEPARATOR);
            screen.print(Texts.BUTTON_A);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.PREPARATION_COMMANDS_CONTROL);
        }
        else if ((game.uiMode == UIModes.PLACE) || (game.uiMode == UIModes.NO_MORE_UNITS) || (game.uiMode == UIModes.TOO_EXPENSIVE))
        {
            screen.setTextColor((game.uiMode == UIModes.PLACE) ? Colors.HELP_ACTIVE : Colors.HELP_INACTIVE);
            screen.print(Texts.BUTTON_A);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.PREPARATION_COMMANDS_PLACE_UNIT_K);
            if (game.uiMode == UIModes.NO_MORE_UNITS)
                screen.print(Texts.PREPARATION_COMMANDS_PLACE_INVALID_NO_MORE_FREE_UNITS);
            else if (game.uiMode == UIModes.TOO_EXPENSIVE)
                screen.print(Texts.PREPARATION_COMMANDS_PLACE_INVALID_TOO_EXPENSIVE);
            else
                screen.print(UnitTypes.idleHandlerForType(game.currentUnitType).name());
        }
        else
        {
            screen.setTextColor(Colors.HELP_INACTIVE);
            if (game.uiMode == UIModes.NOMANSLAND)
                screen.print(Texts.PREPARATION_NO_MANS_LAND);
            else if (game.uiMode == UIModes.ENEMY_TERRITORY)
            {
                if (hasHoveredUnit)
                {
                    screen.print(Texts.TEAMS_ENEMY);
                    screen.print(Texts.MISC_SEPARATOR);
                    screen.print(unitName);
                }
                else
                    screen.print(Texts.PREPARATION_ENEMY_SIDE);
            }
            else
                screen.print(Texts.MISC_ERROR);
        }
        
        int unitTeam = game.battleMode.teamForUnitBox(game);
        
        UnitHandler unitHandler = UnitTypes.idleHandlerForType(game.currentUnitType);
        
        UITools.drawWindow(Dimensions.UNITBOX_X, Dimensions.UNITBOX_Y, Dimensions.UNITBOX_WIDTH, Dimensions.UNITBOX_HEIGHT, screen);
        if (game.uiMode == UIModes.TOO_EXPENSIVE)
            screen.setTextColor(Colors.WINDOW_TEXT_ERROR);
        else if (game.uiMode == UIModes.PLACE)
            screen.setTextColor(Colors.WINDOW_TEXT);
        else
            screen.setTextColor(Colors.WINDOW_TEXT_DISABLED);
        screen.setTextPosition(Dimensions.UNITBOX_X + 2, Dimensions.UNITBOX_Y + 2);
        screen.print(unitHandler.cost());
        screen.print(Texts.MISC_DOLLAR);
        unitHandler.drawAsUI(game.unitsSystem,
                             screen.cameraX + Dimensions.UNITBOX_UNIT_X, screen.cameraY + Dimensions.UNITBOX_UNIT_Y, Math.PI, unitTeam,
                             screen);
        
        game.topBarUI.draw(screen);
        game.padMenuUI.draw(screen);
    }
    
    private void changeCurrentUnit(int delta)
    {
        mGame.currentUnitType = (mGame.currentUnitType + delta + UnitTypes.END) % UnitTypes.END;
        updatePadMenuUnitChoices();
    }
    
    private void updatePadMenuUnitChoices()
    {
        int previousUnitType = (mGame.currentUnitType - 1 + UnitTypes.END) % UnitTypes.END;
        int nextUnitType = (mGame.currentUnitType + 1 + UnitTypes.END) % UnitTypes.END;
        
        mGame.padMenuUI.setChoice(PadMenuUI.CHOICE_RIGHT, UnitTypes.idleHandlerForType(nextUnitType).name());
        mGame.padMenuUI.setChoice(PadMenuUI.CHOICE_LEFT, UnitTypes.idleHandlerForType(previousUnitType).name());
    }
    
    
    private TAZSGame mGame;
    private boolean mFromRetry;

    private static final float CURSOR_PIXELS_PER_TICK = 2.f;
    private static final int AREA_OFFSET_SHIFT = 7;
}