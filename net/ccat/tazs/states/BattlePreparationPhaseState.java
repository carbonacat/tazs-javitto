package net.ccat.tazs.states;

import femto.input.Button;
import femto.mode.HiRes16Color;
import femto.State;

import net.ccat.tazs.battle.handlers.BrawlerIdleHandler;
import net.ccat.tazs.battle.UnitsSystem;
import net.ccat.tazs.resources.Colors;
import net.ccat.tazs.resources.sprites.CursorSprite;
import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.resources.VideoConstants;
import net.ccat.tazs.tools.MathTools;


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
        mGame.unitsSystem.clear();
        
        // TODO: Eventually will be setup with a proper battle plan.
        for (int i = 0; i < 10; i++)
            mGame.unitsSystem.addUnit((Math.random() - 0.0f) * 100, (Math.random() - 0.5f) * 80,
                                      Math.PI,
                                      BrawlerIdleHandler.ennemyInstance) != battle.UnitsSystem.IDENTIFIER_NONE;
        mGame.screen.cameraX = -mGame.screen.width() * 0.5;
        mGame.screen.cameraY = -mGame.screen.height() * 0.5;
        mCursorX = 0;
        mCursorY = 0;
        mGame.cursorSprite.playInvalid();
        mGame.padMenuUI.setPosition(MENU_X, MENU_Y);
        mGame.padMenuUI.setShown(false);
    }
    
    public void update()
    {
        HiRes16Color screen = mGame.screen;
        
        updateInput();
        
        screen.clear(Colors.SCENE_BG_COLOR);
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
        // Updating the mode.
        int newMode = MODE_INVALID;
        
        mGame.padMenuUI.update();
        if (mGame.padMenuUI.isShown())
            newMode = MODE_MENU;
        else
        {
            // Moving the Cursor.
            if (Button.Up.isPressed())
                mCursorY = Math.max(mCursorY - CURSOR_PIXELS_PER_TICK, mGame.sceneYMin);
            if (Button.Down.isPressed())
                mCursorY = Math.min(mCursorY + CURSOR_PIXELS_PER_TICK, mGame.sceneYMax);
            if (Button.Left.isPressed())
                mCursorX = Math.max(mCursorX - CURSOR_PIXELS_PER_TICK, mGame.sceneXMin);
            if (Button.Right.isPressed())
                mCursorX = Math.min(mCursorX + CURSOR_PIXELS_PER_TICK, mGame.sceneXMax);
            
            // Finding a Unit that is hovered.
            int hoveredUnitIdentifier = mGame.unitsSystem.findUnit(mCursorX, mCursorY);
            
            if ((hoveredUnitIdentifier != UnitsSystem.IDENTIFIER_NONE) && (mGame.unitsSystem.unitsHandlers[hoveredUnitIdentifier].isAllied()))
            {
                newMode = MODE_REMOVE;
                if (Button.B.isPressed())
                {
                    mGame.unitsSystem.removeUnit(hoveredUnitIdentifier);
                    mHoveredUnitIdentifier = UnitsSystem.IDENTIFIER_NONE;
                }
                else
                    mHoveredUnitIdentifier = hoveredUnitIdentifier;
            }
            else if (mCursorX < 0)
            {
                if (Button.A.isPressed())
                {
                    mGame.unitsSystem.addUnit(mCursorX, mCursorY, 0, BrawlerIdleHandler.alliedInstance);
                    // Resets the animation.
                    mGame.cursorSprite.currentFrame = mGame.cursorSprite.startFrame;
                }
                if (mGame.unitsSystem.freeUnits() > 0)
                    newMode = MODE_PLACE;
                else
                    newMode = MODE_NO_MORE_UNITS;
            }
            else
                newMode = MODE_ENEMY_TERRITORY;
        }
        // Changing the Cursor's animation.
        if (newMode != mMode)
        {
            mMode = newMode;
            switch (mMode)
            {
            case MODE_PLACE:
                mGame.cursorSprite.playPlace();
                break;
            case MODE_REMOVE:
                mGame.cursorSprite.playDelete();
                break;
            case MODE_INVALID:
            case MODE_ENEMY_TERRITORY:
            case MODE_NO_MORE_UNITS:
            default:
                mGame.cursorSprite.playInvalid();
                break;
            }
        }
    }
    
    private void renderUI()
    {
        HiRes16Color screen = mGame.screen;
        
        if (mMode != MODE_MENU)
            mGame.cursorSprite.draw(screen, mCursorX - VideoConstants.CURSOR_ORIGIN_X, mCursorY - VideoConstants.CURSOR_ORIGIN_Y);
        
        screen.fillRect(0, HELP_BOX_MIN_Y, mGame.screen.width(), mGame.screen.height() - HELP_BOX_MIN_Y, Colors.PREPARATION_HELP_BG);
        screen.setTextPosition(HELP_X, HELP_Y);
        
        if (mMode == MODE_MENU)
        {
            mGame.padMenuUI.draw(mGame.screen);
            screen.setTextColor(Colors.PREPARATION_HELP_ACTIVE);
            screen.print(Texts.BUTTON_PAD);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.MENU_COMMANDS_HELP);
        }
        else if (mMode == MODE_REMOVE)
        {
            boolean hasHoveredUnit = (mHoveredUnitIdentifier != UnitsSystem.IDENTIFIER_NONE);
            
            screen.setTextColor(hasHoveredUnit ? Colors.PREPARATION_HELP_ACTIVE : Colors.PREPARATION_HELP_INACTIVE);
            screen.print(Texts.BUTTON_B);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.HELP_COMMANDS_REMOVE_UNIT_X);
            // TODO: Use the actual Unit's type's name.
            screen.print(hasHoveredUnit ? Texts.UNITS_BRAWLER_NAME : Texts.UNITS_UNKNOWN_NAME);
        }
        else if ((mMode == MODE_PLACE) || (mMode == MODE_NO_MORE_UNITS))
        {
            screen.setTextColor((mMode == MODE_PLACE) ? Colors.PREPARATION_HELP_ACTIVE : Colors.PREPARATION_HELP_INACTIVE);
            screen.print(Texts.BUTTON_A);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.HELP_COMMANDS_PLACE_UNIT_X);
            // TODO: Use the selected type's name.
            if (mMode == MODE_NO_MORE_UNITS)
                screen.print(Texts.HELP_COMMANDS_PLACE_INVALID_NO_MORE_FREE_UNITS);
            else
                screen.print(Texts.UNITS_BRAWLER_NAME);
        }
        else
        {
            screen.setTextColor(Colors.PREPARATION_HELP_INACTIVE);
            if (mMode == MODE_ENEMY_TERRITORY)
                screen.print(Texts.HELP_ENEMY_SIDE);
            else
                screen.print(Texts.MISC_ERROR);
        }
    }
    
    private TAZSGame mGame;
    
    private float mCursorX;
    private float mCursorY;
    private int mMode = MODE_INVALID;
    private int mHoveredUnitIdentifier = UnitsSystem.IDENTIFIER_NONE;
    
    private static final float CURSOR_PIXELS_PER_TICK = 2.f;
    
    private static final int MODE_INVALID = 0;
    private static final int MODE_ENEMY_TERRITORY = 1;
    private static final int MODE_PLACE = 2;
    private static final int MODE_NO_MORE_UNITS = 3;
    private static final int MODE_REMOVE = 4;
    private static final int MODE_MENU = 5;
    
    private static final int HELP_BOX_MIN_Y = 176 - 2 - 6 - 2;
    private static final int HELP_X = 2;
    private static final int HELP_Y = HELP_BOX_MIN_Y + 2;
    private static final int MENU_X = 110;
    private static final int MENU_Y = 88;
}