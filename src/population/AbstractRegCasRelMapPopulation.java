package population;

import availability.AbstractAvailability;
import infection.AbstractInfection;
import java.io.IOException;
import java.util.*;
import person.AbstractIndividualInterface;

import population.person.SiteSpecificTransmissivity;
import population.relationshipMap.RegCasRelationship;
import random.RandomGenerator;
import relationship.RelationshipMap;
import relationship.SingleRelationship;

import util.PersonClassifier;
import util.StaticMethods;

/**
 *
 * @author Ben Hui
 * @version 20180521
 *
 * <p>
 * History:
 * </p>
 *
 * <p>
 * 20140922 - Updated setInstantInfection method </p>
 * <p>
 * 20140624 - Added export fields options, declare initialiseInfection as public </p>
 * <p>
 * 20140128 - Added getIndivdualInfectionList method</p>
 * <p>
 * 20140122 - Addition of FIELDS_SUSCEPT</p>
 * <p>
 * 20180521 - Rework to fit in new package description</p>
 * <p>
 * 20180523 - Adjustment to update pair method for those with undefined relationship array</p>
 *
 */
public abstract class AbstractRegCasRelMapPopulation extends AbstractPopulation {

    public static final int FIELDS_SEED = 0;
    public static final int FIELDS_RNG = FIELDS_SEED + 1;
    public static final int FELIDS_POP = FIELDS_RNG + 1;
    public static final int FIELDS_REL_MAP = FELIDS_POP + 1;
    public static final int FIELDS_AVAIL = FIELDS_REL_MAP + 1;
    public static final int FIELDS_NEXT_ID = FIELDS_AVAIL + 1;
    public static final int FIELDS_NUMINF_LASTSTEP = FIELDS_NEXT_ID + 1;
    public static final int FIELDS_TRANSMIT = FIELDS_NUMINF_LASTSTEP + 1;
    public static final int FIELDS_SUSCEPT = FIELDS_TRANSMIT + 1;
    public static final int FIELDS_INF_RNG = FIELDS_SUSCEPT + 1;
    public static final int LENGTH_FIELDS = FIELDS_INF_RNG + 1;
    protected Object[] fields = new Object[LENGTH_FIELDS];
    protected PersonClassifier[] snapshotClassifier;
    protected int[][] snapshotCount;
    public static final int ONE_YEAR_INT = 360;

    private int[] eventsPointer = null;

    public void exportPop(java.io.ObjectOutputStream objStr) throws IOException {
        objStr.writeObject(fields);
    }

    public int[] getEventsPointer() {
        return eventsPointer;
    }

    public void setEventsPointer(int[] eventsPointer) {
        this.eventsPointer = eventsPointer;
    }

    public PersonClassifier[] getSnapshotClassifier() {
        return snapshotClassifier;
    }

    public void setSnapshotClassifier(PersonClassifier[] snapshotClassifier) {
        this.snapshotClassifier = snapshotClassifier;
    }

    public int[][] getSnapshotCount() {
        return snapshotCount;
    }

    public void setSnapshotCount(int[][] snapshotCount) {
        this.snapshotCount = snapshotCount;
    }
    private Map<Integer, AbstractIndividualInterface> localData;

    // <editor-fold defaultstate="collapsed" desc="Generate local dataMap">
    protected Map<Integer, AbstractIndividualInterface> generateLocalDataMap() {
        Map<Integer, AbstractIndividualInterface> res; // Id -> Person            

        res = new Map<Integer, AbstractIndividualInterface>() {
            // Id -> Array index, with row = pid  / allPerson.length
            // and col = pid % allPerson.length;
            ArrayList<int[]> indexMap = new ArrayList<int[]>();

            @Override
            public int size() {
                return getPop().length;
            }

            @Override
            public boolean isEmpty() {
                return indexMap.isEmpty();
            }

            @Override
            public boolean containsKey(Object key) { // Person Id
                try {
                    int row = ((Number) key).intValue() / getPop().length;
                    int col = ((Number) key).intValue() % getPop().length;
                    return row < indexMap.size() && indexMap.get(row)[col] >= 0;
                } catch (ClassCastException ex) {
                    return false;
                }
            }

            @Override
            public AbstractIndividualInterface get(Object key) { // Person Id
                try {
                    int row = ((Number) key).intValue() / getPop().length;
                    int col = ((Number) key).intValue() % getPop().length;

                    if (row < indexMap.size()) {
                        int allPersonIndex = indexMap.get(row)[col];
                        if (allPersonIndex < 0) {
                            return null;
                        } else {
                            if (getPop()[allPersonIndex].getId() != ((Number) key).intValue()) {
                                return null;   //Expired person
                            } else {
                                return getPop()[allPersonIndex];
                            }
                        }
                    } else {
                        return null;
                    }
                } catch (ClassCastException ex) {
                    return null;
                }
            }

            @Override
            public AbstractIndividualInterface put(Integer key, AbstractIndividualInterface value) { // Key = Array Index
                if (key.intValue() > getPop().length) {
                    throw new IllegalArgumentException(getClass().getName() + ".put: Key should be array index < " + getPop().length);
                }
                int row = value.getId() / getPop().length;
                int col = value.getId() % getPop().length;

                while (indexMap.size() <= row) { // Add a new row
                    int[] newRow = new int[getPop().length];
                    Arrays.fill(new int[getPop().length], -1);
                    indexMap.add(newRow);
                }
                indexMap.get(row)[col] = key.intValue();
                int nKey = ((Number) key).intValue();
                getPop()[nKey] = value;
                return value;
            }

            @Override
            public boolean containsValue(Object value) {
                try {
                    AbstractIndividualInterface p = (AbstractIndividualInterface) value;
                    int row = p.getId() / getPop().length;
                    int col = p.getId() % getPop().length;
                    return row < indexMap.size() && indexMap.get(row)[col] >= 0;
                } catch (ClassCastException ex) {
                    return false;
                }
            }

            @Override
            public AbstractIndividualInterface remove(Object key) {
                try {
                    int row = ((Number) key).intValue() / getPop().length;
                    int col = ((Number) key).intValue() % getPop().length;
                    if (row < indexMap.size() && indexMap.get(row)[col] >= 0) {
                        int oldIndex = indexMap.get(row)[col];
                        indexMap.get(row)[col] = -1;
                        return getPop()[oldIndex];
                    } else {
                        return null;
                    }
                } catch (ClassCastException ex) {
                    return null;
                }
            }

            @Override
            public void putAll(Map<? extends Integer, ? extends AbstractIndividualInterface> m) {
                for (Integer arrKey : m.keySet()) {
                    put(arrKey, m.get(arrKey)); // Just stick with put function
                }
            }

            @Override
            public void clear() {
                indexMap.clear();
            }

            @Override
            public Set<Integer> keySet() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Collection<AbstractIndividualInterface> values() {
                return Arrays.asList(Arrays.copyOf(getPop(), getPop().length, AbstractIndividualInterface[].class));
            }

            @Override
            public Set<Map.Entry<Integer, AbstractIndividualInterface>> entrySet() {
                java.util.HashMap<Integer, AbstractIndividualInterface> res = new java.util.HashMap<Integer, AbstractIndividualInterface>();
                int r = 0;
                for (int[] row : indexMap) {
                    for (int c = 0; c < row.length; c++) {
                        if (row[c] > 0) {
                            AbstractIndividualInterface ent;
                            if (getPop()[row[c]].getId() == (r * getPop().length) + c) {
                                ent = getPop()[row[c]];
                                res.put(ent.getId(), ent);
                            }
                        }
                    }
                    r++;
                }
                return res.entrySet();

            }
        };

        // Filling initial indexMap
        for (int i = 0; i < getPop().length; i++) {
            res.put(i, getPop()[i]);
        }

        return res;
    }

    protected Map<Integer, AbstractIndividualInterface> getLocalData() {
        if (localData == null) {
            localData = generateLocalDataMap();
        }
        return localData;
    }
// </editor-fold>

    public RelationshipMap[] getRelMap() {
        return (RelationshipMap[]) fields[FIELDS_REL_MAP];
    }

    public void setRelMap(RelationshipMap[] relMap) {
        fields[FIELDS_REL_MAP] = relMap;
    }

    public AbstractAvailability[] getAvailablity() {
        return (AbstractAvailability[]) fields[FIELDS_AVAIL];
    }

    public void setAvailablity(AbstractAvailability[] availablity) {
        fields[FIELDS_AVAIL] = availablity;
    }

    public RandomGenerator getRNG() {
        return (RandomGenerator) fields[FIELDS_RNG];
    }

    public void setRNG(RandomGenerator RNG) {
        fields[FIELDS_RNG] = RNG;
    }

    public long getSeed() {
        return ((Long) fields[FIELDS_SEED]).longValue();
    }

    public void setSeed(long seed) {
        fields[FIELDS_SEED] = new Long(seed);
    }

    @Override
    public AbstractIndividualInterface[] getPop() {
        return (AbstractIndividualInterface[]) fields[FELIDS_POP];
    }

    @Override
    protected void setPop(AbstractIndividualInterface[] pop) {
        fields[FELIDS_POP] = pop;
    }

    protected boolean availableForPartnership(AbstractIndividualInterface person, int mappingIndex) {
        // Assume maximum of 1 regular partner or casual         
        return !getRelMap()[mappingIndex].containsVertex(person.getId())
                || getRelMap()[mappingIndex].degreeOf(person.getId()) < 1;
    }

    public Class getFieldClass(int fieldNum) {
        if (fieldNum < getFields().length) {
            return getFields()[fieldNum] != null ? getFields()[fieldNum].getClass() : null;
        } else {
            throw new UnsupportedOperationException(getClass().getName() + ".getFieldClass: Field #" + fieldNum + " not supported");
        }
    }

    protected Object[] getFields() {
        return fields;
    }

    protected Object[] setFields(Object[] fields) {
        Object[] ori = this.fields;
        this.fields = fields;
        return ori;
    }

    @Override
    protected SingleRelationship formRelationship(AbstractIndividualInterface[] pair, RelationshipMap relMap, int d) {
        for (int i = 0; i < getRelMap().length; i++) {
            if (getRelMap()[i] == relMap) {
                return formRelationship(pair, relMap, d, i);
            }
        }
        return null;
    }

    @Override
    public Object getValue(String identifier, int id) {
        if (id > 0) {
            return getFields()[id];
        } else {
            throw new UnsupportedOperationException(getClass().getName() + ".getValue: Input for [" + identifier + "," + id + "] not supported");
        }
    }

    @Override
    public boolean setParameter(String identifier, int id, Object value) {
        if (id >= 0 && id < getFields().length) {
            getFields()[id] = value;
            return true;
        } else {
            throw new UnsupportedOperationException(getClass().getName() + ".setParameter: Input [" + identifier + "," + id + "] not supported");
        }
    }

    protected abstract SingleRelationship formRelationship(AbstractIndividualInterface[] pair, RelationshipMap relMap, int d, int mapType);

    /**
     * Perform acts between two individuals
     *
     * @param rel RelationshipId
     * @return a boolean matrix res, with res[actTypIde][pIdI] = true if infection is transmitted successfully to (pIdI+1)%2
     */
    protected abstract boolean[][] performAct(RegCasRelationship rel);

    protected void updatePairs() {

        for (int map = 0; map < getRelMap().length; map++) {

            RelationshipMap relMap = getRelMap()[map];

            // Update existing             
            SingleRelationship[] relArr = relMap.getRelationshipArray();

            if (relMap.edgeSet().size() != relArr.length) {
                relArr = relMap.edgeSet().toArray(new SingleRelationship[relMap.edgeSet().size()]);                
            }

            for (SingleRelationship relArr1 : relArr) {
                if (relArr1.incrementTime(1) <= 0) {
                    relMap.removeEdge(relArr1);
                } else {
                    performAct((RegCasRelationship) relArr1);
                }
            }

            // No need to be sorted 
            getAvailablity()[map].setAvailablePopulation(getRelMap()[map].getPersonsAvailable(null, getLocalData()));

            // Generate new pairing
            int pairNum = getAvailablity()[map].generatePairing();

            AbstractIndividualInterface[][] pairs = getAvailablity()[map].getPairing();
            for (int pairId = 0; pairId < pairNum; pairId++) {
                RegCasRelationship rel = (RegCasRelationship) formRelationship(pairs[pairId], getRelMap()[map], getGlobalTime(), map);

                if (rel != null) {
                    performAct(rel);
                }
            }
        }
    }

    public int[] getNumInf() {
        if (fields[FIELDS_NUMINF_LASTSTEP] == null) {
            fields[FIELDS_NUMINF_LASTSTEP] = new int[getInfList().length];
        }
        return (int[]) fields[FIELDS_NUMINF_LASTSTEP];
    }

    @Override
    protected void incrementTime(int deltaT) {
        super.incrementTime(deltaT);
        if (getNumInf() != null) {
            Arrays.fill(getNumInf(), 0);
        }
    }

    public void introduceStrains(int infId, PersonClassifier infClassifer, float[] numIntroduced, int strainNum) {
        //System.out.println("Strains #" + strainNum + " introduced at time = " + getGlobalTime());

        int[] numForEachStrainByClassifier = new int[infClassifer.numClass()];
        int[] numInfTotalByClassifier = new int[infClassifer.numClass()];

        for (int i = 0; i < getPop().length; i++) {
            if (getPop()[i] instanceof SiteSpecificTransmissivity) {
                int cI = infClassifer.classifyPerson(getPop()[i]);
                if (cI >= 0 // && getPop()[i].getInfectionStatus(infId) != AbstractIndividualInterface.INFECT_S
                        && strainNum > ((SiteSpecificTransmissivity) getPop()[i]).getCurrentStrainsAtSite()[getInfList()[infId].getInfectionIndex()]) {
                    numInfTotalByClassifier[cI]++;
                }
            }
        }

        for (int c = 0; c < infClassifer.numClass(); c++) {
            numForEachStrainByClassifier[c] = Math.round(numIntroduced[c] >= 1
                    ? numIntroduced[c] : numInfTotalByClassifier[c] * numIntroduced[c]);
        }

        for (int i = 0; i < getPop().length; i++) {
            if (getPop()[i] instanceof SiteSpecificTransmissivity) {
                int cI = infClassifer.classifyPerson(getPop()[i]);
                if (cI >= 0 // && getPop()[i].getInfectionStatus(infId) != AbstractIndividualInterface.INFECT_S
                        && strainNum > ((SiteSpecificTransmissivity) getPop()[i]).getCurrentStrainsAtSite()[getInfList()[infId].getInfectionIndex()]
                        && numForEachStrainByClassifier[cI] > 0) {
                    RandomGenerator infRNG = getInfRNG()[infId];
                    if (infRNG.nextInt(numInfTotalByClassifier[cI]) < numForEachStrainByClassifier[cI]) {
                        if (getPop()[i].getInfectionStatus()[getInfList()[infId].getInfectionIndex()] == AbstractIndividualInterface.INFECT_S) {
                            // Introduce strain to non infected
                            getInfList()[infId].infecting(getPop()[i]);
                        }
                        ((SiteSpecificTransmissivity) getPop()[i]).getCurrentStrainsAtSite()[getInfList()[infId].getInfectionIndex()] = strainNum;

                        System.out.println("PID:" + getPop()[i].getId() + ": Strain " + strainNum + " at site " + getInfList()[infId].getInfectionIndex()
                                + " of status " + getPop()[i].getInfectionStatus()[getInfList()[infId].getInfectionIndex()] + " at " + getGlobalTime());
                        numForEachStrainByClassifier[cI]--;
                    }
                    numInfTotalByClassifier[cI]--;
                }
            }
        }
    }

    @Override
    public int[] setInstantInfection(int infId, PersonClassifier infClassifer, float[] prevalOrg, int preExposeMax) {
        return setInstantInfection(infId, infClassifer, prevalOrg, preExposeMax, null);

    }

    public int[] setInstantInfection(int infId, PersonClassifier infClassifer,
            float[] prevalByClass, int preExposeMax, float[][] strainDecompositionByClassStrains) {

        float[] preval = Arrays.copyOf(prevalByClass, prevalByClass.length);
        boolean[] byCount = new boolean[preval.length];
        boolean preRun = false;
        int[] popCount = new int[preval.length];
        int[] numInf;
        int[] numInfByClass = new int[preval.length];

        if (fields[FIELDS_NUMINF_LASTSTEP] == null) {
            fields[FIELDS_NUMINF_LASTSTEP] = new int[getInfList().length];
        }
        numInf = (int[]) fields[FIELDS_NUMINF_LASTSTEP];
        numInf[infId] = 0;

        for (int i = 0; i < byCount.length; i++) {
            byCount[i] = preval[i] >= 1;
            preRun |= byCount[i];
        }

        if (preRun) {
            for (AbstractIndividualInterface pop : getPop()) {
                int cI = infClassifer.classifyPerson(pop);
                if (cI >= 0) {
                    popCount[cI]++;
                }
            }
            //System.out.println(getGlobalTime() + ": Num Possible infection: " + Arrays.toString(popCount));

            for (int c = 0; c < popCount.length; c++) {
                if (byCount[c] && preval[c] > popCount[c]) {
                    System.out.println("Warning: Initial preval count of " + preval[c]
                            + " is larger than " + popCount[c] + ". Assuming 100% preval.");
                    preval[c] = Math.min(preval[c], popCount[c]);
                }
            }
        }

        for (int i = 0; i < getPop().length; i++) {
            int cI = infClassifer.classifyPerson(getPop()[i]);
            if (cI >= 0) {
                AbstractIndividualInterface p = getPop()[i];
                boolean infected = preval[cI] > 0 && (!preRun || popCount[cI] > 0);
                int preExpose = 0;
                double infectAt, stateStart, cumulStageTime;

                if (infected) {
                    if (byCount[cI]) {
                        infected = getRNG().nextInt(popCount[cI]) < preval[cI];
                    } else {
                        infected = getRNG().nextFloat() < preval[cI];
                    }
                }

                if (infected) {
                    getInfList()[infId].infecting(p);

                    if (preExposeMax > 0) {
                        preExpose = getRNG().nextInt(preExposeMax);
                    }

                    infectAt = p.getAge() - preExpose;
                    p.setLastInfectedAtAge(getInfList()[infId].getInfectionIndex(), infectAt);

                    stateStart = -preExpose;

                    // Determine status immediately
                    cumulStageTime = p.getTimeUntilNextStage(getInfList()[infId].getInfectionIndex());
                    p.setTimeUntilNextStage(getInfList()[infId].getInfectionIndex(), cumulStageTime + stateStart);

                    while ((p.getTimeUntilNextStage(getInfList()[infId].getInfectionIndex())) < 0) {
                        cumulStageTime += Math.round(getInfList()[infId].advancesState(p));
                        p.setTimeUntilNextStage(getInfList()[infId].getInfectionIndex(), cumulStageTime + stateStart);
                    }

                    if (p.getInfectionStatus(getInfList()[infId].getInfectionIndex()) != AbstractIndividualInterface.INFECT_S) {
                        numInf[getInfList()[infId].getInfectionIndex()]++;
                        // Exclude those who recovered
                        if (byCount[cI]) {
                            preval[cI]--;
                        }
                        numInfByClass[cI]++;

                        if (strainDecompositionByClassStrains == null) { // Default strain of zero
                            ((SiteSpecificTransmissivity) p).getCurrentStrainsAtSite()[getInfList()[infId].getInfectionIndex()] = 0;
                        }

                    }
                }
                popCount[cI]--;
            }
        }

        if (strainDecompositionByClassStrains != null) {
            // strainDecompositionByClassStrains = :new float[classIndex][strain decompositon]
            float[][] numByStrains = new float[strainDecompositionByClassStrains.length][];
            boolean[] strainByNum = new boolean[numByStrains.length];
            int[] totalSpecified = new int[numByStrains.length];

            for (int cI = 0; cI < strainDecompositionByClassStrains.length; cI++) {
                for (int k = 0; k < strainDecompositionByClassStrains[cI].length; k++) {
                    strainByNum[cI] |= strainDecompositionByClassStrains[cI][k] >= 1;
                    if (strainDecompositionByClassStrains[cI][k] > 0) {
                        totalSpecified[cI] += strainDecompositionByClassStrains[cI][k];
                    }
                }

                // Replace negative value as "others"
                for (int k = 0; k < strainDecompositionByClassStrains[cI].length; k++) {
                    if (strainDecompositionByClassStrains[cI][k] < 0) {
                        if (strainByNum[cI]) {
                            strainDecompositionByClassStrains[cI][k]
                                    = numInfByClass[cI] - totalSpecified[cI];
                        } else {
                            strainDecompositionByClassStrains[cI][k]
                                    = 1 - totalSpecified[cI];
                        }
                    }
                }
                numByStrains[cI] = StaticMethods.accumulativeArray(strainDecompositionByClassStrains[cI]);
                if (!strainByNum[cI]) {
                    for (int k = 0; k < strainDecompositionByClassStrains[cI].length; k++) {
                        numByStrains[cI][k] *= numInfByClass[cI];
                    }
                }

            }

            for (int i = 0; i < getPop().length; i++) {
                AbstractIndividualInterface p = getPop()[i];
                if (p instanceof SiteSpecificTransmissivity
                        && p.getInfectionStatus(infId) != AbstractIndividualInterface.INFECT_S) {
                    int cI = infClassifer.classifyPerson(p);
                    if (cI >= 0 && numByStrains[cI][numByStrains[cI].length - 1] > 0) {
                        int pStrain = getRNG().nextInt(
                                Math.round(numByStrains[cI][numByStrains[cI].length - 1]));

                        int pt = 0;
                        while (numByStrains[cI][pt] == 0
                                || pStrain > numByStrains[cI][pt]) {
                            pt++;
                        }
                        ((SiteSpecificTransmissivity) p).getCurrentStrainsAtSite()[getInfList()[infId].getInfectionIndex()] = pt;

                        while (pt < numByStrains[cI].length) {
                            numByStrains[cI][pt]--;
                            pt++;
                        }
                    } else {
                        // All been allocated
                        ((SiteSpecificTransmissivity) p).getCurrentStrainsAtSite()[getInfList()[infId].getInfectionIndex()] = 0;
                    }

                }
            }
        }

        return numInfByClass;

    }

    public void loadPropertiesToPop(Object[] propVal) {
        for (int i = 0; i < propVal.length; i++) {
            if (propVal[i] != null) {
                setParameter(null, i, propVal[i]);
            }
        }
    }

    private RandomGenerator[] getInfRNG() {
        return (RandomGenerator[]) getFields()[FIELDS_INF_RNG];
    }

    public int[] generatedPopSnapCount() {
        return null;
    }

    protected AbstractInfection[] getIndivdualInfectionList(AbstractIndividualInterface person) {
        return getInfList(); // Default
    }

    // If seed == 0 then use pre-exisitng RNG (import)
    public abstract void initialiseInfection(long seed);

    protected abstract AbstractIndividualInterface generateNewPerson(int nextId, AbstractIndividualInterface p, double newAge);
}
