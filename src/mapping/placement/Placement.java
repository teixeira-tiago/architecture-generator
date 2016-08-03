package mapping.placement;

import utiliters.XMLToFiles;
import heuristicas.geneticalgorithm.genalgpackage.GenAlgException;
import java.util.HashMap;
import java.util.List;
import org._3pq.jgrapht.Edge;
import xml.topologyclass.Element;
import architectures.dataflows.javagraphs.DataflowGraph;
import architectures.dataflows.graph.RGraph;
import java.util.ArrayList;
import java.util.Iterator;
import utiliters.Utiliter;

public class Placement {

    public ProcessorGraph processorGraph = new ProcessorGraph();
    public ArrayList<String> costEdges = new ArrayList();
    public double media;
    public double total;
    public int critical;

    public void homogeneous(Element individuo, int toroide, String benchmark, int heuristica,
            int time, int population, int typeCost, double cost, int fixType, String fixFile, String orient) {
        try {
            ArrayList<String> tempos = new ArrayList<String>();
            String[] auxBench = benchmark.split(":");
            String nameBench = auxBench[0], tmp;
            int side = (int) Math.ceil(Math.sqrt(Integer.valueOf(auxBench[2])));
            ProcessorGraph processor = getProcessor(individuo, side, toroide, typeCost, cost, fixType, fixFile, orient);
            int error;
            Class<?> graphClass;
            DataflowGraph graph = null;
            graphClass = Class.forName(nameBench);
            graph = (DataflowGraph) graphClass.newInstance();
            RGraph rg = graph.getGraph();
            GeneticAlgorithm ga = new GeneticAlgorithm(processor, rg);
            ga.initializeGA(population, heuristica);
            tmp = auxBench[1];
            long inicio = System.currentTimeMillis(), tempo;
            ga.runGA(Double.valueOf(time));
            tempo = System.currentTimeMillis() - inicio;
            Utiliter util = new Utiliter();
            tmp += "\t" + util.tempo(tempo);
            ga.allocateNodes();
            Route routeProcessor = new Route(processor, rg, nameBench, typeCost, cost);
            inicio = System.currentTimeMillis();
            routeProcessor.routeProcessor();
            tempo = System.currentTimeMillis() - inicio;
            tmp += "\t" + util.tempo(tempo);
            tempos.add(tmp);
            util.printList(tempos, "files/temposGenetic.txt", true);
            processorGraph = routeProcessor.processorGraph;
            costEdges = routeProcessor.costEdges;
            media = routeProcessor.medPath;
            total = routeProcessor.sumCosts;
            critical = routeProcessor.caminhoCritico;
            error = routeProcessor.incomplete;
            if (error != 0) {
                media = -1.0;
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (GenAlgException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ProcessorGraph getProcessor(Element individuo, int side, int toroide,
            int typeCost, double cost, int fixType, String fixFile, String orient) {
        HashMap<String, Object> auxPE = new HashMap<String, Object>();
        ProcessorGraph processor = new ProcessorGraph();
        String from = "", target = "";
        XMLToFiles xtf = new XMLToFiles();
        RGraph individual = xtf.getGraph(individuo, side, toroide, typeCost, cost,
                fixType, fixFile, orient);
        Iterator<?> it = individual.vertexSet().iterator();
        List<?> nos;
        int j;
        int custo = 0;
        while (it.hasNext()) {
            nos = individual.edgesOf(it.next());
            for (j = 0; j < nos.size(); ++j) {
                from = (String) ((Edge) nos.get(j)).getSource();
                target = (String) ((Edge) nos.get(j)).getTarget();
                if (!auxPE.containsKey(from)) {
                    processor.addPE(new PE(new String(from)));
                    auxPE.put(from, null);
                }
                if (!auxPE.containsKey(target)) {
                    processor.addPE(new PE(new String(target)));
                    auxPE.put(target, null);
                }
                custo = (int) ((Edge) nos.get(j)).getWeight();
                processor.makeOneWayConnection(from, target, custo, false);
            }
        }
        processor.calculateAllDistances();
        return processor;
    }

    public ProcessorGraph getProcessor(RGraph individual) {
        HashMap<String, Object> auxPE = new HashMap<String, Object>();
        ProcessorGraph processor = new ProcessorGraph();
        String from = "", target = "";
        Iterator<?> it = individual.vertexSet().iterator();
        List<?> nos;
        int j;
        while (it.hasNext()) {
            nos = individual.edgesOf(it.next());
            for (j = 0; j < nos.size(); ++j) {
                from = (String) ((Edge) nos.get(j)).getSource();
                target = (String) ((Edge) nos.get(j)).getTarget();
                if (!auxPE.containsKey(from)) {
                    processor.addPE(new PE(new String(from)));
                    auxPE.put(from, null);
                }
                if (!auxPE.containsKey(target)) {
                    processor.addPE(new PE(new String(target)));
                    auxPE.put(target, null);
                }
                processor.makeOneWayConnection(from, target, 1,
                        false);
            }
        }
        processor.calculateAllDistances();
        return processor;
    }
}
