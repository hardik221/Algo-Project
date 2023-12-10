import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Graph {
    Map<Integer, Vertex> vertices = new HashMap<>();
    Graph(){};
    // Deep copy constructor
    Graph(Graph other) {
        // Create a new instance of each vertex
        for (Map.Entry<Integer, Vertex> entry : other.vertices.entrySet()) {
            int id = entry.getKey();
            Vertex originalVertex = entry.getValue();
            Vertex newVertex = new Vertex(id);
            this.vertices.put(id, newVertex);

            // Copy neighbors
            for (Map.Entry<Vertex, Integer> neighborEntry : originalVertex.neighbors.entrySet()) {
                Vertex originalNeighbor = neighborEntry.getKey();
                int capacity = neighborEntry.getValue();
                Vertex newNeighbor = new Vertex(originalNeighbor.id);
                newVertex.neighbors.put(newNeighbor, capacity);
            }
        }
    }

    void addEdge(int uId, int vId, int capacity) {
        Vertex u = vertices.computeIfAbsent(uId, Vertex::new);
        Vertex v = vertices.computeIfAbsent(vId, Vertex::new);
        u.neighbors.put(v, capacity);
    }

    Vertex getRandomVertex() {
        List<Vertex> vertexList = new ArrayList<>(vertices.values());
        return vertexList.get(new Random().nextInt(vertexList.size()));
    }

    void getCapacities() {
        for (Vertex u : vertices.values()) {
            for (Map.Entry<Vertex, Integer> entry : u.neighbors.entrySet()) {
                Vertex v = entry.getKey();
                int capacity = entry.getValue();

                System.out.println("Edge: " + u.id + " -> " + v.id + ", Capacity: " + capacity);
            }
        }
    }
}

class Vertex {
    int id;
    Map<Vertex, Integer> neighbors = new HashMap<>();

    Vertex(int id) {
        this.id = id;
    }

    Vertex(Vertex original) {
        this.id = original.id;
        // Create a new map for neighbors and copy the entries
        this.neighbors = new HashMap<>(original.neighbors.size());
        for (Map.Entry<Vertex, Integer> entry : original.neighbors.entrySet()) {
            Vertex neighborCopy = new Vertex(entry.getKey());
            this.neighbors.put(neighborCopy, entry.getValue());
        }
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
        return new Graph(graph);
    }

    // Function to find the longest path in a graph using BFS
    static Vertex findLongestPath(Graph graph, Vertex source) {
        Map<Vertex, Integer> distance = new HashMap<>();
        Queue<Vertex> queue = new LinkedList<>();
        queue.offer(source);
        distance.put(source, 0);

        Vertex farthestNode = source;
        int maxDistance = 0;
        System.out.println(source.id);
        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            for (Vertex neighbor : current.neighbors.keySet()) {
                if (!distance.containsKey(neighbor)) {
                    distance.put(neighbor, distance.get(current) + 1);
                    queue.offer(neighbor);

                    if (distance.get(neighbor) > maxDistance) {
                        maxDistance = distance.get(neighbor);
                        farthestNode = neighbor;
                        System.out.println(farthestNode.id);
                    }
                }
            }
        }

        return farthestNode;
    }

    public static void main(String[] args) throws IOException {
        String fileName = "graph_adjacency_list_100_0.2_50.csv";
        Graph graph = readGraphFromFile(fileName);

        // Select a random source and find the longest path to determine the sink
        Vertex source = graph.getRandomVertex();
        Vertex sink = findLongestPath(graph, source);

        System.out.println(source.id +", "+ sink.id);
        List<Result> results = new ArrayList<>();

        // 1. Shortest Augmenting Path (SAP)
        Graph g1 =  readGraphFromFile(fileName);
        Vertex sourceCopy1 = new Vertex(source);
        Vertex sinkCopy1 = new Vertex(sink);
        g1.getCapacities();
        RunSAPSimulation rss = new RunSAPSimulation();
        Result resultSAP = rss.runSAPSimulation(g1, sourceCopy1, sinkCopy1, "SAP");
        results.add(resultSAP);
        System.out.println();

        // 2. DFS-Like
        Graph g2 =  readGraphFromFile(fileName);
        Vertex sourceCopy2 = new Vertex(source);
        Vertex sinkCopy2 = new Vertex(sink);
        Result resultDFSLike = new RunDFSLikeSimulation().runDFSLikeSimulation(g2, sourceCopy2, sinkCopy2, "DFS-Like");
        results.add(resultDFSLike);

        // 3. Maximum Capacity (MaxCap)
        Graph g3 =  readGraphFromFile(fileName);
        Vertex sourceCopy3 = new Vertex(source);
        Vertex sinkCopy3 = new Vertex(sink);
        Result resultMaxCap = new RunMaxCapSimulation().runMaxCapSimulation(g3, sourceCopy3, sinkCopy3, "Max-Cap");
        results.add(resultMaxCap);

        // 4. Random


        // Display
        System.out.println();
        System.out.println(String.format("%-10s\t%-5s\t%-5s\t%-5s\t%-5s\t%-5s",
                "Algorithm", "maxFlow", "ML", "paths", "MPL", "totalEdges"));
        for(Result result : results) {
            String s = result.toFormattedString();
            System.out.println(s);
        }
    }
}
