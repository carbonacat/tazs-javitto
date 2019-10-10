package net.ccat.tazs;

import femto.font.TIC80;
import femto.mode.HiRes16Color;
import femto.sound.Mixer;

import net.ccat.tazs.battle.BattleMode;
import net.ccat.tazs.battle.UnitsSystem;
import net.ccat.tazs.battle.UnitTypes;
import net.ccat.tazs.resources.palettes.ModifiedNAJI16;
import net.ccat.tazs.resources.sounds.CursorSelectSound;
import net.ccat.tazs.resources.sounds.CursorMoveSound;
import net.ccat.tazs.resources.sprites.CursorSprite;
import net.ccat.tazs.resources.sprites.MenuCursorSprite;
import net.ccat.tazs.ui.PadMenuUI;
import net.ccat.tazs.ui.TopBarUI;
import net.ccat.tazs.ui.UIModes;


/**
 * Contains the game elements.
 */
class TAZSGame
{
    public TAZSGame()
    {
        screen = new HiRes16Color(ModifiedNAJI16.palette(), TIC80.font());
        unitsSystem = new UnitsSystem();
        cursorSprite = new CursorSprite();
        menuCursorSprite = new MenuCursorSprite();
        menuCursorSprite.playDefault();
        menuCursorSprite.setStatic(true);
        padMenuUI = new PadMenuUI();
        topBarUI = new TopBarUI();
        cursorMoveSound = new CursorMoveSound();
        cursorSelectSound = new CursorSelectSound();
        
        Mixer.init(8000);
    }
    
    
    /***** RENDERING *****/
    
    public HiRes16Color screen;
    
    
    /***** GAME *****/
    
    public float sceneXMin = -100;
    public float sceneYMin = -70;
    public float sceneXMax = 100;
    public float sceneYMax = 70;
    // TODO: Maybe have some zones instead.
    public float noMansLandRadius = 5;
    public UnitsSystem unitsSystem;
    public int focusedUnitIdentifier = UnitsSystem.IDENTIFIER_NONE;
    public BattleMode battleMode;
    
    
    /***** UI *****/
    
    public PadMenuUI padMenuUI;
    public TopBarUI topBarUI;
    public int uiMode = UIModes.INVALID;
    public int currentUnitType = UnitTypes.BRAWLER;
    
    // Cursor's in-game coordinates.
    public float cursorX;
    public float cursorY;
    
    
    /***** COMMON RESOURCES *****/
    
    public CursorSprite cursorSprite;
    public MenuCursorSprite menuCursorSprite;
    public CursorMoveSound cursorMoveSound;
    public CursorSelectSound cursorSelectSound;
}
