package engineer.maxbarraclough.txtshuffle.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mb
 */
public final class TextMessageSupplier implements Supplier<byte[]> {

    private final File file;

    public TextMessageSupplier(File fileArg) {
        this.file = fileArg;
    }

    /**
     * In case of exception, return null
     * @return
     */
    @Override
    public byte[] get() {
        byte[] bytes = null;

        try {
            bytes = TextMessageSupplier.textFileToByteArr(this.file);
        } catch (IOException ex) {
//            Logger.getLogger(TextMessageSupplier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bytes;
    }


/**
 * File must contain only ordinary letters, numbers, space, and newline.
 * @param file
 * @return
 * @throws FileNotFoundException
 * @throws IOException
 */
    private static byte[] textFileToByteArr(final File file) throws FileNotFoundException, IOException {

        // TODO can we use Java's CharSets instead of this manual switch business?
        // https://docs.oracle.com/javase/8/docs/api/java/util/Base64.Decoder.html#wrap-java.io.InputStream-
        final StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int currentInt;
            do {
                currentInt = reader.read();

                // // // // TODO remove any '\r' characters
                switch (currentInt) {
                    case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': case 'h': case 'i':
                    case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
                    case 's': case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z': case 'A':
                    case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J':
                    case 'K': case 'L': case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S':
                    case 'T': case 'U': case 'V': case 'W': case 'X': case 'Y': case 'Z': case '0': case '1':
                    case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
                        break;
                    case ' ':
                        currentInt = (int)'+';
                        break;
                    case '\n':
                        currentInt = (int)'/';
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            } while (-1 != currentInt);
        }

        final String str = sb.toString();
        final byte[] bytes = str.getBytes(StandardCharsets.ISO_8859_1); // identical to using a Base64 decoder
        return bytes;
    }

}
