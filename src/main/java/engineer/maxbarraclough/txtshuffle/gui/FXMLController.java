package engineer.maxbarraclough.txtshuffle.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class FXMLController implements Initializable {

    @FXML private RadioButton sdsFromManualRadio;
    @FXML private RadioButton sdsFromFileRadio;

    @FXML private ToggleGroup tg;

    // TODO could move this to its own class
    @FXML
    private void handleSourceContinueButtonAction(ActionEvent event)
    {
        assert(null != sdsFromManualRadio);
        assert(null != sdsFromFileRadio);

        final Toggle tog = tg.getSelectedToggle();

        if (null == tog)
        {
            final Alert alert = new Alert(
                    Alert.AlertType.WARNING,
                    "Select an option before continuing",
                    ButtonType.OK
            );

            alert.showAndWait();
        }
        else
        {
            final boolean b1 = (tog == sdsFromManualRadio);
            final boolean b2 = (tog == sdsFromFileRadio);

            assert(b1 != b2);

            if (b1) {
                System.out.println("[file-reading case]");
            } else {
                System.out.println("[manual case]");
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
