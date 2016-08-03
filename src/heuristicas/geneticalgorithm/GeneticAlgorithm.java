package heuristicas.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;
import xml.topologyclass.Element;
import utiliters.Utiliter;
import utiliters.XMLToFiles;
import heuristicas.Avaliation;
import heuristicas.geneticalgorithm.genalgpackage.GenAlgException;
import heuristicas.geneticalgorithm.genalgpackage.GenAlgManager;

public class GeneticAlgorithm {

    private ArrayList<String> print = new ArrayList<String>();
    private TopologyIndividual[] population;
    private TopologyIndividual bestTopology;
    private Utiliter util = new Utiliter();
    private List<Element> individuals;
    private Avaliation avaliate;
    private GenAlgManager ga;
    private String name;
    private int size;

    public GeneticAlgorithm(String name) {
        this.name = name;
    }

    public void generatePopulation(Avaliation avaliation, String pop) {
        this.avaliate = avaliation;
        XMLToFiles xtf = new XMLToFiles();
        individuals = xtf.getPopulation(pop);
        size = individuals.size();
        population = new TopologyIndividual[size];
        TopologyIndividual topology;
        System.out.println("--Initial Population--");
        util.setProperties("info.properties", "info.new:true");
        util.setProperties("info.properties", "info.zero:true");
        print.add("\t\t\t------Populacao inicial--------");
        avaliate.start();
        avaliate.store(10);
        int i;
        for (i = 0; i < size; ++i) {
            topology = new TopologyIndividual(individuals.get(i), avaliate);
            avaliate = topology.Avaliacao();
            population[i] = topology;
        }
    }

    public void initializeGA() {
        try {
            ga = new GenAlgManager(population, .2, 10, .05, 200);
        } catch (GenAlgException e) {
            e.printStackTrace();
        }
    }

    public void runGA(double time, int generations, int typeCross) throws GenAlgException {
        try {
            bestTopology = (TopologyIndividual) ga.run(time, generations, avaliate, name, typeCross);
            avaliate = ga.getAvaliate();
            ga.ultimateGeneration();
            util.limparArquivoXml("files/xml/dez/" + name + "_Melhor10.xml", "Topologies");
            util.limparArquivoXml("files/xml/" + name + "_Melhor.xml", "Topologies");
            util.limparArquivoXml("files/xml/pior/" + name + "_Pior.xml", "Topologies");
            avaliate.persists(name + "_Melhor", "pior/"+name + "_Pior", "files/" + name);
            avaliate.persistsX("dez/"+name + "_Melhor10");
            avaliate.position(name);
            System.out.println("Melhor resultado: " + avaliate.best().getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Element topology() {
        return ga.getBest();
    }

    public int totalPop() {
        return size;
    }
}
