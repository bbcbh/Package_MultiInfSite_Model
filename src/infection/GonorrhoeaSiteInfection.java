package infection;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import person.AbstractIndividualInterface;
import population.person.SiteSpecificTransmissivity;
import random.RandomGenerator;


/**
 *
 * @author Ben Hui
 */
public class GonorrhoeaSiteInfection extends AbstractInfection {

    public static final String[] GONO_STATUS = {"Exposed", "Asymptomatic", "Symptomatic", "Immune"};
    public static final int DIST_EXPOSED_DUR_INDEX = 0;
    public static final int DIST_INFECT_DUR_ASY_INDEX = DIST_EXPOSED_DUR_INDEX + 1;
    public static final int DIST_INFECT_DUR_SYM_INDEX = DIST_INFECT_DUR_ASY_INDEX + 1;
    public static final int DIST_SYM_INDEX = DIST_INFECT_DUR_SYM_INDEX + 1;
    public static final int DIST_IMMUNE_DUR_INDEX = DIST_SYM_INDEX + 1;
    public static final int DIST_TRANS_PROB_INDEX = DIST_IMMUNE_DUR_INDEX + 1;
    public static final int DIST_SUS_PROB_INDEX = DIST_TRANS_PROB_INDEX + 1;
    public static final int DIST_TOTAL = DIST_SUS_PROB_INDEX + 1;
    private final int siteIndex;

    public GonorrhoeaSiteInfection(RandomGenerator RNG,
            int infectionIndex,
            double[][] siteSpecificParam,
            AbstractRealDistribution[] siteSpecificDist) {
        this(RNG, infectionIndex, infectionIndex, siteSpecificParam, siteSpecificDist);
    }

    public GonorrhoeaSiteInfection(RandomGenerator RNG,
            int infectionIndex, int siteIndex,
            double[][] siteSpecificParam,
            AbstractRealDistribution[] siteSpecificDist) {
        super(RNG);
        this.setInfectionIndex(infectionIndex);
        this.siteIndex = siteIndex;
        this.setInfectionState(GONO_STATUS);
        storeDistributions(siteSpecificDist, siteSpecificParam);
    }


    public int getSiteIndex() {
        return siteIndex;
    }

    @Override
    public double advancesState(AbstractIndividualInterface person) {
        double res = Double.POSITIVE_INFINITY;
        switch (getCurrentInfectionState(person)) {
            case STATUS_EXP: // E -> A or Y 
                // Determine if the person has symptoms when they become infectious
                double sym;
                sym = getRandomDistValue(DIST_SYM_INDEX);
                // Infection states
                int infectState = STATUS_ASY;
                if (sym == 1) {
                    infectState = STATUS_SYM;
                } else if (sym > 0) {
                    infectState = this.getRNG().nextDouble() < sym ? STATUS_SYM : STATUS_ASY;
                }
                if (infectState == STATUS_ASY) {
                    res = getRandomDistValue(DIST_INFECT_DUR_ASY_INDEX);
                } else {
                    res = getRandomDistValue(DIST_INFECT_DUR_SYM_INDEX);

                }
                setInfection(person, infectState, Math.round(res));

                break;
            case STATUS_ASY: // A -> I
                res = getRandomDistValue(DIST_IMMUNE_DUR_INDEX); //dist_immunity.nextDouble();
                this.setInfection(person, STATUS_IMM, Math.round(res));
                break;
            case STATUS_SYM:
                // Y -> I               
                res = getRandomDistValue(DIST_IMMUNE_DUR_INDEX);
                setInfection(person, STATUS_IMM, Math.round(res));
                break;
            case STATUS_IMM: // Immumne -> S
                this.setInfection(person, AbstractIndividualInterface.INFECT_S, Double.POSITIVE_INFINITY);
                res = Double.POSITIVE_INFINITY;
                if (person instanceof SiteSpecificTransmissivity) {
                    // Fully (?) suscepitble after recovery      
                    // If < 0 use default
                    if (((SiteSpecificTransmissivity) person).getProbSusBySite()[getSiteIndex()] < 0) {
                        ((SiteSpecificTransmissivity) person).getProbSusBySite()[getSiteIndex()] = getRandomDistValue(DIST_SUS_PROB_INDEX);
                    }

                    ((SiteSpecificTransmissivity) person).getCurrentStrainsAtSite()[getSiteIndex()] = SiteSpecificTransmissivity.STRAIN_NONE;
                }

                break;
        }

        return res;
    }

    @Override
    public double infecting(AbstractIndividualInterface target) {
        double res = Double.POSITIVE_INFINITY;

        if (target.getInfectionStatus(getSiteIndex()) == AbstractIndividualInterface.INFECT_S) {
            res = getRandomDistValue(DIST_EXPOSED_DUR_INDEX);
            int infIndex = getInfectionIndex();
            setInfectionIndex(getSiteIndex());
            setInfection(target, STATUS_EXP, Math.round(res));
            setInfectionIndex(infIndex);
            if (target instanceof SiteSpecificTransmissivity) {
                // Set probabilty of transmission from target to next person.
                // If < 0 use default
                if (((SiteSpecificTransmissivity) target).getProbTransBySite()[getSiteIndex()] < 0) {
                    ((SiteSpecificTransmissivity) target).getProbTransBySite()[getSiteIndex()] = getRandomDistValue(DIST_TRANS_PROB_INDEX);
                }

            }
        }
        return res;
    }

    @Override
    public boolean isInfectious(AbstractIndividualInterface p) {
        return p.getInfectionStatus(getSiteIndex()) == STATUS_ASY
                || p.getInfectionStatus(getSiteIndex()) == STATUS_SYM;
    }

    /*
     * In this version, it only check if the src can transmit infection
     */
    @Override
    public boolean couldTransmissInfection(AbstractIndividualInterface src, AbstractIndividualInterface target) {
        return isInfectious(src);
    }

    @Override
    public boolean isInfected(AbstractIndividualInterface p) {
        return p.getInfectionStatus(getSiteIndex()) != AbstractIndividualInterface.INFECT_S;
    }

    @Override
    public boolean hasSymptoms(AbstractIndividualInterface p) {
        return p.getInfectionStatus(getSiteIndex()) == STATUS_SYM;
    }

    @Override
    public boolean setParameter(String id, Object value) {
        try {
            int idN = Integer.parseInt(id);
            if (value instanceof double[]) {
                setDistributionState(idN, (double[]) value);
            } else {
                setDistribution(idN, (AbstractRealDistribution) value);
            }
            return true;
        } catch (Exception ex) {
            throw new UnsupportedOperationException(getClass().getName() + ".setParameter: Id " + id
                    + " not supported in this verison.");
        }

    }

    @Override
    protected int getCurrentInfectionState(AbstractIndividualInterface p) {
        return p.getInfectionStatus(getSiteIndex());
    }

    @Override
    protected void setInfection(AbstractIndividualInterface p, int status, double duration) {
        p.setInfectionStatus(getSiteIndex(), status);
        p.setTimeUntilNextStage(getSiteIndex(), duration);
    }

}
