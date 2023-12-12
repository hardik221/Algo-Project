import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;

/**
 * Represents a vertex in a graph with an associated identifier and its adjacent vertices.
 */
class VertexNode {
    int referenceId;
    Map<VertexNode, Integer> adjacentVertices = new HashMap<>();
    VertexNode(int referenceId) {
        this.referenceId = referenceId;
    }
}


/**
 * Represents a graph structure composed of vertices and edges.
 */
class NodeGraph {
    Map<Integer, VertexNode> vertexList = new HashMap<>();
    NodeGraph(){};
    // Ensure the vertices exist in the graph and add them if not present
    void createEdge(int ureferenceId, int vreferenceId, int limit) {
        vertexList
                .computeIfAbsent(ureferenceId, VertexNode::new)
                .adjacentVertices
                .put(vertexList.computeIfAbsent(vreferenceId, VertexNode::new), limit);
    }

    /**
     * Chooses a random vertex from the graph that has adjacent vertices.
     *
     * @return A randomly selected vertex with adjacent vertices, or null if no such vertex exists.
     */
    VertexNode chooseRandomVertex() {
        // Filter vertices that have at least one adjacent vertex
        List<VertexNode> validVertexList = vertexList.values().stream()
                .filter(vertex -> !vertex.adjacentVertices.isEmpty())
                .collect(Collectors.toList());
        if (validVertexList.isEmpty()) {
            return null;
        }
        return validVertexList.get(new Random().nextInt(validVertexList.size()));
    }

}



public class Adapter {

    /**
     * Reads graph data from a file and creates a NodeGraph based on the information.
     */

    public static NodeGraph fileData(String fileName) throws IOException {
        NodeGraph NG = new NodeGraph();
        Files.lines(Paths.get(fileName))
                .forEach(line -> {
                    String[] parts = line.split(",");
                    int ureferenceId = Integer.parseInt(parts[0]);

                    Arrays.stream(parts, 1, parts.length)
                            .map(edge -> edge.split(":"))
                            .forEach(edgeParts -> NG.createEdge(ureferenceId, Integer.parseInt(edgeParts[0]), Integer.parseInt(edgeParts[1])));
                });
        return NG;
    }

    /**
     * Finds the farthest vertex from the given source vertex using BFS.
     *
     * @param src The source vertex from which to find the longest path.
     * @return The vertex that is farthest from the source vertex.
     */
    public static VertexNode getLongestPath(VertexNode src) {
        // Map to store the distance from the source vertex to each vertex
        Map<VertexNode, Integer> distance = new HashMap<>();
        Queue<VertexNode> queue = new LinkedList<>();
        queue.offer(src);
        distance.put(src, 0);
        while (!queue.isEmpty()) {
            VertexNode current = queue.poll();
            current.adjacentVertices.keySet().stream()
                    .filter(adjacentNode -> !distance.containsKey(adjacentNode))
                    .forEach(adjacentNode -> {
                        distance.put(adjacentNode, distance.get(current) + 1);
                        queue.offer(adjacentNode);
                    });
        }
        // Find the farthest node by finding the maximum distance in the distance map
        VertexNode farthestNode = Collections.max(distance.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println("Longest path length: " + distance.get(farthestNode));
        return farthestNode;
    }

    public static void main(String[] args) throws IOException {

        List<SimulationOutcome> simulationOutcomes = new ArrayList<>();
        String fileName = "graph_adjacency_list_200_0.3_50.csv";
        NodeGraph mainNode = fileData(fileName);
        VertexNode source = mainNode.chooseRandomVertex();
        VertexNode sink = getLongestPath(source);
        // Perform simulations with different algorithms and store outcomes
        // Simulate SAP algorithm
        NodeGraph object1 = fileData(fileName);
        VertexNode sourceCopy1 = object1.vertexList.get(source.referenceId), sinkCopy1 = object1.vertexList.get(sink.referenceId);
        SimulationOutcome simulationOutcomeSAP = new SAP().simulationSAP(object1, sourceCopy1, sinkCopy1, "SAP");
        simulationOutcomes.add(simulationOutcomeSAP);
        System.out.println();

        // Simulate DFSLike algorithm
        NodeGraph object2 =  fileData(fileName);
        VertexNode sourceCopy2 = object2.vertexList.get(source.referenceId),sinkCopy2 = object2.vertexList.get(sink.referenceId);
        SimulationOutcome simulationOutcomeDFSLike = new DFSLike().DFSLike(object2, sourceCopy2, sinkCopy2, "DFS");
        simulationOutcomes.add(simulationOutcomeDFSLike);
        System.out.println();
    // Simulate MaxCapacity algorithm
        NodeGraph object3 =  fileData(fileName);
        VertexNode sourceCopy3 = object3.vertexList.get(source.referenceId),sinkCopy3 = object3.vertexList.get(sink.referenceId);
        SimulationOutcome simulationOutcomeMaxCap = new MaxCapacity().max_Capacity(object3, sourceCopy3, sinkCopy3, "Max-Cap");
        simulationOutcomes.add(simulationOutcomeMaxCap);
        System.out.println();
        // Simulate RandomKey algorithm
        NodeGraph object4 =  fileData(fileName);
        VertexNode sourceCopy4 = object4.vertexList.get(source.referenceId),sinkCopy4 = object4.vertexList.get(sink.referenceId);
        SimulationOutcome simulationOutcomeRandom = new RandomKey().randomKey(object4, sourceCopy4, sinkCopy4, "Random");
        simulationOutcomes.add(simulationOutcomeRandom);
        // Display simulation results in a tabular format
        System.out.println();
        System.out.println(String.format("%-11s\t%-5s\t%-5s\t%-5s\t%-5s\t%-5s\t%-5s\t%-5s\t%-5s",
                "Algorithm", "n", "r", "upperCap", "paths", "ML", "MPL", "totalEdges", "maxFlow"));
        for (SimulationOutcome simulationOutcome : simulationOutcomes) {
            String s = simulationOutcome.formatOutput(200, 0.3, 50);
            System.out.println(s);
        }
    }
}


class DFSLike {
    public static class ExploreDFSLike {

        // Dijkstra's algorithm treating edges with non-zero capacity
        static List<VertexNode> dfsLikeDijkstra(NodeGraph NG, VertexNode source, VertexNode sink) {
            System.out.println("Inside dijkstra dfs like");

            if (NG == null || source == null || sink == null) {
                throw new IllegalArgumentException("Input arguments cannot be null.");
            }


            Map<VertexNode, VertexNode> predecessors = new HashMap<>();
            Map<VertexNode, Integer> distances = new HashMap<>();
            PriorityQueue<VertexNode> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

            NG.vertexList.values().forEach(vertex -> {
                distances.put(vertex, Integer.MAX_VALUE / 2);
                predecessors.put(vertex, null);
            });

            int decreasingCounter = 0;
            distances.put(source, 0);
            queue.add(source);

            while (!queue.isEmpty()) {
                VertexNode currentVertex = queue.poll();
                if(currentVertex.referenceId == sink.referenceId) {
                    sink = currentVertex;
                }
                for (Map.Entry<VertexNode, Integer> entry : currentVertex.adjacentVertices.entrySet()) {
                    VertexNode neighbor = entry.getKey();
                    int capacity = entry.getValue();
                    if (capacity > 0) {
                        Integer currentDistance = distances.get(neighbor);
                        if (currentDistance == null || currentDistance == Integer.MAX_VALUE / 2) {
                            distances.put(neighbor, decreasingCounter--);
                            predecessors.put(neighbor, currentVertex);
                            queue.add(neighbor);

                            // Update sink only if it's not initialized or if its id matches the current neighbor's id
                            sink = (sink == null || neighbor.referenceId == sink.referenceId) ? neighbor : sink;
                        }
                    }
                }
            }

            if (distances.getOrDefault(sink, Integer.MAX_VALUE / 2) == Integer.MAX_VALUE / 2) {
                return Collections.emptyList();
            }
            List<VertexNode> path = new ArrayList<>();
            VertexNode currentVertex = sink;
            while (currentVertex != null) {
                path.add(currentVertex);
                currentVertex = predecessors.get(currentVertex);
            }
            Collections.reverse(path);
            return path;
        }
    }

    public SimulationOutcome DFSLike(NodeGraph NG, VertexNode source, VertexNode sink, String type){
        int totalEdges = 0;
        for (VertexNode v : NG.vertexList.values()) {
            totalEdges += v.adjacentVertices.size();
        }

        List<VertexNode> augmentingPath = ExploreDFSLike.dfsLikeDijkstra(NG, source, sink);
        System.out.println("Shortest Augmenting Path from " + source.referenceId + " to " + sink.referenceId + ":");
        augmentingPath.forEach(vertex -> System.out.print(vertex.referenceId + " -> "));

        System.out.println("END");

        // Statistics
        int paths = 0;
        int totalLength = 0;
        int maxLength = 0;

        // Run Ford-Fulkerson
        int maxFlow = 0;
        FordFulkerson f = new FordFulkerson();
//        while (augmentingPath.size() > 1) {
//            paths++;
//            totalLength += augmentingPath.size();
//            maxLength = Math.max(maxLength, augmentingPath.size());
//
//            int minCapacity = f.findMinCapacity(augmentingPath);
//            f.updateResidualGraph(augmentingPath, minCapacity);
//
//            maxFlow += minCapacity;
//
//            augmentingPath = ExploreDFSLike.dfsLikeDijkstra(graph, source, sink);
//
//            System.out.println("Shortest Augmenting Path from " + source.id + " to " + sink.id + ":");
////            for (Vertex vertex : augmentingPath) {
////                System.out.print(vertex.id + " -> ");
////            }
//            String pathString = augmentingPath.stream()
//                    .map(vertex -> Integer.toString(vertex.id))
//                    .collect(Collectors.joining(" -> "));
//            System.out.print(pathString);
//
//            System.out.println("END");
//        }
        do {
            paths++;
            totalLength += augmentingPath.size();
            maxLength = Math.max(maxLength, augmentingPath.size());

            int minCapacity = f.determineMinimumCapacity(augmentingPath);
            f.updateResidualGraph(augmentingPath, minCapacity);

            maxFlow += minCapacity;

            System.out.println("Shortest Augmenting Path from " + source.referenceId + " to " + sink.referenceId + ": " +
                    augmentingPath.stream().map(vertex -> Integer.toString(vertex.referenceId))
                            .collect(Collectors.joining(" -> ")));

            augmentingPath = ExploreDFSLike.dfsLikeDijkstra(NG, source, sink);

            System.out.println("END");
        } while (augmentingPath.size() > 1);

        // Statistics
        double meanLength = (double) totalLength / paths;
        double meanProportionalLength =  meanLength / maxLength;

        System.out.println("DFS Like Results");
        System.out.println("Maximum Flow: " + maxFlow);
        System.out.println("Paths: " + paths);
        System.out.println("Mean Length: " + meanLength);
        System.out.println("Mean Proportional Length: " + meanProportionalLength);
        System.out.println("Total Edges: " + totalEdges);

        return new SimulationOutcome(type, maxFlow, paths, meanLength, meanProportionalLength, totalEdges);

    }
}






class MaxCapacity {
    MaxCapacity(){}

    public static class MaxCapSimulation {

        static List<VertexNode> maxCapDijkstra(NodeGraph NG, VertexNode source, VertexNode sink) {
            System.out.println("Inside dijkstra max cap");
            if (NG == null || source == null || sink == null) {
                throw new IllegalArgumentException("Input arguments cannot be null.");
            }

            Map<VertexNode, VertexNode> predecessors = new HashMap<>();
            Map<VertexNode, Integer> maxCapacities = new HashMap<>();
            PriorityQueue<VertexNode> queue = new PriorityQueue<>(Comparator.comparingInt(maxCapacities::get).reversed());

//            for (Vertex vertex : graph.vertices.values()) {
//                maxCapacities.put(vertex, Integer.MIN_VALUE);
//                predecessors.put(vertex, null);
//            }

            NG.vertexList.values().forEach(vertex -> {
                maxCapacities.compute(vertex, (v, currentMax) -> Integer.MIN_VALUE);
                predecessors.put(vertex, null);
            });


            int maxCapacity = Integer.MIN_VALUE;
            maxCapacities.put(source, Integer.MAX_VALUE);
            queue.add(source);

//            while (!queue.isEmpty()) {
//                Vertex currentVertex = queue.poll();
//                if (currentVertex.id == sink.id) {
//                    sink = currentVertex;
//                    break;
//                }
////                for (Map.Entry<Vertex, Integer> entry : currentVertex.neighbors.entrySet()) {
////                    Vertex neighbor = entry.getKey();
////                    int capacity = entry.getValue();
////
////                    // Consider only edges with non-zero capacity
////                    if (capacity > 0) {
////                        int maxCap = Math.min(maxCapacities.get(currentVertex), capacity);
////
////                        Integer cap = maxCapacities.get(neighbor);
////                        if (cap == null || maxCap > cap) {
////                            maxCapacities.put(neighbor, maxCap);
////                            predecessors.put(neighbor, currentVertex);
////                            queue.add(neighbor);
////                        }
////                    }
////                }
//                currentVertex.neighbors.entrySet().stream()
//                        .filter(entry -> entry.getValue() > 0) // Consider only edges with non-zero capacity
//                        .forEach(entry -> {
//                            Vertex neighbor = entry.getKey();
//                            int capacity = entry.getValue();
//
//                            int maxCap = Math.min(maxCapacities.get(currentVertex), capacity);
//
//                            Integer cap = maxCapacities.get(neighbor);
//                            if (cap == null || maxCap > cap) {
//                                maxCapacities.put(neighbor, maxCap);
//                                predecessors.put(neighbor, currentVertex);
//                                queue.add(neighbor);
//                            }
//                        });
//            }
            do {
                VertexNode currentVertex = queue.poll();

                currentVertex.adjacentVertices.entrySet().stream()
                        .filter(entry -> entry.getValue() > 0) // Consider only edges with non-zero capacity
                        .forEach(entry -> {
                            VertexNode neighbor = entry.getKey();
                            int capacity = entry.getValue();

                            int maxCap = Math.min(maxCapacities.get(currentVertex), capacity);

                            Integer cap = maxCapacities.get(neighbor);
                            if (cap == null || maxCap > cap) {
                                maxCapacities.put(neighbor, maxCap);
                                predecessors.put(neighbor, currentVertex);
                                queue.add(neighbor);
                            }
                        });
            } while (!queue.isEmpty() && sink.referenceId != queue.peek().referenceId);


//            Integer sinkCap = maxCapacities.get(sink);
//            if (sinkCap == null || sinkCap == Integer.MIN_VALUE) {
//                // The sink is not reachable from the source
//                return Collections.emptyList();
//            }
            if (maxCapacities.get(sink) == null || maxCapacities.get(sink) == Integer.MIN_VALUE) {
                // The sink is not reachable from the source
                return Collections.emptyList();
            }


            // Store the maximum capacity of the critical edge
            maxCapacity = maxCapacities.get(sink);

//            List<Vertex> path = new ArrayList<>();
//            for (Vertex currentVertex = sink; currentVertex != null; currentVertex = predecessors.get(currentVertex)) {
//                path.add(currentVertex);
//            }
            List<VertexNode> path = new ArrayList<>();
            VertexNode currentVertex = sink;
            while (currentVertex != null) {
                path.add(currentVertex);
                currentVertex = predecessors.get(currentVertex);
            }

            Collections.reverse(path);

            // Print or use maxCapacity as needed
            System.out.println("Maximum Capacity: " + maxCapacity);

            return path;
        }
    }

    public SimulationOutcome max_Capacity(NodeGraph NG, VertexNode source, VertexNode sink, String type) {
        int totalEdges = NG.vertexList.values().stream().mapToInt(v -> v.adjacentVertices.size()).sum();

        List<VertexNode> augmentingPath = MaxCapSimulation.maxCapDijkstra(NG, source, sink);
//        graph.getCapacities();
        System.out.println("Shortest Augmenting Path from " + source.referenceId + " to " + sink.referenceId + ":");
//        for (Vertex vertex : augmentingPath) {
//            System.out.print(vertex.id + " -> ");
//        }
        augmentingPath.forEach(vertex -> System.out.print(vertex.referenceId + " -> "));

        System.out.println("END");

        // Statistics
        int paths = 0;
        int totalLength = 0;
        int maxLength = 0;

        // Run Ford-Fulkerson
        int maxFlow = 0;
        FordFulkerson f = new FordFulkerson();
        do{
            paths++;
            totalLength += augmentingPath.size();
            maxLength = Math.max(maxLength, augmentingPath.size());

            int minCapacity = f.determineMinimumCapacity(augmentingPath);
            f.updateResidualGraph(augmentingPath, minCapacity);
            //NG.getCapacities();
            maxFlow += minCapacity;

            augmentingPath = MaxCapSimulation.maxCapDijkstra(NG, source, sink);

            System.out.println("Shortest Augmenting Path from " + source.referenceId + " to " + sink.referenceId + ":");
//            for (Vertex vertex : augmentingPath) {
//                System.out.print(vertex.id + " -> ");
//            }
            augmentingPath.forEach(vertex -> System.out.print(vertex.referenceId + " -> "));

            System.out.println("END");
        }while (augmentingPath.size() > 1);

        // Statistics
        double meanLength = (double) totalLength / paths;
        double meanProportionalLength = meanLength / maxLength;

        System.out.println("Maximum Flow: " + maxFlow);
        System.out.println("Paths: " + paths);
        System.out.println("Mean Length: " + meanLength);
        System.out.println("Mean Proportional Length: " + meanProportionalLength);
        System.out.println("Total Edges: " + totalEdges);

        return new SimulationOutcome(type, maxFlow, paths, meanLength, meanProportionalLength, totalEdges);

    }
}





class RandomKey {

    RandomKey(){}

    public static class RandomSimulation {

        // Dijkstra's algorithm treating edges with non-zero capacity
        static List<VertexNode> randomDijkstra(NodeGraph NG, VertexNode source, VertexNode sink) {
            System.out.println("Inside random dijkstra dfs like");

            if (NG == null || source == null || sink == null) {
                throw new IllegalArgumentException("Input arguments cannot be null.");
            }

            Map<VertexNode, VertexNode> predecessors = new HashMap<>();
            Map<VertexNode, Integer> distances = new HashMap<>();
            PriorityQueue<VertexNode> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

//            for (Vertex vertex : graph.vertices.values()) {
//                distances.put(vertex, Integer.MAX_VALUE / 2); // Avoid integer overflow
//                predecessors.put(vertex, null);
//            }
            NG.vertexList.values().forEach(vertex -> {
                distances.put(vertex, Integer.MAX_VALUE / 2);
                predecessors.put(vertex, null);
            });


            distances.put(source, 0);
            queue.add(source);

//            while (!queue.isEmpty()) {
//                Vertex currentVertex = queue.poll();
//                if(currentVertex.id == sink.id) {
//                    sink = currentVertex;
//                }
//                // Continue exploration even if we reach the sink
//                for (Map.Entry<Vertex, Integer> entry : currentVertex.neighbors.entrySet()) {
//                    Vertex neighbor = entry.getKey();
//                    int capacity = entry.getValue();
//
//                    if (capacity > 0) {
//                        Integer currentDistance = distances.get(neighbor);
//
////                        if (currentDistance == null || currentDistance == Integer.MAX_VALUE / 2) {
////                            distances.put(neighbor, new Random().nextInt(Integer.MAX_VALUE / 2));
////                            predecessors.put(neighbor, currentVertex);
////                            queue.add(neighbor);
////
////                            if(neighbor.id == sink.id) {
////                                sink = neighbor;
////                            }
////                        }
//                        if (currentDistance == null || currentDistance == Integer.MAX_VALUE / 2) {
//                            distances.put(neighbor, new Random().nextInt(Integer.MAX_VALUE / 2));
//                            predecessors.put(neighbor, currentVertex);
//                            queue.add(neighbor);
//                            sink = (neighbor.id == sink.id) ? neighbor : sink;
//                        }
//
//                    }
//                }
//            }
            while (!queue.isEmpty()) {
                VertexNode currentVertex = queue.poll();

                if (currentVertex.referenceId == sink.referenceId) {
                    sink = currentVertex;
                }

                // Continue exploration even if we reach the sink
                for (Map.Entry<VertexNode, Integer> entry : currentVertex.adjacentVertices.entrySet()) {
                    VertexNode neighbor = entry.getKey();
                    int capacity = entry.getValue();

                    if (capacity > 0 && (distances.get(neighbor) == null || distances.get(neighbor) == Integer.MAX_VALUE / 2)) {
                        int newDistance = new Random().nextInt(Integer.MAX_VALUE / 2);
                        distances.put(neighbor, newDistance);
                        predecessors.put(neighbor, currentVertex);
                        queue.add(neighbor);
                        sink = (neighbor.referenceId == sink.referenceId) ? neighbor : sink;
                    }
                }
            }
            if (distances.get(sink) == null || distances.get(sink) == Integer.MAX_VALUE / 2) {
                // The sink is not reachable from the source
                return Collections.emptyList();
            }
            List<VertexNode> path = new ArrayList<>();
            VertexNode currentVertex = sink;
            while (currentVertex != null) {
                path.add(currentVertex);
                currentVertex = predecessors.get(currentVertex);
            }

            Collections.reverse(path);
            return path;
        }
    }


    public SimulationOutcome randomKey(NodeGraph NG, VertexNode source, VertexNode sink, String type) {
        int totalEdges = NG.vertexList.values().stream().mapToInt(v -> v.adjacentVertices.size()).sum();
        List<VertexNode> augmentingPath = RandomSimulation.randomDijkstra(NG, source, sink);
        System.out.println("Shortest Augmenting Path from " + source.referenceId + " to " + sink.referenceId + ":");
        for (VertexNode vertex : augmentingPath) {
            System.out.print(vertex.referenceId);
            if (augmentingPath.indexOf(vertex) < augmentingPath.size() - 1) {
                System.out.print(" -> ");
            }
        }

        System.out.println("END");
        int paths = 0;
        int totalLength = 0;
        int maxLength = 0;
        int maxFlow = 0;
        FordFulkerson f = new FordFulkerson();
        do {
            paths++;
            totalLength += augmentingPath.size();
            maxLength = Math.max(maxLength, augmentingPath.size());

            int minCapacity = f.determineMinimumCapacity(augmentingPath);
            f.updateResidualGraph(augmentingPath, minCapacity);

            maxFlow += minCapacity;

            System.out.println("Shortest Augmenting Path from " + source.referenceId + " to " + sink.referenceId + ":");
//            String pathString = augmentingPath.stream()
//                    .map(vertex -> String.valueOf(vertex.id))
//                    .collect(Collectors.joining(" -> "));
            StringJoiner joiner = new StringJoiner(" -> ");
            for (VertexNode vertex : augmentingPath) {
                joiner.add(String.valueOf(vertex.referenceId));
            }
            String pathString = joiner.toString();


            System.out.print(pathString);
            System.out.println(" END");

            augmentingPath = RandomSimulation.randomDijkstra(NG, source, sink);

        } while (augmentingPath.size() > 1);


        // Statistics
        double meanLength = (double) totalLength / paths;
        double meanProportionalLength = meanLength / maxLength;

        System.out.println("Maximum Flow: " + maxFlow);
        System.out.println("Paths: " + paths);
        System.out.println("Mean Length: " + meanLength);
        System.out.println("Mean Proportional Length: " + meanProportionalLength);
        System.out.println("Total Edges: " + totalEdges);

        return new SimulationOutcome(type, maxFlow, paths, meanLength, meanProportionalLength, totalEdges);

    }
}





 class SAP {
    SAP() {}

    public static class SAPSimulation {

        // Dijkstra's algorithm treating edges with non-zero capacity
        static List<VertexNode> dijkstra(NodeGraph NG, VertexNode source, VertexNode sink) {
            System.out.println("Inside dijkstra SAP");
//        NG.getCapacities();
            if (NG == null || source == null || sink == null) {
                throw new IllegalArgumentException("Input arguments cannot be null.");
            }

            Map<VertexNode, VertexNode> predecessors = new HashMap<>();
            Map<VertexNode, Integer> distances = new HashMap<>();
            PriorityQueue<VertexNode> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

//            for (Vertex vertex : NG.vertices.values()) {
//                distances.put(vertex, Integer.MAX_VALUE / 2); // Avoid integer overflow
//                predecessors.put(vertex, null);
//            }
            NG.vertexList.values().forEach(vertex -> {
                distances.put(vertex, Integer.MAX_VALUE / 2);
                predecessors.put(vertex, null);
            });


            distances.put(source, 0);
            queue.add(source);

            do {
                VertexNode currentVertex = queue.poll();
                if (currentVertex.referenceId == sink.referenceId) {
                    sink = currentVertex;
                    break;
                }
                int currentVertexDistance = distances.get(currentVertex);
                for (Map.Entry<VertexNode, Integer> entry : currentVertex.adjacentVertices.entrySet()) {
                    VertexNode neighbor = entry.getKey();
                    int capacity = entry.getValue();

                    // Consider only edges with non-zero capacity
                    if (capacity > 0) {
                        // For unit distances, set altDistance to 1
                        int neighbourDistance = distances.get(neighbor);
                        if (currentVertexDistance+1 < neighbourDistance) {
                            distances.put(neighbor, currentVertexDistance+1);
                            predecessors.put(neighbor, currentVertex);
                            queue.add(neighbor);
                        }
                    }
                }
            }while (!queue.isEmpty());

            Integer sinkDistance = distances.get(sink);
            if (sinkDistance == null || sinkDistance == Integer.MAX_VALUE / 2) {
                // The sink is not reachable from the source
                return Collections.emptyList();
            }

            List<VertexNode> path = new ArrayList<>();
            for (VertexNode currentVertex = sink; currentVertex != null; currentVertex = predecessors.get(currentVertex)) {
                path.add(currentVertex);
            }
            Collections.reverse(path);
            return path;
        }
    }

    public SimulationOutcome simulationSAP(NodeGraph NG, VertexNode source, VertexNode sink, String type) {
        int totalEdges = NG.vertexList.values().stream().mapToInt(v -> v.adjacentVertices.size()).sum();

        List<VertexNode> augmentingPath = SAPSimulation.dijkstra(NG, source, sink);
//        NG.getCapacities();
        System.out.println("Shortest Augmenting Path from " + source.referenceId + " to " + sink.referenceId + ":");
        for (VertexNode vertex : augmentingPath) {
            System.out.print(vertex.referenceId + " -> ");
        }
        System.out.println("END");

        // Statistics
        int paths = 0;
        int totalLength = 0;
        int maxLength = 0;

        // Run Ford-Fulkerson
        int maxFlow = 0;
        FordFulkerson f = new FordFulkerson();
        do {
            paths++;
            totalLength += augmentingPath.size();
            maxLength = Math.max(maxLength, augmentingPath.size());

            int minCapacity = f.determineMinimumCapacity(augmentingPath);
            f.updateResidualGraph(augmentingPath, minCapacity);
            //graph.getCapacities();
            maxFlow += minCapacity;

            augmentingPath = SAPSimulation.dijkstra(NG, source, sink);

            System.out.println("Shortest Augmenting Path from " + source.referenceId + " to " + sink.referenceId + ":");
//            for (Vertex vertex : augmentingPath) {
//                System.out.print(vertex.id + " -> ");
//            }
            String pathString = augmentingPath.stream()
                    .map(vertex -> String.valueOf(vertex.referenceId))
                    .collect(Collectors.joining(" -> "));
            System.out.print(pathString);

            System.out.println("END");
        }while (augmentingPath.size() > 1);

        // Statistics
        double meanLength = (double) totalLength / paths;
        double meanProportionalLength = meanLength / maxLength;

        System.out.println("Maximum Flow: " + maxFlow);
        System.out.println("Paths: " + paths);
        System.out.println("Mean Length: " + meanLength);
        System.out.println("Mean Proportional Length: " + meanProportionalLength);
        System.out.println("Total Edges: " + totalEdges);

        return new SimulationOutcome(type, maxFlow, paths, meanLength, meanProportionalLength, totalEdges);

    }
}



