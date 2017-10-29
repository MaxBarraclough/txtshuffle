package engineer.maxbarraclough.txtshuffle.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class FXMLController implements Initializable {

    // 'select message source' ...
    @FXML private RadioButton smsFromManualRadio;
    @FXML private RadioButton smsFromFileRadio;

    // 'select data source' ...
    @FXML private RadioButton sdsFromManualRadio;
    @FXML private RadioButton sdsFromFileRadio;

    @FXML private ToggleGroup smsTg;
    @FXML private ToggleGroup dsTg;



    @FXML
    private void handleEntDsButtonAction(ActionEvent event) {
        System.out.println("[handleEntDsButtonAction has been called]");
    }

    @FXML
    private void handleEntMsgButtonAction(ActionEvent event) throws IOException {

        // TODO save message somewhere somehow

                    // TODO move this to its own method
                    {
                        final Node source = (Node) event.getSource();
                        final Window window = source.getScene().getWindow();

                        final Stage stage = (Stage) window; // ugly cast following https://stackoverflow.com/a/31686775

                        final Parent parent = FXMLLoader.load(this.getClass().getResource("/fxml/SelectDataSource.fxml"));
                        final Scene scene = new Scene(parent);
                        scene.getStylesheets().add("/styles/Styles.css");

                        stage.setTitle("Select Data-Set Source");
                        stage.setScene(scene);
                    }
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

    private static byte[] binFileToByteArr(final File file) {
        throw new java.lang.UnsupportedOperationException("Not yet implemented");
    }


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

                    final Stage stage = (Stage) window; // ugly cast following https://stackoverflow.com/a/31686775
                    stage.setTitle("Enter Message Text");

                    final Parent rootParent = FXMLLoader.load(this.getClass().getResource("/fxml/EnterMessageText.fxml"));
                    final Scene rootScene = new Scene(rootParent);
                    rootScene.getStylesheets().add("/styles/Styles.css");

                    stage.setScene(rootScene);
                }
            } else {
                final FileChooser fc = new FileChooser();
                fc.setTitle("Select file");

                // FXML binding can only be used for entities within the scene
                // https://stackoverflow.com/a/33933973
                final Node source = (Node) event.getSource();
                final Window window = source.getScene().getWindow();

                final File file = fc.showOpenDialog(window); // can return null

                if (null != file) {
                    // assert(!file.isDirectory());
                    assert (file.isFile());

                    // final long fileLength = file.length();

                    System.out.println("You have selected:");
                    System.out.println(file.getPath());
                    System.out.println();

                    Model.INSTANCE.setMessageSupplier(null); // // // // // // // // // // // // // //

                    // TODO read into memory and save that somewhere, somehow
                    // TODO popup confirming success, or announcing failure
                    // For now, just show success popup
                    final Alert alert = new Alert(
                            Alert.AlertType.NONE,
                            "Successfully read message file",
                            ButtonType.OK
                    );

                    alert.showAndWait(); // TODO go async: use 'show' and a listener

                    // TODO move this to its own method
                    {
                        final Stage stage = (Stage) window; // ugly cast following https://stackoverflow.com/a/31686775
                        stage.setTitle("Select Data-Set Source");

                        final Parent rootParent = FXMLLoader.load(this.getClass().getResource("/fxml/SelectDataSource.fxml"));
                        final Scene rootScene = new Scene(rootParent);
                        rootScene.getStylesheets().add("/styles/Styles.css");

                        stage.setScene(rootScene);
                    }
                } // else do nothing - user cancelled file-selection

            }
        }
    }

    // TODO could move this to its own class
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
                    stage.setTitle("Enter Data-Set Text");

                    final Parent rootParent = FXMLLoader.load(this.getClass().getResource("/fxml/EnterDataSetText.fxml"));
                    final Scene rootScene = new Scene(rootParent);
                    rootScene.getStylesheets().add("/styles/Styles.css");

                    stage.setScene(rootScene);

            } else { // from file
                final FileChooser fc = new FileChooser();
                fc.setTitle("Select data-set file");

                // FXML binding can only be used for entities within the scene
                // https://stackoverflow.com/a/33933973
                final Node source = (Node)event.getSource();
                final Window window = source.getScene().getWindow();

                final File file = fc.showOpenDialog(window); // can return null

                if (null != file) {
                    // assert(!file.isDirectory());
                    assert (file.isFile());

                    final long fileLength = file.length();

                    System.out.println("You have selected:");
                    System.out.println(file.getPath());
                    System.out.println();

                    // TODO read into memory and save that somewhere, somehow

                    // TODO popup confirming success, or announcing failure
                    // For now, just show success popup
                    final Alert alert = new Alert(
                            Alert.AlertType.NONE,
                            "Successfully read data-set file",
                            ButtonType.OK
                    );

                    alert.showAndWait(); // TODO go async: use 'show' and a listener



//                    {
//                        final Stage stage = (Stage)window; // ugly cast following https://stackoverflow.com/a/31686775
//                        stage.setTitle("Select Data-Set Source");
//
//                        final Parent parent = FXMLLoader.load(this.getClass().getResource("/fxml/SelectMessageSource.fxml"));
//                        final Scene rootScene = new Scene(parent);
//                        rootScene.getStylesheets().add("/styles/Styles.css");
//
//                        stage.setScene(rootScene);
//                    }
                } // else do nothing - user cancelled file-selection

            }
        }

    }


    // JavaFX doesn't mind the 'private' modifier
    @FXML
    private void handleEncodeButtonAction(ActionEvent event) throws IOException {

                final Parent sdssParent = FXMLLoader.load(this.getClass().getResource("/fxml/SelectMessageSource.fxml"));
                final Scene sdsScene = new Scene(sdssParent);
                sdsScene.getStylesheets().add("/styles/Styles.css");

                final Stage stage = new Stage();

                // this call must be made before show()
                stage.initModality(Modality.APPLICATION_MODAL); // https://docs.oracle.com/javase/8/javafx/api/javafx/stage/Stage.html

                stage.setTitle("Select Message Source");
                stage.setResizable(false);
                stage.setScene(sdsScene);
                stage.show();
    }


    @FXML
    private void handleDecodeButtonAction(ActionEvent event) {
        System.out.println("[Show decode wizard]");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
