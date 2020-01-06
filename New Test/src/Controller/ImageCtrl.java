package Controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

import javax.swing.event.ChangeEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class ImageCtrl implements Initializable {


    public ImageView box;
    public ScrollPane scrollPane;

    public void zoom(ScrollEvent event) {
        if (event.getDeltaY() == 0) {
            return;
        }

        double scaleFactor = (event.getDeltaY() > 0) ? 1.1
                : 1 / 1.1;
        box.setScaleX(box.getScaleX() * scaleFactor);
        box.setScaleY(box.getScaleY() * scaleFactor);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
//            @Override
//            public void changed(ObservableValue<? extends Bounds> observable,
//                                Bounds oldValue, Bounds newValue) {
//                anchor.setMinSize(newValue.getWidth(), newValue.getHeight());
//            }
//        });
    }


}
