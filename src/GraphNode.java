import java.lang.reflect.Array;
import java.util.ArrayList;

class GraphNode {
    private int i;
    private int j;
    private double x;
    private double y;
    private int id = 0;
    private ArrayList<GraphNode> neighbours;
    private ArrayList<GraphPath> paths;
    private GraphNode torNeighbour1;
    private GraphNode torNeighbour2;
    private boolean isDisabled;

    enum State {
        Susceptible, Infected, Resistant
    }

    private State state;


    public GraphNode(int i, int j, double x, double y, int id) {
        this.i = i;
        this.j = j;
        this.x = x;
        this.y = y;
        this.neighbours = new ArrayList<>();
        this.paths = new ArrayList<>();
        this.state = State.Susceptible;
        this.id = id;
        this.isDisabled = false;
    }

    public GraphNode(int i, int j, double x, double y, int id, State state, boolean isDisabled) {
        this.i = i;
        this.j = j;
        this.x = x;
        this.y = y;
        this.id = id;
        this.state = state;
        this.neighbours = new ArrayList<>();
        this.isDisabled = isDisabled;
        this.paths = new ArrayList<>();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public void addNeighbour(GraphNode node) {
        neighbours.add(node);
    }

    public void addTorNeighbour(GraphNode torNeighbour){
        neighbours.add(torNeighbour);
        if (torNeighbour1 == null)
            this.torNeighbour1 = torNeighbour;
        else
            this.torNeighbour2 = torNeighbour;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void addPath(GraphPath graphPath){
        paths.add(graphPath);
    }


    public void disable(){
        isDisabled = true;
        for (GraphNode neighbour:neighbours) {
            neighbour.getNeighbours().remove(this);
        }
        for (GraphPath graphPath: paths){
            graphPath.disable();
        }
    }

    public void enable(){
        isDisabled = false;
        for (GraphNode neighbour:neighbours) {
            neighbour.getNeighbours().add(this);
        }
        for (GraphPath graphPath: paths){
            graphPath.enable();
        }
    }

//    public GraphNode getTorNeighbour() {
//        return neighbours.get(neighbours.indexOf(torNeighbour));
//    }
//
    public boolean isTorNeighbour(GraphNode neighbour){
        return neighbour.equals(torNeighbour1) || neighbour.equals(torNeighbour2);
    }

    public boolean isNeighbour(GraphNode node){
        return neighbours.contains(node);
    }

    public void removeTorNeighbours(){
        neighbours.remove(torNeighbour1);
        neighbours.remove(torNeighbour2);
        this.torNeighbour1 = null;
        this.torNeighbour2 = null;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public ArrayList<GraphNode> getNeighbours() {
        return neighbours;
    }

    public int getId() {
        return id;
    }

    public void printNeighbours() {
        System.out.println("Node's #" + this.id + " neighbours: ");
        for (GraphNode node : neighbours) {
            System.out.println("Node #" + node.getId());
        }
    }
}