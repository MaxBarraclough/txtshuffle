package engineer.maxbarraclough.txtshuffle.gui;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
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


        // Following https://stackoverflow.com/a/30815504
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/Scene.fxml"));
        final FXMLController controller = new FXMLController(null); // we only ever need this one instance
        loader.setController(controller);

        // getResource plays nice with JAR packaging, unlike file streams https://stackoverflow.com/a/2343224
// // NOOOPE NOT ALLOWED    final Parent rootParent = FXMLLoader.load(this.getClass().getResource("/fxml/Scene.fxml"));

        final VBox rootVBox = loader.load();
        final Scene scene = new Scene(rootVBox);

        // final Scene rootScene = new Scene(rootParent);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("txtshuffle");
        stage.setResizable(false);

        stage.setScene(scene);
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
