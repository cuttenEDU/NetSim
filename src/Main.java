import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class Main extends JFrame {

    public Main(){
        super("Построение визуализации сети");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FXPanel panel = new FXPanel();
        setContentPane(panel.createPanel());
        setLocationRelativeTo(null);

//        addComponentListener(new ComponentAdapter() {
//            public void componentResized(ComponentEvent componentEvent) {
//                panel.resize(getWidth(),getHeight());
//            }
//        });
        setResizable(false);
        pack();
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.setVisible(true);
    }
}
