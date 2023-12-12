import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

class Graph {
    Map<Integer, Vertex> vertices = new HashMap<>();

    void addEdge(int uId, int vId, int capacity) {
        Vertex u = vertices.computeIfAbsent(uId, Vertex::new);
        Vertex v = vertices.computeIfAbsent(vId, Vertex::new);
        u.neighbors.put(v, capacity);
    }

    Vertex getRandomVertex() {
        List<Vertex> eligibleVertices = vertices.values().stream()
                .filter(vertex -> !vertex.neighbors.isEmpty()) // Filter vertices with at least one outgoing edge
                .collect(Collectors.toList());

        if (eligibleVertices.isEmpty()) {
            // Handle the case where there are no vertices with outgoing edges
            return null; // Or throw an exception, return a default vertex, etc.
        }

        return eligibleVertices.get(new Random().nextInt(eligibleVertices.size()));
    }
}

class Vertex {
    int id;
    Map<Vertex, Integer> neighbors = new HashMap<>();

    Vertex(int id) {
        this.id = id;
    }
}

public class Helper {

    static Graph readGraphFromFile(String fileName) throws IOException {
        Graph graph = new Graph();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int uId = Integer.parseInt(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    String[] edge = parts[i].split(":");
                    int vId = Integer.parseInt(edge[0]);
                    int capacity = Integer.parseInt(edge[1]);
                    graph.addEdge(uId, vId, capacity);
                }
            }
        }
        return graph;
    }

    static Vertex findLongestPath(Vertex source) {
        Map<Vertex, Integer> distance = new HashMap<>();
        Queue<Vertex> queue = new LinkedList<>();
        queue.offer(source);
        distance.put(source, 0);

        Vertex farthestNode = source;
        int maxDistance = 0;
        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            for (Vertex neighbor : current.neighbors.keySet()) {
                if (!distance.containsKey(neighbor)) {
                    distance.put(neighbor, distance.get(current) + 1);
                    queue.offer(neighbor);

                    if (distance.get(neighbor) > maxDistance) {
                        maxDistance = distance.get(neighbor);
                        farthestNode = neighbor;

                    }
                }
            }
        }

        return farthestNode;
    }

    public static void main(String[] args) throws IOException {
        int n = Integer.parseInt(args[0]);
        double r = Double.parseDouble(args[1]);
        int upperCap = Integer.parseInt(args[2]);
        String fileName = "graph_adjacency_list_"+args[0]+"_"+args[1]+"_"+args[2]+".csv";
        Graph originalGraph = readGraphFromFile(fileName);

        // Select a random source and find the longest path to determine the sink
        Vertex source = originalGraph.getRandomVertex();
        Vertex sink = findLongestPath(source);

        List<Result> results = new ArrayList<>();

        // 1. Shortest Augmenting Path (SAP)
        Graph g1 =  readGraphFromFile(fileName);

        Vertex sourceCopy1 = g1.vertices.get(source.id);
        Vertex sinkCopy1 = g1.vertices.get(sink.id);
        RunSAPSimulation rss = new RunSAPSimulation();
        Result resultSAP = rss.runSAPSimulation(g1, sourceCopy1, sinkCopy1, "SAP");
        results.add(resultSAP);
        System.out.println();

        // 2. DFS-Like

        Graph g2 =  readGraphFromFile(fileName);
        Vertex sourceCopy2 = g2.vertices.get(source.id);
        Vertex sinkCopy2 = g2.vertices.get(sink.id);
        Result resultDFSLike = new RunDFSLikeSimulation().runDFSLikeSimulation(g2, sourceCopy2, sinkCopy2, "DFS-Like");
        results.add(resultDFSLike);
        System.out.println();

        // 3. Maximum Capacity (MaxCap)

        Graph g3 =  readGraphFromFile(fileName);
        Vertex sourceCopy3 = g3.vertices.get(source.id);
        Vertex sinkCopy3 = g3.vertices.get(sink.id);
        Result resultMaxCap = new RunMaxCapSimulation().runMaxCapSimulation(g3, sourceCopy3, sinkCopy3, "Max-Cap");
        results.add(resultMaxCap);
        System.out.println();

        // 4. Random

        Graph g4 =  readGraphFromFile(fileName);
        Vertex sourceCopy4 = g4.vertices.get(source.id);
        Vertex sinkCopy4 = g4.vertices.get(sink.id);
        Result resultRandom = new RunRandomSimulation().runRandomSimulation(g4, sourceCopy4, sinkCopy4, "Random");
        results.add(resultRandom);
        System.out.println();

        // Display
        display(results, n, r, upperCap);

    }

    static void display(List<Result> results, int n, double r, int upperCap) {
        System.out.println(String.format("%-10s\t%-5s\t%-5s\t%-5s\t%-5s\t%-5s\t%-5s\t%-5s\t%-5s",
                "Algorithm", "n", "r", "upperCap", "paths", "ML", "MPL", "totalEdges", "maxFlow"));
        for (Result result : results) {
            String s = result.toFormattedString(n, r, upperCap);
            System.out.println(s);
        }
    }
}