package population.person;

/**
 *
 * @author Ben Hui
 */
public interface LocationSpecific {
    public static final int LOC_HOME = 0;
    public static final int LOC_CURRENT = LOC_HOME + 1;
    public static final int LOC_LENGTH = LOC_CURRENT + 1;
    public int getHomeLocation();       
    public int getCurrentLocation();
    public void setHomeLocation(int loc);
    public void setCurrentLocation(int loc);
    public int getTimeUntilNextMove();
    public void setTimeUntilNextMove(int t);
    public int getDaysSinceLastMove();
    
}
