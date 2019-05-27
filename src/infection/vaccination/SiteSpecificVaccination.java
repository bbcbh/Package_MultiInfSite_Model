package infection.vaccination;

import java.util.Arrays;
import person.AbstractIndividualInterface;

/**
 * Class represents vaccination that have site specific impact
 *
 * @author Ben Hui
 */
public class SiteSpecificVaccination extends AbstractVaccination {
    
    
    public static final int EFFECT_INDEX_PROPORTION_VACC_ENT_POP = 0;

    // For infected
    public static final int EFFECT_INDEX_TRANMISSION_EFFICACY_G = EFFECT_INDEX_PROPORTION_VACC_ENT_POP+1;
    public static final int EFFECT_INDEX_TRANMISSION_EFFICACY_A = EFFECT_INDEX_TRANMISSION_EFFICACY_G + 1;
    public static final int EFFECT_INDEX_TRANMISSION_EFFICACY_R = EFFECT_INDEX_TRANMISSION_EFFICACY_A + 1;

    // For uninfected 
    public static final int EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_G = EFFECT_INDEX_TRANMISSION_EFFICACY_R + 1;
    public static final int EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_A = EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_G + 1;
    public static final int EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_R = EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_A + 1;

    public static final int EFFECT_INDEX_LENGTH = EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_R + 1;
    
    public SiteSpecificVaccination(double[] parameters) {
        super(parameters);                        
    }

    

    @Override
    public int[] vaccinatePerson(AbstractIndividualInterface person) {
        // Include possible immedate effect for vaccination (e.g. reduce duration, removal of syptoms etc)

        return super.vaccinatePerson(person);
    }

    @Override
    public double[] vaccineImpact(AbstractIndividualInterface person, Object params) {
        int[] vaccRecord = getVaccinationRecord().get(person.getId());

        if (vaccRecord == null) {
            return null;
        } else {
            double[] res = new double[getParameters().length];
            Arrays.fill(res, 1);
            // No waning
            System.arraycopy(getParameters(), 0, res, 0, res.length);
            return res;
        }

    }

}
