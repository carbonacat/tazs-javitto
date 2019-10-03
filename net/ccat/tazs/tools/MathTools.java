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
     * Wraps the given angle into [-PI; PI].
     * @return the wrapped angle.
     */
    public static float wrapAngle(float angle)
    {
        while (angle > Math.PI)
            angle -= PI_2_1;
        while (angle < -Math.PI)
            angle += PI_2_1;
        return angle;
    }
    
    /**
     * @param value The value to be clamped.
     * @param min The minimum value that can be returned.
     * @param max The maximum value that can be returned.
     * @return value, clamped to min and max.
     */
    public static float clamp(float value, float min, float max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }
    
    public static float abs(float value)
    {
        if (value < 0)
            return -value;
        return value;
    }
}