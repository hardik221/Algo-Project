import java.util.List;

public class FordFulkerson {
     public int findMinCapacity(List<Vertex> path) {
        int minCapacity = Integer.MAX_VALUE;

        for (int i = 0; i < path.size() - 1; i++) {
            Vertex u = path.get(i);
            Vertex v = path.get(i + 1);

            int capacity = u.neighbors.get(v);
            minCapacity = Math.min(minCapacity, capacity);
        }

        return minCapacity;
    }

    public void updateResidualGraph(List<Vertex> path, int minCapacity) {
        for (int i = 0; i < path.size() - 1; i++) {
            Vertex u = path.get(i);
            Vertex v = path.get(i + 1);

            int originalCapacity = u.neighbors.get(v);
            int forwardFlow = Math.min(originalCapacity, minCapacity);

            // Update forward edge
            u.neighbors.put(v, Math.max(0, originalCapacity - forwardFlow));

            // Check if backward edge already exists
            if (v.neighbors.containsKey(u)) {
                // Update backward edge (subtract forward flow)
                v.neighbors.put(u, v.neighbors.get(u) - forwardFlow);
            } else {
                // Add backward edge (create if it doesn't exist)
                v.neighbors.put(u, forwardFlow);
            }
        }
    }
}
