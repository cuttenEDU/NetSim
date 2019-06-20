public class NeighboursList {
    private GraphNode leftNeighbour;
    private GraphNode rightNeighbour;
    private GraphNode upperNeighbour;
    private GraphNode lowerNeighbour;

    public GraphNode getLeftNeighbour() {
        return leftNeighbour;
    }

    public void setLeftNeighbour(GraphNode leftNeighbour) {
        this.leftNeighbour = leftNeighbour;
    }

    public GraphNode getRightNeighbour() {
        return rightNeighbour;
    }

    public void setRightNeighbour(GraphNode rightNeighbour) {
        this.rightNeighbour = rightNeighbour;
    }

    public GraphNode getUpperNeighbour() {
        return upperNeighbour;
    }

    public void setUpperNeighbour(GraphNode upperNeighbour) {
        this.upperNeighbour = upperNeighbour;
    }

    public GraphNode getLowerNeighbour() {
        return lowerNeighbour;
    }

    public void setLowerNeighbour(GraphNode lowerNeighbour) {
        this.lowerNeighbour = lowerNeighbour;
    }
}
