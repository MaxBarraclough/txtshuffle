package engineer.maxbarraclough.txtshuffle.gui;

import engineer.maxbarraclough.txtshuffle.backend.TxtShuffle;
import engineer.maxbarraclough.txtshuffle.backend.VectorConversions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;


/**
 * Not thread-safe, so be sure to only access from the GUI thread
 * @author mb
 */
public final class Model {

    public static final Model INSTANCE = new Model();

    private Model() {
    }

    private String[] dataSet;
    public void setDataSet(String[] ds) {
        this.dataSet = ds;
    }
    public String[] getDataSet() {
        return this.dataSet;
    }
    

    private byte[] messageBytes;
    public void setMessageBytes(byte[] m) {
        this.messageBytes = m;
    }
    public byte[] getMessageBytes() {
        return this.messageBytes;
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
    private String[] doEncode() throws TxtShuffle.NumberTooGreatException
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
    public void encodeIntoFile(final byte[] messageBytes, final String[] dataSet, final File outputFile)
            throws TxtShuffle.NumberTooGreatException, IOException // TODO proper exception handling
    {
        this.doEncode();

        // TODO check the file doesn't already exist

        try (FileWriter fw = new FileWriter(outputFile))
        {
            for(String s : dataSet) {
                fw.write(s);
            }
        } // no catch or finally, we just want the with-resource feature
    }

}
