package mapping.placement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import architectures.dataflows.graph.Node;

public class ProcessorGraph {

    protected ArrayList<Integer> sources;
    protected ArrayList<PE> processorGraph;
    public HashMap<String, Integer> nameToPosition;

    public ProcessorGraph() {
        processorGraph = new ArrayList<PE>();
        nameToPosition = new HashMap<String, Integer>();
        sources = new ArrayList<Integer>();
    }

    public Hashtable<String, String> obtemPosicionamento() {
        Hashtable<String, String> posicionamento = new Hashtable<String, String>();
        Iterator<PE> i = processorGraph.iterator();
        PE p;
        Node n;
        while (i.hasNext()) {
            n = (p = i.next()).nodo;
            if (n != null) {
                posicionamento.put(p.name, n.name);
            }
        }
        return posicionamento;
    }

    public Hashtable<String, String> obtemPosicionamentoInverso() {
        Hashtable<String, String> posicionamentoInverso = new Hashtable<String, String>();
        Iterator<PE> i = processorGraph.iterator();
        PE p;
        Node n;
        while (i.hasNext()) {
            n = (p = i.next()).nodo;
            if (n != null) {
                posicionamentoInverso.put(n.name, p.name);
            }
        }
        return posicionamentoInverso;
    }

    public PE getPE(String name) {
        return processorGraph.get(nameToPosition.get(name));
    }

    public ArrayList<PE> getProcessor() {
        return processorGraph;
    }

    public void addPE(PE aux) {
        processorGraph.add(aux);
        nameToPosition.put(aux.name, processorGraph.size() - 1);
    }

    public void makeOneWayConnection(String from, String target, int cost, boolean used) {
        //System.out.print(from+"  ");		
        try {
            PE fromPE = processorGraph.get(nameToPosition.get(from));
            PE targetPE = processorGraph.get(nameToPosition.get(target));
            fromPE.addOutputConnection("O" + fromPE.returnOutputConnections().size(), target, "I" + targetPE.returnInputConnections().size(), cost, used);
            targetPE.addInputConnection("I" + targetPE.returnInputConnections().size(), from, "O" + (fromPE.returnOutputConnections().size() - 1), cost, used);
        } catch (Exception e) {
            System.out.println("Falha ao conectar em um caminho");
            System.gc();
            PE fromPE = processorGraph.get(nameToPosition.get(from));
            PE targetPE = processorGraph.get(nameToPosition.get(target));
            fromPE.addOutputConnection("O" + fromPE.returnOutputConnections().size(), target, "I" + targetPE.returnInputConnections().size(), cost, used);
            targetPE.addInputConnection("I" + targetPE.returnInputConnections().size(), from, "O" + (fromPE.returnOutputConnections().size() - 1), cost, used);
        }
    //System.out.println(target);
    }

    public void calculateAllDistances() {
        HashMap<String, DistRoute> aux;
        int[] dist, pred;
        boolean[] visited;
        int next, v, d, i, j, k;
        ArrayList<Path> n;
        for (j = 0; j < processorGraph.size(); j++) {
            dist = new int[processorGraph.size()];  // shortest known distance from "s"

            pred = new int[processorGraph.size()];  // preceeding node in path

            visited = new boolean[processorGraph.size()]; // all false initially

            for (i = 0; i < dist.length; i++) {
                dist[i] = Integer.MAX_VALUE;
            }
            dist[j] = 0;
            for (i = 0; i < dist.length; i++) {
                next = minVertex(dist, visited);
                if (next == -1) {
                    continue;
                }
                visited[next] = true;
                // The shortest path to next is dist[next] and via pred[next].
                n = processorGraph.get(next).returnOutputConnections();
                for (k = 0; k < n.size(); k++) {
                    v = nameToPosition.get(n.get(k).another.peName);
                    d = dist[next] + n.get(k).cost;
                    if (dist[v] > d) {
                        dist[v] = d;
                        pred[v] = next;
                    }
                }
            }
            aux = new HashMap<String, DistRoute>();
            for (i = 0; i < dist.length; i++) {
                aux.put(processorGraph.get(i).name, new DistRoute(processorGraph.get(i).name, dist[i], processorGraph.get(pred[i]).name));
            }
            processorGraph.get(j).setDistanceTable(aux);
            //return pred;  // (ignore pred[s]==0!)
            if (processorGraph.get(j).returnInputConnections().size() == 0) {
                sources.add(j);
            }
        }
    }

    private static int minVertex(int[] dist, boolean[] v) {
        int x = Integer.MAX_VALUE, i;
        int y = -1;   // graph not connected, or no unvisited vertices

        for (i = 0; i < dist.length; i++) {
            if (!v[i] && dist[i] < x) {
                y = i;
                x = dist[i];
            }
        }
        return y;
    }

    public ArrayList<Integer> getSources() {
        if (sources.size() == 0) {
            ArrayList<Integer> aux = new ArrayList<Integer>();
            Random auxRandom = new Random();
            aux.add(Math.abs(auxRandom.nextInt()) % processorGraph.size());
            return aux;
        }
        return sources;
    }
}
