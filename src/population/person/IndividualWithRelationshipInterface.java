package population.person;

import person.AbstractIndividualInterface;

/**
 * An extends of an AbstractIndividualInterface which also
 * include relationship status.
 * @author Ben Hui
 */
public interface IndividualWithRelationshipInterface extends AbstractIndividualInterface{

    /**
     * Return the maximum number of long term partners allowed for this person
     * @return the maximum number of partners allowed for this person
     */
    int getMaxPartners();

    /**
     * Get time (in days) it needs to be passed
     * before this person becomes available for pairing.
     *
     * @return the value of timeUntilNextRelationship
     */
    double getTimeUntilNextRelationship();

    /**
     * Set the maximum number of long term partners allowed for this person.
     *
     * @param maxPartners maximum number of partners allow
     */
    void setMaxPartners(int maxPartners);

    /**
     * Set the minimum time (in days) it needs to be
     * passed before this person becomes available for pairing
     *
     * @param timeUntilNextRelationship new value of timeUntilNextRelationship
     */
    void setTimeUntilNextRelationship(double timeUntilNextRelationship);
    
}
