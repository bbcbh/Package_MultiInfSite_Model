package util.runnable;

/**
 *
 * @author Ben Hui
 */
public class CloneObjectRunnable implements java.util.concurrent.Callable<Object> {

    private Object clonedObject;
    private final Object orgObject;

    public CloneObjectRunnable(Object orgObject) {
        this.orgObject = orgObject;
    }

    public Object getClonedObject() {
        return clonedObject;
    }
    
    public void run() {
        try {
            java.io.ByteArrayOutputStream bos;
            java.io.ObjectOutputStream out;
            java.io.ByteArrayInputStream bis;
            java.io.ObjectInputStream in;

            bos = new java.io.ByteArrayOutputStream();
            out = new java.io.ObjectOutputStream(bos);
            out.writeObject(orgObject);
            bis = new java.io.ByteArrayInputStream(bos.toByteArray());
            in = new java.io.ObjectInputStream(bis);
            clonedObject = in.readObject();
        } catch (java.io.IOException | ClassNotFoundException ex) {
            // Debugging statements
            java.io.StringWriter wri = new java.io.StringWriter();
            ex.printStackTrace(new java.io.PrintWriter(wri));
            System.err.println(wri.toString());
        }

    }

    @Override
    public Object call() throws Exception {
        run();
        return clonedObject;

    }
}
