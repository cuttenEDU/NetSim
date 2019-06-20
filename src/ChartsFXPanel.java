import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.io.IOException;

public class ChartsFXPanel {

    private Parent parent;
    private JFXPanel mainPanel;
    private XYChart.Series sSeries;
    private XYChart.Series iSeries;
    private XYChart.Series rSeries;
    LineChart<Number,Number> lineChart;

    public JFXPanel createPanel(){
        mainPanel = new JFXPanel();
        try {
            sSeries = new XYChart.Series();
            iSeries = new XYChart.Series();
            rSeries = new XYChart.Series();
            parent = FXMLLoader.load(FXPanel.class.getResource("chartScene.fxml"));
            final NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel("Модельное время");
            xAxis.setTickUnit(1);
            final NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Количество узлов");
            lineChart = new LineChart<>(xAxis,yAxis);
            System.out.println(lineChart.getAnimated());


            mainPanel.setScene(new Scene(lineChart));
        } catch (IOException e) {
            System.err.println("FATAL ERROR: FAILED TO INIT THE GRAPH SCENE");
            System.err.println(e.toString());
        }
        return mainPanel;
    }

    public void updateChart(XYChart.Series sSeries, XYChart.Series iSeries, XYChart.Series rSeries){
        lineChart.getData().addAll(iSeries,sSeries,rSeries);
    }

}
