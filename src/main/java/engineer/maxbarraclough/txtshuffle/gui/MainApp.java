package engineer.maxbarraclough.txtshuffle.gui;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public final class MainApp extends Application {

//    protected Stage stage;
//
//    protected /*final*/ Scene rootScene;
//
//    /**
//     * selectDataSourceScene
//     */
//    protected /*final*/ Scene sdsScene;

    @Override
    public void start(Stage stage) throws Exception {

        // This stuff *must* be done here, *not* in 'init' or in ctor,
        // for execution on correct thread https://www.javaworld.com/article/3057072/
        final Parent rootParent = FXMLLoader.load(this.getClass().getResource("/fxml/Scene.fxml"));
        final Scene rootScene = new Scene(rootParent);
        rootScene.getStylesheets().add("/styles/Styles.css");

        final Parent sdssParent = FXMLLoader.load(this.getClass().getResource("/fxml/SelectDataSource.fxml"));
        final Scene sdsScene = new Scene(sdssParent);
        sdsScene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("txtshuffle");
        stage.setResizable(false);

        stage.setScene(rootScene);
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
