package engineer.maxbarraclough.txtshuffle.gui;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.Optional;
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
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class FXMLController implements Initializable {

    @FXML private RadioButton sdsFromManualRadio;
    @FXML private RadioButton sdsFromFileRadio;

    @FXML private ToggleGroup tg;

    // TODO could move this to its own class
    @FXML
    private void handleSourceContinueButtonAction(ActionEvent event) throws IOException
    {
        assert(null != sdsFromManualRadio);
        assert(null != sdsFromFileRadio);

        final Toggle tog = tg.getSelectedToggle();

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

                    // TODO move this to its own method
                {
                    final Node source = (Node)(event.getSource());
                    final Window window = source.getScene().getWindow();

                    final Stage stage = (Stage)window; // ugly cast following https://stackoverflow.com/a/31686775
                    stage.setTitle("Enter Message Text");

                    final Parent rootParent = FXMLLoader.load(this.getClass().getResource("/fxml/EnterMessageText.fxml"));
                    final Scene rootScene = new Scene(rootParent);
                    rootScene.getStylesheets().add("/styles/Styles.css");

                    stage.setScene(rootScene);
                }


/*
                // TextInputDialog uses TextField, which is necessarily single-line
                // For multi-line we'd need to use a custom scene with a TextArea widget
                // This is ok for simple messages though.
                final TextInputDialog tid = new TextInputDialog("You may use ordinary letters, numbers, and the + and / symbols");

                final Optional<String> theText_Opt = tid.showAndWait(); // TODO go async: use 'show' and a listener

                if (theText_Opt.isPresent()) {
                    final String theText = theText_Opt.get();

                    // TODO read into memory and save that somewhere, somehow
                    System.out.println("You have entered:");
                    System.out.println(theText);

                    final java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();

                    byte[] decoded = null;
                    try {
                        decoded = decoder.decode(theText);
                    } catch (final IllegalArgumentException iae) {
                        final Alert alert = new Alert(
                                Alert.AlertType.NONE,
                                "Successfully read file",
                                ButtonType.OK
                        );
                        alert.showAndWait(); // TODO go async: use 'show' and a listener
                    }
                    // We don't want to catch for anything other than that one line
                    // so we do this slightly perverse flow-control

                    if (null != decoded)
                    {
                        final BigInteger bi = new BigInteger(decoded);

                        System.out.println("Numerical representation:");
                        System.out.println(bi);
                    } // else do nothing: user entered bad data and was already alerted,
                      // so we just send them back to the previous window
                } // else do nothing - user cancelled their text-entry
*/
            } else {
                final FileChooser fc = new FileChooser();
                fc.setTitle("Select file");

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
                            "Successfully read file",
                            ButtonType.OK
                    );

                    alert.showAndWait(); // TODO go async: use 'show' and a listener

                    // TODO move this to its own method
                    {
                        final Stage stage = (Stage)window; // ugly cast following https://stackoverflow.com/a/31686775
                        stage.setTitle("Select Message Source");

                        final Parent rootParent = FXMLLoader.load(this.getClass().getResource("/fxml/SelectMessageSource.fxml"));
                        final Scene rootScene = new Scene(rootParent);
                        rootScene.getStylesheets().add("/styles/Styles.css");

                        stage.setScene(rootScene);
                    }
                } // else do nothing - user cancelled file-selection

            }
        }

    }



    // JavaFX doesn't mind the 'private' modifier
    @FXML
    private void handleEncodeButtonAction(ActionEvent event) throws IOException {

                final Parent sdssParent = FXMLLoader.load(this.getClass().getResource("/fxml/SelectDataSource.fxml"));
                final Scene sdsScene = new Scene(sdssParent);
                sdsScene.getStylesheets().add("/styles/Styles.css");

                final Stage stage = new Stage();

                // this call must be made before show()
                stage.initModality(Modality.APPLICATION_MODAL); // https://docs.oracle.com/javase/8/javafx/api/javafx/stage/Stage.html

                stage.setTitle("Select Data Source");
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
