/**
 * The main class that executes the simulation and generates a graph.
 */
public class Main {
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        double r = Double.parseDouble(args[1]);
        int upperCap = Integer.parseInt(args[2]);

        // Create a unique result file name based on simulation parameters
        String resFile = "graph_adjacency_list_"+n+"_"+r+"_"+upperCap+".csv";
        ComposeGraph g1 = new ComposeGraph();
        // Generate a graph based on the simulation parameters and save it to a file
        g1.constructFlowGraph(n, r, upperCap, resFile);
    }
}

class SimulationOutcome {
    private String simulationName;  // Identifier for the simulation
    private int maxFlow;
    private int paths;
    private double meanLength;
    private double meanProportionalLength;
    private int totalEdges;
    public SimulationOutcome(String simulationName, int maxFlow, int paths, double meanLength, double meanProportionalLength, int totalEdges) {
        this.simulationName = simulationName; this.maxFlow = maxFlow; this.paths = paths;
        this.meanLength = meanLength; this.meanProportionalLength = meanProportionalLength; this.totalEdges = totalEdges;

    }
    public String formatOutput(int n, double r, int upperCap) {
        return String.format("%-10s\t%-5s\t%-5.2f\t%-10s\t%-7s\t%-5.2f\t%-5.2f\t%-10s\t%-5s",
                simulationName, n, r, upperCap, paths, meanLength, meanProportionalLength, totalEdges, maxFlow);
    }
}
