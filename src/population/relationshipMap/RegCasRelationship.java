package population.relationshipMap;

import relationship.SingleRelationship;

/**
 *
 * @author Ben Hui
 * @version 20180521
 *
 * <p>
 * History</p>
 *
 * <p>
 * 20140212: Check support for no acts </p>
 * <p>
 * 20180521: Rework to fit in new package description</p>
 */
public class RegCasRelationship extends SingleRelationship {

    public static final int REL_TYPE_REG = 0;
    public static final int REL_TYPE_CAS = 1;
    protected int type;
    protected int relPt = 0;

    private final int numActsType;
    private boolean[][] actSchedule; // boolean[durations][numActsType];        

    public RegCasRelationship(Integer[] links, int type, int numActsType) {
        super(links);
        this.type = type;
        this.numActsType = numActsType;
    }

    @Override
    public void setDurations(double durations) {
        super.setDurations(durations);
        actSchedule = new boolean[(int) durations][numActsType];
    }

    public void setActSchedule(int actType, float freq, random.RandomGenerator RNG) {
        float f = freq;
        boolean byNum = freq >= 1;
        if (byNum) {
            f = (int) f;
        }

        for (int d = 0; d < actSchedule.length && f > 0; d++) {
            if (byNum) {
                // Number
                actSchedule[d][actType] = (RNG.nextInt(actSchedule.length - d) < f);
                if (actSchedule[d][actType]) {
                    f--;
                }
            } else {
                actSchedule[d][actType] = RNG.nextFloat() < f;
            }
        }

        relPt = 0;

    }

    @Override
    public double incrementTime(double deltaT) {
        double res = super.incrementTime(deltaT);
        relPt += deltaT;
        return res;
    }

    public int getType() {
        return type;
    }

    public int getRelPt() {
        return relPt;
    }

    public boolean[] hasActToday() {
        return actSchedule[relPt];
    }
}
