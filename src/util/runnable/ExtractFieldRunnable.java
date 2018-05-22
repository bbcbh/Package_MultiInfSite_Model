package util.runnable;

import java.io.File;
import java.util.concurrent.Callable;

public class ExtractFieldRunnable implements Callable<Object[]> {

    Object[] extFields;
    File eFile;

    public ExtractFieldRunnable(File eFile) {
        this.eFile = eFile;
    }

   
    public void run() {
        try (java.io.ObjectInputStream objIn = new java.io.ObjectInputStream(new java.io.FileInputStream(eFile))) {            
            extFields = (Object[]) objIn.readObject();
            System.out.println("Importing population fields from " + eFile.getAbsolutePath() + " done.");

        } catch (Exception ex) {
            System.out.println("Importing population fields from " + eFile.getAbsolutePath() + " failed due to " + ex.toString() + " ! Using default.");
        }
    }

    public Object[] getExtFields() {
        return extFields;
    }

    @Override
    public Object[] call() throws Exception {
        run();
        return extFields;
    }

}
