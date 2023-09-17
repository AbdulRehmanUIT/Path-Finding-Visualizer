import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;

/**
 * This is the n x n Grid where the path finding happens
 */
public class Grid extends JPanel implements MouseListener, MouseMotionListener {

     /*
        We Have 6 STATES currently (we can add more if we want):
            1 - "SEARCHING"
            2 - "PLACESTART"
            3 - "PLACEGOAL"
            4 - "PLACEWALLS"
            5 - "ERASEWALLS"
            6 - "LIVETRACKINGMOUSE"
        Our visualization will be in one of the 6 states at a given time
     */

    final String SEARCHING = "SEARCHING";
    final String PLACESTART = "PLACESTART";
    final String PLACEGOAL = "PLACEGOAL";
    final String PLACEWALLS = "PLACEWALLS";
    final String ERASEWALLS = "ERASEWALLS";
    final String LIVETRACKING = "LIVETRACKING";

    String STATE;
    String PSTATE;

    int width;
    int height;

    int rows;
    int columns;

    int vertexWidth;        // width of the cell
    int vertexHeight;       // height of the cell

    Vertex[][] vertexGrid;  // 2D Adjacent List
    Vertex startVertex;
    Vertex goalVertex;
    Vertex previousLiveVertex;

    /**
     * This is the contructor of the Grid
     * @param width
     * @param height
     * @param rows
     * @param columns
     */
    public Grid(int width, int height, int rows, int columns) {
        this.width = width;
        this.height = height;

        this.rows = rows;
        this.columns = columns;

        this.vertexWidth = width / columns;
        this.vertexHeight = height / rows;

        this.STATE = PLACESTART;
        this.PSTATE = STATE;

        generateGraph();
        addMouseListener(this);
        addMouseMotionListener(this);
        this.setPreferredSize(new Dimension(width,height));

    }

    public void newGrid(int width, int height, int rows, int columns) {
        this.width = width;
        this.height = height;

        this.rows = rows;
        this.columns = columns;

        this.vertexWidth = width / columns;
        this.vertexHeight = height / rows;

        this.STATE = PLACESTART;
        this.PSTATE = STATE;

        generateGraph();
        addMouseListener(this);
        addMouseMotionListener(this);
        this.setPreferredSize(new Dimension(width, height));
    }

    /**
     * This generates the graph using the 2D Adjaceny List
     */
    private void generateGraph() {
        vertexGrid = new Vertex[rows][columns];

        // Creating the vertices
        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < columns; column++) {
                // creates an object of vertex with position and the size of vertex
                vertexGrid[row][column] = new Vertex(new Point(row * vertexWidth, column * vertexHeight), vertexWidth, vertexHeight);
            }
        }

        // Creating a relation between vertices and their neighbours by creating an edge between them
        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < columns; column++) {
                if(row + 1 < rows)
                    vertexGrid[row][column].addEdge(vertexGrid[row + 1][column]);
                if(column + 1 < columns)
                    vertexGrid[row][column].addEdge(vertexGrid[row][column + 1]);
                if(row - 1 >= 0)
                    vertexGrid[row][column].addEdge(vertexGrid[row - 1][column]);
                if(column - 1 >= 0)
                    vertexGrid[row][column].addEdge(vertexGrid[row][column - 1]);
                if(row + 1 < rows && column + 1 < columns)
                    vertexGrid[row][column].addEdge(vertexGrid[row + 1][column + 1]);
                if(row - 1 >= 0 && column - 1 >= 0)
                    vertexGrid[row][column].addEdge(vertexGrid[row - 1][column - 1]);
                if(row + 1 < rows && column - 1 >= 0)
                    vertexGrid[row][column].addEdge(vertexGrid[row + 1][column - 1]);
                if(row - 1 >= 0 && column + 1 < columns)
                    vertexGrid[row][column].addEdge(vertexGrid[row - 1][column + 1]);
            }
        }
        // starting position of the program
        startVertex = vertexGrid[0][0];
        startVertex.setColor(Color.MAGENTA);
        goalVertex = vertexGrid[rows - 1][columns - 1];
        goalVertex.setColor(Color.RED);
        goalVertex.isGoal = true;
        update();
    }

    /**
     * Clears the grid
     */
    public void clearGrid() {
        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < columns; column++) {
                if ((vertexGrid[row][column].traversed || vertexGrid[row][column].isPath) && !vertexGrid[row][column].isWall) {
                    vertexGrid[row][column].setColor(Color.white);
                    vertexGrid[row][column].traversed = false;
                    vertexGrid[row][column].isPath = false;
                }


                if (Objects.equals(STATE, LIVETRACKING)) {
                    // sets the color of all other vertices that are not goal,start,wall
                    if (!vertexGrid[row][column].isWall && !vertexGrid[row][column].equals(startVertex) && !vertexGrid[row][column].equals(goalVertex)) {
                        vertexGrid[row][column].setColor(Color.white);
                    }
                }

            }
        }
    }

    /**
     * Updates the grid when there is a chagne in the UI
     * @param g the <code>Graphics</code> object to protect
     */
    public void paintComponent(Graphics g){
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                vertexGrid[i][j].draw(g);
            }
        }

    }


    /**
     * It repaints the whole screen by call paint component
     */
    public void update(){
        this.repaint();
    }

    /**
     * Starts the BFS algorithm (generatePath())
     * @param delay
     */
    public void start(int delay) {
        clearGrid();
        STATE = SEARCHING;
        generatePath(delay);
        STATE = PSTATE;

    }

    /**
     * Shows the shortest path between the mouse and the start vertex. Live Tracking
     * @param mouseGoal
     */
    public void livetrackingpath(Vertex mouseGoal) {
        clearGrid();
        update();

        Queue<Vertex> queue = new ArrayDeque<>();
        LinkedList<Vertex> visited = new LinkedList<>();
        Vertex[][] prevVertex = new Vertex[rows][columns];
        boolean found = false;
        queue.add(startVertex);
        visited.add(startVertex);
        Vertex connection = null;
        while (queue.size() != 0 && !found) {
            Vertex cell = queue.remove();
            for (int i = 0; i < cell.neighbouringCells.size(); i++) {
                connection = cell.neighbouringCells.get(i);
                Point vertexIndex = connection.position;
                if (!visited.contains(connection) && !connection.isWall & !connection.isGoal) {
                    queue.add(connection);
                    visited.add(connection);
                    connection.traversed = true;
                    prevVertex[vertexIndex.x/vertexWidth][vertexIndex.y/vertexHeight] = cell;
                }
                if (connection.equals(mouseGoal)) {
                    found = true;
                    goalVertex.traversed = false;
                    break;
                }
            }
        }

        Vertex current = connection;
        Point indexCurrent;
        if(connection.equals(mouseGoal)) {
            current.setColor(Color.GREEN);
            current.traversed = true;
            while (current != null && current != startVertex) {
                indexCurrent = current.position;
                current = prevVertex[indexCurrent.x / vertexWidth][indexCurrent.y / vertexHeight];
                if (!current.equals(startVertex) && !current.isWall && !current.isGoal) {
                    current.traversed = true;
                    current.setColor(Color.GREEN);
                }

                update();
            }
        }
//        update();
    }

    /**
     * Generates the shortest path between start and goal. This uses BFS
     * @param delay
     */
    public void generatePath(int delay) {
        Queue<Vertex> queue = new ArrayDeque<>();
        LinkedList<Vertex> visited = new LinkedList<>();
        Vertex[][] prevVertex = new Vertex[rows][columns];
        boolean found = false;

        queue.add(startVertex);
        visited.add(startVertex);
        Vertex connection = null;

        // For delay
        long currentTime = System.currentTimeMillis();

        while (queue.size() != 0 && !found) {

            Vertex cell = queue.remove();
            // Changing color of whose neighbours we are now going to visit
            if (!cell.equals(startVertex))
                cell.setColor(Color.yellow);

            // i++ is not here for a reason, don't add
            for (int i = 0; i < cell.neighbouringCells.size(); ) {

                // Moves only after the delay
                if (System.currentTimeMillis() - currentTime > delay){

                    connection = cell.neighbouringCells.get(i);
                    Point vertexIndex = connection.position;
                    if (!visited.contains(connection) && !connection.isWall) {

                        //for delay
                        currentTime = System.currentTimeMillis();

                        queue.add(connection);
                        visited.add(connection);

                        connection.traversed = true;
                        prevVertex[vertexIndex.x/vertexWidth][vertexIndex.y/vertexHeight] = cell;
                        if (!connection.equals(goalVertex)) {
                            connection.setColor(Color.BLUE);
                        }
                    }

                    if (connection.equals(goalVertex)) {
                        found = true;
                        goalVertex.traversed = false;
                        clearGrid();
                        break;
                    }

                    update();
                    i++;

                }


            }

        }

        Vertex current = connection;
        Point indexCurrent;

        currentTime = System.currentTimeMillis();

        while (current != null && current != startVertex && connection.isGoal){

            if (System.currentTimeMillis() - currentTime > delay / 2)  {

                //for delay
                currentTime = System.currentTimeMillis();

                indexCurrent = current.position;
                current = prevVertex[indexCurrent.x/vertexWidth][indexCurrent.y/vertexHeight];

                if (!current.equals(startVertex)) {
                    current.traversed = true;
                    current.setColor(Color.GREEN);
                }


                update();
            }

        }
        update();
    }

    /**
     * This resets the whole grid
     */
    public void reset(){
        STATE = PSTATE;
        clearGrid();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                vertexGrid[i][j].isWall = false;
                vertexGrid[i][j].traversed = false;
                vertexGrid[i][j].isGoal = false;
                vertexGrid[i][j].isPath = false;
                vertexGrid[i][j].setColor(Color.WHITE);
            }
        }

        startVertex = vertexGrid[0][0];
        startVertex.setColor(Color.MAGENTA);
        goalVertex = vertexGrid[rows - 1][columns - 1];
        goalVertex.setColor(Color.RED);
        goalVertex.isGoal = true;
        update();

    }

    /**
     * This allows the program to switch between the states depending on the input provided by the user from the GUI
     * @param cellType
     */
    public void setPositionable(int cellType){

        clearGrid();

        switch(cellType){
            case 0:
                STATE = PLACESTART;
                PSTATE = STATE;
                break;
            case 1:
                STATE = PLACEGOAL;
                PSTATE = STATE;
                break;
            case 2:
                STATE = PLACEWALLS;
                PSTATE = STATE;
                break;
            case 3:
                STATE = ERASEWALLS;
                PSTATE = STATE;
                break;
            case 4:
                STATE = LIVETRACKING;
                PSTATE = STATE;

        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {


    }

    /**
     * Detects when the mouse is pressed. Depending on the STATE when can place walls, start vertex, goal vertex and erase walls
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Set Start or Goal while the algorithm is not running
        Point mousePos = new Point(e.getX(),e.getY());
        if (STATE != SEARCHING) {

            if(STATE == PLACESTART){
                if(startVertex != null)
                    startVertex.setColor(Color.WHITE);
                startVertex = vertexGrid[(int)(mousePos.x/vertexWidth)][(int)(mousePos.y/vertexHeight)];
                startVertex.setColor(Color.MAGENTA);

                clearGrid();
            }

            if(STATE == PLACEGOAL){
                if(goalVertex != null)
                    goalVertex.setColor(Color.WHITE);
                goalVertex.isGoal = false;
                goalVertex = vertexGrid[(int)(mousePos.x/vertexWidth)][(int)(mousePos.y/vertexHeight)];
                goalVertex.setColor(Color.RED);
                goalVertex.isGoal = true;
                clearGrid();
            }
            if(STATE == PLACEWALLS){
                Vertex currentVertex = vertexGrid[(int)(mousePos.x/vertexWidth)][(int)(mousePos.y/vertexHeight)];
                if (!currentVertex.isWall() && !currentVertex.isGoal && !currentVertex.equals(startVertex)) {
                    currentVertex.setAsWall();
                }
            }
            if(STATE == ERASEWALLS){
                Vertex currentVertex = vertexGrid[(int)(mousePos.x/vertexWidth)][(int)(mousePos.y/vertexHeight)];
                if(currentVertex.isWall()) {
                    currentVertex.setAsPath();
                }
            }

            update();

        }

        System.out.println("PRESSED X" + mousePos.x + " Y:" + mousePos.y);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Detects when the mouse is dragged and allows user to place wall by dragging instead of one by one
     * @param e the event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        Point mousePos = new Point(e.getX(),e.getY());
        if (STATE != SEARCHING) {

            if(STATE == PLACEWALLS){
                Vertex currentVertex = vertexGrid[(int)(mousePos.x/vertexWidth)][(int)(mousePos.y/vertexHeight)];
                if (!currentVertex.isWall() && !currentVertex.isGoal && !currentVertex.equals(startVertex)) {
                    currentVertex.setAsWall();
                }
            }
            if(STATE == ERASEWALLS){
                Vertex currentVertex = vertexGrid[(int)(mousePos.x/vertexWidth)][(int)(mousePos.y/vertexHeight)];
                if(currentVertex.isWall()) {
                    currentVertex.setAsPath();
                }
            }
            update();
        }


    }

    /**
     * Detects when the mouse moves so that shortest path between mouse and start can be generates
     * @param e the event to be processed
     */
    @Override
    public void mouseMoved(MouseEvent e) {

        if (STATE == LIVETRACKING) {
            Point mousePos = new Point(e.getX(),e.getY());
            Vertex currentVertex = vertexGrid[(int)(mousePos.x/vertexWidth)][(int)(mousePos.y/vertexHeight)];
            if (previousLiveVertex != currentVertex){
                if (!currentVertex.equals(startVertex) && !currentVertex.isWall && !currentVertex.isGoal) {
                    livetrackingpath(currentVertex);
                    previousLiveVertex = currentVertex;
                }

            }

        }
    }
}
