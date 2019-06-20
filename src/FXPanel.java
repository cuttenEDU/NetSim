
import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;

import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;


public class FXPanel {


    private ArrayList<ArrayList<GraphNode>> nodesArray = new ArrayList<>();
    private ArrayList<Circle> circlesArray = new ArrayList<>();
    private ArrayList<GraphPath> pathsArray = new ArrayList<>();
    private ArrayList<Pane> linesArray = new ArrayList<>();

    //TODO: dynamic margin
    private double margin = 70;
    static double diameter = 20;

    private Color nodeSColor = Color.ORANGE;
    private Color nodeIColor = Color.RED;
    private Color nodeRColor = Color.GREEN;
    private Color nodeStrokeColor = Color.BLACK;

    CheckBox torCheckBox;
    private double canvasWOffset = 0;
    private double canvasHOffset = 0;

    private Pane graphPane;
    private Slider iSlider;
    private TextField iTextField;
    private Slider rSlider;
    private TextField rTextField;
    private Slider sizeSlider;
    private TextField sizeTextField;
    private Parent parent;
    private long deltaTime, lastTime;
    private Runnable runnable;

    private int nodesX;
    private int nodesY;
    private int nCount;
    private int sCount;
    private int iCount;
    private int rCount;
    private double graphAreaWidth;
    private double graphAreaHeight;
    private AnchorPane mainPane;

    private int iProb;
    private int rProb;
    private double verticalEdge, horizontalEdge;
    double greaterEdge;

    private ChartsWindow chartsWindow;
    private int modelTime;
    private XYChart.Series sSeries;
    private XYChart.Series iSeries;
    private XYChart.Series rSeries;
    private Pane controls;
    private Pane graphControls;
    private Button generateButton;
    private Button loadButton;
    private Button saveButton;

    private GenerationWindow generationWindow;

    private boolean tor;
    private boolean isBuilt;
    private boolean firstInit = true;
    private boolean running;
    private boolean loadedFromFile;
    private boolean isbeingloaded;


    JFXPanel createPanel() {
        JFXPanel mainPanel = new JFXPanel();
        try {
            parent = FXMLLoader.load(FXPanel.class.getResource("mainScene2.fxml"));
            mainPane = (AnchorPane) parent.lookup("#mainPane");
            controls = (Pane) parent.lookup("#controlsPane");
            controls.setVisible(false);
            Button startButton = (Button) parent.lookup("#startSim");
            Button graphsButton = (Button) parent.lookup("#graphs");

            isbeingloaded = false;
            generateButton = (Button) parent.lookup("#generationButton");
            loadButton = (Button) parent.lookup("#loadButton");
            saveButton = (Button) parent.lookup("#saveGraph");

            torCheckBox = (CheckBox) parent.lookup("#tor");
            tor = torCheckBox.isSelected();
            graphPane = (Pane) parent.lookup("#graphPane");
            sSeries = new XYChart.Series();
            sSeries.setName("Воприимчивые");
            iSeries = new XYChart.Series();
            iSeries.setName("Инфецированные");
            rSeries = new XYChart.Series();
            rSeries.setName("С иммунитетом");
            isBuilt = false;
//            drawPaneNode(new GraphNode(0,0,60,620,1));
//            drawPaneNode(new GraphNode(0,1,30,60,2));
//            drawPanePath(new GraphNode(0,0,30,30,1), new GraphNode(0,1,30,60,2));
            graphAreaWidth = graphPane.getPrefWidth();
            graphAreaHeight = graphPane.getPrefHeight();


            generateButton.setOnAction(event -> {
                generationWindow = new GenerationWindow(this);
            });

            saveButton.setOnAction(event -> {
                saveGraph();
            });

            loadButton.setOnAction(event -> {
                loadGraph();
            });


            runnable = () -> {
                if (lastTime + deltaTime < System.currentTimeMillis()) {
                    updateSim();
                    System.out.println("Susceptible: " + sCount + "\t" + "Infected: " + iCount + "\t" + "Resistant: " + rCount);
                    sSeries.getData().add(new XYChart.Data<>(modelTime, sCount));
                    iSeries.getData().add(new XYChart.Data<>(modelTime, iCount));
                    rSeries.getData().add(new XYChart.Data<>(modelTime, rCount));

                    modelTime++;
                    lastTime = System.currentTimeMillis();
                }
                if (running)
                    Platform.runLater(runnable);
            };
            startButton.setOnAction(event -> {
                if (running) {
                    running = false;
                    startButton.setText("Запустить симуляцию");
                    iSlider.setDisable(false);
                    rSlider.setDisable(false);
                    iTextField.setDisable(false);
                    rTextField.setDisable(false);
                    torCheckBox.setDisable(false);
                } else {
                    startButton.setText("Остановить симуляцию");
                    iSlider.setDisable(true);
                    rSlider.setDisable(true);
                    iTextField.setDisable(true);
                    rTextField.setDisable(true);

                    torCheckBox.setDisable(true);
                    iProb = Integer.parseInt(iTextField.getText());
                    rProb = Integer.parseInt(rTextField.getText());
                    modelTime = 0;
                    deltaTime = 1000;
                    running = true;
                    Platform.runLater(runnable);
                }
            });

            graphsButton.setOnAction(event -> {
                if (chartsWindow == null) {
                    chartsWindow = new ChartsWindow();
                    chartsWindow.updateChart(sSeries, iSeries, rSeries);
                } else {
                    chartsWindow.setVisible(true);
                }
            });

            initControls();
            mainPanel.setScene(new Scene(parent));
        } catch (IOException e) {
            System.err.println("FATAL ERROR: FAILED TO INIT THE MAIN SCENE");
            System.err.println(e.toString());
        }
        return mainPanel;
    }

    private void updateSim() {
        sCount = iCount = rCount = 0;
        //System.out.printf("IM WORKING!!!!");
        for (ArrayList<GraphNode> arrayList : nodesArray
                ) {
            for (GraphNode graphNode : arrayList
                    ) {
                switch (graphNode.getState()) {
                    case Susceptible:
                        sCount++;
                        break;
                    case Infected:
                        for (GraphNode neighbour : graphNode.getNeighbours()) {
                            if (neighbour.getState() == GraphNode.State.Susceptible)
                                if (new Random().nextInt(101) < iProb) {
                                    neighbour.setState(GraphNode.State.Infected);
                                    iCount++;
                                }
                        }
                        if (new Random().nextInt(101) < rProb) {
                            graphNode.setState(GraphNode.State.Resistant);
                            rCount++;
                        } else
                            iCount++;
                        break;
                    case Resistant:
                        rCount++;
                        break;
                }
                circlesArray.get(graphNode.getId()).setFill(getColor(graphNode));
            }
        }
    }


    //TODO: sanitize input
    private void initControls() {
        iSlider = (Slider) parent.lookup("#iSlider");
        iTextField = (TextField) parent.lookup("#iTextField");
        rSlider = (Slider) parent.lookup("#rSlider");
        rTextField = (TextField) parent.lookup("#rTextField");
        sizeSlider = (Slider) parent.lookup("#sizeSlider");
        sizeTextField = (TextField) parent.lookup("#sizeTextField");
        iSlider.valueProperty().addListener((observable, oldValue, newValue) -> iTextField.setText(Integer.toString(newValue.intValue())));
        rSlider.valueProperty().addListener((observable, oldValue, newValue) -> rTextField.setText(Integer.toString(newValue.intValue())));
//        sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            sizeTextField.setText(Double.toString(newValue.doubleValue()));
//            for (Circle node : circlesArray) {
//                node.setRadius(greaterEdge * newValue.doubleValue() / 2);
//            }
//        });
        iTextField.textProperty().addListener((observable, oldValue, newValue) -> iSlider.setValue(Integer.parseInt(newValue)));
        rTextField.textProperty().addListener((observable, oldValue, newValue) -> rSlider.setValue(Integer.parseInt(newValue)));
//        sizeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//            sizeSlider.setValue(Double.parseDouble(newValue));
//            for (Circle node : circlesArray) {
//                node.setRadius(greaterEdge * Double.parseDouble(newValue) / 2);
//            }
//        });
//        sizeSlider.setMax(1.0);
//        sizeSlider.setValue(0.5);
//        sizeSlider.setMajorTickUnit(0.1);
//        sizeSlider.setBlockIncrement(0.05);
        torCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!isbeingloaded) {
                tor = newValue;
                if (isBuilt) {
                    if (!oldValue && newValue)
                        addTorNeighbours();
                    else if (oldValue && !newValue)
                        removeTorNeighbours();
                }
            }
        });
    }


    void distributeNodes(int sCount, int iCount, int rCount) {
        this.sCount = sCount;
        this.iCount = iCount;
        this.rCount = rCount;
        ArrayList<GraphNode.State> states = new ArrayList<>();
        for (int i = 0; i < sCount; i++) {
            states.add(GraphNode.State.Susceptible);
        }
        for (int i = 0; i < iCount; i++) {
            states.add(GraphNode.State.Infected);
        }
        for (int i = 0; i < rCount; i++) {
            states.add(GraphNode.State.Resistant);
        }
        Collections.shuffle(states);
        for (ArrayList<GraphNode> a : nodesArray) {
            for (GraphNode node : a) {
                node.setState(states.get(states.size() - 1));
                states.remove(states.size() - 1);
            }
        }


        drawGraph();

    }


    void initGraph(int nodesX, int nodesY) {
        if (!firstInit) {
            clearGraphPane();
        }
        this.nodesX = nodesX;
        this.nodesY = nodesY;
        nCount = nodesX * nodesY;
        double graphWidth = graphAreaWidth - 2 * margin;
        double graphHeight = graphAreaHeight - 2 * margin;
        horizontalEdge = nodesX == 0 ? graphWidth / 2 : graphWidth / (nodesX - 1);
        verticalEdge = nodesY == 0 ? graphHeight / 2 : graphHeight / (nodesY - 1);
        greaterEdge = horizontalEdge > verticalEdge ? horizontalEdge : verticalEdge;
        diameter = greaterEdge * 0.5;
    }

    void generateGraph() {
        nodesArray.clear();
        int id = 0;
        for (int i = 0; i < nodesY; i++) {
            nodesArray.add(new ArrayList<>());
            for (int j = 0; j < nodesX; j++) {
                GraphNode graphNode = new GraphNode(i, j, margin + j * horizontalEdge - canvasWOffset, margin + i * verticalEdge - canvasHOffset, id++);
                nodesArray.get(i).add(graphNode);

            }
        }


        // Neighbours init
        for (int i = 0; i < nodesY; i++) {
            for (int j = 0; j < nodesX; j++) {
                try {
                    nodesArray.get(i).get(j).addNeighbour(nodesArray.get(i).get(j + 1));
                } catch (IndexOutOfBoundsException ignored) {
                    if (tor)
                        nodesArray.get(i).get(j).addTorNeighbour(nodesArray.get(i).get(0));
                }
                try {
                    nodesArray.get(i).get(j).addNeighbour(nodesArray.get(i).get(j - 1));
                } catch (IndexOutOfBoundsException ignored) {
                    if (tor)
                        nodesArray.get(i).get(j).addTorNeighbour(nodesArray.get(i).get(nodesX - 1));
                }
                try {
                    nodesArray.get(i).get(j).addNeighbour(nodesArray.get(i + 1).get(j));
                } catch (IndexOutOfBoundsException ignored) {
                    if (tor)
                        nodesArray.get(i).get(j).addTorNeighbour(nodesArray.get(0).get(j));
                }
                try {
                    nodesArray.get(i).get(j).addNeighbour(nodesArray.get(i - 1).get(j));
                } catch (IndexOutOfBoundsException ignored) {
                    if (tor)
                        nodesArray.get(i).get(j).addTorNeighbour(nodesArray.get(nodesY - 1).get(j));
                }
                System.out.println(nodesArray.get(i).get(j).getNeighbours().toString());

            }
        }
        drawGraph();
    }

    private void drawGraph() {
        clearGraphPane();
        for (int i = 0; i < nodesY; i++) {
            for (int j = 0; j < nodesX; j++) {
                GraphNode node = nodesArray.get(i).get(j);
                try {
                    GraphNode neighbour1 = nodesArray.get(i).get(j + 1);
                    drawPanePath(node, neighbour1);
                } catch (IndexOutOfBoundsException ignored) {
                }
                try {
                    GraphNode neighbour2 = nodesArray.get(i + 1).get(j);
                    drawPanePath(node, neighbour2);
                } catch (IndexOutOfBoundsException ignored) {
                }
                drawPaneNode(node);
            }
        }

        isBuilt = true;

        generateButton.toFront();
        loadButton.toFront();
        if (firstInit) {
            firstInit = false;
            controls.setVisible(true);
            generateButton.setLayoutX(170);
            generateButton.setLayoutY(620);
            loadButton.setLayoutX(340);
            loadButton.setLayoutY(620);
        }
    }

    private void drawPanePath(GraphNode graphNode1, GraphNode graphNode2) {
        boolean isVertical = graphNode1.getX() == graphNode2.getX();
        GraphPath graphPath = new GraphPath(graphNode1, graphNode2, isVertical ? verticalEdge : horizontalEdge, loadedFromFile);
        if (loadedFromFile) {
            if (graphNode1.isNeighbour(graphNode2))
                graphPath.enable();
        }
        graphNode1.addPath(graphPath);
        graphNode2.addPath(graphPath);
        graphPane.getChildren().add(graphPath.getpPane());
        pathsArray.add(graphPath);
    }

    public void saveGraph() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Файл XML (*.xml)", "*.xml"));
            fileChooser.setInitialDirectory(new File("."));
            File file = fileChooser.showSaveDialog(null);

            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
//            XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(new FileWriter("network.xml"));
            Writer writer = new FileWriter(file);
            XMLStreamWriter xMLStreamWriter = new IndentingXMLStreamWriter(xMLOutputFactory.createXMLStreamWriter(writer));
            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("Graph");
            xMLStreamWriter.writeAttribute("width", Integer.toString(nodesX));
            xMLStreamWriter.writeAttribute("height", Integer.toString(nodesY));
            xMLStreamWriter.writeAttribute("isTor", Boolean.toString(tor));
            xMLStreamWriter.writeAttribute("sCount", Integer.toString(sCount));
            xMLStreamWriter.writeAttribute("iCount", Integer.toString(iCount));
            xMLStreamWriter.writeAttribute("rCount", Integer.toString(rCount));
            xMLStreamWriter.writeStartElement("Nodes");
            for (int i = 0; i < nodesArray.size(); i++) {
                for (int j = 0; j < nodesArray.get(i).size(); j++) {
                    GraphNode node = nodesArray.get(i).get(j);
                    xMLStreamWriter.writeEmptyElement("Node");
                    xMLStreamWriter.writeAttribute("state", node.getState().toString());
                    xMLStreamWriter.writeAttribute("i", Integer.toString(node.getI()));
                    xMLStreamWriter.writeAttribute("j", Integer.toString(node.getJ()));
                    xMLStreamWriter.writeAttribute("x", Double.toString(node.getX()));
                    xMLStreamWriter.writeAttribute("y", Double.toString(node.getY()));
                    xMLStreamWriter.writeAttribute("id", Integer.toString(node.getId()));
                    xMLStreamWriter.writeAttribute("isDisabled", Boolean.toString(node.isDisabled()));
                }
            }
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("Neighbours");
            for (int i = 0; i < nodesArray.size(); i++) {
                for (int j = 0; j < nodesArray.get(i).size(); j++) {
                    GraphNode node = nodesArray.get(i).get(j);
                    xMLStreamWriter.writeStartElement("Node");
                    xMLStreamWriter.writeAttribute("i", Integer.toString(node.getI()));
                    xMLStreamWriter.writeAttribute("j", Integer.toString(node.getJ()));
                    for (GraphNode neighbour : node.getNeighbours()) {
                        xMLStreamWriter.writeEmptyElement("Neighbour");
                        xMLStreamWriter.writeAttribute("i", Integer.toString(neighbour.getI()));
                        xMLStreamWriter.writeAttribute("j", Integer.toString(neighbour.getJ()));
                        xMLStreamWriter.writeAttribute("isTorNeighbour",Boolean.toString(node.isTorNeighbour(neighbour)));
                    }
                    xMLStreamWriter.writeEndElement();
                }
            }
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndDocument();
            xMLStreamWriter.flush();
            xMLStreamWriter.close();

            stringWriter.close();

        } catch (XMLStreamException e) {
            System.err.println(e.toString());
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    public void loadGraph() {
        boolean nodesAddingMode = false;
        boolean neighboursAddingMode = false;
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Файл XML (*.xml)", "*.xml"));
            fileChooser.setInitialDirectory(new File("."));
            File file = fileChooser.showOpenDialog(null);

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader eventReader =
                    factory.createXMLEventReader(new FileReader(file));
            nodesArray.clear();
            loadedFromFile = true;
            Attribute attribute = null;
            String value = null;
            int nodeI = 0;
            int nodeJ = 0;
            int neighbourI = 0;
            int neighbourJ = 0;
            isbeingloaded = true;
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                switch (event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        StartElement startElement = event.asStartElement();
                        String qName = startElement.getName().getLocalPart();

                        if (qName.equalsIgnoreCase("Graph")) {
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                attribute = attributes.next();
                                value = attribute.getValue();
                                switch (attribute.getName().getLocalPart()) {
                                    case "isTor":
                                        tor = Boolean.parseBoolean(value);
                                        torCheckBox.setSelected(true);
                                        break;
                                    case "sCount":
                                        sCount = Integer.parseInt(value);
                                        break;
                                    case "iCount":
                                        iCount = Integer.parseInt(value);
                                        break;
                                    case "rCount":
                                        rCount = Integer.parseInt(value);
                                        break;
                                    case "width":
                                        nodesX = Integer.parseInt(value);
                                        break;
                                    case "height":
                                        nodesY = Integer.parseInt(value);
                                }
                            }
                            for (int i = 0; i < nodesY; i++) {
                                nodesArray.add(new ArrayList<>());
                            }
                        } else if (qName.equalsIgnoreCase("Nodes"))
                            nodesAddingMode = true;
                        else if (qName.equalsIgnoreCase("Node") && nodesAddingMode) {
                            int i = 0, j = 0, id = 0;
                            double x = 0, y = 0;
                            boolean isDisabled = false;
                            GraphNode.State state = null;
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext()) {
                                attribute = attributes.next();
                                value = attribute.getValue();
                                switch (attribute.getName().getLocalPart()) {
                                    case "x":
                                        x = Double.parseDouble(value);
                                        break;
                                    case "y":
                                        y = Double.parseDouble(value);
                                        break;
                                    case "i":
                                        i = Integer.parseInt(value);
                                        break;
                                    case "j":
                                        j = Integer.parseInt(value);
                                        break;
                                    case "state":
                                        state = GraphNode.State.valueOf(value);
                                        break;
                                    case "id":
                                        id = Integer.parseInt(value);
                                        break;
                                    case "isDisabled":
                                        isDisabled = Boolean.parseBoolean(value);
                                        break;
                                }
                            }

                            GraphNode node = new GraphNode(i, j, x, y, id, state, isDisabled);
                            nodesArray.get(i).add(j, node);
                        } else if (qName.equalsIgnoreCase("Neighbours")) {
                            neighboursAddingMode = true;
                        } else if (qName.equalsIgnoreCase("Node") && neighboursAddingMode) {
                            Iterator<Attribute> attributes = event.asStartElement().getAttributes();
                            while (attributes.hasNext()) {
                                attribute = attributes.next();
                                value = attribute.getValue();
                                switch (attribute.getName().getLocalPart()) {
                                    case "i":
                                        nodeI = Integer.parseInt(value);
                                        break;
                                    case "j":
                                        nodeJ = Integer.parseInt(value);
                                        break;
                                }
                            }
                        } else if (qName.equalsIgnoreCase("Neighbour") && neighboursAddingMode) {
                            Iterator<Attribute> attributes = event.asStartElement().getAttributes();
                            boolean isTorNeighbour = false;
                            while (attributes.hasNext()) {
                                attribute = attributes.next();
                                value = attribute.getValue();
                                switch (attribute.getName().getLocalPart()) {
                                    case "i":
                                        neighbourI = Integer.parseInt(value);
                                        break;
                                    case "j":
                                        neighbourJ = Integer.parseInt(value);
                                        break;
                                    case "isTorNeighbour":
                                        isTorNeighbour = Boolean.parseBoolean(value);
                                        break;
                                }
                            }
                            try {
                                GraphNode currNode = nodesArray.get(nodeI).get(nodeJ);
                                if (isTorNeighbour)
                                    currNode.addTorNeighbour(nodesArray.get(neighbourI).get(neighbourJ));
                                else
                                    currNode.addNeighbour(nodesArray.get(neighbourI).get(neighbourJ));
                            } catch (NullPointerException e) {
                                System.err.println("node I: " + nodeI + "nodeJ: " + nodeJ + "neighbourI: " + neighbourI + "neighbourJ: " + neighbourJ);
                                System.err.println(nodesArray.get(neighbourI).get(neighbourJ));
                                e.printStackTrace();
                            }
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        EndElement endElement = event.asEndElement();
                        String eName = endElement.getName().getLocalPart();
                        if (eName.equalsIgnoreCase("Nodes"))
                            nodesAddingMode = false;
                        if (eName.equalsIgnoreCase("Neighbours"))
                            neighboursAddingMode = false;
                }
            }
            initGraph(this.nodesX, this.nodesY);
            drawGraph();
            isbeingloaded = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

    }

    private void drawPaneNode(GraphNode graphNode) {
        Circle nodeCircle = new Circle(graphNode.getX(), graphNode.getY(), diameter / 2, getColor(graphNode));
        nodeCircle.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!graphNode.isDisabled()) {
                    switch (graphNode.getState()) {
                        case Susceptible:
                            graphNode.setState(GraphNode.State.Infected);
                            break;
                        case Infected:
                            graphNode.setState(GraphNode.State.Resistant);
                            break;
                        case Resistant:
                            graphNode.setState(GraphNode.State.Susceptible);
                            break;
                    }
                    nodeCircle.setFill(getColor(graphNode));
                    graphNode.printNeighbours();
                }
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                if (graphNode.isDisabled()) {
                    graphNode.enable();
                    nodeCircle.setFill(getColor(graphNode));

                } else {
                    graphNode.disable();
                    nodeCircle.setFill(getColor(graphNode));
                }
            }

        });
        nodeCircle.setStroke(nodeStrokeColor);
        nodeCircle.setCursor(Cursor.HAND);
        graphPane.getChildren().add(nodeCircle);
        circlesArray.add(nodeCircle);
    }


    private void clearGraphPane() {
        pathsArray.clear();
        circlesArray.clear();
        linesArray.clear();
        graphPane.getChildren().clear();
    }


    private Color getColor(GraphNode graphNode) {
        if (graphNode.isDisabled())
            return new Color(0.9568627451, 0.9568627451, 0.9568627451, 1);
        else {
            switch (graphNode.getState()) {
                case Susceptible:
                    return nodeSColor;
                case Infected:
                    return nodeIColor;
                case Resistant:
                    return nodeRColor;
            }
            return Color.BLACK;
        }
    }


    private void addTorNeighbours() {
        for (int i = 0; i < nodesY; i++) {
            for (int j = 0; j < nodesX; j++) {
                GraphNode node = nodesArray.get(i).get(j);
                if (i == 0) {
                    node.addTorNeighbour(nodesArray.get(nodesY - 1).get(j));
                }
                if (i == nodesY - 1) {
                    node.addTorNeighbour(nodesArray.get(0).get(j));
                }
                if (j == 0) {
                    node.addTorNeighbour(nodesArray.get(i).get(nodesX - 1));
                }
                if (j == nodesX - 1) {
                    node.addTorNeighbour(nodesArray.get(i).get(0));
                }
            }
        }
    }

    private void removeTorNeighbours() {
        for (int i = 0; i < nodesY; i++) {
            for (int j = 0; j < nodesX; j++) {
                GraphNode node = nodesArray.get(i).get(j);
                if (i == 0 || i == nodesY - 1 || j == 0 || j == nodesX - 1) {
                    node.removeTorNeighbours();
                }
            }
        }
    }


    public void resize(double width, double height) {
        mainPane.resize(width, height);
        System.out.println("width: " + mainPane.getWidth() + "\t" + "height: " + mainPane.getHeight());
    }

    private boolean isVertical(Line line) {
        return line.getStartX() == line.getEndX();
    }

    public boolean isFirstInit() {
        return firstInit;
    }


    public int getnCount() {
        return nCount;
    }
}

