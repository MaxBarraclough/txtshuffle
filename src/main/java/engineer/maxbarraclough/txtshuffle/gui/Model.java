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
// // // TODO this should probably live in the controller instead

    private Model() {
    }

    private String[] encodedDataSet;
    public void setEncodedDataSet(final String[] ds) {
        this.encodedDataSet = ds;
    }
    public String[] getEncodedDataSet() {
        return this.encodedDataSet;
    }

    private String[] dataSet;
    public void setDataSet(final String[] ds) {
        this.dataSet = ds;
    }
    public String[] getDataSet() {
        return this.dataSet;
    }
    

    private byte[] messageBytes;
    public void setMessageBytes(final byte[] m) {
        this.messageBytes = m;
    }
    public byte[] getMessageBytes() {
        return this.messageBytes;
    }


    private File file;
    public void setFile(final File f)
    {
        this.file = f;
    }
    public File getFile()
    {
        return this.file;
    }


//    /**
//     * TODO make this no-allocate
//     *
//     * Side-effect: shuffles this.dataSet to encode the message
//     * (according to this.messageBytes)
//     *
//     * @throws engineer.maxbarraclough.txtshuffle.backend.TxtShuffle.NumberTooGreatException
//     */
//    private String[] doEncode() throws TxtShuffle.NumberTooGreatException
//    {
//                // TODO move this logic to the backend package
//
//                final BigInteger bi = new BigInteger(this.messageBytes);
//
//              // TODO handle NumberTooGreatExceptionproperly
//		final int[] compact =
//				VectorConversions.intToCompactVector(
//				  this.dataSet.length,
//				  bi
//				);
//
//              // TODO can we make a no-allocate version of this method?
//		final int[] isvFromCompact = VectorConversions.compactToIsv(compact);
//
//		java.util.Arrays.sort(this.dataSet); // dataSet is now sorted
//
//                // TODO mutative version of this method, avoiding new array
//		final String[] ret = TxtShuffle.applyIsvToStringArr(this.dataSet, isvFromCompact); // that method is 'pure'
//                // dataSet is now ordered to encode our number
//
//                return ret;
//    }
//
//
//    // TODO make non-static and use the existing members
//    public void encodeIntoFile(final byte[] messageBytes, final String[] dataSet, final File outputFile)
//            throws TxtShuffle.NumberTooGreatException, IOException // TODO proper exception handling
//    {
//
// n.b. THIS METHOD IS UNTESTED AND MAY WELL BE BROKEN!
//        // do we really want to ignore the returned value????
//
//        this.doEncode(); // already done
//
//        // this.dataSet is now scrambled to encode this.messageBytes
//
//        // TODO check the file doesn't already exist
//        try (FileWriter fw = new FileWriter(outputFile)) {
//
//            if (this.dataSet.length > 0) {
//                fw.write(dataSet[0]);
//
//                // https://stackoverflow.com/a/209771
//                //final String lineSep = System.getProperty("line.separator");
//
//                // https://stackoverflow.com/a/10937340
//                final String lineSep = System.lineSeparator();
//
//                for (int i = 1; i != this.dataSet.length; ++i) {
//                    fw.write(lineSep);
//                    final String s = this.dataSet[i];
//                    fw.write(s);
//                }
//            }
//        } // no catch or finally, we just want the with-resource feature
//    }

}
