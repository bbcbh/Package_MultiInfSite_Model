package population.person;

/**
 *
 * @author Ben Hui
 * @version 20140827
 */
public interface SiteSpecificTransmissivity  {
    
    public static final int STRAIN_NONE = -1;
    
    double[] getProbTransBySite();
    double[] getProbSusBySite();
    public int[] getCurrentStrainsAtSite();
    public int[] getLastActStainsAtSite();    
    public double[][] getCurrentStrainLastUntilOfAgeAtSite();
    public int[][] getStrainPriorityBitAtSite();
    public int getCurrentDomainantStrainAtSite(int site);
    
}
