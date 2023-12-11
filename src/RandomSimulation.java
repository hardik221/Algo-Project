import java.util.*;

public class RandomSimulation {

    // Dijkstra's algorithm treating edges with non-zero capacity
    static List<Vertex> randomDijkstra(Graph graph, Vertex source, Vertex sink) {
        System.out.println("Inside random dijkstra dfs like");

        if (graph == null || source == null || sink == null) {
            throw new IllegalArgumentException("Input arguments cannot be null.");
        }

        Map<Vertex, Vertex> predecessors = new HashMap<>();
        Map<Vertex, Integer> distances = new HashMap<>();
        PriorityQueue<Vertex> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (Vertex vertex : graph.vertices.values()) {
            distances.put(vertex, Integer.MAX_VALUE / 2); // Avoid integer overflow
            predecessors.put(vertex, null);
        }

        distances.put(source, 0);
        queue.add(source);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();
            if(currentVertex.id == sink.id) {
                sink = currentVertex;
            }
            // Continue exploration even if we reach the sink
            for (Map.Entry<Vertex, Integer> entry : currentVertex.neighbors.entrySet()) {
                Vertex neighbor = entry.getKey();
                int capacity = entry.getValue();

                if (capacity > 0) {
                    Integer currentDistance = distances.get(neighbor);

                    if (currentDistance == null || currentDistance == Integer.MAX_VALUE / 2) {
                        distances.put(neighbor, new Random().nextInt(Integer.MAX_VALUE / 2));
                        predecessors.put(neighbor, currentVertex);
                        queue.add(neighbor);

                        if(neighbor.id == sink.id) {
                            sink = neighbor;
                        }
                    }
                }
            }
        }

        Integer sinkDistance = distances.get(sink);
        if (sinkDistance == null || sinkDistance == Integer.MAX_VALUE / 2) {
            // The sink is not reachable from the source
            return Collections.emptyList();
        }

        // Explore the predecessors to construct the path
        List<Vertex> path = new ArrayList<>();
        for (Vertex currentVertex = sink; currentVertex != null; currentVertex = predecessors.get(currentVertex)) {
            path.add(currentVertex);
        }
        Collections.reverse(path);
        return path;
    }
}
