package net.ccat.tazs.tools;


/**
 * A collection of math-related tools.
 */
public class MathTools
{
    /***** TRIGONOMETRY *****/

    public static float PI_1_2 = 0.5f * Math.PI;
    public static float PI_3_2 = 1.5f * Math.PI;
    public static float PI_2_1 = 2.f * Math.PI;

    /**
     * Wraps the given angle into [0; 2 PI].
     * @return the wrapped angle.
     */
    public static float wrapAngle(float angle)
    {
        while (angle > PI_2_1)
            angle -= PI_2_1;
        while (angle < 0.f)
            angle += PI_2_1;
        return angle;
    }
    
    public static float abs(float value)
    {
        if (value < 0)
            return -value;
        return value;
    }
}