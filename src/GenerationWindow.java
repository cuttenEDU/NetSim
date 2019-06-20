import javafx.scene.chart.XYChart;

import javax.swing.*;

public class GenerationWindow extends JDialog {

    GenerationFXPanel panel;

    public GenerationWindow(FXPanel parentPanel){
        //super("Сгенерировать граф...");
        setTitle("Сгенерировать сеть...");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        panel = new GenerationFXPanel(parentPanel);
        setContentPane(panel.createPanel(parentPanel.getnCount()));
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        setSize(250,350);
        pack();
    }


}
