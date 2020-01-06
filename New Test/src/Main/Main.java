package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Main extends Application {


  public static Stage stage ;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("../Views/asd.fxml"));
        primaryStage.setTitle("Hello World");

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setScene(new Scene(root, screenBounds.getWidth(), screenBounds.getHeight()));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
