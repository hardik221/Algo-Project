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

    public String toFormattedString() {
        return String.format("%-10s\t%-5d\t%-5.2f\t%-5d\t%-5.2f\t%-5d",
                simulationName, maxFlow, meanLength, paths, meanProportionalLength, totalEdges);
    }



    // Getters (you can generate these using your IDE)
    public String getSimulationName() {
        return simulationName;
    }

    public int getMaxFlow() {
        return maxFlow;
    }

    public int getPaths() {
        return paths;
    }

    public double getMeanLength() {
        return meanLength;
    }

    public double getMeanProportionalLength() {
        return meanProportionalLength;
    }

    public int getTotalEdges() {
        return totalEdges;
    }
}
