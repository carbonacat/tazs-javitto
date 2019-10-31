package net.ccat.tazs.resources.musics;


/**
 * A Static Class that facilitates the reading of a Music.
 */
public class MusicReader
{
    /***** GENERAL STRUCTURE *****/

    /**
     * @param musicPointer
     * @return A pointer at the music's title.
     */
    public static final pointer titlePointerFromMusic(pointer musicPointer)
    {
        return musicPointer + readUnsigned16(musicPointer + MUSIC_TITLE_ADDRESS_OFFSET);
    }
    
    /**
     * @param musicPointer
     * @return A pointer at the music's instrument
     */
    public static final pointer instrumentPointerFromMusic(pointer musicPointer)
    {
        return musicPointer + readUnsigned16(musicPointer + MUSIC_INSTRUMENT_ADDRESS_OFFSET);
    }
    
    /**
     * @param musicPointer
     * @return A pointer at the music's events
     */
    public static final pointer eventsPointerFromMusic(pointer musicPointer)
    {
        return musicPointer + readUnsigned16(musicPointer + MUSIC_EVENTS_ADDRESS_OFFSET);
    }
    
    
    /***** INSTRUMENT *****/
    
    /**
     * @param instrumentPointer A pointer given by instrumentPointerFromMusic.
     * @return The attack for that instrument.
     */
    public static final int attackFromInstrument(pointer instrumentPointer)
    {
        return (int)readUnsigned16(instrumentPointer + INSTRUMENT_ATTACK_OFFSET);
    }
    
    /**
     * @param instrumentPointer A pointer given by instrumentPointerFromMusic.
     * @return The attack for that instrument.
     */
    public static final int decayFromInstrument(pointer instrumentPointer)
    {
        return (int)readUnsigned16(instrumentPointer + INSTRUMENT_DECAY_OFFSET);
    }
    
    /**
     * @param instrumentPointer A pointer given by instrumentPointerFromMusic.
     * @return The attack for that instrument.
     */
    public static final int releaseFromInstrument(pointer instrumentPointer)
    {
        return (int)readUnsigned16(instrumentPointer + INSTRUMENT_RELEASE_OFFSET);
    }
    
    /**
     * @param instrumentPointer A pointer given by instrumentPointerFromMusic.
     * @return The attack for that instrument.
     */
    public static final float sustainMaxRatioFromInstrument(pointer instrumentPointer)
    {
        int ratio256 = (int)readUnsigned16(instrumentPointer + INSTRUMENT_SUSTAIN_MAX_RATIO_OFFSET);
        
        return (float)ratio256 / 256.f;
    }
    
    
    /***** EVENTS *****/
    
    /**
     * @param eventsPointer A pointer given by eventsPointerFromMusic.
     * @return The number of events.
     */
    public static final int countFromEvents(pointer eventsPointer)
    {
        return (int)System.memory.LDRB(eventsPointer);
    }
    
    /**
     * @param eventsPointer A pointer given by eventsPointerFromMusic.
     * @return The pointer to a given Event.
     */
    public static final pointer eventPointerFromEvents(pointer eventsPointer, int eventIndex)
    {
        return eventsPointer + 2 + eventIndex * EVENT_SIZE;
    }
    
    
    /***** EVENT *****/
    
    public static final int COMMAND_JUMP = -128;
    
    /**
     * @param eventPointer A pointer given by eventPointerFromEvents.
     * @return The Event's command.
     */
    public static final int commandFromEvent(pointer eventPointer)
    {
        return (int)readSigned8(eventPointer + EVENT_COMMAND_OFFSET);
    }
    
    /**
     * @param eventPointer A pointer given by eventPointerFromEvents.
     * @return The Event's duration.
     */
    public static final int durationFromEvent(pointer eventPointer)
    {
        return (int)readSigned8(eventPointer + EVENT_DURATION_OFFSET);
    }
    
    /**
     * @param eventPointer A pointer given by eventPointerFromEvents.
     * @return The Event's loudness.
     */
    public static final float loudnessFromEvent(pointer eventPointer)
    {
        return (float)(int)readSigned8(eventPointer + EVENT_LOUDNESS_OFFSET);
    }
    
    /**
     * @param eventPointer A pointer given by eventPointerFromEvents.
     * @return The Event's jump index.
     */
    public static final int jumpIndexFromEvent(pointer eventPointer)
    {
        return (int)readUnsigned16(eventPointer + EVENT_JUMPINDEX_OFFSET);
    }
            
    
    /***** PRIVATE *****/
    
    /**
     * @return A int8 read at the given location.
     */
    private static final int readSigned8(pointer pointer)
    {
        int value = (int)System.memory.LDRB(pointer);
        
        if (value <= 127)
            return value;
        return value - 256;
    }
    
    /**
     * @return A uint16 read at the given location.
     */
    private static final int readUnsigned16(pointer pointer)
    {
        return ((int)System.memory.LDRB(pointer)) + ((int)System.memory.LDRB(pointer + 1) << 8);
    }
    
    
    private static final int MUSIC_TITLE_ADDRESS_OFFSET = 16;
    private static final int MUSIC_INSTRUMENT_ADDRESS_OFFSET = 18;
    private static final int MUSIC_EVENTS_ADDRESS_OFFSET = 20;
    private static final int MUSIC_EXTRA_ADDRESS_OFFSET = 22;
    
    private static final int INSTRUMENT_ATTACK_OFFSET = 0;
    private static final int INSTRUMENT_DECAY_OFFSET = 2;
    private static final int INSTRUMENT_RELEASE_OFFSET = 4;
    private static final int INSTRUMENT_SUSTAIN_MAX_RATIO_OFFSET = 6;
    
    
    private static final int EVENT_SIZE = 3;
    private static final int EVENT_COMMAND_OFFSET = 0;
    private static final int EVENT_DURATION_OFFSET = 1;
    private static final int EVENT_LOUDNESS_OFFSET = 2;
    private static final int EVENT_JUMPINDEX_OFFSET = 1;
}