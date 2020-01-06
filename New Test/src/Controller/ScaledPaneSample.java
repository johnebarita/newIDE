package Controller;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class ScaledPaneSample extends Application {
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // create a pane with various objects on it.
        final Pane pane = new Pane();
        pane.setStyle("-fx-background-color: linear-gradient(to bottom right, derive(goldenrod, 20%), derive(goldenrod, -40%));");
        final Label resizeMe = new Label("Resize me");
        resizeMe.setAlignment(Pos.CENTER);
        resizeMe.prefWidthProperty().bind(pane.widthProperty());
        final ImageView iv1 = new ImageView(
                new Image(
                        "http://icons.iconarchive.com/icons/kidaubis-design/cool-heroes/128/Ironman-icon.png"));
        final ImageView iv2 = new ImageView(
                new Image(
                        "http://icons.iconarchive.com/icons/kidaubis-design/cool-heroes/128/Starwars-Stormtrooper-icon.png"));
        iv1.relocate(10, 10);
        iv2.relocate(80, 60);
        Button button = new Button("Zap!");
        button.relocate(25, 140);
        pane.getChildren().addAll(resizeMe, iv1, iv2, button);

        // layout the scene.
        final Group group = new Group(pane);
        final Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.show();

        // scale the entire scene as the stage is resized.
        final double initWidth = scene.getWidth();
        final double initHeight = scene.getHeight();
        Scale scale = new Scale();
        scale.xProperty().bind(scene.widthProperty().divide(initWidth));
        scale.yProperty().bind(scene.heightProperty().divide(initHeight));
        scale.setPivotX(0);
        scale.setPivotY(0);
        group.getTransforms().addAll(scale);
    }
}