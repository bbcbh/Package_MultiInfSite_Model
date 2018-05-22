package util;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import sim.SimulationInterface;

/**
 * Static methods for object/array manipulations
 * @author Ben Hui
 * @version 20180521
 */
public class StaticMethods {

    //private static java.util.regex.Pattern PRIMITIVE_ARRAY_PATTERN = java.util.regex.Pattern.compile("\\[.*?\\]");
    //private static java.util.regex.Pattern MULTI_ARRAY_PATTERN = java.util.regex.Pattern.compile("\\[\\[+.*?\\]+\\]");
    public static int[] accumulativeArray(int total, float[] ratio) {
        int[] result = new int[ratio.length];
        result[0] = (int) (total * ratio[0]);
        for (int i = 1; i < result.length; i++) {
            result[i] = result[i - 1] + (int) (total * ratio[i]);
        }
        return result;
    }

    public static float[] accumulativeArray(float[] ratio) {
        float[] result = new float[ratio.length];
        result[0] = ratio[0];
        for (int i = 1; i < result.length; i++) {
            result[i] = result[i - 1] + ratio[i];
        }
        return result;
    }

    public static int[] accumulativeArray(int[] ratio) {
        int[] result = new int[ratio.length];
        result[0] = ratio[0];
        for (int i = 1; i < result.length; i++) {
            result[i] = result[i - 1] + ratio[i];
        }
        return result;
    }

    /**
     * A convenience method of generating parameter required for Gamma distribution. For Gamma distribution, <code> alpha = mean*mean / variance, lambda = 1 / (variance / mean);</code> *
     *
     * @param input mean and standard derivation of the distribution.
     * @return alpha (shape) and lambda (beta, or rate) value
     */
    public static double[] generatedGammaParam(double[] input) {
        // For Gamma distribution
        // alpha = mean*mean / variance
        // lambda = 1 / (variance / mean);

        double[] res = new double[2];
        double var = input[1] * input[1];
        // lambda
        res[1] = input[0] / var;
        //alpha
        res[0] = input[0] * res[1];
        return res;
    }

    /**
     * A convenience method of generating parameter required for Beta distribution. For Beta distribution, <code>alpha = mean*(mean*(1-mean)/variance - 1), beta = (1-mean)*(mean*(1-mean)/variance - 1) </code>
     *
     * @param input mean and standard derivation of the distribution.
     * @return alpha and beta value
     */
    public static double[] generatedBetaParam(double[] input) {
        // For Beta distribution, 
        // alpha = mean*(mean*(1-mean)/variance - 1)
        // beta = (1-mean)*(mean*(1-mean)/variance - 1)
        double[] res = new double[2];
        double var = input[1] * input[1];
        double rP = input[0] * (1 - input[0]) / var - 1;
        //alpha
        res[0] = rP * input[0];
        //beta
        res[1] = rP * (1 - input[0]);
        return res;

    }

    public static Object propStrToObject(String ent, Class cls) {
        Object res = null;
        if (ent != null && !ent.isEmpty()) {
            if ("null".equalsIgnoreCase(ent)) {
                res = null;
            } else if (String.class.equals(cls)) {
                res = ent;
            } else if (Integer.class.equals(cls)) {
                res = Integer.valueOf(ent);
            } else if (Long.class.equals(cls)) {
                res = Long.valueOf(ent);
            } else if (Boolean.class.equals(cls)) {
                res = Boolean.valueOf(ent);
            } else if (Float.class.equals(cls)) {
                res = Float.valueOf(ent);
            } else if (cls.isArray()) {
                res = parsePrimitiveArray(ent, cls);
            } else {
                System.err.print("StaticMehthod.propToObject: Parsing of '" + ent + "' to class " + cls.getName() + " not yet supported.");
            }
        }
        return res;
    }

    public static String objectToPropStr(Object ent, Class cls) {
        String res = "";
        if (ent != null) {
            if (boolean[].class.equals(ent)) {
                res = Arrays.toString((boolean[]) ent);
            } else if (int[].class.equals(cls)) {
                res = Arrays.toString((int[]) ent);
            } else if (float[].class.equals(cls)) {
                res = Arrays.toString((float[]) ent);
            } else if (double[].class.equals(cls)) {
                res = Arrays.toString((double[]) ent);
            } else if (cls.isArray()) {
                res = Arrays.deepToString((Object[]) ent);
            } else {
                res = ent.toString();
            }
        }
        return res;
    }

    private static Object parsePrimitiveArray(String arrayStr, Class c) {
        Object res = null;
        try {
            if (arrayStr != null && !arrayStr.isEmpty() && !"null".equalsIgnoreCase(arrayStr)) {

                String numOnly = arrayStr.substring(1, arrayStr.length() - 1); // Exclude the ending brackets
                String[] splitNum = numOnly.split(",");

                if (splitNum.length == 1 && splitNum[0].isEmpty()) { // Special case for empty string
                    splitNum = new String[0];
                }
                if (boolean[].class.equals(c)) {
                    res = new boolean[splitNum.length];
                    for (int i = 0; i < ((boolean[]) res).length; i++) {
                        ((boolean[]) res)[i] = Boolean.getBoolean(splitNum[i].trim());
                    }
                } else if (int[].class.equals(c)) {
                    res = new int[splitNum.length];
                    for (int i = 0; i < ((int[]) res).length; i++) {
                        ((int[]) res)[i] = Integer.parseInt(splitNum[i].trim());
                    }
                } else if (float[].class.equals(c)) {
                    res = new float[splitNum.length];

                    for (int i = 0; i < ((float[]) res).length; i++) {
                        ((float[]) res)[i] = Float.parseFloat(splitNum[i].trim());
                    }

                } else if (double[].class.equals(c)) {
                    res = new double[splitNum.length];
                    for (int i = 0; i < ((double[]) res).length; i++) {
                        ((double[]) res)[i] = Double.parseDouble(splitNum[i].trim());
                    }
                } else if (c.isArray()) {

                    int numGenArr = numOnly.trim().length() == 0 ? 0 : 1;
                    int bBal = 0;

                    for (int b = 0; b < numOnly.length(); b++) {
                        if (numOnly.charAt(b) == '[') {
                            bBal++;
                        } else if (numOnly.charAt(b) == ']') {
                            bBal--;
                        } else if (numOnly.charAt(b) == ',' && bBal == 0) {
                            numGenArr++;
                        }
                    }

                    StringBuilder subString = new StringBuilder();
                    res = java.lang.reflect.Array.newInstance(c.getComponentType(), numGenArr);

                    int entNum = 0;
                    for (int b = 0; b < numOnly.length(); b++) {
                        if (numOnly.charAt(b) == '[') {
                            bBal++;
                        } else if (numOnly.charAt(b) == ']') {
                            bBal--;
                        }
                        if (numOnly.charAt(b) == ',' && bBal == 0) {
                            ((Object[]) res)[entNum] = parsePrimitiveArray(subString.toString().trim(), c.getComponentType());
                            subString = new StringBuilder();
                            entNum++;
                        } else {
                            subString.append(numOnly.charAt(b));
                        }
                    }

                    if (subString.length() != 0) { // Last entry
                        ((Object[]) res)[entNum] = parsePrimitiveArray(subString.toString().trim(), c.getComponentType());
                    }

                } else {
                    System.err.print("StaticMehthod.parsePrimitiveArray: Parsing of string '"
                            + arrayStr + "' to class " + c.getName() + " not yet supported.");
                }
            }
        } catch (NumberFormatException ex) {
            throw ex;
        }
        return res;
    }

    protected static int[][][] extractCountTable(int numSim, ObjectInputStream objStr, int[][][] countTableFull, int numCol) throws IOException, ClassNotFoundException {
        int[] entryLength = null;
        for (int simIndex = 0; simIndex < numSim; simIndex++) {
            Object obj = objStr.readObject();
            int[][][] snapcount = (int[][][]) obj;
            if (countTableFull == null) {
                entryLength = new int[snapcount[0].length];
                for (int cId = 0; cId < snapcount[0].length; cId++) {
                    entryLength[cId] = snapcount[0][cId].length;
                    numCol += entryLength[cId];
                }
                numCol++;
                countTableFull = new int[numSim][snapcount.length][numCol];
                for (int sI = 0; sI < numSim; sI++) {
                    for (int r = 0; r < countTableFull[sI].length; r++) {
                        countTableFull[sI][r][0] = r;
                    }
                }
            }
            for (int t = 0; t < snapcount.length; t++) {
                int cBase = 0;
                for (int cId = 0; cId < snapcount[t].length; cId++) {

                    for (int cType = 0; cType < snapcount[t][cId].length; cType++) {
                        countTableFull[simIndex][t][cBase + cType + 1] = snapcount[t][cId][cType];
                    }
                    cBase += entryLength[cId];
                }
            }
        }
        return countTableFull;
    }

    public static Object[] cloneFieldsArray(Object[] ent)
            throws IOException, ClassNotFoundException {

        Object[] res = null;
        java.io.ByteArrayOutputStream bos;
        java.io.ObjectOutputStream out;
        java.io.ByteArrayInputStream bis;
        java.io.ObjectInputStream in;

        res = new Object[ent.length];

        for (int i = 0; i < res.length; i++) {
            bos = new java.io.ByteArrayOutputStream();
            out = new java.io.ObjectOutputStream(bos);
            out.writeObject(ent[i]);
            bis = new java.io.ByteArrayInputStream(bos.toByteArray());
            in = new java.io.ObjectInputStream(bis);
            res[i] = in.readObject();
        }

        return res;
    }

    public static void decodeResultObjFile(File objFile, int numSim)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream objStr;
        File targetFile;
        PrintWriter pWri;
        objStr = new ObjectInputStream(new FileInputStream(objFile));
        targetFile = new File(objFile.getAbsolutePath() + ".csv");
        pWri = new PrintWriter(targetFile);
        for (int i = 0; i < numSim; i++) {
            Object obj = objStr.readObject();
            StringBuilder buf = new StringBuilder();
            buf.append(i);
            if (obj instanceof int[]) {
                for (int r = 0; r < ((int[]) obj).length; r++) {
                    buf.append(',');
                    buf.append(((int[]) obj)[r]);
                }
            } else {
                System.err.println("decodeResultObjFile: Warning: Invaild/unsupported result object at " + objFile.getAbsolutePath());
                buf.append(',');
                buf.append(obj);
            }
            pWri.println(buf.toString());
            pWri.flush();
        }

        objStr.close();
        pWri.close();
    }

    public static void decodeColumnPercentage(File objFile, int numSim, int[] denom, int[] nom, String fileSuffix)
            throws IOException, ClassNotFoundException {
        ObjectInputStream objStr;
        File targetFiles;
        PrintWriter pWri;
        int numCol = 0;
        objStr = new ObjectInputStream(new FileInputStream(objFile));
        targetFiles
                = new File(objFile.getAbsolutePath() + "_" + objFile.getParentFile().getName() + "_" + fileSuffix + ".csv");

        pWri = new PrintWriter(targetFiles);
        pWri.println("Time," + fileSuffix);
        pWri.flush();

        int[][][] countTableFull = null; // countTableFull[numSim][row][cI]    
        if (numSim <= 0) {
            numSim = 0;
            try {
                while (objStr.readObject() != null) {
                    numSim++;
                }
            } catch (EOFException ex) {
            }
            objStr = new ObjectInputStream(new FileInputStream(objFile));
        }
        countTableFull = extractCountTable(numSim, objStr, countTableFull, numCol);

        if (countTableFull != null) {
            for (int r = 0; r < countTableFull[0].length; r++) {
                pWri.print(countTableFull[0][r][0]);
                int[] denom_ent = new int[numSim];
                int[] nom_ent = new int[numSim];
                float[] percentage = new float[numSim];

                for (int s = 0; s < numSim; s++) {
                    for (int c = 0; c < countTableFull[s][r].length; c++) {
                        for (int cI = 0; cI < denom.length; cI++) {
                            if (denom[cI] == c) {
                                denom_ent[s] += countTableFull[s][r][c];
                            }
                        }
                        for (int cI = 0; cI < nom.length; cI++) {
                            if (nom[cI] == c) {
                                nom_ent[s] += countTableFull[s][r][c];
                            }
                        }
                    }
                    percentage[s] = 100f * nom_ent[s] / denom_ent[s];
                }
                Arrays.sort(percentage);
                pWri.print(',');
                pWri.print(percentage.length % 2 == 0
                        ? (percentage[(percentage.length) / 2] + percentage[(percentage.length) / 2 - 1]) / 2f : percentage[(percentage.length - 1) / 2]);
                pWri.println();
            }
        }
        pWri.close();
        objStr.close();
    }

    public static void decodeArrFile(File objFile, int numSim, String fileSuffix)
            throws IOException, ClassNotFoundException {
        ObjectInputStream objStr;
        File targetFiles;
        PrintWriter pWri;

        objStr = new ObjectInputStream(new FileInputStream(objFile));
        targetFiles
                = new File(objFile.getAbsolutePath() + "_" + objFile.getParentFile().getName() + "_" + fileSuffix + ".csv");

        pWri = new PrintWriter(targetFiles);
        pWri.println(fileSuffix);
        pWri.flush();

        if (numSim <= 0 || true) {
            numSim = 0;
            try {
                Object obj;
                while ((obj = objStr.readObject()) != null) {
                    //System.out.println("#"+ numSim + ":" +  Arrays.deepToString((Object[]) obj));                    
                    numSim++;
                }
            } catch (EOFException ex) {
            }
            objStr = new ObjectInputStream(new FileInputStream(objFile));
        }

        for (int simIndex = 0; simIndex < numSim; simIndex++) {
            Object obj = null;

            try {
                obj = objStr.readObject();
            } catch (ClassCastException ex) {
                System.out.println("Error from reading result for sim #" + simIndex + ":" + ex.toString());
            }

            if (obj == null) {
                pWri.println("null");
            } else if (obj instanceof int[]) {
                String ent = Arrays.toString((int[]) obj);
                ent = ent.substring(1, ent.length() - 1);
                pWri.println(ent);
            } else if (obj instanceof Object[]) {
                String ent = Arrays.deepToString((Object[]) obj);
                ent = ent.replaceAll("\\[", "");
                ent = ent.replaceAll("\\]", "");
                pWri.println(ent);
            } else {
                pWri.println(obj.toString());
            }
            pWri.flush();
        }
        pWri.close();
        objStr.close();
    }

    public static void decodeColumn(File objFile, int numSim, int columnIndex, String fileSuffix)
            throws IOException, ClassNotFoundException {
        decodeColumn(objFile, numSim, new int[]{columnIndex}, new String[]{fileSuffix});
    }

    public static void decodeColumn(File objFile, int numSim, int[] columnIndex, String[] fileSuffix)
            throws IOException, ClassNotFoundException {
        ObjectInputStream objStr;
        File[] targetFiles;
        PrintWriter[] pWri;
        int numCol = 0;
        objStr = new ObjectInputStream(new FileInputStream(objFile));

        targetFiles = new File[Math.min(columnIndex.length, fileSuffix.length)];
        pWri = new PrintWriter[targetFiles.length];

        for (int f = 0; f < fileSuffix.length; f++) {
            targetFiles[f]
                    = new File(objFile.getAbsolutePath() + "_" + objFile.getParentFile().getName() + "_" + fileSuffix[f] + ".csv");
            pWri[f] = new PrintWriter(targetFiles[f]);
            pWri[f].println("Time," + fileSuffix[f]);
            pWri[f].flush();
        }

        int[][][] countTableFull = null; // countTableFull[numSim][row][cI]
        int[] entryLength = null;
        if (numSim <= 0) {
            numSim = 0;
            try {
                while (objStr.readObject() != null) {
                    numSim++;
                }
            } catch (EOFException ex) {
            }
            objStr = new ObjectInputStream(new FileInputStream(objFile));
        }
        for (int simIndex = 0; simIndex < numSim; simIndex++) {
            Object obj = objStr.readObject();
            int[][][] snapcount = (int[][][]) obj;
            if (countTableFull == null) {
                entryLength = new int[snapcount[0].length];
                for (int cId = 0; cId < snapcount[0].length; cId++) {
                    entryLength[cId] = snapcount[0][cId].length;
                    numCol += entryLength[cId];
                }
                numCol++;
                countTableFull = new int[numSim][snapcount.length][numCol];
                for (int sI = 0; sI < numSim; sI++) {
                    for (int r = 0; r < countTableFull[sI].length; r++) {
                        countTableFull[sI][r][0] = r;
                    }
                }
            }

            for (int t = 0; t < snapcount.length; t++) {
                int cBase = 0;
                for (int cId = 0; cId < snapcount[t].length; cId++) {
                    try {
                        for (int cType = 0; cType < snapcount[t][cId].length; cType++) {
                            countTableFull[simIndex][t][cBase + cType + 1] = snapcount[t][cId][cType];
                        }
                    } catch (NullPointerException ex) {
                        System.out.println("decodeColumn: NullPointerException at Sim #" + simIndex + ", timestep = "
                                + t + " columnBase = " + (cBase + 1));

                    }
                    cBase += entryLength[cId];
                }
            }
        }

        if (countTableFull != null) {
            for (int r = 0; r < countTableFull[0].length; r++) {

                for (int f = 0; f < pWri.length; f++) {

                    pWri[f].print(countTableFull[0][r][0]);

                    for (int s = 0; s < numSim; s++) {
                        pWri[f].print(',');
                        pWri[f].print(countTableFull[s][r][columnIndex[f]]);
                    }
                    pWri[f].println();
                }
            }
        }

        for (PrintWriter outputWriter : pWri) {
            outputWriter.close();
        }
        objStr.close();
    }

    public static void decodeOutputCountFile(File objFile, int numSim, String[] header)
            throws IOException, ClassNotFoundException {
        decodeOutputCountFile(objFile, numSim, header, null);
    }

    public static void decodeOutputCountFile(File objFile, int numSim, String[] header, int[] exclIfEntEqZero)
            throws IOException, ClassNotFoundException {

        ObjectInputStream objStr;
        File[] targetFiles;
        PrintWriter[] pWri;
        int numCol = 0;
        objStr = new ObjectInputStream(new FileInputStream(objFile));

        boolean[] exclSim = new boolean[numSim];

        String exclAdd = exclIfEntEqZero == null ? "" : "_excl";

        targetFiles = new File[]{
            new File(objFile.getAbsolutePath() + "_" + objFile.getParentFile().getName() + exclAdd + "_out_sum.csv"),
            new File(objFile.getAbsolutePath() + "_" + objFile.getParentFile().getName() + exclAdd + "_out_median.csv"),
            new File(objFile.getAbsolutePath() + "_" + objFile.getParentFile().getName() + exclAdd + "_out_max.csv"),
            new File(objFile.getAbsolutePath() + "_" + objFile.getParentFile().getName() + exclAdd + "_out_min.csv"),};

        pWri = new PrintWriter[targetFiles.length];
        for (int f = 0; f < pWri.length; f++) {
            pWri[f] = new PrintWriter(targetFiles[f]);
            for (int h = 0; h < header.length; h++) {
                pWri[f].println(header[h]);
            }
            pWri[f].flush();
        }
        int[][][] countTableFull = null; // countTableFull[numSim][row][cI]
        int[] entryLength = null;
        if (numSim <= 0) {
            numSim = 0;
            try {
                while (objStr.readObject() != null) {
                    numSim++;
                }
            } catch (EOFException ex) {
            }
            objStr = new ObjectInputStream(new FileInputStream(objFile));
        }

        int exclCount = 0;
        for (int simIndex = 0; simIndex < numSim; simIndex++) {
            Object obj = objStr.readObject();
            int[][][] snapcount = (int[][][]) obj;
            if (countTableFull == null) {
                entryLength = new int[snapcount[0].length];
                for (int cId = 0; cId < snapcount[0].length; cId++) {
                    entryLength[cId] = snapcount[0][cId].length;
                    numCol += entryLength[cId];
                }
                numCol++;
                countTableFull = new int[numSim][snapcount.length][numCol];
                for (int sI = 0; sI < numSim; sI++) {
                    for (int r = 0; r < countTableFull[sI].length; r++) {
                        countTableFull[sI][r][0] = r;
                    }
                }
            }

            for (int t = 0; t < snapcount.length; t++) {
                int cBase = 0;
                for (int cId = 0; cId < snapcount[t].length; cId++) {

                    try {
                        for (int cType = 0; cType < snapcount[t][cId].length; cType++) {
                            countTableFull[simIndex][t][cBase + cType + 1] = snapcount[t][cId][cType];
                        }
                    } catch (NullPointerException ex) {
                        System.out.println("decodeOutputCount: NullPointerException at Sim #" + simIndex + ", timestep = "
                                + t + " columnBase = " + (cBase + 1));

                    }
                    cBase += entryLength[cId];
                }

                if (exclIfEntEqZero != null && t == snapcount.length - 1) {
                    boolean excludeBool = true;
                    for (int i = 0; i < exclIfEntEqZero.length && excludeBool; i++) {
                        excludeBool &= countTableFull[simIndex][t][exclIfEntEqZero[i]] == 0;
                    }
                    exclSim[simIndex] = excludeBool;
                    if (exclSim[simIndex]) {
                        exclCount++;
                    }
                }

            }
        }

        if (countTableFull != null) {
            for (int r = 0; r < countTableFull[0].length; r++) {
                for (int c = 0; c < countTableFull[0][r].length; c++) {

                    if (c == 0) {
                        // Index column
                        for (int f = 0; f < pWri.length; f++) {
                            pWri[f].print(countTableFull[0][r][c]);
                        }

                    } else {
                        int[] ent = new int[numSim];
                        int total = 0;
                        for (int s = 0; s < numSim; s++) {
                            if (!exclSim[s]) {
                                ent[s] = countTableFull[s][r][c];
                                total += countTableFull[s][r][c];
                            } else {
                                ent[s] = Integer.MIN_VALUE;
                            }
                        }
                        // Print sum
                        pWri[0].print(total);

                        // Print median                    
                        Arrays.sort(ent);

                        if (exclCount != ent.length) {
                            ent = Arrays.copyOfRange(ent, exclCount, ent.length);
                        }

                        float median = ent.length % 2 == 0 ? (ent[(ent.length) / 2] + ent[(ent.length) / 2 - 1]) / 2f : ent[(ent.length - 1) / 2];
                        pWri[1].print(median);

                        float max = ent[(ent.length) - 1];
                        pWri[2].print(max);

                        float min = ent[0];
                        pWri[3].print(min);

                    }

                    for (int f = 0; f < pWri.length; f++) {
                        if (c + 1 < countTableFull[0][r].length) {
                            pWri[f].print(',');
                        } else {
                            pWri[f].println();
                        }
                    }
                }
            }
        }
        for (int f = 0; f < pWri.length; f++) {
            pWri[f].close();
        }
        objStr.close();

        StringBuilder outS = null;
        for (int i = 0; i < exclSim.length; i++) {
            if (exclSim[i]) {

                if (outS == null) {
                    outS = new StringBuilder();
                } else {
                    outS.append(",");
                }
                outS.append(i);
            }
        }

        if (exclCount > 0 && outS != null) {
            System.out.println("decodeOutputCountFile: Number sim exclued = " + exclCount
                    + " (" + outS.toString() + ")");
            File exclBool = new File(objFile.getParentFile(), SimulationInterface.EXCL_SEED_FILE_PREFIX + System.currentTimeMillis() + ".obj");
            try (ObjectOutputStream outStr = new ObjectOutputStream(new FileOutputStream(exclBool))) {
                outStr.writeObject(exclSim);
            }
            System.out.println("Exclude seed file written at " + exclBool.getAbsolutePath());
        }

    }

}
