package mapping.placement;

import heuristicas.geneticalgorithm.genalgpackage.Individual;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import architectures.dataflows.graph.Node;
import architectures.dataflows.graph.RGraph;
import org._3pq.jgrapht.Edge;

public class ProcessorIndividual implements Individual {

    double fitness = Double.MAX_VALUE;
    public String[] individual;
    private ProcessorGraph processorGraph;
    private RGraph dataFlowGraph;
    private static Random myRandom;

    public ProcessorIndividual(int size, ProcessorGraph processor, Random myRand, RGraph dataGraph) {
        individual = new String[size];
        int i;
        for (i = 0; i < size; i++) {
            individual[i] = new String();
        }
        processorGraph = processor;
        myRandom = myRand;
        dataFlowGraph = dataGraph;
    }

    @Override
    public void crossOver(Individual other, Individual firstChild, Individual secondChild) {
        ProcessorIndividual outro = (ProcessorIndividual) other;
        ProcessorIndividual primFilho = (ProcessorIndividual) firstChild;
        ProcessorIndividual segFilho = (ProcessorIndividual) secondChild;
        try {
            int pos = Math.abs(myRandom.nextInt()) % individual.length, i;
            HashMap<String, Object> auxNodes = new HashMap<String, Object>();
            for (i = 0; i < pos; i++) {
                primFilho.individual[i] = individual[i];
                auxNodes.put(individual[i], null);
            }
            ArrayList<Integer> auxPE = new ArrayList<Integer>();
            for (i = pos; i < outro.individual.length; i++) {
                if (!auxNodes.containsKey(outro.individual[i])) {
                    auxNodes.put(outro.individual[i], null);
                    primFilho.individual[pos] = outro.individual[i];
                } else {
                    auxPE.add(pos);
                }
                pos++;
            }
            for (i = 0; i < outro.individual.length; i++) {
                if (!auxNodes.containsKey(outro.individual[i])) {
                    auxNodes.put(outro.individual[i], null);
                    primFilho.individual[auxPE.remove(0)] = outro.individual[i];
                }
            }
            pos = Math.abs(myRandom.nextInt()) % individual.length;
            auxNodes = new HashMap<String, Object>();
            for (i = 0; i < pos; i++) {
                segFilho.individual[i] = individual[i];
                auxNodes.put(individual[i], null);
            }
            auxPE = new ArrayList<Integer>();
            for (i = pos; i < outro.individual.length; i++) {
                if (!auxNodes.containsKey(outro.individual[i])) {
                    auxNodes.put(outro.individual[i], null);
                    segFilho.individual[pos] = outro.individual[i];
                } else {
                    auxPE.add(pos);
                }
                pos++;
            }
            for (i = 0; i < outro.individual.length; i++) {
                if (!auxNodes.containsKey(outro.individual[i])) {
                    auxNodes.put(outro.individual[i], null);
                    segFilho.individual[auxPE.remove(0)] = outro.individual[i];
                }
            }
            ((ProcessorIndividual) firstChild).calcFitness();
            ((ProcessorIndividual) secondChild).calcFitness();
        } catch (Exception e) {
            System.out.println("Falha crossover do posicionamento");
            System.gc();
        }
    }

    /**
     * Executa mutao sobre o indivduo.
     */
    @Override
    public void mutation() {
        int pos1 = Math.abs(myRandom.nextInt()) % individual.length;
        int pos2 = Math.abs(myRandom.nextInt()) % individual.length;
        String aux = individual[pos1];
        individual[pos1] = individual[pos2];
        individual[pos2] = aux;
    }

    /**
     * Retorna valor da funo de fitness do indivduo. Indivduos mais
     * adaptados devem retornar valores maiores.
     * @return Valor da funo de fitness (nmero real).
     */
    @Override
    public double fitnessValue() {
        if (fitness > (Double.MAX_VALUE / 2)) {
            fitness = .0;
            calcFitness();
            return fitness;
        } else {
            return fitness;
        }
    }

    private void calcFitness() {
        Iterator<?> i = dataFlowGraph.edgeSet().iterator();
        int hist[] = {0, 0, 0, 0, 0};
        Edge auxEdge;
        int pos1, pos2;
        while (i.hasNext()) {
            auxEdge = (Edge) i.next();
            pos1 = returnNodePosition(((Node) auxEdge.getSource()).name);
            pos2 = returnNodePosition(((Node) auxEdge.getTarget()).name);
            if ((pos1 >= 0) && (pos2 >= 0)) {
                DistRoute aux = processorGraph.getProcessor().get(pos1).distanceTable.get(processorGraph.getProcessor().get(pos2).name);
                if (aux.cost > 4) {
                    hist[4]++;
                } else {
                    hist[aux.cost - 1]++;
                }
            }
        }
        // P1^3+P2^2+P3>-P^4 tiago
        fitness = Math.pow(hist[0], 3) + Math.pow(hist[1], 2) + hist[2] - (Math.pow(hist[3], 4) + Math.pow(hist[4], 4));
    //fitness = (hist[0] * 1000 + hist[1] * 10 + hist[2] * 1 - hist[3] * 1000 - hist[4] * 1000);
    }

    public int returnNodePosition(String name) {
        int i;
        for (i = 0; i < individual.length; i++) {
            if (name.compareTo(individual[i]) == 0) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void crossOver(int type, Individual other, Individual firstChild, Individual secondChild) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}