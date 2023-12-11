import java.util.Map;

public class Main {
    public static void main(String[] args) {
        int n = 200;
        double r = 0.3;
        int upperCap = 50;
        String outputFileName = "graph_adjacency_list_"+n+"_"+r+"_"+upperCap+".csv";
        GraphGenerator graphGenerator = new GraphGenerator();


        graphGenerator.generateSinkSourceGraph(n, r, upperCap, outputFileName);





//        Vertex source = new Vertex(5, 6);  // Set the source vertex coordinates
//        Vertex sink = new Vertex(10, 8);   // Set the sink vertex coordinates

//        Map<Vertex, Map<Vertex, Integer>> graph = fordFulkersonSAP.readGraphFromFile(filename);
//

    }
}