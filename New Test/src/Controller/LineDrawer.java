package Controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class LineDrawer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Line curLine;

    @Override
    public void start(Stage stage) throws Exception {
        Pane drawingPane = new Pane();
        drawingPane.setPrefSize(800, 800);
        drawingPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        ScrollPane scrollPane = new ScrollPane(drawingPane);
        scrollPane.setPrefSize(300, 300);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");

        drawingPane.setOnMousePressed(event -> {
            if (!event.isPrimaryButtonDown()) {
                return;
            }

            curLine = new Line(
                    event.getX(), event.getY(),
                    event.getX(), event.getY()
            );
            drawingPane.getChildren().add(curLine);
        });

        drawingPane.setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown()) {
                return;
            }

            if (curLine == null) {
                return;
            }

            curLine.setEndX(event.getX());
            curLine.setEndY(event.getY());

            double mx = Math.max(curLine.getStartX(), curLine.getEndX());
            double my = Math.max(curLine.getStartY(), curLine.getEndY());

            if (mx > drawingPane.getMinWidth()) {
                drawingPane.setMinWidth(mx);
            }

            if (my > drawingPane.getMinHeight()) {
                drawingPane.setMinHeight(my);
            }
        });

        drawingPane.setOnMouseReleased(event -> curLine = null);

        Scene scene = new Scene(scrollPane);
        stage.setMinWidth(100);
        stage.setMinHeight(100);
        stage.setScene(scene);
        stage.show();
    }
}