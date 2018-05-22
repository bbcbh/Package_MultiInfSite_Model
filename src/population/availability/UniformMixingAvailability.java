package population.availability;


import availability.AbstractAvailability;
import person.AbstractIndividualInterface;
import random.RandomGenerator;
import util.ArrayUtilsRandomGenerator;

/**
 *
 * @author Ben Hui
 */
public class UniformMixingAvailability extends AbstractAvailability {

    private AbstractIndividualInterface[][] available;
    private AbstractIndividualInterface[][] pairing = null;

    public UniformMixingAvailability(RandomGenerator RNG) {
        super(RNG);
    }

    @Override
    public int generatePairing() {
        int numPairing = Integer.MAX_VALUE; // Max pairing in one turn
        for (int i = 0; i < available.length; i++) {
            numPairing = Math.min(available[i].length, numPairing);
            ArrayUtilsRandomGenerator.shuffleArray(available[i], getRNG());
        }
        pairing = new AbstractIndividualInterface[numPairing][2];
        int r = 0, pt = 0;
        while (r < numPairing) {
            pairing[r][0] = available[0][pt];            
            pairing[r][1] = available[1][pt];
            pt++;
            r++;
        }
        return numPairing;
    }

    @Override
    public AbstractIndividualInterface[][] getPairing() {
        return pairing;
    }

    @Override
    public void setAvailablePopulation(AbstractIndividualInterface[][] available) {
        this.available = available;
    }

    @Override
    public boolean removeMemberAvailability(AbstractIndividualInterface p) {
        throw new UnsupportedOperationException("Not supported in this verison.");
    }

    @Override
    public boolean memberAvailable(AbstractIndividualInterface p) {
        throw new UnsupportedOperationException("Not supported in this verison.");
    }

    @Override
    public boolean setParameter(String id, Object value) {
        throw new UnsupportedOperationException("Not supported in this verison.");
    }

    @Override
    public Object getParameter(String id) {
        throw new UnsupportedOperationException("Not supported in this verison.");
    }
}
