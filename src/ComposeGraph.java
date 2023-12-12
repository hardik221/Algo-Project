import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.util.stream.IntStream;


public class ComposeGraph {
    public void constructFlowGraph(int n, double r, int upperCap, String outputFileName) {
//        Set<Vertex> vertexSet = new HashSet<>(); Set<Edge> edgeSet = new HashSet<>();
//        IntStream.range(0, n)
//                .mapToObj(i -> new Vertex())
//                .forEach(vertexSet::add);
//        vertexSet.forEach(vertexU ->
//                vertexSet.stream()
//                        .filter(vertexV -> !vertexU.equals(vertexV) && Math.pow(vertexU.cord1 - vertexV.cord1, 2) + Math.pow(vertexU.cord2 - vertexV.cord2, 2) <= Math.pow(r, 2))
//                        .findAny()
//                        .ifPresent(vertexV -> {
//                            double rand = Math.random();
//                            Edge edge = rand < 0.5 ? new Edge(vertexU, vertexV) : new Edge(vertexV, vertexU);
//                            if (!edgeSet.contains(edge)) {
//                                edgeSet.add(edge);
//                            }
//                        })
//        );
//        for (Edge edge : edgeSet) {
//            edge.capacity = new Random().nextInt(upperCap) + 1;
//        }
//        Map<Vertex, Map<Vertex, Integer>> adjacencyList = edgeSet.stream()
//                .collect(Collectors.groupingBy(
//                        edge -> edge.vertexU,
//                        Collectors.toMap(edge -> edge.vertexV, edge -> edge.capacity)
//                ));
//        try (PrintWriter writer = new PrintWriter(new File(outputFileName))) {
//            for (Vertex vertex : adjacencyList.keySet()) {
//                writer.println(vertex.toCsvString(adjacencyList.get(vertex)));
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//
//    }
        Set<Vertex> vertexSet = new HashSet<>();
        Set<Edge> edgeSet = new HashSet<>();

        // Create vertices
        for (int i = 0; i < n; i++) {
            vertexSet.add(new Vertex());
        }

        // Create edges based on distance and random capacity
        for (Vertex vertexU : vertexSet) {
            for (Vertex vertexV : vertexSet) {
                if (!vertexU.equals(vertexV) && Math.pow(vertexU.cord1 - vertexV.cord1, 2) + Math.pow(vertexU.cord2 - vertexV.cord2, 2) <= Math.pow(r, 2)) {
                    double rand = Math.random();
                    Edge edge = rand < 0.5 ? new Edge(vertexU, vertexV) : new Edge(vertexV, vertexU);
                    if (!edgeSet.contains(edge)) {
                        edgeSet.add(edge);
                    }
                }
            }
        }

        // Assign random capacities to edges
        Random random = new Random();
        for (Edge edge : edgeSet) {
            edge.capacity = random.nextInt(upperCap) + 1;
        }

        // Create adjacency list
        Map<Vertex, Map<Vertex, Integer>> adjacencyList = new HashMap<>();
        for (Edge edge : edgeSet) {
            adjacencyList.computeIfAbsent(edge.vertexU, k -> new HashMap<>()).put(edge.vertexV, edge.capacity);
        }

        // Write adjacency list to a file
        try (PrintWriter writer = new PrintWriter(new File(outputFileName))) {
            for (Vertex vertex : adjacencyList.keySet()) {
                writer.println(vertex.toCsvString(adjacencyList.get(vertex)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static class Vertex {
        static int idCounter = 1;
        double cord1;
        double cord2;
        int id;
        Vertex() {
            this.cord1 = Math.random();
            this.cord2 = Math.random();
            this.id = idCounter++;
        }
        String toCsvString(Map<Vertex, Integer> neighbors) {
            StringBuilder builder = new StringBuilder();
            builder.append(id).append(",");

            for (Vertex neighbour : neighbors.keySet()) {
                builder.append(neighbour.id).append(":").append(neighbors.get(neighbour)).append(",");
            }
            if (builder.length() > 0) {
                // Remove the trailing comma
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.toString();
        }
    }

    static class Edge {
        Vertex vertexU;
        Vertex vertexV;
        int capacity;

        Edge(Vertex vertexU, Vertex vertexV) {
            this.vertexU = vertexU;
            this.vertexV = vertexV;
        }


        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Edge edge = (Edge) obj;
            return Objects.equals(vertexU, edge.vertexU) && Objects.equals(vertexV, edge.vertexV) ||
                    Objects.equals(vertexU, edge.vertexV) && Objects.equals(vertexV, edge.vertexU);
        }

        @Override
        public int hashCode() {
            return Objects.hash(vertexU, vertexV);
        }
    }


}
