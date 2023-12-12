import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.util.stream.IntStream;


class ComposeGraph {
    public void constructFlowGraph(int n, double r, int upperCap, String outputFileName) {
        Set<Vertex> vertexSet = new HashSet<>(); Set<Edge> edgeSet = new HashSet<>();
        IntStream.range(0, n)
                .mapToObj(i -> new Vertex())
                .forEach(vertexSet::add);
        vertexSet.forEach(vertexU ->
                vertexSet.stream()
                        .filter(vertexV -> !vertexU.equals(vertexV) && Math.pow(vertexU.cord1 - vertexV.cord1, 2) + Math.pow(vertexU.cord2 - vertexV.cord2, 2) <= Math.pow(r, 2))
                        .findAny()
                        .ifPresent(vertexV -> {
                            double rand = Math.random();
                            Edge edge = rand < 0.5 ? new Edge(vertexU, vertexV) : new Edge(vertexV, vertexU);
                            if (!edgeSet.contains(edge)) {
                                edgeSet.add(edge);
                            }
                        })
        );
        for (Edge edge : edgeSet) {
            edge.capacity = new Random().nextInt(upperCap) + 1;
        }
        Map<Vertex, Map<Vertex, Integer>> adjacencyList = edgeSet.stream()
                .collect(Collectors.groupingBy(
                        edge -> edge.vertexU,
                        Collectors.toMap(edge -> edge.vertexV, edge -> edge.capacity)
                ));
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

            for (Map.Entry<Vertex, Integer> entry : neighbors.entrySet()) {
                builder.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
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
