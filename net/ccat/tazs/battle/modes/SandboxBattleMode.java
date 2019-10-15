package net.ccat.tazs.battle.modes;

import femto.input.Button;

import net.ccat.tazs.resources.Texts;
import net.ccat.tazs.states.BattlePreparationPhaseState;
import net.ccat.tazs.ui.UIModes;


/**
 * A Battle where the Player can add anything they want on both side, then watch things unfold.
 */
public class SandboxBattleMode
    extends BattleMode
{
    /***** PREPARATION *****/
    
    public void onPreparationInit(TAZSGame game)
    {
        updateTopBarUI(game);
    }
    
    public void onPreparationCursorUpdate(TAZSGame game)
    {
        // Finding a Unit that is hovered.
        game.focusedUnitIdentifier = game.unitsSystem.findUnit(game.cursorX, game.cursorY);
        
        if (game.focusedUnitIdentifier != UnitsSystem.IDENTIFIER_NONE)
        {
            if (Button.B.isPressed())
            {
                UnitHandler unitHandler = game.unitsSystem.unitsHandlers[game.focusedUnitIdentifier];
                
                game.unitsSystem.removeUnit(game.focusedUnitIdentifier);
                updateTopBarUI(game);
                game.focusedUnitIdentifier = UnitsSystem.IDENTIFIER_NONE;
            }
            game.uiMode = UIModes.REMOVE;
        }
        else
        {
            int teamUnderCursor = game.areaTeamAtPosition((int)game.cursorX, (int)game.cursorY);
            
            if (teamUnderCursor != Teams.NONE)
            {
                if (Button.A.isPressed())
                {
                    // That'll be good until we have to move the area someplace else.
                    float angle = (teamUnderCursor == Teams.PLAYER) ? 0 : Math.PI;
                    UnitHandler initialHandler = UnitTypes.idleHandlerForType(game.currentUnitType);
                    
                    if (game.unitsSystem.addUnit(game.cursorX, game.cursorY, angle,
                                                 initialHandler,
                                                 teamUnderCursor) != UnitsSystem.IDENTIFIER_NONE)
                    {
                        updateTopBarUI(game);
                        // Resets the animation.
                        game.cursorSprite.currentFrame = game.cursorSprite.startFrame;
                    }
                }
                if (game.unitsSystem.freeUnits() > 0)
                    game.uiMode = UIModes.PLACE;
                else
                    game.uiMode = UIModes.NO_MORE_UNITS;
            }
            else
                game.uiMode = UIModes.NOMANSLAND;
        }
    }
    
    public int teamForUnitBox(TAZSGame game)
    {
        if (game.cursorX > 0)
            return Teams.ENEMY;
        return Teams.PLAYER;
    }
    
    
    
    /***** PRIVATE *****/
    
    private void updateTopBarUI(TAZSGame game)
    {
        game.topBarUI.setLeftCountAndCost(Texts.TEAMS_LEFT, game.unitsSystem.unitsCount(Teams.PLAYER), game.unitsSystem.unitsCost(Teams.PLAYER));
        game.topBarUI.setRightCountAndCost(Texts.TEAMS_RIGHT, game.unitsSystem.unitsCount(Teams.ENEMY), game.unitsSystem.unitsCost(Teams.ENEMY));
    }
}