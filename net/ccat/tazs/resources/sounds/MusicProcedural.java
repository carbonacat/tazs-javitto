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
        // So we can start on the first note.
        mCurrentNoteIndex = -1;
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
            mNoteSustainAmplitude = mNoteMaxAmplitude * INSTRUMENT_SUSTAIN_RATIO;
        }
        
        final int attackToDecayTime = mNoteDuration * INSTRUMENT_ATTACK_DURATION / INSTRUMENT_DURATION;
        final int decayToSustainTime = mNoteDuration * (INSTRUMENT_ATTACK_DURATION + INSTRUMENT_DECAY_DURATION) / INSTRUMENT_DURATION;
        final int sustainToReleaseTime = mNoteDuration * (INSTRUMENT_ATTACK_DURATION + INSTRUMENT_DECAY_DURATION + INSTRUMENT_SUSTAIN_DURATION) / INSTRUMENT_DURATION;
        final int releaseToSilenceTime = mNoteDuration * (INSTRUMENT_ATTACK_DURATION + INSTRUMENT_DECAY_DURATION + INSTRUMENT_SUSTAIN_DURATION + INSTRUMENT_RELEASE_DURATION) / INSTRUMENT_DURATION;

        float signal = ((float)noteT * mFrequency + (float)MIXER_FREQUENCY_2) / (float)MIXER_FREQUENCY;
        float amplitude;
        
        if (noteT < attackToDecayTime)
            amplitude = MathTools.lerp(noteT, 0, 0.f, attackToDecayTime, mNoteMaxAmplitude);
        else if (noteT < decayToSustainTime)
            amplitude = MathTools.lerp(noteT, attackToDecayTime, mNoteMaxAmplitude, decayToSustainTime, mNoteSustainAmplitude);
        else if (noteT < sustainToReleaseTime)
            amplitude = mNoteSustainAmplitude;
        else if (noteT < releaseToSilenceTime)
            amplitude = MathTools.lerp(noteT, sustainToReleaseTime, mNoteSustainAmplitude, releaseToSilenceTime, 0.f);
        else
            amplitude = 0;
        
        //return 128 + (int)(Math.round(signal * 2.f) * amplitude);
        
        /*if (((int)(signal) % 2) == 0)
            return 128 + (int)Math.round(amplitude);
        else
            return 128 - (int)Math.round(amplitude);*/
        return 128 + (int)(Math.sin(signal * 2.f * Math.PI) * amplitude);
        
        //return 128 + (int)(Math.round(Math.sin(signal * 2.f * Math.PI) * amplitude)) + (int)(Math.round(Math.sin(signal * 1.f * Math.PI) * amplitude * 0.5f));
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
    final int INSTRUMENT_ATTACK_DURATION = 200;
    final int INSTRUMENT_DECAY_DURATION = 50;
    final int INSTRUMENT_SUSTAIN_DURATION = 50;
    final int INSTRUMENT_RELEASE_DURATION = 200;
    final int INSTRUMENT_DURATION = INSTRUMENT_ATTACK_DURATION + INSTRUMENT_DECAY_DURATION + INSTRUMENT_SUSTAIN_DURATION + INSTRUMENT_RELEASE_DURATION;
    final float INSTRUMENT_SUSTAIN_RATIO = 192.f/256.f;
}