import java.util.List;

public class RunRandomSimulation {

    RunRandomSimulation(){}

    public Result runRandomSimulation(Graph graph, Vertex source, Vertex sink, String type) {
        List<Vertex> augmentingPath = RandomSimulation.randomDijkstra(graph, source, sink);
//        graph.getCapacities();
        System.out.println("Shortest Augmenting Path from " + source.id + " to " + sink.id + ":");
        for (Vertex vertex : augmentingPath) {
            System.out.print(vertex.id + " -> ");
        }
        System.out.println("END");

        // Statistics
        int paths = 0;
        int totalLength = 0;
        int maxLength = 0;

        // Run Ford-Fulkerson
        int maxFlow = 0;
        FordFulkerson f = new FordFulkerson();
        while (augmentingPath.size() > 1) {
            paths++;
            totalLength += augmentingPath.size();
            maxLength = Math.max(maxLength, augmentingPath.size());

            int minCapacity = f.findMinCapacity(augmentingPath);
            f.updateResidualGraph(augmentingPath, minCapacity);
            //graph.getCapacities();
            maxFlow += minCapacity;

            augmentingPath = RandomSimulation.randomDijkstra(graph, source, sink);

            System.out.println("Shortest Augmenting Path from " + source.id + " to " + sink.id + ":");
            for (Vertex vertex : augmentingPath) {
                System.out.print(vertex.id + " -> ");
            }
            System.out.println("END");
        }

        // Statistics
        double meanLength = (double) totalLength / paths;
        double meanProportionalLength = (double) totalLength / maxLength;
        int totalEdges = graph.vertices.values().stream().mapToInt(v -> v.neighbors.size()).sum();

        System.out.println("Maximum Flow: " + maxFlow);
        System.out.println("Paths: " + paths);
        System.out.println("Mean Length: " + meanLength);
        System.out.println("Mean Proportional Length: " + meanProportionalLength);
        System.out.println("Total Edges: " + totalEdges);

        return new Result(type, maxFlow, paths, meanLength, meanProportionalLength, totalEdges);

    }
}

