package engineer.maxbarraclough.txtshuffle.gui;

import java.util.function.Supplier;

/**
 * Not thread-safe, so be sure to only access from the GUI thread
 * @author mb
 */
public final class Model {

    public static final Model INSTANCE = new Model();

    private Model() {
    }

    private Supplier<String[]> dataSetSupplier;
    public void setDataSetSupplier(Supplier<String[]> dss) {
        this.dataSetSupplier = dss;
    }
    public Supplier<String[]> getDataSetSupplier() {
        return this.dataSetSupplier;
    }
    

    private Supplier<byte[]> messageSupplier;
    public void setMessageSupplier(Supplier<byte[]> ms) {
        this.messageSupplier = ms;
    }
    public Supplier<byte[]> getMessageSupplier() {
        return this.messageSupplier;
    }
}
