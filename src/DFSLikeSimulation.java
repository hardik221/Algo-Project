import java.util.*;

public class DFSLikeSimulation {

    static List<Vertex> dfsLikeDijkstra(Graph graph, Vertex source, Vertex sink) {
        System.out.println("Inside dijkstra dfs like");

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

        int decreasingCounter = 0;
        distances.put(source, 0);
        queue.add(source);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();
            if(currentVertex.id == sink.id) {
                sink = currentVertex;
            }
            for (Map.Entry<Vertex, Integer> entry : currentVertex.neighbors.entrySet()) {
                Vertex neighbor = entry.getKey();
                int capacity = entry.getValue();

                if (capacity > 0) {
                    int currentDistance = distances.get(neighbor);

                    if (currentDistance == Integer.MAX_VALUE) {
                        distances.put(neighbor, decreasingCounter--);
                        predecessors.put(neighbor, currentVertex);
                        queue.add(neighbor);

                        if(neighbor.id == sink.id) {
                            sink = neighbor;
                        }
                    }
                }
            }
        }

        if (distances.get(sink) == Integer.MAX_VALUE) {
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
