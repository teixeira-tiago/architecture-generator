package mapping.placement;

import architectures.dataflows.extractor.Pattern;
import utiliters.Utiliter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import architectures.dataflows.graph.Node;
import architectures.dataflows.graph.RGraph;
import org._3pq.jgrapht.Edge;
import utiliters.UtilXML;

public class Route {

    public HashMap<Integer, Integer> estatisticas = new HashMap<Integer, Integer>();    
    private HashMap<String, Integer> nodeNameToPosition;
    public ArrayList<String> costEdges = new ArrayList<String>();
    public ProcessorGraph processorGraph;
    public RGraph dataflowGraph;
    public HashMap<String, Integer> arcos = new HashMap<String, Integer>();
    private String dataflowName;
    public double sumCosts = .0;
    public double medPath = .0;
    public int incomplete = 0;
    public int caminhoCritico;
    private int typeCost;
    private double cost;


    public Route(ProcessorGraph processorAux, RGraph dataflowAux) {
        processorGraph = processorAux;
        dataflowGraph = dataflowAux;
    }
    
    public Route(ProcessorGraph processorAux, RGraph dataflowAux, String dataflowName, int typeCost, double cost) {
        processorGraph = processorAux;
        dataflowGraph = dataflowAux;
        this.dataflowName = dataflowName;
        this.typeCost = typeCost;
        this.cost = cost;
    }

    public void routeProcessor() {
//		System.out.println();
//		System.out.println("Routing:");

        float complete = 0;

        nodeNameToPosition = new HashMap<String, Integer>();
        int i, k, a, b;
        for (i = 0; i < processorGraph.getProcessor().size(); i++) {
            if (processorGraph.getProcessor().get(i).nodo != null) {
                nodeNameToPosition.put(
                        processorGraph.getProcessor().get(i).nodo.name,
                        new Integer(i));
            }
        }
        List<?> edgesOut;
        int[] dist, pred;
        boolean[] visited;
        Path[] predPath;
        int next, v, d, targetPos, sourcePos, cont = 0;
        ArrayList<Path> n;
        UtilXML utilXML = new UtilXML();
        String source, target;
        double sumCostLink = .0;
        ArrayList<String> print = new ArrayList<String>();
        
        for (a = 0; a < processorGraph.getProcessor().size(); a++) {
            if (processorGraph.getProcessor().get(a).nodo != null) {
                source = processorGraph.getProcessor().get(a).nodo.name;
                edgesOut = dataflowGraph.outgoingEdgesOf(processorGraph.getProcessor().get(a).nodo);
                for (b = 0; b < edgesOut.size(); b++) {
                    dist = new int[processorGraph.getProcessor().size()];
                    pred = new int[processorGraph.getProcessor().size()];
                    visited = new boolean[processorGraph.getProcessor().size()];
                    predPath = new Path[processorGraph.getProcessor().size()];
                    for (i = 0; i < dist.length; i++) {
                        dist[i] = Integer.MAX_VALUE;
                    }
                    dist[a] = 0;

                    for (i = 0; i < dist.length; i++) {
                        next = minVertex(dist, visited);
                        if (next == -1) {
                            continue;
                        }
                        visited[next] = true;
                        // The shortest path to next is dist[next] and via
                        // pred[next].
                        n = processorGraph.getProcessor().get(next).returnNotUsedOutputConnections();
                        for (k = 0; k < n.size(); k++) {
                            v = processorGraph.nameToPosition.get(n.get(k).another.peName);
                            d = dist[next] + n.get(k).cost;
                            if (dist[v] > d) {
                                dist[v] = d;
                                pred[v] = next;
                                predPath[v] = n.get(k);
                            }
                        }
                    }
                    target = ((Node) ((Edge) edgesOut.get(b)).getTarget()).name;
                    targetPos = nodeNameToPosition.get(target);
                    /*					System.out
                    .print("Trying connect "
                    + processorGraph.getProcessor().get(a).name
                    + " to "
                    + processorGraph.getProcessor().get(
                    targetPos).name);
                     */ if (dist[targetPos] > 99999) {
//						System.out.println("              ERROR");
                        // para inserir o codigo do marcone pegar o valor de "a" como source e o targetPos
                        incomplete++;
                        continue;
                    } else {
//						System.out.println(" Cost:" + dist[targetPos]);
                        complete++;
                        sumCosts += utilXML.calcCost(dist[targetPos], typeCost, cost);
                        
                        if (!estatisticas.containsKey(dist[targetPos])) {
                            estatisticas.put(dist[targetPos], new Integer(1));
                        } else {
                            estatisticas.put(dist[targetPos], estatisticas.get(dist[targetPos]) + 1);
                        }
                    }
                    while (targetPos != a) {
                        predPath[targetPos].used = true;
                        sumCostLink += utilXML.calcCost(dist[targetPos], typeCost, cost);
                        ++cont;
                        targetPos = pred[targetPos];
                    }
                    print.add(source + ":" + target + ":" + sumCostLink + ":" + cont);
                    if (!arcos.containsKey(source+":"+target)){
                        arcos.put(source+":"+target, cont);
                    } else if (arcos.get(source+":"+target) < cont){
                        arcos.put(source+":"+target, cont);
                    }
                    sumCostLink = .0;
                    cont = 0;
                }
            }
        }
        /*        Iterator<Integer> it = estatisticas.keySet().iterator();
        @SuppressWarnings("unused")
        Integer aux;
        System.out.println();
        System.out.println("Estatisticas: ");
        while (it.hasNext()) {
        aux = it.next();            
        System.out.println("Cost: " + aux + "  Number: "
        + estatisticas.get(aux));
        }
         */ medPath = sumCosts / complete;
         Pattern critical = new Pattern();
         critical.load(dataflowName);
         caminhoCritico = critical.getCriticalPath(arcos);
        System.out.println();
        System.out.println("Benchmark utilizado: "+dataflowName.substring(dataflowName.lastIndexOf(".") + 1));
        System.out.println("Total de Arcos do dataflow: " + complete);
        System.out.println("Total de Segmentos da arquitetura: " + sumCosts);
        System.out.println("Media dos segmentos: " + medPath);
        System.out.println("Arcos nao roteados: " + incomplete);
        System.out.println("Tamanho Caminho Critico: "+caminhoCritico);
        String str = "dijsktra.pcomplete:" + complete + "#dijsktra.sumcomplete:" + sumCosts +
                "#dijsktra.avgcomplete:" + medPath + "#dijsktra.pincomplete:" + incomplete +
                "#dijsktra.pincomplete:" + caminhoCritico;
        Utiliter util = new Utiliter();
        util.setProperties("dijsktra.properties", str);
 //       util.printList(print, "files/" + dataflowName + ".dat");
        costEdges = print;
    }

    private static int minVertex(int[] dist, boolean[] v) {
        int x = Integer.MAX_VALUE, i;
        int y = -1; // graph not connected, or no unvisited vertices

        for (i = 0; i < dist.length; i++) {
            if (!v[i] && dist[i] < x) {
                y = i;
                x = dist[i];
            }
        }
        return y;
    }
}
