package Controller;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main2 extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ZoomableCanvas canvas = new ZoomableCanvas(300, 200) {
            @Override
            public void paint(GraphicsContext gc) {
//                 Рисуем:
                gc.setFill(Color.LIGHTGREEN);
                gc.fillOval(60, 10, 180, 180);
                gc.setFill(Color.WHITE);
                gc.fillOval(100, 50, 100, 100);
                gc.strokeLine(0,0,100,100);
            }
        };
        Label zoomLabel = new Label();
        zoomLabel.textProperty().bind(canvas.zoomProperty().asString());
        Slider slider = new Slider(0.8, 2.0, 1.0);
        slider.valueProperty().bindBidirectional(canvas.zoomProperty());
        VBox root = new VBox(zoomLabel, canvas, slider);
        root.setPadding(new Insets(15));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        canvas.redraw();
    }
}


