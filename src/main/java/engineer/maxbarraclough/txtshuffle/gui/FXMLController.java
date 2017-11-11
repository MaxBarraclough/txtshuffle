package engineer.maxbarraclough.txtshuffle.gui;

import engineer.maxbarraclough.txtshuffle.backend.TxtShuffle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.function.Supplier;
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
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
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

    @FXML private TextArea edsTextArea;
    @FXML private TextArea emTextArea;

    @FXML private Text soPathText;


    @FXML
    private void handleEntDsButtonAction(ActionEvent event) throws IOException, TxtShuffle.NumberTooGreatException {
        System.out.println("[handleEntDsButtonAction has been called]");

        final String text = this.edsTextArea.getText();
        final String[] split = text.split("\\r?\\n"); // https://stackoverflow.com/a/454913

        Model.INSTANCE.setDataSet(split);

        // TODO exception-handling

        final byte[]   msgBytes = Model.INSTANCE.getMessageBytes();
        final String[] ds       = Model.INSTANCE.getDataSet();

        if ((null == msgBytes) || (null == ds)) {
            System.err.println("Failed to initialize a supplier");
        } else {
            final byte[] messageBytes = Model.INSTANCE.getMessageBytes();
            final String[] dataSet = Model.INSTANCE.getDataSet();
            final File file = new File("C:\\Users\\Kingsley\\Documents\\demo.txt"); // // // ANOTHER FILE-SELECT DIALOG...???

            // TODO check for unique rows

            // TODO check enough rows to encode the message

            Model.INSTANCE.encodeIntoFile(messageBytes, dataSet, file);
        }



        /*******************************************/
        /*************** TEMPORARY *****************/
        /*******************************************/
        final Alert alert = new Alert(
                Alert.AlertType.NONE,
                "Saved to demo.txt",
                ButtonType.OK
        );

        // TODO can we just do 'show' here, as we don't do anything afterwards on the thread?
        alert.showAndWait(); // TODO go async: use 'show' and a listener

        final Node source = (Node) event.getSource();
        final Window window = source.getScene().getWindow();
        final Stage stage = (Stage) window;
        stage.close();
    }



    @FXML
    private void handleEntMsgButtonAction(ActionEvent event) throws IOException {

        final String msgStr = this.emTextArea.getText();

        try {
            final byte[] msgBytes = TextMessageSupplier.strToByteArr(msgStr);

            Model.INSTANCE.setMessageBytes(msgBytes);

            // TODO move this to its own method
            final Node source = (Node) event.getSource();
            final Window window = source.getScene().getWindow();

            final Stage stage = (Stage) window; // ugly cast following https://stackoverflow.com/a/31686775

            final Parent parent = FXMLLoader.load(this.getClass().getResource("/fxml/SelectDataSource.fxml"));
            final Scene scene = new Scene(parent);
            scene.getStylesheets().add("/styles/Styles.css");

            stage.setTitle("Select Data-Set Source");
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


                    final byte[] fileBytes = Files.readAllBytes(file.toPath());
                    Model.INSTANCE.setMessageBytes(fileBytes);

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

                    this.goToSelectOutputSink(window);
                } // else do nothing - user cancelled file-selection

            }
        }

    }


    private void goToSelectOutputSink(final Window window) throws IOException
    {
                        final Stage stage = (Stage)window; // ugly cast following https://stackoverflow.com/a/31686775
                        stage.setTitle("Select Output");

                        final Parent parent = FXMLLoader.load(this.getClass().getResource("/fxml/SelectOutputSink.fxml"));
                        final Scene rootScene = new Scene(parent);
                        rootScene.getStylesheets().add("/styles/Styles.css");

                        stage.setScene(rootScene);
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
    private void handleSelectOutputFileButtonAction()
    {

        this.soPathText.setText("TODO");

        // //
    }



    @FXML
    private void handleGoEncodeButtonAction()
    {
        // //
    }





    @FXML
    private void handleDecodeButtonAction(ActionEvent event) {
        System.out.println("[Show decode wizard]");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
