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
        game.screen.cameraX = -Dimensions.SCREEN_WIDTH * 0.5;
        game.screen.cameraY = -Dimensions.SCREEN_HEIGHT * 0.5;
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
        
        screen.clear(Colors.SCENE_BG);
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
            screen.print(Texts.PREPARATION_COMMANDS_REMOVE_UNIT_K);
            screen.print(unitName);
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
                screen.print(Texts.UNIT_BRAWLER);
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
    boolean mFromRetry;

    private static final float CURSOR_PIXELS_PER_TICK = 2.f;
}