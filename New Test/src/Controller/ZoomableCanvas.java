package Controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;

public abstract class ZoomableCanvas extends Canvas {
    public ZoomableCanvas() {
        this(0, 0);
    }

    public ZoomableCanvas(double width, double height) {
        super(width, height);
        this.setOnScroll(zoomHandler);
        this.zoomProperty().addListener(o -> redraw());
    }

    static protected EventHandler<ScrollEvent> zoomHandler = new EventHandler<ScrollEvent>() {
        @Override
        public void handle(ScrollEvent event) {
            ZoomableCanvas zcanvas = (ZoomableCanvas) event.getSource();
            GraphicsContext gc = zcanvas.getGraphicsContext2D();
            Affine affine = gc.getTransform();
            double zoom = affine.getMxx() + event.getDeltaY() / 800;
            if (zoom <= 0.8) {
                zoom = 0.8;
            }
            if (zoom >= 2.0) {
                zoom = 2.0;
            }
            zcanvas.setZoom(zoom);
            zcanvas.redraw();
        }
    };
    private SimpleDoubleProperty zoom = new SimpleDoubleProperty(1.0);

    public void setZoom(double value) {
        if (value != getZoom()) {
            this.zoom.set(value);
            redraw();
        }
    }

    public double getZoom() {
        return zoom.get();
    }

    public DoubleProperty zoomProperty() {
        return zoom;
    }

    public void redraw() {
        GraphicsContext gc = this.getGraphicsContext2D();
        if (gc == null)
            return;
        // Чистим:
        Canvas canvas = gc.getCanvas();
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        gc.setTransform(1, 0, 0, 1, 0, 0);
        gc.clearRect(0, 0, w, h);
        double z = getZoom();
        // Масштабирование в левый верхний угол:
         gc.setTransform(z, 0, 0, z, 0, 0);
        // Масштабирование в центр canvas:
         gc.setTransform(z, 0, 0, z, (w - w * z) / 2.0, (h - h * z) / 2.0);
        // Рисуем:
         paint(gc);
    }

    public abstract void paint(GraphicsContext gc);
}
