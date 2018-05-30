package population.person;

/**
 *
 * @author Ben Hui
 * @version 20150828
 * 
 * <p>
 * 20180528: Add set strain at site method</p>
 *
 */
public interface MultiSiteMultiStrainPersonInterface  {
    
    public static final int STRAIN_NONE = 0;
    
    double[] getProbTransBySite();
    double[] getProbSusBySite();
    public int[] getCurrentStrainsAtSite();
    public int[] getLastActStainsAtSite();    
    
    public void setCurrentStrainAtSite(int site, int strainNum);   
    
    
}
