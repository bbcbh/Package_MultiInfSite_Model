package population.person;

import infection.AbstractInfection;
import java.io.Serializable;
import java.util.Arrays;
import person.AbstractIndividualInterface;


/**
 *
 * @author Ben Hui
 * @version 20180521
 *
 * <p>
 * History</p>
 * <p>
 * 20140828: Add strain last until of age field and supports </p>
 * <p>
 * 20140409: Check support for getting last act infectious </p>
 * <p>
 * 20180521: Rework to fit in new package description</p>
 *
 */
public class RelationshipPerson implements IndividualWithRelationshipInterface, SiteSpecificTransmissivity, Serializable {

    protected static final String[] PARAM_NANE_INT = {"PARAM_ID", "PARAM_MAX_PARTNER", "PARAM_ENTER_POP_AT"};
    protected static final String[] PARAM_NAME_DOUBLE = {"PARAM_AGE", "PARAM_STARTING_AGE", "PARAM_TIME_UNTIL_NEXT_REL"};
    // Integer    
    public static final int PARAM_ID = 0;
    public static final int PARAM_MAX_PARTNER = PARAM_ID + 1;
    public static final int PARAM_ENTER_POP_AT = PARAM_MAX_PARTNER + 1;
    // Double   
    public static final int PARAM_AGE = 0;
    public static final int PARAM_STARTING_AGE = PARAM_AGE + 1;
    public static final int PARAM_TIME_UNTIL_NEXT_REL = PARAM_STARTING_AGE + 1;
    protected static final int LENGTH_PARAM_TOTAL = PARAM_TIME_UNTIL_NEXT_REL + 1;
    // Fields
    boolean isMale;
    protected int[] paramInt = new int[PARAM_NANE_INT.length];
    protected double[] paramDouble = new double[PARAM_NAME_DOUBLE.length];
    // Inf status
    protected int[] infectionStatus;
    protected double[] timeUntilNextStage;
    protected double[] lastInfectedAtAge;
    protected boolean[] lastActInfectious;
    protected double[] probTrans;
    protected double[] probSus;

    // Strain info
    protected int[] currentStrainsAtSite;
    protected int[] lastActStainsAtSite;
    protected double[][] currentStrainLastUntilOfAgeAtSite;
    protected int[][] strainPriorityAtSite; // int[site] { 0, 1, (if the last bit dominant, for example) }

    //<editor-fold defaultstate="collapsed" desc="Atomic function">    
    @Override
    public int getId() {
        return paramInt[PARAM_ID];
    }

    @Override
    public double getAge() {
        return paramDouble[PARAM_AGE];
    }

    @Override
    public int getMaxPartners() {
        return paramInt[PARAM_MAX_PARTNER];
    }

    @Override
    public void setMaxPartners(int maxPartners) {
        paramInt[PARAM_MAX_PARTNER] = maxPartners;
    }

    @Override
    public double getTimeUntilNextRelationship() {
        return paramDouble[PARAM_TIME_UNTIL_NEXT_REL];
    }

    @Override
    public void setTimeUntilNextRelationship(double timeUntilNextRelationship) {
        paramDouble[PARAM_TIME_UNTIL_NEXT_REL] = timeUntilNextRelationship;
    }

    @Override
    public boolean isMale() {
        return isMale;
    }

    @Override
    public void setAge(double age) {
        paramDouble[PARAM_AGE] = age;
    }

    @Override
    public int getEnterPopulationAt() {
        return paramInt[PARAM_ENTER_POP_AT];
    }

    @Override
    public void setEnterPopulationAt(int enterPopulationAt) {
        paramInt[PARAM_ENTER_POP_AT] = enterPopulationAt;
    }

    @Override
    public double getStartingAge() {
        return paramDouble[PARAM_STARTING_AGE];
    }

    // </editor-fold>   
    //<editor-fold defaultstate="collapsed" desc="Infection">    
    @Override
    public int[] getInfectionStatus() {              
        return infectionStatus;
    }

    @Override
    public int getInfectionStatus(int index) {              
        return infectionStatus[index];
    }

    @Override
    public void setInfectionStatus(int index, int newInfectionStatus) {
        infectionStatus[index] = newInfectionStatus;
    }

    @Override
    public double getTimeUntilNextStage(int index) {
        return timeUntilNextStage[index];
    }

    @Override
    public void setTimeUntilNextStage(int index, double newTimeUntilNextStage) {
        timeUntilNextStage[index] = newTimeUntilNextStage;
    }

    @Override
    public void setLastActInfectious(int infectionIndex, boolean lastActInf) {
        lastActInfectious[infectionIndex] = lastActInf;
    }

    @Override
    public double getLastInfectedAtAge(int infectionIndex) {
        return lastInfectedAtAge[infectionIndex];
    }

    @Override
    public void setLastInfectedAtAge(int infectionIndex, double age) {
        lastInfectedAtAge[infectionIndex] = age;
    }

    // </editor-fold>       
    // <editor-fold defaultstate="collapsed" desc="Get and set parameter function ">    
    @Override
    public Comparable getParameter(String id) {
        for (int i = 0; i < PARAM_NANE_INT.length; i++) {
            if (PARAM_NANE_INT[i].equals(id)) {
                return paramInt[i];
            }
        }
        for (int i = 0; i < PARAM_NAME_DOUBLE.length; i++) {
            if (PARAM_NAME_DOUBLE[i].equals(id)) {
                return paramDouble[i];
            }
        }
        return null;
    }

    @Override
    public Comparable setParameter(String id, Comparable val) {
        Comparable ret = null;
        if (val instanceof Number) {
            if (val instanceof Integer) {
                for (int i = 0; i < PARAM_NANE_INT.length; i++) {
                    if (PARAM_NANE_INT[i].equals(id)) {
                        ret = paramInt[i];
                        paramInt[i] = ((Number) val).intValue();
                    }
                }
            } else {

                for (int i = 0; i < PARAM_NAME_DOUBLE.length; i++) {
                    if (PARAM_NAME_DOUBLE[i].equals(id)) {
                        ret = paramDouble[i];
                        paramDouble[i] = ((Number) val).doubleValue();
                    }
                }
            }
        }
        return ret;
    }
    // </editor-fold>           

    public RelationshipPerson(int id, boolean isMale, double age) {
        this.isMale = isMale;
        this.paramInt[PARAM_ID] = id;
        this.paramDouble[PARAM_AGE] = age;
        this.paramDouble[PARAM_TIME_UNTIL_NEXT_REL] = Double.NEGATIVE_INFINITY;
    }

    @Override
    public int incrementTime(int deltaT, AbstractInfection[] infectionList) {
        paramDouble[PARAM_AGE] += deltaT;
        paramDouble[PARAM_TIME_UNTIL_NEXT_REL] -= deltaT;
        return incrementInfectionStatus(deltaT, infectionList);
    }

    protected void initalisedInfections(int numSite) {
        infectionStatus = new int[numSite];
        timeUntilNextStage = new double[numSite];
        lastInfectedAtAge = new double[numSite];
        lastActInfectious = new boolean[numSite];
        probTrans = new double[numSite];
        probSus = new double[numSite];
        currentStrainsAtSite = new int[numSite];
        lastActStainsAtSite = new int[numSite];
        currentStrainLastUntilOfAgeAtSite = new double[numSite][];
        strainPriorityAtSite = new int[numSite][];

        Arrays.fill(infectionStatus, AbstractIndividualInterface.INFECT_S);
        Arrays.fill(timeUntilNextStage, Double.POSITIVE_INFINITY);
        Arrays.fill(lastInfectedAtAge, -1);
        Arrays.fill(lastActInfectious, false);
        Arrays.fill(probTrans, -1); // -1 = use infection default
        Arrays.fill(probSus, -1);
        Arrays.fill(currentStrainsAtSite, SiteSpecificTransmissivity.STRAIN_NONE);
        Arrays.fill(lastActStainsAtSite, SiteSpecificTransmissivity.STRAIN_NONE);

    }

    protected int incrementInfectionStatus(int deltaT, AbstractInfection[] infectionList) {
        int res = Integer.MAX_VALUE;
        // Update infection status
        for (int i = 0; i < infectionStatus.length; i++) {
            timeUntilNextStage[i] -= deltaT;
            // Self progress
            if (timeUntilNextStage[i] <= 0 && infectionList[i] != null) {
                res = Math.min(res, (int) infectionList[i].advancesState(this));
                if (infectionStatus[i] == INFECT_S) {
                    currentStrainsAtSite[i] = SiteSpecificTransmissivity.STRAIN_NONE;
                    if (currentStrainLastUntilOfAgeAtSite[i] != null) {
                        // Full recovery
                        Arrays.fill(currentStrainLastUntilOfAgeAtSite[i], Double.NaN);
                    }
                } else if (currentStrainLastUntilOfAgeAtSite[i] != null) {
                    domainatingLastUntil(infectionList, i, res);
                }

            }
            // Infection from last act
            if (this.lastActInfectious[i] && infectionStatus[i] == INFECT_S) {
                res = Math.min(res, (int) infectionList[i].infecting(this));
                currentStrainsAtSite[i] = lastActStainsAtSite[i];

                if (currentStrainLastUntilOfAgeAtSite[i] != null) {
                    domainatingLastUntil(infectionList, i, res);
                }

            }

            res = Math.min(res, (int) this.timeUntilNextStage[i]);
            this.lastActInfectious[i] = false;
            this.lastActStainsAtSite[i] = SiteSpecificTransmissivity.STRAIN_NONE;

        }
        return res;
    }

    @Override
    public int getCurrentDomainantStrainAtSite(int siteIndex) {
        int curStrain = currentStrainsAtSite[siteIndex];
        int strainPt = 0;

        if (curStrain > 0) {

            if (strainPriorityAtSite[siteIndex] != null) {
                strainPt = -1;
                for (int strainPointer = 0; strainPointer < getStrainPriorityBitAtSite()[siteIndex].length && strainPt < 0; strainPointer++) {
                    if ((curStrain & (1 << getStrainPriorityBitAtSite()[siteIndex][strainPointer])) != 0) {
                        strainPt = getStrainPriorityBitAtSite()[siteIndex][strainPointer];

                    }
                }

            } else {

                // Undefined - use the last 
                while ((curStrain & (1 << strainPt)) == 0) {
                    strainPt++;
                }

            }
        }
        return strainPt;
    }

    private void domainatingLastUntil(AbstractInfection[] infectionList, int siteIndex, int domainantDuration) {
        int domainantStrainIndex = infectionList[siteIndex].getInfectionIndex() / currentStrainsAtSite.length;

        // The duration control by the domainant one
        double lastUntilDominated = getAge() + domainantDuration;
        currentStrainLastUntilOfAgeAtSite[siteIndex][domainantStrainIndex] = lastUntilDominated;

        // The duration of the other strains will be dominated
        for (int s = 0; s < currentStrainLastUntilOfAgeAtSite[siteIndex].length; s++) {
            if (currentStrainLastUntilOfAgeAtSite[siteIndex][s] != Double.NaN) {
                currentStrainLastUntilOfAgeAtSite[siteIndex][s] = Math.min(lastUntilDominated,
                        currentStrainLastUntilOfAgeAtSite[siteIndex][s]);
            }
        }
    }

    public String indexToParamName(int index) {
        if (index < PARAM_NANE_INT.length) {
            return PARAM_NANE_INT[index];
        } else {
            return PARAM_NAME_DOUBLE[index - PARAM_NANE_INT.length];
        }
    }

    @Override
    public double[] getProbTransBySite() {
        return probTrans;
    }

    @Override
    public double[] getProbSusBySite() {
        // Currently fixed to {1}
        return probSus;
    }

    @Override
    public int[] getCurrentStrainsAtSite() {
        return currentStrainsAtSite;
    }

    @Override
    public int[] getLastActStainsAtSite() {
        return lastActStainsAtSite;
    }

    public boolean[] getLastActInfectious() {
        return lastActInfectious;
    }

    @Override
    public double[][] getCurrentStrainLastUntilOfAgeAtSite() {
        return currentStrainLastUntilOfAgeAtSite;
    }

    @Override
    public int[][] getStrainPriorityBitAtSite() {
        return strainPriorityAtSite;
    }

}
