import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class GraphPath {

    private double length;
    private GraphNode firstNode;
    private GraphNode secondNode;
    private Pane pPane;
    private Line pathLine;
    private boolean isDisabled;

    private final Color strokeOnColor = Color.BLACK;
    private final Color strokeOffColor = Color.GREY;
    public GraphPath(GraphNode firstNode, GraphNode secondNode, double length, boolean isDisabled) {
        this.firstNode = firstNode;
        this.secondNode = secondNode;
        this.length = length;
        this.isDisabled = isDisabled;
        pPane = new Pane();
        pPane.setCursor(Cursor.HAND);
        if (firstNode.getX() == secondNode.getX()) {
            pPane.setPrefWidth(20);
            pPane.setPrefHeight(length);
            pPane.setLayoutX(firstNode.getX() - 10);
            pPane.setLayoutY(firstNode.getY());
            pathLine = new Line(10, 0, pPane.getPrefWidth() - 10, pPane.getPrefHeight());
        } else {
            pPane.setPrefWidth(length);
            pPane.setPrefHeight(20);
            pPane.setLayoutX(firstNode.getX());
            pPane.setLayoutY(firstNode.getY() - 10);
            pathLine = new Line(0, 10, pPane.getPrefWidth(), pPane.getPrefHeight() - 10);
        }
        pathLine.setStroke(isDisabled ? strokeOffColor : strokeOnColor);
        pathLine.setStrokeWidth(2);
        pPane.getChildren().add(pathLine);
        pPane.setOnMouseClicked(event -> {
            System.out.println(firstNode.getId() + "\n" + secondNode.getId());
            if (this.isDisabled) {
                pathLine.setStroke(strokeOnColor);
                firstNode.getNeighbours().add(secondNode);
                secondNode.getNeighbours().add(firstNode);
                System.out.println("on");
                setDisabled(false);

            } else {
                pathLine.setStroke(strokeOffColor);
                firstNode.getNeighbours().remove(secondNode);
                secondNode.getNeighbours().remove(firstNode);
                System.out.println("off");
                setDisabled(true);
            }
            System.out.println("click");
        });
    }


    public Pane getpPane() {
        return pPane;
    }

    void disable(){
        isDisabled = true;
        pathLine.setStroke(strokeOffColor);
    }

    void enable(){
        isDisabled = false;
        pathLine.setStroke(strokeOnColor);
    }
    double getX1(){
        return firstNode.getX();
    }

    double getY1(){
        return firstNode.getY();
    }

    double getX2(){
        return secondNode.getX();
    }

    double getY2(){
        return secondNode.getY();
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }
}
