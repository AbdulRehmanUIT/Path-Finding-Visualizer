import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is the Node/Vertex of the Graph
 */
public class Vertex {

    // For Displaying the vertex as a Cell on the Grid
    int width;                  // Width of the cell
    int height;                 // Height of the cell
    Color color;                // Color of the cell

    // Properties of the vertex
    Point position;             // x and y coordinates
    boolean isWall;
    boolean isGoal;
    boolean isPath;
    boolean traversed;
    ArrayList<Vertex> neighbouringCells = new ArrayList<>();

    // Constructor
    public Vertex(Point position, int width, int height) {
        this.position = position; // position on the grid
        this.width = width; //width depending or grid size
        this.height = height; // height depending upon grid size
        this.isWall = false; // All vertex initially are paths
        this.isGoal = false;
        this.isPath = false; // path between start and goal
        this.traversed = false; // visited or not
        this.color = Color.WHITE;
    }

    // Adds Neighbouring Cell
    public void addEdge(Vertex neighbour) {
        if (!neighbouringCells.contains(neighbour)) {
            neighbouringCells.add(neighbour);
            neighbour.neighbouringCells.add(this);
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setAsWall() {
        this.color = Color.BLACK;
        this.isWall = true;
    }

    public void setAsPath() {
        this.color = Color.WHITE;
        this.isWall = false;
    }

    public boolean isWall() {
        return isWall;
    }

    /**
     * Draws the cell on the grid
     * @param g
     */
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(position.x, position.y, width, height);

        // To make the border of the cell
        // all cells have black borders
        g.setColor(Color.BLACK);
        g.drawRect(position.x, position.y, width, height);
    }

    public String toString() {
        return "Vertex {" + position.getX() + "," + position.getY() +"}";
    }

}
