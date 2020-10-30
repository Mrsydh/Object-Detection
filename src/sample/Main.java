package sample;

import org.opencv.core.Core;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) { /*throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();*/

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
            BorderPane rootElement = (BorderPane) loader.load();
            Scene scene = new Scene(rootElement, 800, 600);
            //scene.getStylesheets().add(getClass().getResource())
            primaryStage.setTitle("JavaFx meets openCV");
            primaryStage.setScene(scene);
            primaryStage.show();

            Controller controller=loader.getController();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                   // controller.setClosed();
                }
            });

        }

        catch (Exception e){
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
