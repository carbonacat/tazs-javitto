package net.ccat.tazs.ui;


/**
 * Available UI Modes while within a Battle.
 */
public class UIModes
{
    // Too expensive.
    private static final int TOO_EXPENSIVE = -2;
    // No free Unit remaining.
    private static final int NO_MORE_UNITS = -1;
    // Invalid/Error.
    private static final int INVALID = 0;
    // "Enemy Territory".
    private static final int ENEMY_TERRITORY = 1;
    // Can place a Unit.
    private static final int PLACE = 2;
    // Can remove a Unit.
    private static final int REMOVE = 4;
    // In PadMenu.
    private static final int MENU = 5;
    // "No Man's Land.""
    private static final int NOMANSLAND = 6;
}