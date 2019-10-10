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
    public BattlePreparationPhaseState(TAZSGame game)
    {
        mGame = game;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void init()
    {
        TAZSGame game = mGame;
        
        game.unitsSystem.clear();
        game.screen.cameraX = -game.screen.width() * 0.5;
        game.screen.cameraY = -game.screen.height() * 0.5;
        game.cursorX = 0;
        game.cursorY = 0;
        game.cursorSprite.playInvalid();
        game.padMenuUI.setPosition(MENU_X, MENU_Y);
        game.padMenuUI.setShown(false);
        game.padMenuUI.clearChoices();
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_UP, Texts.PREPARATION_MENU_LAUNCH);
        game.padMenuUI.setChoice(PadMenuUI.CHOICE_DOWN, Texts.PREPARATION_MENU_EXIT);
        
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
        game.padMenuUI.update();
        if (game.padMenuUI.isShown())
        {
            game.uiMode = UIModes.MENU;
            switch (game.padMenuUI.selectedChoice())
            {
            case PadMenuUI.CHOICE_UP:
                game.cursorSelectSound.play();
                Game.changeState(new BattlePhaseState(game));
                break ;
            case PadMenuUI.CHOICE_DOWN:
                game.cursorCancelSound.play();
                Game.changeState(new TitleScreenState(game));
                break ;
            case PadMenuUI.CHOICE_RIGHT:
                changeCurrentUnit(1);
                break ;
            case PadMenuUI.CHOICE_LEFT:
                changeCurrentUnit(-1);
                break ;
            }
            game.battleMode.onPreparationMenuUpdate(game);
        }
        else
        {
            float cursorX = game.cursorX;
            float cursorY = game.cursorY;
            
            // Moving the Cursor.
            if (Button.Up.isPressed())
                cursorY = Math.max(cursorY - CURSOR_PIXELS_PER_TICK, game.sceneYMin);
            if (Button.Down.isPressed())
                cursorY = Math.min(cursorY + CURSOR_PIXELS_PER_TICK, game.sceneYMax);
            if (Button.Left.isPressed())
                cursorX = Math.max(cursorX - CURSOR_PIXELS_PER_TICK, game.sceneXMin);
            if (Button.Right.isPressed())
                cursorX = Math.min(cursorX + CURSOR_PIXELS_PER_TICK, game.sceneXMax);
            
            game.cursorX = cursorX;
            game.cursorY = cursorY;
            game.battleMode.onPreparationCursorUpdate(game);
        }
        // Changing the Cursor's animation.
        if (game.uiMode != oldUIMode)
        {
            switch (game.uiMode)
            {
            case UIModes.PLACE:
                game.cursorSprite.playPlace();
                break;
            case UIModes.REMOVE:
                game.cursorSprite.playDelete();
                break;
            case UIModes.INVALID:
            case UIModes.ENEMY_TERRITORY:
            case UIModes.NO_MORE_UNITS:
            default:
                game.cursorSprite.playInvalid();
                break;
            }
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
        
        if (game.padMenuUI.isShown())
            UITools.fillRectBlended(0, 0, screen.width(), HELP_BOX_MIN_Y - 1,
                                    Colors.PADMENU_OVERLAY, 0,
                                    UITools.PATTERN_25_75_HEX,
                                    screen);
        screen.fillRect(0, HELP_BOX_MIN_Y, game.screen.width(), game.screen.height() - HELP_BOX_MIN_Y, Colors.HELP_BG);
        screen.setTextPosition(HELP_X, HELP_Y);
        
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
        
        UITools.drawWindow(UNITBOX_X, UNITBOX_Y, UNITBOX_WIDTH, UNITBOX_HEIGHT, screen);
        if (game.uiMode == UIModes.TOO_EXPENSIVE)
            screen.setTextColor(Colors.WINDOW_TEXT_ERROR);
        else if (game.uiMode == UIModes.PLACE)
            screen.setTextColor(Colors.WINDOW_TEXT);
        else
            screen.setTextColor(Colors.WINDOW_TEXT_DISABLED);
        screen.setTextPosition(UNITBOX_X + 2, UNITBOX_Y + 2);
        screen.print(unitHandler.cost());
        screen.print(Texts.MISC_DOLLAR);
        unitHandler.drawAsUI(game.unitsSystem,
                             screen.cameraX + UNITBOX_UNIT_X, screen.cameraY + UNITBOX_UNIT_Y, Math.PI, unitTeam,
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

    private static final float CURSOR_PIXELS_PER_TICK = 2.f;
    
    // TODO: This is common to a lot of things. [012]
    private static final int HELP_BOX_MIN_Y = 176 - 2 - 6 - 2;
    private static final int HELP_X = 2;
    private static final int HELP_Y = HELP_BOX_MIN_Y + 2;
    private static final int UNITBOX_WIDTH = 50;
    private static final int UNITBOX_HEIGHT = 10;
    // TODO: Use Screen's constants.
    private static final int UNITBOX_X = 220 - UNITBOX_WIDTH;
    private static final int UNITBOX_Y = 176 - UNITBOX_HEIGHT;
    private static final int UNITBOX_UNIT_X = 220 - 8;
    private static final int UNITBOX_UNIT_Y = 176 - 5;

    private static final int MENU_X = 110;
    private static final int MENU_Y = 88;
}