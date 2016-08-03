package mapping.placement;

import heuristicas.geneticalgorithm.genalgpackage.GenAlgException;
import heuristicas.geneticalgorithm.genalgpackage.GenAlgManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import architectures.dataflows.graph.Node;
import architectures.dataflows.graph.RGraph;
import org._3pq.jgrapht.*;

public class GeneticAlgorithm {

    protected ProcessorGraph processorGraph;
    protected RGraph dataflowGraph;
    protected Random randomNumbers;
    protected ProcessorIndividual[] population;
    protected GenAlgManager ga;
    protected ProcessorIndividual bestProcessor;
    protected HashMap<String, Integer> nodesNameToPosition;

    public GeneticAlgorithm(ProcessorGraph processor, RGraph dataflow) {
        processorGraph = processor;
        dataflowGraph = dataflow;
        randomNumbers = new Random();
    }

    public void initializeGA(int initialPopulation, int generateType) {
        switch (generateType) {
            case 1:
                generateWidthDepthPopulation(initialPopulation);
                break;
            case 2:
                generateRandomPopulation(initialPopulation);
                break;
            case 3:
                generateSemiDepthPopulation(initialPopulation);
                break;
            default:
                generateAllTypesPopulation(initialPopulation);
                break;
        }
//        if (initialPopulation == 1) {
//            return;
//        }
        try {
            ga = new GenAlgManager(population, .2, 10, .05, 200);
        } catch (GenAlgException e) {
            e.printStackTrace();
        }
    }

    public void runGA(double time) throws GenAlgException {
        bestProcessor = (ProcessorIndividual) ga.run(time);
    }

    public void allocateNodesWithoutGenetic() {
        nodesNameToPosition = new HashMap<String, Integer>();
        int i;
        for (i = 0; i < population[0].individual.length; i++) {
            nodesNameToPosition.put(population[0].individual[i], new Integer(i));
        }
        Node auxNode;
        Iterator<?> it = dataflowGraph.vertexSet().iterator();
        while (it.hasNext()) {
            auxNode = (Node) it.next();
            processorGraph.getProcessor().get(
                    nodesNameToPosition.get(auxNode.name)).setNode(auxNode);
        }
    }

    public void allocateNodes() {
        nodesNameToPosition = new HashMap<String, Integer>();
        int i;
        for (i = 0; i < bestProcessor.individual.length; i++) {
            nodesNameToPosition.put(bestProcessor.individual[i], new Integer(i));
        }
        Node auxNode;
        Iterator<?> it = dataflowGraph.vertexSet().iterator();
        while (it.hasNext()) {
            auxNode = (Node) it.next();
            processorGraph.getProcessor().get(
                    nodesNameToPosition.get(auxNode.name)).setNode(auxNode);
        }
    }

    public void generateAllTypesPopulation(int size) {
        population = new ProcessorIndividual[size];
        int i;
        for (i = 0; i < size; i++) {
            population[i] = new ProcessorIndividual(processorGraph.getProcessor().size(), processorGraph, randomNumbers,
                    dataflowGraph);
            switch (Math.abs(randomNumbers.nextInt()) % 4) {
                case 0:
                    // System.out.println("genetico0");
                    widthGen(population[i]);
                    break;
                case 1:
                    //System.out.println("genetico1");
                    depthGen(population[i]);
                    break;
                case 2:
                    // System.out.println("genetico2");
                    randomGen(population[i]);
                    break;
                case 3:
                    semiDepthGen(population[i]);
                    break;
            }
        }
    }

    public void generateRandomPopulation(int size) {
        population = new ProcessorIndividual[size];
        int i;
        for (i = 0; i < size; i++) {
            population[i] = new ProcessorIndividual(processorGraph.getProcessor().size(), processorGraph, randomNumbers,
                    dataflowGraph);
            randomGen(population[i]);
        }
    }

    public void generateWidthDepthPopulation(int size) {
        population = new ProcessorIndividual[size];
        int i;
        for (i = 0; i < size / 2; i++) {
            population[i] = new ProcessorIndividual(processorGraph.getProcessor().size(), processorGraph, randomNumbers,
                    dataflowGraph);
            widthGen(population[i]);
        }
        for (i = size / 2; i < size; i++) {
            population[i] = new ProcessorIndividual(processorGraph.getProcessor().size(), processorGraph, randomNumbers,
                    dataflowGraph);
            depthGen(population[i]);
        }
    }

    protected void widthGen(ProcessorIndividual ind) {
        ArrayList<Node> auxVertex = new ArrayList<Node>();
        Node[] initialNodes = dataflowGraph.getSources();
        List<?> auxEd;
        int i, j, k;
        for (i = 0; i < initialNodes.length; i++) {
            if (!auxVertex.contains(initialNodes[i])) {
                auxVertex.add(initialNodes[i]);
            }
        }
        i = 0;
        while (i < auxVertex.size()) {
            auxEd = dataflowGraph.outgoingEdgesOf(auxVertex.get(i));
            for (j = 0; j < auxEd.size(); j++) {
                if (!auxVertex.contains(((Node) ((Edge) (auxEd.get(j))).getTarget()))) {
                    auxVertex.add(((Node) ((Edge) (auxEd.get(j))).getTarget()));
                }
            }
            i++;
        }
        ArrayList<Integer> auxPE = new ArrayList<Integer>(processorGraph.getSources());
        ArrayList<Integer> auxPE2 = new ArrayList<Integer>();
        boolean[] used = new boolean[processorGraph.getProcessor().size()];
        for (i = 0; i < auxPE.size(); i++) {
            used[auxPE.get(i)] = true;
        }
        int pePosition;
        while (true) {
            for (j = 0; j < auxPE.size(); j++) {
                for (k = 0; k < processorGraph.getProcessor().get(
                        auxPE.get(j)).returnOutputConnections().size(); k++) {
                    pePosition = processorGraph.nameToPosition.get(processorGraph.getProcessor().get(auxPE.get(j)).returnOutputConnections().get(k).another.peName);
                    if (!used[pePosition]) {
                        auxPE2.add(pePosition);
                        used[pePosition] = true;
                    }
                }
            }
            while (auxPE.size() > 0 && auxVertex.size() > 0) {
                ind.individual[auxPE.remove(Math.abs(randomNumbers.nextInt()) % auxPE.size())] = auxVertex.remove(0).name;            // System.out.println(auxPE2.size());

            }
            if (auxPE2.size() > 0 && auxVertex.size() > 0) {
                auxPE = auxPE2;
                auxPE2 = new ArrayList<Integer>();
            } else {
                break;
            }
        }
    }

    protected void depthGen(ProcessorIndividual ind) {
        ArrayList<Node> auxVertex = new ArrayList<Node>();
        Node[] initialNodes = dataflowGraph.getSources();
        // Get initial Dataflow nodes, put in auxVertex
        int i;
        for (i = 0; i < initialNodes.length; i++) {
            if (!auxVertex.contains(initialNodes[i])) {
                auxVertex.add(initialNodes[i]);            // Get Initial Architecture Nodes, put into auxPE

            }
        }
        ArrayList<Integer> auxPE = new ArrayList<Integer>(processorGraph.getSources());
        ArrayList<Integer> used = new ArrayList<Integer>();
        // scan dataflow in detph mode
        ArrayList<Node> visited = new ArrayList<Node>();
        Node node;
        while (auxVertex.size() > 0) {
            // dataflow
            node = auxVertex.remove(0); // remove Node

            dataflowdepthbind(node, visited);
        } // now visited is a scan node list

        Integer nodeUsed;
        while (auxPE.size() > 0) {
            // architecture
            nodeUsed = auxPE.remove(0); // remove Node

            architecturedepthbind(nodeUsed, used);
        } // now used is a scan node list
        // scan both list and place nodes....

        while (visited.size() > 0 && used.size() > 0) {
            ind.individual[used.remove(0)] = visited.remove(0).name;
        }
    }

    protected void architecturedepthbind(Integer PE, ArrayList<Integer> used) {
        if (!used.contains(PE)) {
            used.add(PE); // MARK PE

        } else {
            return;
        }
        ArrayList<Integer> auxPE2 = new ArrayList<Integer>();
        int pePosition, k;
        for (k = 0; k < processorGraph.getProcessor().get(PE).returnOutputConnections().size(); k++) {
            pePosition = processorGraph.nameToPosition.get(processorGraph.getProcessor().get(PE).returnOutputConnections().get(k).another.peName);
            if (!used.contains(pePosition) && !auxPE2.contains(pePosition)) {
                if (auxPE2.size() == 0) {
                    auxPE2.add(pePosition);
                } else {
                    auxPE2.add(Math.abs(randomNumbers.nextInt()) % auxPE2.size(), pePosition);
                }
            }
        } // create a random children architecture list

        Integer pe;
        while (auxPE2.size() > 0) {
            pe = auxPE2.remove(0);
            architecturedepthbind(pe, used);
        }
    }

    protected void dataflowdepthbind(Node x, ArrayList<Node> visited) {
        if (!visited.contains(x)) {
            visited.add(x); // MARK dataflow

        } else {
            return;
        }
        List<?> auxEd;
        ArrayList<Node> children = new ArrayList<Node>();
        auxEd = dataflowGraph.outgoingEdgesOf(x); // egdes list

        int j;
        for (j = 0; j < auxEd.size(); j++) {
            if (!visited.contains(((Node) ((Edge) (auxEd.get(j))).getTarget())) && !children.contains(((Node) ((Edge) (auxEd.get(j))).getTarget()))) {
                if (children.size() == 0) {
                    children.add(((Node) ((Edge) (auxEd.get(j))).getTarget()));
                } else {
                    children.add(Math.abs(randomNumbers.nextInt()) % children.size(), ((Node) ((Edge) (auxEd.get(j))).getTarget()));
                }
            }
        } // create a random children dataflow list

        Node Y;
        while (children.size() > 0) {
            Y = children.remove(0);
            dataflowdepthbind(Y, visited);
        }
    }

    protected void generateSemiDepthPopulation(int size) {
        population = new ProcessorIndividual[size];
        int i;
        for (i = 0; i < size; i++) {
            population[i] = new ProcessorIndividual(processorGraph.getProcessor().size(), processorGraph, randomNumbers,
                    dataflowGraph);
            semiDepthGen(population[i]);
        }
    }

    protected void semiDepthGen(ProcessorIndividual ind) {
        ArrayList<Node> auxVertex = new ArrayList<Node>();
        Node[] initialNodes = dataflowGraph.getSources();
        // Get initial Dataflow nodes, put in auxVertex
        int i;
        for (i = 0; i < initialNodes.length; i++) {
            if (!auxVertex.contains(initialNodes[i])) {
                auxVertex.add(initialNodes[i]);
            }
        }
        backtracking_table = new Hashtable<Node, Integer>();
        ArrayList<Node> visited = new ArrayList<Node>();
        depth_position = 0;
        Node x;
        while (auxVertex.size() > 0) {
            // dataflow
            x = auxVertex.remove(0); // remove Node

            dataflow_mark_depth_traversal(x, visited);
        } // now visited is a scan node list
        // Get Initial Architecture Nodes, put into auxPE

        ArrayList<Integer> auxPE = new ArrayList<Integer>(processorGraph.getSources());
        ArrayList<Integer> used = new ArrayList<Integer>();
        Integer last = auxPE.remove(0);
        ArrayList<Node> place_list = new ArrayList<Node>();
        // scan dataflow depth list, place nodes and pay attention in
        // backtracking nodes
        // System.out.println("Architecture traversal");
        int s, bound, back_position, back;
        ArrayList<Integer> breadth;
        while (visited.size() > 0) {
            x = visited.remove(0);
            s = 0;
            // building a partial path
            while (!backtracking_table.containsKey(x)) {
                place_list.add(x);
                s++;
                if (visited.size() > 0) {
                    x = visited.remove(0);
                } else {
                    break;
                }
            }
            if (backtracking_table.containsKey(x)) {
                // call recursive Depth-First Bound by S
                bound = used.size() + s;
                if (!last.equals(-1)) {
                    architecture_limited_depth(last, used, bound);
                } else {
                    System.gc();
                    return;
                }
                while (used.size() < bound) {
                    // get last visited node
                    last = used.get(used.size() - 1);
                    // perform a breadth first until found a unused node
                    breadth = new ArrayList<Integer>();
                    breadth.add(last);
                    last = get_breadth_free_node(breadth, used);
                    if (!last.equals(-1)) {
                        architecture_limited_depth(last, used, bound);
                    } else {
                        System.gc();
                        return;
                    }
                }
                back_position = (Integer) backtracking_table.get(x);
                back = used.get(back_position);
                breadth = new ArrayList<Integer>();
                breadth.add(back);
                last = get_breadth_free_node(breadth, used);
                backtracking_table.remove(x);
                visited.add(0, x);
            } else if (visited.size() == 0) {
                bound = used.size() + s;
                if (!last.equals(-1)) {
                    architecture_limited_depth(last, used, bound);
                } else {
                    System.gc();
                    return;
                }
                while (used.size() < bound) {
                    // get last visited node
                    last = used.get(used.size() - 1);
                    // perform a breadth first until found a unused node
                    breadth = new ArrayList<Integer>();
                    breadth.add(last);
                    last = get_breadth_free_node(breadth, used);
                    if (!last.equals(-1)) {
                        architecture_limited_depth(last, used, bound);
                    } else {
                        System.gc();
                        return;
                    }
                }
            }
        }
        // scan both list and place nodes....
        String fu;
        int pe;
        while (place_list.size() > 0 && used.size() > 0) {
            fu = place_list.remove(0).name;
            pe = used.remove(0);
            ind.individual[pe] = fu;
        }
    }

    protected Integer get_breadth_free_node(ArrayList<Integer> PEs,
            ArrayList<Integer> used) {
        ArrayList<Integer> auxPEs = new ArrayList<Integer>();
        Integer PE;
        int pePosition, k;
        while (PEs.size() > 0) {
            PE = PEs.remove(0);
            if (!used.contains(PE)) {
                return PE;
            }
            if (!auxPEs.contains(PE)) {
                for (k = 0; k < processorGraph.getProcessor().get(PE).returnOutputConnections().size(); k++) {
                    pePosition = processorGraph.nameToPosition.get(processorGraph.getProcessor().get(PE).returnOutputConnections().get(k).another.peName);
                    PEs.add(pePosition);
                }
                auxPEs.add(PE);
            }
        }
        System.out.println("Breadth close Node search FAIL !!!");
        return -1;
    }

    protected void architecture_limited_depth1(Integer PE,
            ArrayList<Integer> used, int bound) {
        if (used.contains(PE)) {
            return;
        }
        used.add(PE); // MARK PE

        if (used.size() == bound) {
            return;        //ArrayList<Integer> auxPE2 = new ArrayList<Integer>();

        }
        int pePosition, k;
        for (k = 0; k < processorGraph.getProcessor().get(PE).returnOutputConnections().size(); k++) {
            pePosition = processorGraph.nameToPosition.get(processorGraph.getProcessor().get(PE).returnOutputConnections().get(k).another.peName);
            architecture_limited_depth1(pePosition, used, bound);
            if (used.size() == bound) {
                return;
            }
        }
    }

    protected void architecture_limited_depth(Integer PE,
            ArrayList<Integer> used, int bound) {

        if (used.contains(PE)) {
            return;
        }
        used.add(PE); // MARK PE

        if (used.size() == bound) {
            return;
        }
        ArrayList<Integer> auxPE2 = new ArrayList<Integer>();
        int pePosition, k;
        for (k = 0; k < processorGraph.getProcessor().get(PE).returnOutputConnections().size(); k++) {
            pePosition = processorGraph.nameToPosition.get(processorGraph.getProcessor().get(PE).returnOutputConnections().get(k).another.peName);
            if (!used.contains(pePosition) && !auxPE2.contains(pePosition)) {
                if (auxPE2.size() == 0) {
                    auxPE2.add(pePosition);
                } else {
                    auxPE2.add(Math.abs(randomNumbers.nextInt()) % auxPE2.size(), pePosition);
                }
            }
        }
        Integer pe;
        while (auxPE2.size() > 0) {
            pe = auxPE2.remove(0);
            architecture_limited_depth(pe, used, bound);
            if (used.size() == bound) {
                return;
            }
        }
    }
    protected Hashtable<Node, Integer> backtracking_table;    // containsKey(key), get(key), remove(key)

    protected int depth_position;

    protected void dataflow_mark_depth_traversal(Node x, ArrayList<Node> visited) {
        int myposition = depth_position, j;
        if (!visited.contains(x)) {
            visited.add(x); // MARK dataflow

            depth_position++;
        } else {
            return;
        }
        List<?> auxEd;
        auxEd = dataflowGraph.outgoingEdgesOf(x); // egdes list

        boolean first_free_child = false;
        Node c;
        for (j = 0; j < auxEd.size(); j++) {
            c = (Node) ((Edge) (auxEd.get(j))).getTarget();
            if (!first_free_child) {
                if (!visited.contains(c)) {
                    first_free_child = true;
                    dataflow_mark_depth_traversal(c, visited);
                }
            } else {
                if (!visited.contains(c)) {
                    backtracking_table.put(c, myposition); // key, object

                    dataflow_mark_depth_traversal(c, visited);
                }
            }
        } // create a backtracking table if this node has more than one
    // child....

    }

    protected void randomGen(ProcessorIndividual ind) {
        Set<?> set = dataflowGraph.vertexSet();
        Iterator<?> i = set.iterator();
        ArrayList<String> auxNodes = new ArrayList<String>();
        while (i.hasNext()) {
            auxNodes.add(((Node) (i.next())).name);
        }
        String auxStr;
        int aux;
        while (auxNodes.size() > 0) {
            auxStr = auxNodes.remove(Math.abs(randomNumbers.nextInt()) % auxNodes.size());
            aux = Math.abs(randomNumbers.nextInt()) % ind.individual.length;
            while (ind.individual[aux % ind.individual.length].compareTo("") != 0) {
                aux++;
            }
            ind.individual[aux % ind.individual.length] = auxStr;
        }
    }
}
