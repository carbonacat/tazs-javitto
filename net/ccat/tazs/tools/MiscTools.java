package net.ccat.tazs.tools;


public class MiscTools
{
    /**
     * @return The current stack size.
     */
    public static int getStackSize()
    {
        int size;
        
        __inline_cpp__("size = 0x10008000 - uintptr_t(&size)");
        return size;
    }
}