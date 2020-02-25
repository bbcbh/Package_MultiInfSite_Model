package infection.vaccination;

import java.util.Arrays;
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
    public static final int EFFECT_INDEX_TRANMISSION_EFFICACY_G = EFFECT_INDEX_PROPORTION_VACC_COVERAGE_SETTING+1;
    public static final int EFFECT_INDEX_TRANMISSION_EFFICACY_A = EFFECT_INDEX_TRANMISSION_EFFICACY_G + 1;
    public static final int EFFECT_INDEX_TRANMISSION_EFFICACY_R = EFFECT_INDEX_TRANMISSION_EFFICACY_A + 1;

    // For uninfected 
    public static final int EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_G = EFFECT_INDEX_TRANMISSION_EFFICACY_R + 1;
    public static final int EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_A = EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_G + 1;
    public static final int EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_R = EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_A + 1;                
    // Optional     
    public static final int OPTIONAL_EFFECT_VACCINE_DURATION = EFFECT_INDEX_SUSCEPTIBLE_EFFICACY_R + 1;
    
    
   
    
    public SiteSpecificVaccination(double[] parameters) {
        super(parameters);                                
    }

    

    @Override
    public int[] vaccinatePerson(AbstractIndividualInterface person) {
        int[] res = super.vaccinatePerson(person);                
        // Include possible immedate effect for vaccination (e.g. reduce duration, removal of syptoms etc)                                

        return res;
    }
    
    

    @Override
    public double[] vaccineImpact(AbstractIndividualInterface person, Object params) {
        // For now, params is not used 
        int[] vaccRecord = getVaccinationRecord().get(person.getId());

        if (vaccRecord == null) {
            return null;
        } else {
            
            /*
            if(getParameters().length > OPTIONAL_EFFECT_VACCINE_DURATION){
                double vaccDur = getParameters()[OPTIONAL_EFFECT_VACCINE_DURATION];
                if(person.getAge() - vaccRecord[vaccRecord.length-1] > vaccDur){
                    return null;
                }                                
            }
            */
            
            
            double[] res = new double[getParameters().length];
            Arrays.fill(res, 1);
            // No waning
            System.arraycopy(getParameters(), 0, res, 0, res.length);
            return res;
        }

    }

}
