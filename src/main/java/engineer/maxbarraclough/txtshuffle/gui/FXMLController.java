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
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLController implements Initializable {

    // JavaFX doesn't mind the 'private' modifier
    @FXML
    private void handleEncodeButtonAction(ActionEvent event) throws IOException {

                Parent sdssParent = FXMLLoader.load(this.getClass().getResource("/fxml/SelectDataSource.fxml"));
                Scene sdsScene = new Scene(sdssParent);
                sdsScene.getStylesheets().add("/styles/Styles.css");

                Stage stage = new Stage();

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
        // TODO
    }
}
