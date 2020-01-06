package Controller;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 *
 * @author maher
 */
public class ZoomAndPan extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        ZoomablePane zoomPane = new ZoomablePane();
        root.setCenter(zoomPane);
        Scene scene = new Scene(root, 300, 250);

        Button btn = new Button();
        btn.setText("Add Circle");

        btn.setOnAction((ActionEvent event) -> {
            zoomPane.content.getChildren().add(new DraggableCircle());
        });

        root.setTop(btn);
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

class ZoomablePane extends AnchorPane {

    final double SCALE_DELTA = 1.1;

    public Group content = new Group();

    public ZoomablePane() {
        super();
        getChildren().add(content);
        content.setAutoSizeChildren(true);
        setOnScroll((ScrollEvent event) -> {
            event.consume();
            if (event.getDeltaY() == 0) {
                return;
            }

            double scaleFactor
                    = (event.getDeltaY() > 0)
                    ? SCALE_DELTA
                    : 1 / SCALE_DELTA;

            content.setScaleX(content.getScaleX() * scaleFactor);
            content.setScaleY(content.getScaleY() * scaleFactor);
        });
    }

}

class DraggableCircle extends Circle {

    public double mouseX, mouseY, deltaX, deltaY, posX, posY;

    public DraggableCircle() {
        double r = Math.random() * 255;
        double g = Math.random() * 255;
        double b = Math.random() * 255;
        setRadius(Math.random() * 10 + 2);
        setCenterX(50);
        setCenterY(50);
        this.setStyle("-fx-fill: rgb(" + r + "," + g + "," + b + ");");

        setOnMousePressed((MouseEvent event) -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
            toFront();
        });
        setOnMouseDragged((MouseEvent event) -> {
            deltaX = event.getSceneX() - mouseX + posX;
            deltaY = event.getSceneY() - mouseY + posY;
            setLayoutX(deltaX);
            setLayoutY(deltaY);
        });
        setOnMouseReleased((MouseEvent event) -> {
            posX = getLayoutX();
            posY = getLayoutY();
        });
    }

}