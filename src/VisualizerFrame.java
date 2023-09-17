import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * This is where our program shows hte GUI
 */
public class VisualizerFrame extends JFrame implements ActionListener {

    private Grid grid;
    private JPanel container;
    private JPanel controlPanel;
    private JPanel buttonPanel;
    private JPanel optionPanel;
    private JButton playButton;
    private JButton resetButton;
    private JSpinner stepSpinner;
    private JButton ResizeButton;
    private JSpinner gridResizer;
    private JComboBox gridEditorList;
    private JLabel stepSpinnerLabel;
    private JLabel gridResizerLabel;
    private JLabel gridEditorListLabel;

    /**
     * Setups the UI
     */
    public VisualizerFrame() {

        grid = new Grid(400, 400, 35, 35);

        container = new JPanel(new BorderLayout());
        controlPanel = new JPanel(new BorderLayout());

        playButton = new JButton("Start");
        playButton.setMnemonic(KeyEvent.VK_S);
        playButton.setActionCommand("start");
        playButton.addActionListener(this);

        resetButton = new JButton("Reset");
        resetButton.setMnemonic(KeyEvent.VK_R);
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(this);

        ResizeButton = new JButton("Resize");
        ResizeButton.setMnemonic(KeyEvent.VK_T);
        ResizeButton.setActionCommand("resize");
        ResizeButton.addActionListener(this);


        SpinnerNumberModel stepSizeModel = new SpinnerNumberModel(5, 5, 1000, 5);
        stepSpinner = new JSpinner(stepSizeModel);
        stepSpinnerLabel = new JLabel("Time per Step (ms): ");
        stepSpinnerLabel.setLabelFor(stepSpinner);
        stepSpinnerLabel.setHorizontalAlignment(JLabel.RIGHT);

        SpinnerNumberModel GridSizeModel = new SpinnerNumberModel(30, 20, 100, 1);
        gridResizer = new JSpinner(GridSizeModel);
        gridResizer.setUI(new MyUI());
        gridResizerLabel = new JLabel("Grid Size (nxn):");
        gridResizerLabel.setHorizontalAlignment(JLabel.RIGHT);


        String editList[] = {"Start", "Goal", "Walls", "Erase Walls", "LIVE TRACKING"};
        gridEditorList = new JComboBox(editList);
        gridEditorList.addActionListener(this);
        gridEditorListLabel = new JLabel("Place on Grid: ");
        gridEditorListLabel.setLabelFor(gridEditorList);
        gridEditorListLabel.setHorizontalAlignment(JLabel.RIGHT);

        buttonPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        buttonPanel.add(playButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(ResizeButton);
        controlPanel.add(buttonPanel, BorderLayout.WEST);

        optionPanel = new JPanel(new GridLayout(3, 2, 0, 5));
        optionPanel.add(stepSpinnerLabel);
        optionPanel.add(stepSpinner);
        optionPanel.add(gridResizerLabel);
        optionPanel.add(gridResizer);
        optionPanel.add(gridEditorListLabel);
        optionPanel.add(gridEditorList);
        controlPanel.add(optionPanel, BorderLayout.CENTER);

        controlPanel.setPreferredSize(new Dimension(400, 75));

        container.add(grid, BorderLayout.SOUTH);
        container.add(controlPanel, BorderLayout.CENTER);

        this.add(container);
        this.setResizable(false);
        this.pack();

    }

    /**
     * Performs start, reset, resize and changing state options when there buttons are clicked
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == gridEditorList){
            grid.setPositionable(gridEditorList.getSelectedIndex());
        }
        if("start".equals(e.getActionCommand())) {

            SwingWorker worker = new SwingWorker<Void,Void>(){
                protected Void doInBackground(){
                    grid.start((int)stepSpinner.getValue());
                    return null;
                }
            };

            worker.execute();
        }
        if("reset".equals(e.getActionCommand())){
            grid.reset();
        }

        if ("resize".equals(e.getActionCommand())) {
            grid.newGrid(400, 400, (int) gridResizer.getValue(), (int) gridResizer.getValue());
        }

    }

    /**
     * Used to resize the grid in real time
     */
    class MyUI extends javax.swing.plaf.basic.BasicSpinnerUI
    {
        public Component createNextButton()
        {
            JButton btnUp = (JButton)super.createNextButton();
            btnUp.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    System.out.println("Going up");
                    int nextVal = (int) gridResizer.getValue();
                    if ((int) gridResizer.getValue() <= 99) {
                        nextVal = (int) gridResizer.getValue() + 1;
                    }
                    grid.newGrid(400, 400, nextVal, nextVal);
                }
            });
            return btnUp;
        }
        public Component createPreviousButton()
        {
            JButton btnDown = (JButton)super.createPreviousButton();
            btnDown.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    System.out.println("Going down");
                    int nextVal = (int) gridResizer.getValue();
                    if ((int) gridResizer.getValue() >= 21) {
                        nextVal = (int) gridResizer.getValue() - 1;
                    }
                    grid.newGrid(400, 400, nextVal, nextVal);
                }
            });
            return btnDown;
        }
    }
}
