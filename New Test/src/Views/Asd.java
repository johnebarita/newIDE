package Views;

import Main.Main;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;

import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;

import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;

import javafx.scene.shape.*;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;

import javax.swing.*;


import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;


public class Asd implements Initializable {

    public ScrollPane scroller;
    public Group scrollContent;
    public StackPane zoomPane;
    public Group group;

    final double SCALE_DELTA = 1.1;
    final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<Point2D>();

    public Image image;
    public ImageView imageView;
    public Line tempLine = new Line();
    public Button scale, length, area, save, loadImage;
    public ToggleGroup tools;
    public ToggleButton drowbtn;
    public ToggleButton rubberbtn;
    public ToggleButton linebtn;
    public ToggleButton rectbtn;
    public ToggleButton elpslebtn;
    public ToggleButton circlebtn;
    public Button undo;
    public Button redo;
    public Canvas canvas;
    public ColorPicker cpLine;
    public ColorPicker cpFill;
    public Label line_width;
    public Slider slider;
    public SubScene sub;

    Stack<Shape> undoHistory = new Stack();
    Stack<Shape> redoHistory = new Stack();

    boolean canDraw = true;
    boolean isNewline = true;
    boolean hovered = false;
    Polyline polyline = new Polyline();
    Point.Double start;
    Point.Double end;
    GraphicsContext gc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //ADJUSTING THE ZOOMPANE WHEN ScrollPane is resized;
        //This is similar to Swing's setprefferedSize();
        scroller.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable,
                                Bounds oldValue, Bounds newValue) {
                zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
            }
        });
        gc = canvas.getGraphicsContext2D();
        draw();
    }

    public void zoomController(ScrollEvent events) {
        events.consume();
        if (events.isControlDown()) {
            if (events.getDeltaY() == 0) {
                return;
            }
            double scaleFactor = (events.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;
            // amount of scrolling in each direction in scrollContent coordinate
            // units
            double oldScale = group.getScaleX();
            double newScale = group.getScaleX() * scaleFactor;

            if (newScale < 1 || newScale > 24) {
                newScale = oldScale;
            }
            Point2D scrollOffset = figureScrollOffset(scrollContent, scroller);
            group.setScaleX(newScale);
            group.setScaleY(newScale);
            // move viewport so that old center remains in the center after the
            // scaling

            repositionScroller(scrollContent, scroller, scaleFactor, scrollOffset);
            System.out.println(group.getScaleX());
            System.out.println("TRANSLATE");
            System.out.println(group.getTranslateX());
            System.out.println(group.getTranslateY());
            System.out.println(group.getBoundsInParent());
            System.out.println(group.getBoundsInLocal());
            Affine at = new Affine();
//            at.translate(group.getBoundsInParent().getMinX(),group.getBoundsInParent().getMinY());
//            at.scale(group.getScaleX(),group.getScaleY());
//            gc.setTransform(at);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            Affine affine = gc.getTransform();
            double zoom = affine.getMxx() + events.getDeltaY() / 800;
            if (zoom <= 0.8) {
                zoom = 0.8;
            }
            if (zoom >= 2.0) {
                zoom = 2.0;
            }
            setZoom(zoom);
            redraw();
        }
    }

    private SimpleDoubleProperty zoom = new SimpleDoubleProperty(1.0);

    public void redraw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
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

    public void paint(GraphicsContext gc) {
        if (!undoHistory.empty()) {
            for (int i = 0; i < undoHistory.size(); i++) {
                Shape shape = undoHistory.elementAt(i);
                if (shape.getClass() == Line.class) {
                    Line temp = (Line) shape;
                    gc.setLineWidth(temp.getStrokeWidth());
                    gc.setStroke(temp.getStroke());
                    gc.setFill(temp.getFill());
                    gc.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                } else if (shape.getClass() == Rectangle.class) {
                    Rectangle temp = (Rectangle) shape;
                    gc.setLineWidth(temp.getStrokeWidth());
                    gc.setStroke(temp.getStroke());
                    gc.setFill(temp.getFill());
                    gc.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                    gc.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                } else if (shape.getClass() == Circle.class) {
                    Circle temp = (Circle) shape;
                    gc.setLineWidth(temp.getStrokeWidth());
                    gc.setStroke(temp.getStroke());
                    gc.setFill(temp.getFill());
                    gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                    gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                } else if (shape.getClass() == Ellipse.class) {
                    Ellipse temp = (Ellipse) shape;
                    gc.setLineWidth(temp.getStrokeWidth());
                    gc.setStroke(temp.getStroke());
                    gc.setFill(temp.getFill());
                    gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                    gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                }
            }
        }
    }

    public void setZoom(double value) {
        if (value != getZoom()) {
            this.zoom.set(value);
            redraw();
        }
    }

    public double getZoom() {
        return zoom.get();
    }

    public SimpleDoubleProperty zoomProperty() {
        return zoom;
    }

    // Panning via drag....
    public void handlePan(MouseEvent event) {
        double scrollerValue = 0;

        scrollerValue = scroller.getHvalue();
        if (Double.isNaN(scrollerValue)) {
            scrollerValue = 0;
        }

        double deltaX = event.getX() - lastMouseCoordinates.get().getX();
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        double deltaH = deltaX * (scroller.getHmax() - scroller.getHmin()) / extraWidth;
        double desiredH = scrollerValue - deltaH;
        scroller.setHvalue(Math.max(0, Math.min(scroller.getHmax(), desiredH)));

        double deltaY = event.getY() - lastMouseCoordinates.get().getY();
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        double deltaV = deltaY * (scroller.getVmax() - scroller.getVmin()) / extraHeight;

        scrollerValue = scroller.getVvalue();
        if (Double.isNaN(scrollerValue)) {
            scrollerValue = 0;
        }

        double desiredV = scrollerValue - deltaV;
        scroller.setVvalue(Math.max(0, Math.min(scroller.getVmax(), desiredV)));
    }

    public void getCenterPoint(MouseEvent event) {
        lastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
    }

    private Point2D figureScrollOffset(Node scrollContent, ScrollPane scroller) {
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        double hScrollProportion = (scroller.getHvalue() - scroller.getHmin()) / (scroller.getHmax() - scroller.getHmin());
        double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        double vScrollProportion = (scroller.getVvalue() - scroller.getVmin()) / (scroller.getVmax() - scroller.getVmin());
        double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
        return new Point2D(scrollXOffset, scrollYOffset);
    }

    private void repositionScroller(Node scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset) {
        double scrollXOffset = scrollOffset.getX();
        double scrollYOffset = scrollOffset.getY();
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        if (extraWidth > 0) {
            double halfWidth = scroller.getViewportBounds().getWidth() / 2;
            double newScrollXOffset = (scaleFactor - 1) * halfWidth + scaleFactor * scrollXOffset;
            scroller.setHvalue(scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        if (extraHeight > 0) {
            double halfHeight = scroller.getViewportBounds().getHeight() / 2;
            double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
            scroller.setVvalue(scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
    }


    public void loadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(Main.stage);

        if (selectedFile == null) {
            JOptionPane.showMessageDialog(null, "No Image selected");
        } else {
            image = new Image("file:" + selectedFile);
            imageView.setImage(image);
            group.getChildren().add(polyline);
            group.getChildren().add(tempLine);
        }
    }

    //Drawing lines
//    public void startDraw(MouseEvent event) {
//        if (canDraw) {
//
//            start = new Point.Double();
//            start.x = event.getX();
//            start.y = event.getY();
//
//            System.out.println("pressed");
//
//            if (tempLine != null) {
//                tempLine.setStartX(event.getX());
//                tempLine.setStartY(event.getY());
//                tempLine.setEndX(event.getX());
//                tempLine.setEndY(event.getY());
//                isNewline = false;
////                polyline.getPoints().addAll(new Double[]{start.x, start.y});
//            } else {
//                group.getChildren().remove(tempLine);
//                group.getChildren().remove(polyline);
//                tempLine = new Line();
//                polyline = new Polyline();
//                group.getChildren().add(polyline);
//                group.getChildren().add(tempLine);
//            }
//
//
//            Rectangle r = new Rectangle(start.x - 5, start.y - 4, 8, 8);
//            r.setStrokeWidth(1);
//            r.setStroke(Color.BLACK);
//            r.setFill(Color.TRANSPARENT);
//            r.setOnMouseEntered(event1 -> {
//                r.setStroke(Color.RED);
//                hovered = true;
//            });
//            r.setOnMouseExited(event1 -> {
//                r.setStroke(Color.BLACK);
//                hovered = false;
//            });
//            if (hovered && polyline.getPoints().size() > 3) {
//                isNewline = true;
//                tempLine = null;
//                System.out.println("ENCLOSED");
//            }
//            group.getChildren().add(r);
//        }
//    }


//    public void updateLine(MouseEvent event) {
//        if (canDraw) {
//            if (!isNewline) {
//                tempLine.setEndX(event.getX());
//                tempLine.setEndY(event.getY());
//            }
//        }
//    }

    public void draw() {
        gc.setLineWidth(1);

        Line line = new Line();
        Rectangle rect = new Rectangle();
        Circle circ = new Circle();
        Ellipse elps = new Ellipse();

        canvas.setOnMousePressed(e -> {
            if (drowbtn.isSelected()) {

                gc.setStroke(cpLine.getValue());
                gc.beginPath();
                gc.lineTo(e.getX(), e.getY());
            } else if (rubberbtn.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            } else if (linebtn.isSelected()) {
                gc.setStroke(cpLine.getValue());
                line.setStartX(e.getX());
                line.setStartY(e.getY());
            } else if (rectbtn.isSelected()) {
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                rect.setX(e.getX());
                rect.setY(e.getY());
            } else if (circlebtn.isSelected()) {
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                circ.setCenterX(e.getX());
                circ.setCenterY(e.getY());
            } else if (elpslebtn.isSelected()) {
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                elps.setCenterX(e.getX());
                elps.setCenterY(e.getY());
            }
        });
        canvas.setOnMouseDragged(e -> {
            if (drowbtn.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            } else if (rubberbtn.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            }
        });
        canvas.setOnMouseReleased(e -> {
            if (drowbtn.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
                gc.closePath();
            } else if (rubberbtn.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            } else if (linebtn.isSelected()) {
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

                undoHistory.push(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
            } else if (rectbtn.isSelected()) {
                rect.setWidth(Math.abs((e.getX() - rect.getX())));
                rect.setHeight(Math.abs((e.getY() - rect.getY())));
                //rect.setX((rect.getX() > e.getX()) ? e.getX(): rect.getX());
                if (rect.getX() > e.getX()) {
                    rect.setX(e.getX());
                }
                //rect.setY((rect.getY() > e.getY()) ? e.getY(): rect.getY());
                if (rect.getY() > e.getY()) {
                    rect.setY(e.getY());
                }

                gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

                undoHistory.push(new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));

            } else if (circlebtn.isSelected()) {
                circ.setRadius((Math.abs(e.getX() - circ.getCenterX()) + Math.abs(e.getY() - circ.getCenterY())) / 2);

                if (circ.getCenterX() > e.getX()) {
                    circ.setCenterX(e.getX());
                }
                if (circ.getCenterY() > e.getY()) {
                    circ.setCenterY(e.getY());
                }

                gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());

                undoHistory.push(new Circle(circ.getCenterX(), circ.getCenterY(), circ.getRadius()));
            } else if (elpslebtn.isSelected()) {
                elps.setRadiusX(Math.abs(e.getX() - elps.getCenterX()));
                elps.setRadiusY(Math.abs(e.getY() - elps.getCenterY()));

                if (elps.getCenterX() > e.getX()) {
                    elps.setCenterX(e.getX());
                }
                if (elps.getCenterY() > e.getY()) {
                    elps.setCenterY(e.getY());
                }

                gc.strokeOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                gc.fillOval(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY());
                undoHistory.push(new Ellipse(elps.getCenterX(), elps.getCenterY(), elps.getRadiusX(), elps.getRadiusY()));
            }
            redoHistory.clear();
            Shape lastUndo = undoHistory.lastElement();
            lastUndo.setFill(gc.getFill());
            lastUndo.setStroke(gc.getStroke());
            lastUndo.setStrokeWidth(gc.getLineWidth());

        });
        cpLine.setOnAction(e -> {
            gc.setStroke(cpLine.getValue());
        });
        cpFill.setOnAction(e -> {
            gc.setFill(cpFill.getValue());
        });

        // slider
        slider.valueProperty().addListener(e -> {
            double width = slider.getValue();
            line_width.setText(String.format("%.1f", width));
            gc.setLineWidth(width);
        });

        /*------- Undo & Redo ------*/
        // Undo
        undo.setOnAction(e -> {
            if (!undoHistory.empty()) {
                gc.clearRect(0, 0, 1080, 790);
                Shape removedShape = undoHistory.lastElement();
                if (removedShape.getClass() == Line.class) {
                    Line tempLine = (Line) removedShape;
                    tempLine.setFill(gc.getFill());
                    tempLine.setStroke(gc.getStroke());
                    tempLine.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));

                } else if (removedShape.getClass() == Rectangle.class) {
                    Rectangle tempRect = (Rectangle) removedShape;
                    tempRect.setFill(gc.getFill());
                    tempRect.setStroke(gc.getStroke());
                    tempRect.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                } else if (removedShape.getClass() == Circle.class) {
                    Circle tempCirc = (Circle) removedShape;
                    tempCirc.setStrokeWidth(gc.getLineWidth());
                    tempCirc.setFill(gc.getFill());
                    tempCirc.setStroke(gc.getStroke());
                    redoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                } else if (removedShape.getClass() == Ellipse.class) {
                    Ellipse tempElps = (Ellipse) removedShape;
                    tempElps.setFill(gc.getFill());
                    tempElps.setStroke(gc.getStroke());
                    tempElps.setStrokeWidth(gc.getLineWidth());
                    redoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
                }
                Shape lastRedo = redoHistory.lastElement();
                lastRedo.setFill(removedShape.getFill());
                lastRedo.setStroke(removedShape.getStroke());
                lastRedo.setStrokeWidth(removedShape.getStrokeWidth());
                undoHistory.pop();

                for (int i = 0; i < undoHistory.size(); i++) {
                    Shape shape = undoHistory.elementAt(i);
                    if (shape.getClass() == Line.class) {
                        Line temp = (Line) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                    } else if (shape.getClass() == Rectangle.class) {
                        Rectangle temp = (Rectangle) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                        gc.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                    } else if (shape.getClass() == Circle.class) {
                        Circle temp = (Circle) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                        gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                    } else if (shape.getClass() == Ellipse.class) {
                        Ellipse temp = (Ellipse) shape;
                        gc.setLineWidth(temp.getStrokeWidth());
                        gc.setStroke(temp.getStroke());
                        gc.setFill(temp.getFill());
                        gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                        gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                    }
                }
            } else {
                System.out.println("there is no action to undo");
            }
        });

        // Redo
        redo.setOnAction(e -> {
            if (!redoHistory.empty()) {
                Shape shape = redoHistory.lastElement();
                gc.setLineWidth(shape.getStrokeWidth());
                gc.setStroke(shape.getStroke());
                gc.setFill(shape.getFill());

                redoHistory.pop();
                if (shape.getClass() == Line.class) {
                    Line tempLine = (Line) shape;
                    gc.strokeLine(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY());
                    undoHistory.push(new Line(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY()));
                } else if (shape.getClass() == Rectangle.class) {
                    Rectangle tempRect = (Rectangle) shape;
                    gc.fillRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());
                    gc.strokeRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());

                    undoHistory.push(new Rectangle(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight()));
                } else if (shape.getClass() == Circle.class) {
                    Circle tempCirc = (Circle) shape;
                    gc.fillOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
                    gc.strokeOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());

                    undoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
                } else if (shape.getClass() == Ellipse.class) {
                    Ellipse tempElps = (Ellipse) shape;
                    gc.fillOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());
                    gc.strokeOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());

                    undoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
                }
                Shape lastUndo = undoHistory.lastElement();
                lastUndo.setFill(gc.getFill());
                lastUndo.setStroke(gc.getStroke());
                lastUndo.setStrokeWidth(gc.getLineWidth());
            } else {
                System.out.println("there is no action to redo");
            }
        });
    }


//    public void takeSnapShot(ActionEvent actionEvent) {
//        FileChooser fileChooser = new FileChooser();
//        File selectedFile = fileChooser.showSaveDialog(Main.stage);
//
//        WritableImage snapshot = zoomPane.snapshot(new SnapshotParameters(), null);
//        BufferedImage bufferedImage = new BufferedImage(550, 400, BufferedImage.TYPE_INT_ARGB);
//        BufferedImage image;
//        System.out.println("SNAP "+snapshot.getWidth()+" "+ snapshot.getHeight());
//        image = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, bufferedImage);
//        try {
//            Graphics2D gd = (Graphics2D) image.getGraphics();
//            gd.translate(zoomPane.getWidth(), zoomPane.getHeight());
//            ImageIO.write(image, "png",new File( selectedFile+".png"));
//        } catch (IOException ex) {
////            Logger.getLogger(TrySnapshot.class.getName()).log(Level.SEVERE, null, ex);
//        };


}
