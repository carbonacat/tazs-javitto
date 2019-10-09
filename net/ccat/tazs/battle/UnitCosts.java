package net.ccat.tazs.battle;


/**
 * All the Costs for all Units.
 */
public class UnitCosts
{
    public static final int BRAWLER_COST = 20;
    public static final int SLAPPER_COST = 25;
    
    
    /**
     * @param unitType The Unit Type.
     * @return How many beans this unit cost.
     */
    public static final int costForType(int unitType)
    {
        if (unitType == UnitTypes.BRAWLER)
            return BRAWLER_COST;
        if (unitType == UnitTypes.SLAPPER)
            return SLAPPER_COST;
        // Not supposed to happen.
        while (true);
        return 0;
    }
}