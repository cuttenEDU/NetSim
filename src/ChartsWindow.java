import javafx.scene.chart.XYChart;

import javax.swing.*;

public class ChartsWindow extends JFrame {

    ChartsFXPanel panel;

    public ChartsWindow(){
        super("Графики");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new ChartsFXPanel();
        setContentPane(panel.createPanel());
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        pack();
    }

    public void updateChart(XYChart.Series sSeries, XYChart.Series iSeries, XYChart.Series rSeries){
        panel.updateChart(sSeries,iSeries,rSeries);
    }
}
