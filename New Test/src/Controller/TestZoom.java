package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;


import java.awt.geom.AffineTransform;
import java.net.URL;
import java.util.ResourceBundle;

public class TestZoom implements Initializable {
    public ScrollPane scrollPane;
    public StackPane zoomPane;
    final double SCALE_DELTA = 1.1;
    ImageView imageView;
    float IMAGE_SCALE = 72 / 200f;
    Image image = new Image("/Images/report.jpg");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setPannable(true);
        zoomPane.setPrefHeight(1000);
        zoomPane.setPrefWidth(1000);

    }

    public void loadImage(ActionEvent actionEvent) {


        imageView = new ImageView(image);
        zoomPane.getChildren().add(imageView);
        zoomPane.setPrefWidth(image.getWidth());
        zoomPane.setPrefWidth(image.getHeight());

        AffineTransform at = new AffineTransform();
        double TRANSLATE_X = (zoomPane.getWidth() - IMAGE_SCALE * image.getWidth()) / 2;
        double TRANSLATE_Y = (zoomPane.getHeight() - IMAGE_SCALE * image.getHeight()) / 2;
        scrollPane.setVvalue(scrollPane.getVmax()/2);
        scrollPane.setHvalue(scrollPane.getHmax()/2);
        zoomPane.setTranslateX(0);
        zoomPane.setTranslateY(0);
    }

    public void zoom(ScrollEvent event) {
        event.consume();

        if (event.getDeltaY() == 0) {
            return;
        }

        double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;


        zoomPane.setScaleX(zoomPane.getScaleX() * scaleFactor);
        zoomPane.setScaleY(zoomPane.getScaleY() * scaleFactor);
        zoomPane.setPrefWidth(zoomPane.getWidth() * zoomPane.getScaleX());
        zoomPane.setPrefHeight(zoomPane.getHeight() * zoomPane.getScaleY());
        double TRANSLATE_X = (zoomPane.getWidth() - IMAGE_SCALE * image.getWidth()) / 2;
        double TRANSLATE_Y = (zoomPane.getHeight() - IMAGE_SCALE * image.getHeight()) / 2;

        zoomPane.setTranslateX(0);
        zoomPane.setTranslateY(0);
        scrollPane.setVvalue(scrollPane.getVmax()/2);
        scrollPane.setHvalue(scrollPane.getHmax()/2);
        System.out.println("ScrollPane  " + scrollPane.getVvalue()+ " " + scrollPane.getHvalue());
    }

}
