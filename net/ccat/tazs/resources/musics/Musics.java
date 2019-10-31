package net.ccat.tazs.resources.musics;


/**
 * Gathers all the available musics.
 */
public class Musics
{
    public static final int SILENCE = 0;
    public static final int MUSIC00 = SILENCE + 1;
    public static final int MUSIC01 = MUSIC00 + 1;
    public static final int MUSIC02 = MUSIC01 + 1;
    public static final int MUSIC03 = MUSIC02 + 1;
    public static final int COUNT = MUSIC03 + 1;
    
    
    public static pointer musicPointerForIdentifier(int musicIdentifier)
    {
        if (musicIdentifier == SILENCE)
            return Silence.bin();
        else if (musicIdentifier == MUSIC00)
            return Music00.bin();
        else if (musicIdentifier == MUSIC01)
            return Music01.bin();
        else if (musicIdentifier == MUSIC02)
            return Music02.bin();
        else if (musicIdentifier == MUSIC03)
            return Music03.bin();
        // Something went wrong here!
        while (true);
        return null;
    }
}