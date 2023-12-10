import java.util.*;

public class MaxCapSimulation {

    static List<Vertex> maxCapDijkstra(Graph graph, Vertex source, Vertex sink) {
        System.out.println("Inside dijkstra max cap");
        graph.getCapacities();
        if (graph == null || source == null || sink == null) {
            throw new IllegalArgumentException("Input arguments cannot be null.");
        }

        Map<Vertex, Vertex> predecessors = new HashMap<>();
        Map<Vertex, Integer> maxCapacities = new HashMap<>();
        PriorityQueue<Vertex> queue = new PriorityQueue<>(Comparator.comparingInt(maxCapacities::get).reversed());

        for (Vertex vertex : graph.vertices.values()) {
            maxCapacities.put(vertex, Integer.MIN_VALUE);
            predecessors.put(vertex, null);
        }

        int maxCapacity = Integer.MIN_VALUE;
        maxCapacities.put(source, Integer.MAX_VALUE);
        queue.add(source);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();
            if (currentVertex.id == sink.id) {
                sink = currentVertex;
                break;
            }
            for (Map.Entry<Vertex, Integer> entry : currentVertex.neighbors.entrySet()) {
                Vertex neighbor = entry.getKey();
                int capacity = entry.getValue();

                // Consider only edges with non-zero capacity
                if (capacity > 0) {
                    int maxCap = Math.min(maxCapacities.get(currentVertex), capacity);

                    Integer cap = maxCapacities.get(neighbor);
                    if (cap == null || maxCap > cap) {
                        maxCapacities.put(neighbor, maxCap);
                        predecessors.put(neighbor, currentVertex);
                        queue.add(neighbor);
                    }
                }
            }
        }

        Integer sinkCap = maxCapacities.get(sink);
        if (sinkCap == null || sinkCap == Integer.MIN_VALUE) {
            // The sink is not reachable from the source
            return Collections.emptyList();
        }

        // Store the maximum capacity of the critical edge
        maxCapacity = maxCapacities.get(sink);

        List<Vertex> path = new ArrayList<>();
        for (Vertex currentVertex = sink; currentVertex != null; currentVertex = predecessors.get(currentVertex)) {
            path.add(currentVertex);
        }
        Collections.reverse(path);

        // Print or use maxCapacity as needed
        System.out.println("Maximum Capacity: " + maxCapacity);

        return path;
    }
}
