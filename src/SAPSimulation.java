import java.util.*;

public class SAPSimulation {

    static List<Vertex> dijkstra(Graph graph, Vertex source, Vertex sink) {
        System.out.println("Inside dijkstra SAP");
        if (graph == null || source == null || sink == null) {
            throw new IllegalArgumentException("Input arguments cannot be null.");
        }

        Map<Vertex, Vertex> predecessors = new HashMap<>();
        Map<Vertex, Integer> distances = new HashMap<>();
        PriorityQueue<Vertex> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (Vertex vertex : graph.vertices.values()) {
            distances.put(vertex, Integer.MAX_VALUE); // Avoid integer overflow
            predecessors.put(vertex, null);
        }

        distances.put(source, 0);
        queue.add(source);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();
            if (currentVertex.id == sink.id) {
                sink = currentVertex;
                break;
            }
            int currentVertexDistance = distances.get(currentVertex);
            for (Map.Entry<Vertex, Integer> entry : currentVertex.neighbors.entrySet()) {
                Vertex neighbor = entry.getKey();
                int capacity = entry.getValue();

                if (capacity > 0) {
                    int currentDistanceOfNeighbour = distances.get(neighbor);

                    if ( currentVertexDistance +1 < currentDistanceOfNeighbour) {
                        distances.put(neighbor, currentVertexDistance +1);
                        predecessors.put(neighbor, currentVertex);
                        queue.add(neighbor);
                    }
                }
            }
        }

        Integer sinkDistance = distances.get(sink);
        if (sinkDistance == null || sinkDistance == Integer.MAX_VALUE) {
            return Collections.emptyList();
        }

        List<Vertex> path = new ArrayList<>();
        for (Vertex currentVertex = sink; currentVertex != null; currentVertex = predecessors.get(currentVertex)) {
            path.add(currentVertex);
        }
        Collections.reverse(path);
        return path;
    }
}
