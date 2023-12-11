public class Result {
    private String simulationName;  // Identifier for the simulation
    private int maxFlow;
    private int paths;
    private double meanLength;
    private double meanProportionalLength;
    private int totalEdges;

    Result(){}

    // Constructor
    public Result(String simulationName, int maxFlow, int paths, double meanLength, double meanProportionalLength, int totalEdges) {
        this.simulationName = simulationName;
        this.maxFlow = maxFlow;
        this.paths = paths;
        this.meanLength = meanLength;
        this.meanProportionalLength = meanProportionalLength;
        this.totalEdges = totalEdges;
    }

    public String toFormattedString(int n, double r, int upperCap) {
        return String.format("%-10s\t%-5s\t%-5.2f\t%-10s\t%-7s\t%-5.2f\t%-5.2f\t%-5s\t%-5s",
                simulationName, n, r, upperCap, paths, meanLength, meanProportionalLength, totalEdges, maxFlow);
    }
}
