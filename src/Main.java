import javax.swing.*;

/**
 * This is our Main Class and it runs the program
 */
public class Main {

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                VisualizerFrame frame = new VisualizerFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setTitle("Path Finding Visualiser");
                frame.setVisible(true);
            }
        });
    }


}
