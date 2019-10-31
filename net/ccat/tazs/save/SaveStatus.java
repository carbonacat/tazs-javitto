package net.ccat.tazs.save;


/**
 * Possible values for a SaveCookie's check.
 */
public class SaveStatus
{
    // The cookie is totally empty. Indicates the first launch.
    public static final int EMPTY = 0;
    // The cookie is OK and ready to be used.
    public static final int OK = 1;
    // The cookie is corrupted (wrong sum check, wrong magic value).
    public static final int CORRUPTED = -1;
    // The cookie doesn't have the right version.
    public static final int VERSION_MISMATCH = -2;
}