
package infection;

/**
 *
 * @author Ben Hui
 */
public interface MultiStrainInfectionInterface {
    public void setStrainCoexistMatrix(float[][] mat); // float[existing][new]{probabiliy of coexist}
    public float[][] getStrainCoexistMatrix();
    public float getProbabityCoexist(int strainNum, int infectionStat);
    public void setStrainSpecificParamter(int strainNum, Object[] value);
    public Object[] getStrainSpecificParamter(int strainNum);
    
}
