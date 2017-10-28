package engineer.maxbarraclough.txtshuffle.gui;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public final class MainApp extends Application {

    protected final Scene rootScene;
    
    /**
     * selectDataSourceScene
     */
    protected final Scene sdsScene;
    
    
    public MainApp() throws IOException
    {
        Parent rootParent = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        this.rootScene = new Scene(rootParent);
        this.rootScene.getStylesheets().add("/styles/Styles.css");
        
        Parent sdssParent = FXMLLoader.load(getClass().getResource("/fxml/SelectDataSource.fxml"));
        this.sdsScene = new Scene(sdssParent);
        this.rootScene.getStylesheets().add("/styles/Styles.css");
    }
    
    
    @Override
    public void start(Stage stage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
//        
//        Scene scene = new Scene(root);
//        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("txtshuffle");
        stage.setResizable(false);

        stage.setScene(this.rootScene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
