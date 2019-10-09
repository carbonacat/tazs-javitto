package net.ccat.tazs.states;

import femto.Game;
import femto.input.Button;
import femto.mode.HiRes16Color;
import femto.State;

import net.ccat.tazs.battle.handlers.brawler.BrawlerIdleHandler;
import net.ccat.tazs.battle.handlers.slapper.SlapperIdleHandler;
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
import net.ccat.tazs.ui.UITools;


/**
 * Handles the Battle Preparation Phase, where the Player sets up their armies.
 */
public class BattlePreparationPhaseState
    extends State
{
    // Launches a random, quick battle.
    public static final int GAMEMODE_QUICKBATTLE = 0;
    // Opens an empty battlefield, allowing the Player to add and remove Units on/from every side.
    public static final int GAMEMODE_SANDBOX = 1;
    
    
    public BattlePreparationPhaseState(TAZSGame game, int gameMode)
    {
        mGame = game;
        mGameMode = gameMode;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void init()
    {
        mGame.unitsSystem.clear();
        if (mGameMode == GAMEMODE_QUICKBATTLE)
        {
            // TODO: Eventually will be setup with a proper battle plan. [014]
            for (int remainingCluster = Math.random(1, 16); remainingCluster > 0 ; remainingCluster--)
            {
                float clusterX = 60 + (Math.random() - 0.5f) * 80;
                float clusterY = (Math.random() - 0.5f) * 80;
                
                for (int remainingUnit = Math.random(1, 4); remainingUnit > 0 ; remainingUnit--)
                {
                    mGame.unitsSystem.addUnit(clusterX + (Math.random() - 0.5f) * 20, clusterY + (Math.random() - 0.5f) * 20,
                                              Math.PI,
                                              BrawlerIdleHandler.instance,
                                              Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
                    mEnemyUnitsCount++;
                    mEnemyUnitsCost += BrawlerIdleHandler.instance.cost();
                }
                for (int remainingUnit = Math.random(0, 4); remainingUnit > 0 ; remainingUnit--)
                {
                    mGame.unitsSystem.addUnit(clusterX + (Math.random() - 0.5f) * 20, clusterY + (Math.random() - 0.5f) * 20,
                                              Math.PI,
                                              SlapperIdleHandler.instance,
                                              Teams.ENEMY) != battle.UnitsSystem.IDENTIFIER_NONE;
                    mEnemyUnitsCount++;
                    mEnemyUnitsCost += SlapperIdleHandler.instance.cost();
                }
            }
        }
        mGame.screen.cameraX = -mGame.screen.width() * 0.5;
        mGame.screen.cameraY = -mGame.screen.height() * 0.5;
        mCursorX = 0;
        mCursorY = 0;
        mGame.cursorSprite.playInvalid();
        mGame.padMenuUI.setPosition(MENU_X, MENU_Y);
        mGame.padMenuUI.setShown(false);
        mGame.padMenuUI.clearChoices();
        mGame.padMenuUI.setChoice(PadMenuUI.CHOICE_UP, Texts.PREPARATION_MENU_LAUNCH);
        mGame.padMenuUI.setChoice(PadMenuUI.CHOICE_DOWN, Texts.PREPARATION_MENU_EXIT);
        updatePadMenuUnitChoices();
        refreshTopBar();
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
        // Updating the mode.
        int newMode = MODE_INVALID;
        
        mGame.padMenuUI.setEnabledChoice(PadMenuUI.CHOICE_UP, (mAlliedUnitsCount > 0) && (mEnemyUnitsCount > 0));
        mGame.padMenuUI.update();
        if (mGame.padMenuUI.isShown())
        {
            newMode = MODE_MENU;
            switch (mGame.padMenuUI.selectedChoice())
            {
            case PadMenuUI.CHOICE_UP:
                Game.changeState(new BattlePhaseState(mGame));
                break ;
            case PadMenuUI.CHOICE_DOWN:
                Game.changeState(new TitleScreenState(mGame));
                break ;
            case PadMenuUI.CHOICE_RIGHT:
                changeCurrentUnit(1);
                break ;
            case PadMenuUI.CHOICE_LEFT:
                changeCurrentUnit(-1);
                break ;
            }
        }
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
            
            if (mGameMode == GAMEMODE_QUICKBATTLE)
                newMode = updateModeForQuickBattle();
            else if (mGameMode == GAMEMODE_SANDBOX)
                newMode = updateModeForSandbox();
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
    
    private int updateModeForQuickBattle()
    {
        // Finding a Unit that is hovered.
        mHoveredUnitIdentifier = mGame.unitsSystem.findUnit(mCursorX, mCursorY);
        
        if ((mHoveredUnitIdentifier != UnitsSystem.IDENTIFIER_NONE) && (mGame.unitsSystem.unitsTeams[mHoveredUnitIdentifier] == Teams.PLAYER))
        {
            if (Button.B.isPressed())
            {
                UnitHandler unitHandler = mGame.unitsSystem.unitsHandlers[mHoveredUnitIdentifier];
                
                mGame.unitsSystem.removeUnit(mHoveredUnitIdentifier);
                mAlliedUnitsCount--;
                mAlliedUnitsCost -= unitHandler.cost();
                refreshTopBar();
                mHoveredUnitIdentifier = UnitsSystem.IDENTIFIER_NONE;
            }
            return MODE_REMOVE;
        }
        else if (mCursorX < -NOMANSLAND_RADIUS)
        {
            if (Button.A.isPressed())
            {
                UnitHandler unitHandler = UnitTypes.idleHandlerForType(mCurrentUnitType);
                
                mGame.unitsSystem.addUnit(mCursorX, mCursorY, 0,
                                          unitHandler,
                                          Teams.PLAYER);
                mAlliedUnitsCount++;
                mAlliedUnitsCost += unitHandler.cost();
                refreshTopBar();
                
                // Resets the animation.
                mGame.cursorSprite.currentFrame = mGame.cursorSprite.startFrame;
            }
            if (mGame.unitsSystem.freeUnits() > 0)
                return MODE_PLACE;
            return MODE_NO_MORE_UNITS;
        }
        else if (mCursorX > NOMANSLAND_RADIUS)
            return MODE_ENEMY_TERRITORY;
        return MODE_NOMANSLAND;
    }
    
    private int updateModeForSandbox()
    {
        // Finding a Unit that is hovered.
        int hoveredUnitIdentifier = mGame.unitsSystem.findUnit(mCursorX, mCursorY);
        
        if (hoveredUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
        {
            if (Button.B.isPressed())
            {
                int team =  mGame.unitsSystem.unitsTeams[hoveredUnitIdentifier];
                UnitHandler unitHandler = mGame.unitsSystem.unitsHandlers[mHoveredUnitIdentifier];
                
                mGame.unitsSystem.removeUnit(hoveredUnitIdentifier);
                if (team == Teams.PLAYER)
                {
                    mAlliedUnitsCount--;
                    mAlliedUnitsCost -= unitHandler.cost();
                }
                else if (team == Teams.ENEMY)
                {
                    mEnemyUnitsCount--;
                    mEnemyUnitsCost -= unitHandler.cost();
                }
                refreshTopBar();
                mHoveredUnitIdentifier = UnitsSystem.IDENTIFIER_NONE;
            }
            else
                mHoveredUnitIdentifier = hoveredUnitIdentifier;
            return MODE_REMOVE;
        }
        else if ((mCursorX < -NOMANSLAND_RADIUS) || (mCursorX > NOMANSLAND_RADIUS))
        {
            if (Button.A.isPressed())
            {
                boolean onPlayerTeam = (mCursorX < 0);
                int team = onPlayerTeam ? Teams.PLAYER : Teams.ENEMY;
                float angle = onPlayerTeam ? 0 : Math.PI;
                UnitHandler initialHandler = UnitTypes.idleHandlerForType(mCurrentUnitType);
                
                mGame.unitsSystem.addUnit(mCursorX, mCursorY, angle,
                                          initialHandler,
                                          team);
                if (onPlayerTeam)
                {
                    mAlliedUnitsCount++;
                    mAlliedUnitsCost += initialHandler.cost();
                }
                else
                {
                    mEnemyUnitsCount++;
                    mEnemyUnitsCost += initialHandler.cost();
                }
                refreshTopBar();
                // Resets the animation.
                mGame.cursorSprite.currentFrame = mGame.cursorSprite.startFrame;
            }
            if (mGame.unitsSystem.freeUnits() > 0)
                return MODE_PLACE;
            return MODE_NO_MORE_UNITS;
        }
        return MODE_NOMANSLAND;
    }
    
    private void renderUI()
    {
        HiRes16Color screen = mGame.screen;
        boolean hasHoveredUnit = (mHoveredUnitIdentifier != UnitsSystem.IDENTIFIER_NONE);
        String unitName = Texts.MISC_UNKNOWN;
        
        if (hasHoveredUnit)
            unitName =  mGame.unitsSystem.unitsHandlers[mHoveredUnitIdentifier].name();

        if (mMode != MODE_MENU)
            mGame.cursorSprite.draw(screen, mCursorX - VideoConstants.CURSOR_ORIGIN_X, mCursorY - VideoConstants.CURSOR_ORIGIN_Y);
        
        if (mGame.padMenuUI.isShown())
            UITools.fillRectBlended(0, 0, screen.width(), HELP_BOX_MIN_Y - 1,
                                    Colors.PADMENU_OVERLAY, 0,
                                    UITools.PATTERN_25_75_HEX,
                                    screen);
        screen.fillRect(0, HELP_BOX_MIN_Y, mGame.screen.width(), mGame.screen.height() - HELP_BOX_MIN_Y, Colors.HELP_BG);
        screen.setTextPosition(HELP_X, HELP_Y);
        
        if (mMode == MODE_MENU)
        {
            screen.setTextColor(Colors.HELP_ACTIVE);
            screen.print(Texts.BUTTON_PAD);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.MENU_COMMANDS_HELP);
        }
        else if (mMode == MODE_REMOVE)
        {
            screen.setTextColor(hasHoveredUnit ? Colors.HELP_ACTIVE : Colors.HELP_INACTIVE);
            screen.print(Texts.BUTTON_B);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.PREPARATION_COMMANDS_REMOVE_UNIT_K);
            screen.print(unitName);
        }
        else if ((mMode == MODE_PLACE) || (mMode == MODE_NO_MORE_UNITS))
        {
            screen.setTextColor((mMode == MODE_PLACE) ? Colors.HELP_ACTIVE : Colors.HELP_INACTIVE);
            screen.print(Texts.BUTTON_A);
            screen.print(Texts.MISC_SEPARATOR);
            screen.print(Texts.PREPARATION_COMMANDS_PLACE_UNIT_K);
            if (mMode == MODE_NO_MORE_UNITS)
                screen.print(Texts.PREPARATION_COMMANDS_PLACE_INVALID_NO_MORE_FREE_UNITS);
            else
                screen.print(Texts.UNIT_BRAWLER);
        }
        else
        {
            screen.setTextColor(Colors.HELP_INACTIVE);
            if (mMode == MODE_NOMANSLAND)
                screen.print(Texts.PREPARATION_NO_MANS_LAND);
            else if (mMode == MODE_ENEMY_TERRITORY)
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
        
        // Renders the Unit Box.
        int unitTeam = Teams.PLAYER;
        
        if ((mGameMode == GAMEMODE_SANDBOX) && (mCursorX > 0))
            unitTeam = Teams.ENEMY;
        
        UnitHandler unitHandler = UnitTypes.idleHandlerForType(mCurrentUnitType);
        
        UITools.drawWindow(UNITBOX_X, UNITBOX_Y, UNITBOX_WIDTH, UNITBOX_HEIGHT, screen);
        screen.setTextColor(Colors.WINDOW_TEXT);
        screen.setTextPosition(UNITBOX_X + 2, UNITBOX_Y + 2);
        screen.print(unitHandler.cost());
        screen.print(Texts.MISC_DOLLAR);
        UnitTypes.idleHandlerForType(mCurrentUnitType).drawAsUI(mGame.unitsSystem,
                                                                screen.cameraX + UNITBOX_UNIT_X, screen.cameraY + UNITBOX_UNIT_Y, Math.PI, unitTeam,
                                                                screen);
        
        mGame.topBarUI.draw(screen);
        mGame.padMenuUI.draw(screen);
    }
    
    private void refreshTopBar()
    {
        if (mGameMode == GAMEMODE_SANDBOX)
        {
            mGame.topBarUI.setLeftCountAndCost(Texts.TEAMS_LEFT, mAlliedUnitsCount, mAlliedUnitsCost);
            mGame.topBarUI.setRightCountAndCost(Texts.TEAMS_RIGHT, mEnemyUnitsCount, mEnemyUnitsCost);
        }
        else if (mGameMode == GAMEMODE_QUICKBATTLE)
        {
            mGame.topBarUI.setLeftCountAndCost(Texts.TEAMS_PLAYER, mAlliedUnitsCount, mAlliedUnitsCost);
            mGame.topBarUI.setRightCountAndCost(Texts.TEAMS_ENEMY, mEnemyUnitsCount, mEnemyUnitsCost);
        }
        else
        {
            mGame.topBarUI.setLeftCountAndCost(Texts.MISC_UNKNOWN, 0, 0);
            mGame.topBarUI.setRightCountAndCost(Texts.MISC_UNKNOWN, 0, 0);
        }
    }
    
    private void changeCurrentUnit(int delta)
    {
        mCurrentUnitType = (mCurrentUnitType + delta + UnitTypes.END) % UnitTypes.END;
        updatePadMenuUnitChoices();
    }
    
    private void updatePadMenuUnitChoices()
    {
        int previousUnitType = (mCurrentUnitType - 1 + UnitTypes.END) % UnitTypes.END;
        int nextUnitType = (mCurrentUnitType + 1 + UnitTypes.END) % UnitTypes.END;
        
        mGame.padMenuUI.setChoice(PadMenuUI.CHOICE_RIGHT, UnitTypes.idleHandlerForType(nextUnitType).name());
        mGame.padMenuUI.setChoice(PadMenuUI.CHOICE_LEFT, UnitTypes.idleHandlerForType(previousUnitType).name());
    }
    
    
    private TAZSGame mGame;
    private int mGameMode = GAMEMODE_QUICKBATTLE;
    
    private int mCurrentUnitType = UnitTypes.SLAPPER;
    
    private float mCursorX;
    private float mCursorY;
    private int mMode = MODE_INVALID;
    private int mHoveredUnitIdentifier = UnitsSystem.IDENTIFIER_NONE;
    private int mAlliedUnitsCount = 0;
    private int mAlliedUnitsCost = 0;
    private int mEnemyUnitsCount = 0;
    private int mEnemyUnitsCost = 0;
    
    private static final float CURSOR_PIXELS_PER_TICK = 2.f;
    
    private static final float NOMANSLAND_RADIUS = 5;
    
    private static final int MODE_INVALID = 0;
    private static final int MODE_ENEMY_TERRITORY = 1;
    private static final int MODE_PLACE = 2;
    private static final int MODE_NO_MORE_UNITS = 3;
    private static final int MODE_REMOVE = 4;
    private static final int MODE_MENU = 5;
    private static final int MODE_NOMANSLAND = 6;
    
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