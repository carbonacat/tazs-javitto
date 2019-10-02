import femto.font.TIC80;
import femto.Game;

import net.ccat.tazs.states.TitleScreenState;


/**
 * Entry point.
 */
class Main
{
    public static void main(String[] args)
    {
        Game.run(TIC80.font(), new TitleScreenState(null));
    }
}
