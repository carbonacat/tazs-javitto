package net.ccat.tazs.resources.sounds;

import femto.sound.Procedural;

import net.ccat.tazs.tools.MathTools;


/**
 * Some sort of tiny music generator out of partitions.
 */
public class MusicProcedural
    extends Procedural
{
    public MusicProcedural()
    {
        super();
    }
    
    public MusicProcedural(int channel)
    {
        super(channel);
        // TODO: Can't wait to Binarify this.
        
        int choice = Math.random(0, 4);
        
        choice = 2;
        switch (choice)
        {
        case 0:
            mNotes = new byte[]
            {
                -5, 2, 32,
                0, 2, 0,
                
                -5, 2, 32,
                -4, 2, 32,
                
                -3, 2, 32,
                -2, 2, 32,
                
                -1, 2, 32,
                0, 2, 0,
                
                -1, 2, 32,
                0, 2, 0,
                
                -1, 2, 32,
                0, 2, 0,
                
                0, 4, 32,
                0, 4, 0,
            };
            break ;
        case 1:
            mNotes = new byte[]
            {
                -5, 8, 16,
                -6, 8, 16,
                -7, 8, 16,
                0, 4, 32,
                -1, 4, 32,
                
                -10, 8, 16,
                -9, 8, 16,
                -8, 8, 16,
                0, 4, 32,
                -1, 4, 32,
            };
            break ;
        case 2:
            mNotes = new byte[]
            {
                -24, 1, 64,
                -24, 3, 0,
                
                -24, 1, 64,
                -24, 3, 0,
                
                -24, 4, 0,
                
                -24, 1, 64,
                -24, 1, 0,
                -24, 1, 64,
                -24, 1, 0,
                
                -24, 1, 64,
                -24, 3, 0,
                
                -24, 4, 0,
                
                -21, 1, 64,
                -24, 3, 0,
                
                -24, 4, 0,
            };
            break ;
        default:
        case 3:
            mNotes = new byte[]
            {
                -1, 16, 32,
                -2, 16, 32,
                
                -3, 32, 32,
                
                0, 4, 0,
                -3, 4, 16,
                0, 4, 0,
                -3, 4, 8,
                
                0, 4, 0,
                -3, 4, 4,
                0, 4, 0,
                0, 8, 0,
                
                -3, 16, 32,
                -2, 16, 32,
                
                -1, 32, 32,
                
                0, 4, 0,
                -1, 4, 16,
                0, 4, 0,
                -1, 4, 8,
                
                0, 4, 0,
                -1, 4, 4,
                0, 4, 0,
                0, 8, 0,
            };
            break;
        }
        // So we can start on the first note.
        mCurrentNoteIndex = mNotes.length;
    }
    
    public ubyte update()
    {
        t++;
        
        final int noteT = (int)t;
        
        if (noteT >= mNoteDuration)
        {
            // The note ended. We'll play the next one.
            t = 0;
            noteT = 0;
            mCurrentNoteIndex += NOTE_SIZE;
            if (mCurrentNoteIndex >= mNotes.length)
                mCurrentNoteIndex = 0;
            mFrequency = frequencyForPitch(mNotes[mCurrentNoteIndex + NOTE_PITCH_OFFSET]);
            mNoteDuration = mNotes[mCurrentNoteIndex + NOTE_DURATION_OFFSET] * NOTE_DURATION_MULTIPLIER;
            mNoteMaxAmplitude = (float)mNotes[mCurrentNoteIndex + NOTE_LOUDNESS_OFFSET];
            
            if (mNoteDuration < INSTRUMENT_DURATION_MIN)
            {
                // Scaling down the amplitude to avoid increasing the slopes.
                mNoteMaxAmplitude = mNoteMaxAmplitude * mNoteDuration / INSTRUMENT_DURATION_MIN;
                mAttackToDecayTime = INSTRUMENT_ATTACK_DURATION * mNoteDuration / INSTRUMENT_DURATION_MIN;
                mDecayToSustainTime = (INSTRUMENT_ATTACK_DURATION + INSTRUMENT_DECAY_DURATION) * mNoteDuration / INSTRUMENT_DURATION_MIN;
                mSustainToReleaseTime = (INSTRUMENT_DURATION_MIN - INSTRUMENT_RELEASE_DURATION) * mNoteDuration / INSTRUMENT_DURATION_MIN;
            }
            else
            {
                int sustainTime = mNoteDuration - INSTRUMENT_DURATION_MIN;
                
                mAttackToDecayTime = INSTRUMENT_ATTACK_DURATION;
                mDecayToSustainTime = INSTRUMENT_ATTACK_DURATION + INSTRUMENT_DECAY_DURATION;
                mSustainToReleaseTime = mDecayToSustainTime + sustainTime;
            }
            mNoteSustainAmplitude = mNoteMaxAmplitude * INSTRUMENT_SUSTAIN_RATIO;
        }

        float signal = ((float)noteT * mFrequency + (float)MIXER_FREQUENCY_2) / (float)MIXER_FREQUENCY;
        float amplitude;
        
        if (noteT < mAttackToDecayTime)
            amplitude = MathTools.lerp(noteT, 0, 0.f, mAttackToDecayTime, mNoteMaxAmplitude);
        else if (noteT < mDecayToSustainTime)
            amplitude = MathTools.lerp(noteT, mAttackToDecayTime, mNoteMaxAmplitude, mDecayToSustainTime, mNoteSustainAmplitude);
        else if (noteT < mSustainToReleaseTime)
            amplitude = mNoteSustainAmplitude;
        else if (noteT < mNoteDuration)
            amplitude = MathTools.lerp(noteT, mSustainToReleaseTime, mNoteSustainAmplitude, mNoteDuration, 0.f);
        else
            amplitude = 0;
        
        //return 128 + (int)(Math.round(signal * 2.f) * amplitude);
        
        /*if (((int)(signal) % 2) == 0)
            return 128 + (int)Math.round(amplitude);
        else
            return 128 - (int)Math.round(amplitude);*/
        return 128 + (int)Math.round((Math.sin(signal * 2.f * Math.PI)) * amplitude);
    }
    
    
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
    
    private float mFrequency = 0;
    private int mNoteDuration = 0;
    private float mNoteMaxAmplitude = 0;
    private float mNoteSustainAmplitude = 0;
    private int mAttackToDecayTime;
    private int mDecayToSustainTime;
    private int mSustainToReleaseTime;
    
    private int mCurrentNoteIndex = 0;
    private byte[] mNotes;
    
    private static final int MIXER_FREQUENCY = 8000;
    private static final int MIXER_FREQUENCY_2 = MIXER_FREQUENCY / 2;
    
    private static final int NOTE_DURATION_MULTIPLIER = MIXER_FREQUENCY / 16;
    
    private static final int NOTE_SIZE = 3;
    private static final int NOTE_PITCH_OFFSET = 0;
    private static final int NOTE_DURATION_OFFSET = 1;
    private static final int NOTE_LOUDNESS_OFFSET = 2;
    
    // Defines the envelop of a standard note.
    final int INSTRUMENT_ATTACK_DURATION = 250;
    final int INSTRUMENT_DECAY_DURATION = 250;
    final int INSTRUMENT_RELEASE_DURATION = 500;
    final int INSTRUMENT_DURATION_MIN = INSTRUMENT_ATTACK_DURATION + INSTRUMENT_DECAY_DURATION + INSTRUMENT_RELEASE_DURATION;
    final float INSTRUMENT_SUSTAIN_RATIO = 0.875f;
}