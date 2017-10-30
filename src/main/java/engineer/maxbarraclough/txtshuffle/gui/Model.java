package engineer.maxbarraclough.txtshuffle.gui;

import engineer.maxbarraclough.txtshuffle.backend.TxtShuffle;
import engineer.maxbarraclough.txtshuffle.backend.VectorConversions;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
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



    /**
     * Does not close the stream
     *
     * TODO make this no-allocate
     *
     * @param messageBytes This array is treated as read-only
     * @param dataSet      NOTE This array will be reordered
     *
     * @throws engineer.maxbarraclough.txtshuffle.backend.TxtShuffle.NumberTooGreatException
     */
    private static String[] doEncode(final byte[] messageBytes, final String[] dataSet) throws TxtShuffle.NumberTooGreatException
    {
                // TODO move this logic to the backend package

                final BigInteger bi = new BigInteger(messageBytes);

                // // TODO handle NumberTooGreatExceptionproperly, somewhere
		final int[] compact =
				VectorConversions.intToCompactVector(
				  dataSet.length,
				  bi
				);

                // TODO can we make a no-allocate version of this method?
		final int[] isvFromCompact = VectorConversions.compactToIsv(compact);

		java.util.Arrays.sort(dataSet); // dataSet is now sorted

                // TODO mutative version of this method, avoiding new array
		final String[] ret = TxtShuffle.applyIsvToStringArr(dataSet, isvFromCompact);
                // dataSet is now ordered to encode our number

                return ret;

    }


    // TODO make non-static and use the existing members
    public static void encodeIntoFile(final byte[] messageBytes, final String[] dataSet, final File outputFile)
            throws TxtShuffle.NumberTooGreatException, IOException // TODO proper exception handling
    {
        Model.doEncode(messageBytes, dataSet);

        // TODO check the file doesn't already exist

        try (FileWriter fw = new FileWriter(outputFile))
        {
            for(String s : dataSet) {
                fw.write(s);
            }
        } // no catch or finally, we just want the with-resource feature
    }

}
