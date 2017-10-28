package engineer.maxbarraclough.txtshuffle.gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class FXMLController implements Initializable {

    // JavaFX doesn't mind the 'private' modifier
    @FXML
    private void handleEncodeButtonAction(ActionEvent event) {
        System.out.println("[Show encode wizard]");
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
