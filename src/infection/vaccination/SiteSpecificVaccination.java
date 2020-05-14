package infection.vaccination;

import person.AbstractIndividualInterface;

/**
 * Class represents vaccination that have site specific impact
 *
 * @author Ben Hui
 */
public class SiteSpecificVaccination extends AbstractVaccination {

    // EFFECT_INDEX_PROPORTION_VACC_COVERAGE_SETTING
    // If >0, this will represents the probability of vaccination as person enter population
    // If <0, this will represents the probability of vaccination through screening
    public static final int EFFECT_INDEX_PROPORTION_VACC_COVERAGE_SETTING = 0;

    // For infected
    public static final int EFFECT_INDEX_TRANMISSION_EFFICACY_G = EFFECT_INDEX_PROPORTION_VACC_COVERAGE_SETTING + 1;
    public static final int EFFECT_INDEX_TRANMISSION_EFFICACY_A = EFFECT_INDEX_TRANMISSION_EFFICACY_G + 1;
    public static final int EFFECT_INDEX_TRANMISSION_EFFICACY_R = EFFECT_INDEX_TRANMISSION_EFFICACY_A + 1;

    // For uninfected 
    public static final int EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_G = EFFECT_INDEX_TRANMISSION_EFFICACY_R + 1;
    public static final int EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_A = EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_G + 1;
    public static final int EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_R = EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_A + 1;
    // Optional. -1 if not used
    // Vaccine duration
    public static final int OPTIONAL_EFFECT_VACCINE_DURATION_DEFAULT = EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_R + 1;
    // Vaccine infection duration adjustment
    public static final int OPTIONAL_EFFECT_ADJ_INF_DUR_DEFAULT = OPTIONAL_EFFECT_VACCINE_DURATION_DEFAULT + 1;
    // Vaccine symptom removal
    public static final int OPTIONAL_EFFECT_REMOVE_SYM_RATE_DEFAULT = OPTIONAL_EFFECT_ADJ_INF_DUR_DEFAULT + 1;
    public static final int OPTIONAL_EFFECT_REMOVE_SYM_STATE_DEFAULT = OPTIONAL_EFFECT_REMOVE_SYM_RATE_DEFAULT + 1;
    public static final int OPTIONAL_EFFECT_REMOVE_SYM_INF_DUR_DEFAULT_MEDIAN = OPTIONAL_EFFECT_REMOVE_SYM_STATE_DEFAULT + 1;
    public static final int OPTIONAL_EFFECT_REMOVE_SYM_INF_DUR_DEFAULT_SD = OPTIONAL_EFFECT_REMOVE_SYM_INF_DUR_DEFAULT_MEDIAN + 1;

    public SiteSpecificVaccination(double[] parameters) {
        super(parameters);
    }

    public SiteSpecificVaccination(double[] parameters, int[] validTime) {
        super(parameters, validTime);
    }

    @Override
    public double[] vaccineImpact(AbstractIndividualInterface person, Object params) {
        double[] res = new double[getParameters().length];
        System.arraycopy(getParameters(), 0, res, 0, res.length);
        return res;
    }

}
