package engineer.maxbarraclough.txtshuffle.gui;

import engineer.maxbarraclough.txtshuffle.backend.TxtShuffle;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class FXMLController implements Initializable {

    public FXMLController(Void dummy) {

    } // block Java sythesising a default constructor
    // public FXMLController() { } // don't define this, to block automatic instantiation by JavaFX

    private enum FinalAction {ENCODE, DECODE}
    private FinalAction finalAction = null;

    // 'select message source' ...
    @FXML private RadioButton smsFromManualRadio;
    @FXML private RadioButton smsFromFileRadio;

    // 'select data source' ...
    @FXML private RadioButton sdsFromManualRadio;
    @FXML private RadioButton sdsFromFileRadio;

    // select source of encoded data (aka decode data)
    @FXML private RadioButton sdcsFromManualRadio;
    @FXML private RadioButton sdcsFromFileRadio;

    @FXML private ToggleGroup smsTg;
    @FXML private ToggleGroup dsTg;
    @FXML private ToggleGroup dcsTg;

    @FXML private TextArea edsTextArea;
    @FXML private TextArea emTextArea;
    @FXML private TextArea edcdsTextArea;

    // @FXML private Text soPathText;
    @FXML private Label soPathLabel;


    private static byte[] binFileToByteArr(final File file) {
        throw new java.lang.UnsupportedOperationException("Not yet implemented");
    }


    /**
     * Handle manually entering 'data' text (*not* message)
     * Do not confuse with handleDataSrcCntButtonAction
     * which concerns selection of which data source (file or 'manual')
     * @param event
     * @throws IOException
     */
    @FXML
    private void handleEntDsButtonAction(ActionEvent event) throws IOException {
        System.out.println("[handleEntDsButtonAction has been called]");

        final String text = this.edsTextArea.getText();
        final String[] split = text.split("\\r?\\n"); // https://stackoverflow.com/a/454913

        Model.INSTANCE.setDataSet(split);

        final Node source = (Node) event.getSource();
        final Window window = source.getScene().getWindow();

        this.goToSelectOutputSink(window, FinalAction.ENCODE);
    }


    /**
     * Handle enter message data through text box
     * @param event
     * @throws IOException
     */
    @FXML
    private void handleEntMsgButtonAction(ActionEvent event) throws IOException {
        final String msgStr = this.emTextArea.getText();

        try {
            final byte[] msgBytes = msgStr.getBytes();

            Model.INSTANCE.setMessageBytes(msgBytes);

            // TODO move this to its own method
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/SelectDataSource.fxml"));
            loader.setController(this);

            final Parent parent = loader.load();

            final Scene scene = new Scene(parent);
            scene.getStylesheets().add("/styles/Styles.css");

            final Node source = (Node)event.getSource();
            final Window window = source.getScene().getWindow();
            final Stage stage = (Stage)window; // ugly cast following https://stackoverflow.com/a/31686775
            stage.setTitle("Select Shopping List Source");
            stage.setScene(scene);
        } catch (IllegalArgumentException iae) {
            final Alert alert = new Alert(
                    Alert.AlertType.NONE,
                    "Message should contain only letters, numbers, spaces, and newlines",
                    ButtonType.OK
            );

            // TODO can we just do 'show' here, as we don't do anything afterwards on the thread?
            alert.showAndWait(); // TODO go async: use 'show' and a listener
        }
    }



    /**
     * Handle selecting a message source
     * @param event
     * @throws IOException
     */
    @FXML
    private void handleMsgSrcContButtonAction(ActionEvent event) throws IOException {
        assert (null != smsFromManualRadio);
        assert (null != smsFromFileRadio);

        final Toggle tog = smsTg.getSelectedToggle();

        if (null == tog) {
            final Alert alert = new Alert(
                    Alert.AlertType.NONE,
                    "Select an option before continuing",
                    ButtonType.OK
            );

            alert.showAndWait(); // TODO go async: use 'show' and a listener
        } else {
            final boolean b1 = (tog == smsFromManualRadio);
            final boolean b2 = (tog == smsFromFileRadio);

            assert (b1 != b2);

            if (b1) {

                // TODO move this to its own method
                {
                    final Node source = (Node) (event.getSource());
                    final Window window = source.getScene().getWindow();

                    this.goToEnterMessageText(window);
                }
            } else {
                final FileChooser fc = new FileChooser();
                fc.setTitle("Select file");

                // FXML binding can only be used for entities within the scene
                // https://stackoverflow.com/a/33933973
                final Node source = (Node)(event.getSource());
                final Window window = source.getScene().getWindow();

                final File file = fc.showOpenDialog(window); // can return null

                if (null != file) {
                    // assert(!file.isDirectory());
                    assert (file.isFile()); // TODO handle that properly

                    // final long fileLength = file.length();

                    System.out.println("You have selected:");
                    System.out.println(file.getPath());
                    System.out.println();


                    final byte[] fileBytes = Files.readAllBytes(file.toPath());
                    Model.INSTANCE.setMessageBytes(fileBytes);

                    // For now, just show success popup
                    final Alert alert = new Alert(
                            Alert.AlertType.NONE,
                            "Successfully read the message file",
                            ButtonType.OK
                    );

                    alert.showAndWait(); // TODO go async: use 'show' and a listener

                    // TODO move this to its own method

                    this.goToSelectDataSource(window);

                } // else do nothing - user cancelled file-selection

            }
        }
    }


    /**
     * Helper: switch stage's scene to the one for entering message text 'manually'
     * @param window
     * @throws IOException
     */
    private void goToEnterMessageText(final Window window) throws IOException {
        final Stage stage = (Stage) window; // ugly cast following https://stackoverflow.com/a/31686775
        stage.setTitle("Enter Message Text");

        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/EnterMessageText.fxml"));
        loader.setController(this);

        final Parent rootParent = loader.load();
        final Scene rootScene = new Scene(rootParent);
        rootScene.getStylesheets().add("/styles/Styles.css");

        stage.setScene(rootScene);
    }


    /**
     * Helper: switch stage's scene to the one for select data source (file or manual entry?)
     * @param window
     * @throws IOException
     */
    private void goToSelectDataSource(final Window window) throws IOException {
        final Stage stage = (Stage) window; // ugly cast following https://stackoverflow.com/a/31686775
        stage.setTitle("Select Shopping List Source");

        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/SelectDataSource.fxml"));
        loader.setController(this);
        final Parent rootParent = loader.load();

        final Scene rootScene = new Scene(rootParent);
        rootScene.getStylesheets().add("/styles/Styles.css");

        stage.setScene(rootScene);
    }




    /**
     * Part of the 'encode' path, *not* decode.
     * Continue on from selecting which data source (file or 'manual' text entry).
     * Transition to the appropriate scene.
     * Do not confuse with handleEntDsButtonAction
     * which handles 'manual' data entry finalisation.
     * @param event
     * @throws IOException
     */
    @FXML
    private void handleDataSrcCntButtonAction(ActionEvent event) throws IOException
    {
        assert(null != sdsFromManualRadio);
        assert(null != sdsFromFileRadio);

        final Toggle tog = dsTg.getSelectedToggle();

        if (null == tog)
        {
            final Alert alert = new Alert(
                    Alert.AlertType.NONE,
                    "Select an option before continuing",
                    ButtonType.OK
            );

            alert.showAndWait(); // TODO go async: use 'show' and a listener
        }
        else
        {
            final boolean b1 = (tog == sdsFromManualRadio);
            final boolean b2 = (tog == sdsFromFileRadio);

            assert(b1 != b2);

            if (b1) {
                    final Node source = (Node)(event.getSource());
                    final Window window = source.getScene().getWindow();

                    final Stage stage = (Stage)window; // ugly cast following https://stackoverflow.com/a/31686775
                    stage.setTitle("Enter Shopping List Text");

                    final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/EnterDataSetText.fxml"));
                    loader.setController(this);
                    final Parent rootParent = loader.load();

                    final Scene rootScene = new Scene(rootParent);
                    rootScene.getStylesheets().add("/styles/Styles.css");

                    stage.setScene(rootScene);

            } else { // from file
                final FileChooser fc = new FileChooser();
                fc.setTitle("Select shopping list file");

                // FXML binding can only be used for entities within the scene
                // https://stackoverflow.com/a/33933973
                final Node source = (Node)event.getSource();
                final Window window = source.getScene().getWindow();

                final File file = fc.showOpenDialog(window); // can return null

                if (null != file) {
                    // assert(!file.isDirectory());
                    final boolean isFile = file.isFile();

                    if (isFile) {
                        final long fileLength = file.length();

                        System.out.println("You have selected:");
                        System.out.println(file.getPath());
                        System.out.println();

                        {
                            // TODO eliminate this awful redundant copy. setDataSet should accept List<String>
                            final List<String> split = Files.readAllLines(file.toPath()); // uses UTF-8 charset // // TODO change?
                            final int count = split.size();
                            final String[] arr = new String[count];
                            split.toArray(arr);
                            Model.INSTANCE.setDataSet(arr);
                            // final String[] split = text.split("\\r?\\n"); // https://stackoverflow.com/a/454913
                        }

                        final Alert alert = new Alert(
                                Alert.AlertType.NONE,
                                "Successfully read the shopping list file",
                                ButtonType.OK
                        );

                        alert.showAndWait(); // TODO go async: use 'show' and a listener

                        this.goToSelectOutputSink(window, FinalAction.ENCODE);
                    } else {
                        final Alert alert = new Alert(
                                Alert.AlertType.NONE,
                                "Please select a file",
                                ButtonType.OK
                        );

                        alert.showAndWait(); // TODO go async: use 'show' and a listener
                    }
                } // else do nothing - user cancelled file-selection

            }
        }

    }


    /**
     * Helper: Set the stage's scene to the one where we select the output sink
     * (currently, we always write to file)
     * @param window
     * @throws IOException
     */
    private void goToSelectOutputSink(final Window window, final FinalAction fa) throws IOException
    {
                        this.finalAction = fa;

                        final Stage stage = (Stage)window; // ugly cast following https://stackoverflow.com/a/31686775
                        stage.setTitle("Select Output");

                        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/SelectOutputSink.fxml"));
                        loader.setController(this);
                        final Parent parent = loader.load();
                        final Scene rootScene = new Scene(parent);
                        rootScene.getStylesheets().add("/styles/Styles.css");

                        stage.setScene(rootScene);
    }



    /**
     * Set the wizard started for the 'Encode' use-case.
     * Do not confuse with handleGoEncodeButtonAction which does the final encode work.
     * Finish up: do the actual work
     * @param event
     * @throws IOException
     */
    @FXML
    private void handleEncodeButtonAction(ActionEvent event) throws IOException {

        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/SelectMessageSource.fxml"));
        loader.setController(this);

        final Parent parent = loader.load();
        final Scene scene = new Scene(parent);
        scene.getStylesheets().add("/styles/Styles.css");
        final Stage stage = new Stage();

        // this call must be made before show()
        stage.initModality(Modality.APPLICATION_MODAL); // https://docs.oracle.com/javase/8/javafx/api/javafx/stage/Stage.html

        stage.setTitle("Select Message Source");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }



    // // // // // // // // DO WE NEED BOTH THESE METHODS???????????

    /**
     * If all is well, progress to next scene
     */
    @FXML
    private void handleSelectOutputFileButtonAction(final ActionEvent event) { // // TODO IS THAT ARG OK ???

        final FileChooser fc = new FileChooser();
        fc.setTitle("Select output file");

        // following https://docs.oracle.com/javase/8/javafx/api/javafx/stage/FileChooser.html
        fc.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter("Text files", "*.txt"),
          new FileChooser.ExtensionFilter("All files", "*.*")
        );

        // FXML binding can only be used for entities within the scene
        // https://stackoverflow.com/a/33933973
        final Node source = (Node) event.getSource();
        final Window window = source.getScene().getWindow();

        final File file = fc.showSaveDialog(window); // can return null

        if (null != file) {
            // assert(!file.isDirectory());
            final boolean existingDir = file.exists() && file.isDirectory();

            if (!existingDir) {

                if (file.exists()) {
                    /* // No need for this, JavaFX does the prompt for us
                    final Alert alert = new Alert(
                            Alert.AlertType.NONE,
                            "Overwrite this file?",
                            ButtonType.YES,
                            ButtonType.NO
                    );

                    final Optional<ButtonType> bt = alert.showAndWait(); // TODO go async: use 'show' and a listener
                    final boolean goAhead = bt.get().equals(ButtonType.YES);
                    */
                    final boolean goAhead = true;
                    if (goAhead) {
                        Model.INSTANCE.setFile(file);
                        try {
                            final String str = file.getCanonicalPath(); // can, in theory, throw
                            this.soPathLabel.setText(str);
                        } catch (Exception exc) {
                            // Do nothing
                        }
                    }
                } else {
                    Model.INSTANCE.setFile(file);
                }
            } else { // then existingDir == true
                final Alert alert = new Alert(
                        Alert.AlertType.NONE,
                        "Please select a file",
                        ButtonType.OK
                );

                alert.showAndWait(); // TODO go async: use 'show' and a listener
            }
        } // else user canceled - failed to select an output file

    }





        // // // // // // // RENAME method now to handle decode, too


    /**
     * Do final encode work.
     * Do not confuse with handleEncodeButtonAction which begins
     * the wizard for the encode process.
     */
    @FXML
    private void handleGoEncodeButtonAction(final ActionEvent event) {
        final byte[] msgBytes = Model.INSTANCE.getMessageBytes();
        final String[] ds = Model.INSTANCE.getDataSet();

        {

            try {

                if (this.finalAction.equals(FinalAction.ENCODE)) // throws if finalAction is null. We want that.
                {
                    if ((null == msgBytes) || (null == ds)) {
                        System.err.println("Failed to initialize a shopping list source");
                    } else {
                        final String[] dataSet = Model.INSTANCE.getDataSet();

                        final boolean compress = (dataSet.length >= 100);

                        final byte[] uncompressedBytes = Model.INSTANCE.getMessageBytes();
                        String[] outputStrs;

                        if (compress) {
                            // final java.io.InputStream is = new java.io.ByteArrayInputStream(uncompressedBytes);
                            // nope, that's for decompressing! final java.util.zip.GZIPInputStream gis = new java.util.zip.GZIPInputStream(is, uncompressedBytes.length);

                            // this object will end up holding the compressed bytes
                            final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                            {
                                final java.util.zip.GZIPOutputStream gos = new java.util.zip.GZIPOutputStream(baos);
                                gos.write(uncompressedBytes);
                                gos.close(); // probably important - https://stackoverflow.com/a/14783672
                            }
                            baos.close();

                            final byte[] compressedBytes = baos.toByteArray();

                            outputStrs = engineer.maxbarraclough.txtshuffle.backend.TxtShuffle.encodeBytesIntoData(
                                    dataSet,
                                    compressedBytes
                            ); // can throw NumberTooGreatException only
                        } else {
                            outputStrs = engineer.maxbarraclough.txtshuffle.backend.TxtShuffle.encodeBytesIntoData(
                                    dataSet,
                                    uncompressedBytes
                            ); // can throw NumberTooGreatException only
                        }

                        final File outFile = Model.INSTANCE.getFile();
                        assert (null != outFile);

//              We already got the go-ahead to overwrite, if applicable
//              asList doesn't do a copy, it's just indirection. Java arrays aren't Iterable
                        // java.nio.file.Files.write(outFile.toPath(), Arrays.asList(outputStrs));//, Charset.defaultCharset());
                        if (outputStrs.length >= 1) {

                            boolean justDidAWrite = false;

                            if (outputStrs.length >= 2) {
                                final Stream<String> stream = Arrays.stream(outputStrs).limit(outputStrs.length - 1);

                                final Iterable<String> it = (Iterable<String>) stream::iterator;
                                // https://stackoverflow.com/q/20129762 , http://www.lambdafaq.org/how-do-i-turn-a-stream-into-an-iterable/

                                // use UTF-8, *not* the default charset. TODO CHANGE? // //
                                // If file already exists, silently overwrite
                                java.nio.file.Files.write(outFile.toPath(), it);//, Charset.defaultCharset());

                                justDidAWrite = true;
                            }

                            // Last line is special: we don't want to put a newline at the end.
                            // The overload of Files.write above, non-negotiably does so.
                            // Rather ugly that we have to open/close the file twice,
                            // particularly as we must now handle the single-line case specially.
                            final java.nio.file.StandardOpenOption[] options
                                    = justDidAWrite ?
                                    new java.nio.file.StandardOpenOption[]{java.nio.file.StandardOpenOption.CREATE,
                                        java.nio.file.StandardOpenOption.WRITE,
                                        java.nio.file.StandardOpenOption.APPEND}
                                    : new java.nio.file.StandardOpenOption[]{java.nio.file.StandardOpenOption.CREATE,
                                        java.nio.file.StandardOpenOption.WRITE,
                                        java.nio.file.StandardOpenOption.TRUNCATE_EXISTING};

                            // use UTF-8, *not* the default charset. TODO CHANGE? // //
                            java.nio.file.Files.write(
                                    outFile.toPath(),
                                    outputStrs[outputStrs.length - 1].getBytes(Charset.forName("UTF-8")),
                                    options
                            );
                        }


                        // these three are the default:
//                            java.nio.file.StandardOpenOption.CREATE_NEW,
//                            java.nio.file.StandardOpenOption.WRITE,
//                            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING

                        // can throw IOException only
//                try (final FileWriter fw = new FileWriter(outFile)) {
//                    for (String str : outputStrs) {
//                        fw.write(str);
//                    }
//                }
                    }
                } else { // we're doing a decode
                    final String[] encodedDS = Model.INSTANCE.getEncodedDataSet();

                    final java.math.BigInteger bi =
                            engineer.maxbarraclough.txtshuffle.backend.TxtShuffle.retrieveNumberFromData(encodedDS);

                    final byte[] uncompressedBytes = bi.toByteArray();
                    byte[] bytes;

                    if (encodedDS.length >= 100)
                    {
                        final java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(uncompressedBytes);
                        final java.util.zip.GZIPInputStream gis = new java.util.zip.GZIPInputStream(bais, uncompressedBytes.length);

                        // InputStream#readAllBytes is Java 9 only - https://stackoverflow.com/a/37681322

                        // based on https://stackoverflow.com/a/17861016
                        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        final byte[] buffer = new byte[512];
                        for (int len; (len = gis.read(buffer)) != -1;)
                        {
                            bos.write(buffer, 0, len);
                        }
                        // bos.close(); // NOP
                        bytes = bos.toByteArray();
                    }
                    else
                    {
                        bytes = uncompressedBytes;
                    }

                    final File outFile = Model.INSTANCE.getFile();
                    assert (null != outFile);

//              We already got the go-ahead to overwrite, if applicable
//              asList doesn't do a copy, it's just indirection. Java arrays aren't Iterable
                    java.nio.file.Files.write(outFile.toPath(), bytes);
                }

                final Alert alert = new Alert(
                        Alert.AlertType.NONE,
                        "Saved",
                        ButtonType.OK
                );
                alert.showAndWait(); // TODO go async: use 'show' and a listener

                final Node source = (Node) event.getSource();
                final Window window = source.getScene().getWindow();
                final Stage stage = (Stage) window;
                stage.close();


            } catch (IOException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                final Alert alert = new Alert(
                        Alert.AlertType.NONE,
                        "Error writing to file",
                        ButtonType.OK
                );
                alert.showAndWait(); // TODO go async: use 'show' and a listener
            } catch (TxtShuffle.NumberTooGreatException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                final Alert alert = new Alert(
                        Alert.AlertType.NONE,
                        "Input too big to encode",
                        ButtonType.OK
                );
                alert.showAndWait(); // TODO go async: use 'show' and a listener
            }
        }
    }





    @FXML
    private void handleDecodeButtonAction(final ActionEvent event) throws IOException {

                final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/SelectDecodeSource.fxml"));
                loader.setController(this);
                final Parent sdssParent = loader.load();
                final Scene sdsScene = new Scene(sdssParent);
                sdsScene.getStylesheets().add("/styles/Styles.css");

                final Stage stage = new Stage();

                // this call must be made before show()
                stage.initModality(Modality.APPLICATION_MODAL); // https://docs.oracle.com/javase/8/javafx/api/javafx/stage/Stage.html

                stage.setTitle("Select Source");
                stage.setResizable(false);
                stage.setScene(sdsScene);
                stage.show();
//        System.out.println("[Show decode wizard]");
    }



    @FXML
    public void handleDecSrcCntButtonAction(final ActionEvent event) throws IOException
    {
        assert(null != this.sdcsFromManualRadio);
        assert(null != this.sdcsFromFileRadio);

        final Toggle tog = this.dcsTg.getSelectedToggle();

        if (null == tog)
        {
            final Alert alert = new Alert(
                    Alert.AlertType.NONE,
                    "Select an option before continuing",
                    ButtonType.OK
            );

            alert.showAndWait(); // TODO go async: use 'show' and a listener
        }
        else
        {
            final boolean b1 = (tog == this.sdcsFromManualRadio);
            final boolean b2 = (tog == this.sdcsFromFileRadio);

            assert(b1 != b2);

            if (b1) {
                    final Node source = (Node)(event.getSource());
                    final Window window = source.getScene().getWindow();

                    final Stage stage = (Stage)window; // ugly cast following https://stackoverflow.com/a/31686775
                    stage.setTitle("Enter Scrambled Shopping List");

                    final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/EnterEncodedDataSetText.fxml"));
                    loader.setController(this);
                    final Parent rootParent = loader.load();
                    final Scene rootScene = new Scene(rootParent);
                    rootScene.getStylesheets().add("/styles/Styles.css");

                    stage.setScene(rootScene);

            } else { // from file
                final FileChooser fc = new FileChooser();
                fc.setTitle("Select shopping list file");

                // FXML binding can only be used for entities within the scene
                // https://stackoverflow.com/a/33933973
                final Node source = (Node)event.getSource();
                final Window window = source.getScene().getWindow();

                final File file = fc.showOpenDialog(window); // can return null

                if (null != file) {
                    // assert(!file.isDirectory());
                    final boolean isFile = file.isFile();

                    if (isFile) {
                        final long fileLength = file.length();

                        System.out.println("You have selected:");
                        System.out.println(file.getPath());
                        System.out.println();

                        {
                            // TODO eliminate this awful redundant copy. setDataSet should accept List<String>
                            final List<String> split = Files.readAllLines(file.toPath());
                            final int count = split.size();
                            final String[] arr = new String[count];
                            split.toArray(arr);

                            Model.INSTANCE.setEncodedDataSet(arr);

                            // final String[] split = text.split("\\r?\\n"); // https://stackoverflow.com/a/454913
                        }

                        final Alert alert = new Alert(
                                Alert.AlertType.NONE,
                                "Successfully read the scrambled file",
                                ButtonType.OK
                        );

                        alert.showAndWait(); // TODO go async: use 'show' and a listener

                        this.goToSelectOutputSink(window, FinalAction.DECODE);
                    } else {
                        final Alert alert = new Alert(
                                Alert.AlertType.NONE,
                                "Please select a file",
                                ButtonType.OK
                        );

                        alert.showAndWait(); // TODO go async: use 'show' and a listener
                    }
                } // else do nothing - user cancelled file-selection

            }
        }

    }



    @FXML
    void handleEntEncDsButtonAction(final ActionEvent event) {
        System.out.println("[Data is]");
        System.out.println(this.edcdsTextArea.getText());
        System.out.println("[Next step]");
    }


// // TODO deprecated
// // https://docs.oracle.com/javafx/2/fxml_get_started/whats_new2.htm
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
