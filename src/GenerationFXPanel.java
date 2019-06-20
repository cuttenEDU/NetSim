import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

class GenerationFXPanel {

    private JFXPanel mainpanel;
    private Parent parent;
    private IntField XTextField;
    private IntField YTextField;
    private Slider SCSlider;
    private Slider ICSlider;
    private Slider RCSlider;
    private IntField SCTextField;
    private IntField ICTextField;
    private IntField RCTextField;
    private AnchorPane anchorPane;
    private FXPanel parentPanel;
    private Button generateButton;
    private Button buildButton;
    private int nodesX;
    private int nodesY;


    public GenerationFXPanel(FXPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    JFXPanel createPanel(int graphNodesCount) {
        mainpanel = new JFXPanel();
        try {
            parent = FXMLLoader.load(FXPanel.class.getResource("generateScene.fxml"));
            generateButton = (Button) parent.lookup(("#generateButton"));
            buildButton = (Button) parent.lookup(("#buildButton"));
            initFields();
            anchorPane = (AnchorPane) parent.lookup("#anchor");
            anchorPane.getChildren().addAll(SCTextField, ICTextField, RCTextField, XTextField, YTextField);
            mainpanel.setScene(new Scene(parent));
            if (graphNodesCount!=0)
                enableFields(graphNodesCount);
            buildButton.setOnAction(event -> {
                nodesX = XTextField.getValue();
                nodesY = YTextField.getValue();
                parentPanel.initGraph(nodesX,nodesY);
                parentPanel.generateGraph();
                int nCount = nodesX*nodesY;
                enableFields(nCount);
            });


            generateButton.setOnAction(event -> {
                parentPanel.distributeNodes((int)SCSlider.getValue(),(int)ICSlider.getValue(),(int)RCSlider.getValue());
            });

            //initControls();
        } catch (IOException e) {
            System.err.println("FATAL ERROR: FAILED TO INIT GENERATION WINDOW");
            System.err.println(e.toString());
        }
        return mainpanel;
    }


    void initFields() {
        XTextField = new IntField(0, 200, 5);
        XTextField.setLayoutX(191);
        XTextField.setLayoutY(18);
        XTextField.setPrefWidth(36);
        XTextField.setPrefHeight(25);
        XTextField.setAlignment(Pos.CENTER_RIGHT);
        YTextField = new IntField(0, 200, 5);
        YTextField.setLayoutX(249);
        YTextField.setLayoutY(18);
        YTextField.setPrefWidth(36);
        YTextField.setPrefHeight(25);
        YTextField.setAlignment(Pos.CENTER_RIGHT);
        SCTextField = new IntField(0, 100, 0);
        SCTextField.setLayoutX(241);
        SCTextField.setLayoutY(120);
        SCTextField.setPrefWidth(44);
        SCTextField.setPrefHeight(25);
        SCTextField.setAlignment(Pos.CENTER_RIGHT);
        ICTextField = new IntField(0, 100, 0);
        ICTextField.setLayoutX(241);
        ICTextField.setLayoutY(200);
        ICTextField.setPrefWidth(44);
        ICTextField.setPrefHeight(25);
        ICTextField.setAlignment(Pos.CENTER_RIGHT);
        RCTextField = new IntField(0, 100, 0);
        RCTextField.setLayoutX(241);
        RCTextField.setLayoutY(280);
        RCTextField.setPrefWidth(44);
        RCTextField.setPrefHeight(25);
        RCTextField.setAlignment(Pos.CENTER_RIGHT);
        SCSlider = (Slider) parent.lookup("#SCSlider");
        ICSlider = (Slider) parent.lookup("#ICSlider");
        RCSlider = (Slider) parent.lookup("#RCSlider");
        SCSlider.valueProperty().bindBidirectional(SCTextField.valueProperty());
        ICSlider.valueProperty().bindBidirectional(ICTextField.valueProperty());
        RCSlider.valueProperty().bindBidirectional(RCTextField.valueProperty());
        if(parentPanel.isFirstInit()){
            SCTextField.setDisable(true);
            ICTextField.setDisable(true);
            RCTextField.setDisable(true);
            SCSlider.setDisable(true);
            ICSlider.setDisable(true);
            RCSlider.setDisable(true);
            generateButton.setDisable(true);
        }
    }

    void enableFields(int nCount){
        SCSlider.setDisable(false);
        ICSlider.setDisable(false);
        RCSlider.setDisable(false);
        SCTextField.setDisable(false);
        ICTextField.setDisable(false);
        RCTextField.setDisable(false);
        generateButton.setDisable(false);
        SCSlider.setMax(nCount);
        SCSlider.setValue(nCount);
        ICSlider.setMax(nCount);
        RCSlider.setMax(nCount);
        SCTextField.setMaxValue(nCount);
        ICTextField.setMaxValue(nCount);
        RCTextField.setMaxValue(nCount);
    }
//    private void initControls() {
//
//
//
//        SCSlider = (Slider) parent.lookup("#SCSlider");
//        SCTextField = (TextField) parent.lookup("#SCTF");
//        ICSlider = (Slider) parent.lookup("#ICSlider");
//        ICTextField = (TextField) parent.lookup("#ICTF");
//        RCSlider = (Slider) parent.lookup("#RCSlider");
//        RCTextField = (TextField) parent.lookup("#RCTF");
//        SCSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            SCTextField.setText(Integer.toString(newValue.intValue()));
//            if (RCSlider.getValue() + ICSlider.getValue()>100-newValue.intValue())
//        });
//        SCTextField.textProperty().addListener((observable, oldValue, newValue) -> SCSlider.setValue(Integer.parseInt(newValue)));
//        ICSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            ICTextField.setText(Integer.toString(newValue.intValue()));
//            if (SCSlider.getValue() + RCSlider.getValue()>100-newValue)
//        });
//        ICTextField.textProperty().addListener((observable, oldValue, newValue) -> ICSlider.setValue(Integer.parseInt(newValue)));
//        RCSlider.valueProperty().addListener((observable, oldValue, newValue) -> RCTextField.setText(Integer.toString(newValue.intValue())));
//        RCTextField.textProperty().addListener((observable, oldValue, newValue) -> RCSlider.setValue(Integer.parseInt(newValue)));
//    }
}
