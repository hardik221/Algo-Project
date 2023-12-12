import java.util.List;
import java.util.stream.IntStream;

public class FordFulkerson {
    public int determineMinimumCapacity(List<VertexNode> path) {
        return IntStream.range(0, path.size() - 1)
            .map(i -> path.get(i).adjacentVertices.get(path.get(i + 1)))
            .min()
            .orElse(Integer.MAX_VALUE);
}

    public void updateResidualGraph(List<VertexNode> path, int minCapacity) {
        for (int i = 0; i < path.size() - 1; i++) {
            VertexNode u = path.get(i);
            VertexNode v = path.get(i + 1);
            int originalCapacity = u.adjacentVertices.getOrDefault(v, 0);
            int forwardFlow = Math.min(originalCapacity, minCapacity);
            u.adjacentVertices.put(v, originalCapacity - forwardFlow);
            v.adjacentVertices.merge(u, -forwardFlow, Integer::sum);
        }
    }

}
