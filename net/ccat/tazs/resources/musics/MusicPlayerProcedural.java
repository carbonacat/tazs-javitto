package net.ccat.tazs.resources.musics;

import femto.sound.Procedural;

import net.ccat.tazs.tools.MathTools;


/**
 * Plays Music from Music Binaries.
 */
public class MusicPlayerProcedural
    extends Procedural
{
    public MusicPlayerProcedural()
    {
        super();
    }
    
    public MusicPlayerProcedural(int channel)
    {
        super(channel);
    }
    
    
    /***** MUSIC CONTROL *****/
    
    public void playMusic(pointer musicPointer)
    {
        pointer instrumentPointer = MusicReader.instrumentPointerFromMusic(musicPointer);
        
        mEventsPointer = MusicReader.eventsPointerFromMusic(musicPointer);
        
        mInstrumentAttackDuration = MusicReader.attackFromInstrument(instrumentPointer);
        mInstrumentDecayDuration = MusicReader.decayFromInstrument(instrumentPointer);
        mInstrumentReleaseDuration = MusicReader.releaseFromInstrument(instrumentPointer);
        mInstrumentMinDuration = mInstrumentAttackDuration + mInstrumentDecayDuration + mInstrumentReleaseDuration;
        mInstrumentSustainMaxRatio = MusicReader.sustainMaxRatioFromInstrument(instrumentPointer);
        
        playEvent(0);
    }
    
    /**
     * Plays a given event from the music.
     * Will stop the music if that note doesn't exist.
     */
    public void playEvent(int noteIndex)
    {
        if ((noteIndex < 0) || (noteIndex >= MusicReader.countFromEvents(mEventsPointer)))
            mNoteIndex = -1;
        else
        {
            pointer eventPointer = MusicReader.eventPointerFromEvents(mEventsPointer, noteIndex);
            int command = MusicReader.commandFromEvent(eventPointer);
            
            if (command == MusicReader.COMMAND_JUMP)
                playEvent(MusicReader.jumpIndexFromEvent(eventPointer));
            else
            {
                mNoteIndex = noteIndex;
                mNoteFrequency = frequencyForPitch(command);
                mNoteDuration = MusicReader.durationFromEvent(eventPointer) * NOTE_DURATION_MULTIPLIER;
                mNoteMaxAmplitude = MusicReader.loudnessFromEvent(eventPointer);
                mNoteSustainAmplitude = 0;
                if (mNoteDuration < mInstrumentMinDuration)
                {
                    // Scaling down the amplitude to avoid increasing the slopes.
                    mNoteMaxAmplitude = mNoteMaxAmplitude * mNoteDuration / mInstrumentMinDuration;
                    mNoteAttackToDecayTime = mInstrumentAttackDuration * mNoteDuration / mInstrumentMinDuration;
                    mNoteDecayToSustainTime = (mInstrumentAttackDuration + mInstrumentDecayDuration) * mNoteDuration / mInstrumentMinDuration;
                    mNoteSustainToReleaseTime = (mInstrumentMinDuration - mInstrumentReleaseDuration) * mNoteDuration / mInstrumentMinDuration;
                }
                else
                {
                    int sustainTime = mNoteDuration - mInstrumentMinDuration;
                    
                    mNoteAttackToDecayTime = mInstrumentAttackDuration;
                    mNoteDecayToSustainTime = mInstrumentAttackDuration + mInstrumentDecayDuration;
                    mNoteSustainToReleaseTime = mNoteDecayToSustainTime + sustainTime;
                }
                mNoteSustainAmplitude = mNoteMaxAmplitude * mInstrumentSustainMaxRatio;
            }
        }
        t = 0;
    }
    
    
    /***** PROCEDURAL *****/
    
    public ubyte update()
    {
        if (mNoteIndex < 0)
            return 128;
        
        t++;
        
        final int noteT = (int)t;
        
        if (noteT >= mNoteDuration)
        {
            playEvent(mNoteIndex + 1);
            // t is reset, but not this temporary value.
            noteT = 0;
            // Could have ended right now!
            if (mNoteIndex < 0)
                return 128;
        }

        float signal = ((float)noteT * mNoteFrequency + (float)MIXER_FREQUENCY_2) / (float)MIXER_FREQUENCY;
        float amplitude;
        
        if (noteT < mNoteAttackToDecayTime)
            amplitude = MathTools.lerp(noteT, 0, 0.f, mNoteAttackToDecayTime, mNoteMaxAmplitude);
        else if (noteT < mNoteDecayToSustainTime)
            amplitude = MathTools.lerp(noteT, mNoteAttackToDecayTime, mNoteMaxAmplitude, mNoteDecayToSustainTime, mNoteSustainAmplitude);
        else if (noteT < mNoteSustainToReleaseTime)
            amplitude = mNoteSustainAmplitude;
        else if (noteT < mNoteDuration)
            amplitude = MathTools.lerp(noteT, mNoteSustainToReleaseTime, mNoteSustainAmplitude, mNoteDuration, 0.f);
        else
            amplitude = 0;
        return 128 + (int)Math.round((Math.sin(signal * 2.f * Math.PI)) * amplitude);
    }
    
    
    /***** MUSIC *****/
    
    
    /***** PRIVATE *****/
    
    private float frequencyForPitch(int pitch)
    {
        if (pitch < 0)
            return frequencyForPitch(pitch + 12) * 0.5f;
        if (pitch >= 12)
            return frequencyForPitch(pitch - 12) * 2.f;
        switch (pitch)
        {
            case 0: // DO / C
                return 523.25f;
            case 1:
                return 554.37f;
            case 2: // RE / D
                return 587.33f;
            case 3:
                return 622.25f;
            case 4: // MI / E
                return 659.26f;
            case 5: // FA / F
                return 698.46f;
            case 6:
                return 739.99f;
            case 7: // SOL /G
                return 783.99f;
            case 8:
                return 830.61f;
            case 9: // LA / A
                return 880.00f;
            case 10:
                return 932.33f;
            case 11: // SI / B
                return 987.77f;
        }
        return 0;
    }

    private pointer mEventsPointer;
    
    private int mInstrumentAttackDuration;
    private int mInstrumentDecayDuration;
    private int mInstrumentReleaseDuration;
    private int mInstrumentMinDuration;
    private float mInstrumentSustainMaxRatio;
    
    private int mNoteIndex = -1;
    private float mNoteFrequency = 0;
    private int mNoteDuration = 0;
    private float mNoteMaxAmplitude = 0;
    private float mNoteSustainAmplitude = 0;
    private int mNoteAttackToDecayTime;
    private int mNoteDecayToSustainTime;
    private int mNoteSustainToReleaseTime;
    
    private static final int MIXER_FREQUENCY = 8000;
    private static final int MIXER_FREQUENCY_2 = MIXER_FREQUENCY / 2;
    
    private static final int NOTE_DURATION_MULTIPLIER = MIXER_FREQUENCY / 16;
}