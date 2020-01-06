package Controller;

import Main.Main;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public ImageView imageView;
    public Pane pane;
    public ScrollPane scrollPane;
    public Button loadBtn;

    final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
    public double rotation = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void openFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(Main.stage);
        if (selectedFile != null) {
            System.out.println("path: " + selectedFile.getAbsoluteFile());
            Image image = new Image("file:" + selectedFile.getAbsolutePath());
            scrollPane.setContent(imageView);
            imageView.setImage(image);

        }
        zoomProperty.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                imageView.setFitWidth(zoomProperty.get() * 4);
                imageView.setFitHeight(zoomProperty.get() * 3);
            }
        });

        scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        });

    }

    public void rotate(ActionEvent actionEvent) {
        rotation += 90;
        if (rotation > 270) {
            rotation = 0;
        }

        imageView.setRotate(rotation);
    }


}
