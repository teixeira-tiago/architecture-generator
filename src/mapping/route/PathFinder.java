package mapping.route;

import architectures.dataflows.extractor.Pattern;
import mapping.placement.Posicionamento;
import mapping.Dataflow;
import java.util.ArrayList;
import architectures.dataflows.javagraphs.DataflowGraph;
import xml.topologyclass.Element;
import utiliters.Utiliter;
import utiliters.XMLToFiles;
import architectures.dataflows.graph.RGraph;
import mapping.placement.GeneticAlgorithm;
import mapping.placement.Placement;
import mapping.placement.ProcessorGraph;
import mapping.placement.Route;

public class PathFinder {

    private int caminhoCritico;
    private String aproveita = "";
    private String naoAproveita = "";
    public ProcessorGraph processorGraph;

    public void calculate(Element individuo, String benchmark, int population,
            int heuristic, int toroide, int time, int limiteInferiorDeConexoes,
            int limiteSuperiorDeConexoes, int size, String name, int typeCost,
            double cost, int fixType, String fixFile, String orient) {
        int limiteInf = limiteInferiorDeConexoes;
        int limiteSup = limiteSuperiorDeConexoes;
        Roteamento router;
        Placement mapear = new Placement();
        processorGraph = mapear.getProcessor(individuo, size, toroide,
                typeCost, cost, fixType, fixFile, orient);
        Class<?> graphClass;
        DataflowGraph graph = null;
        try {
            graphClass = Class.forName(benchmark);
            graph = (DataflowGraph) graphClass.newInstance();
        } catch (Exception e) {
            System.out.println("Falha carregamento do dataflow");
        }
        RGraph rg = graph.getGraph();
        GeneticAlgorithm ga = new GeneticAlgorithm(processorGraph, rg);
        ga.initializeGA(population, heuristic);
        long inicio = System.currentTimeMillis(), tempo;
        try {
            //for(int j =0; j < 200; ++j){
            ga.runGA(Double.valueOf(time));
            //}
        } catch (Exception e) {
            System.out.println("Falha posicionamento");
        }        
        tempo = System.currentTimeMillis() - inicio;
        Utiliter util = new Utiliter();
        ArrayList<String> tempos = new ArrayList<String>();
        String tmp = benchmark.substring(benchmark.lastIndexOf(".") + 1);
        tmp += "\t" + util.tempo(tempo);
        if (population > 1) {
            ga.allocateNodes();
        } else {
            ga.allocateNodesWithoutGenetic();
        }
        XMLToFiles xtf = new XMLToFiles();
        RGraph architecture = xtf.getGraph(individuo, size, toroide, typeCost, cost, fixType, fixFile, orient);
        Dataflow dflow = new Dataflow(rg);
        Posicionamento pos = new Posicionamento();
        pos.posicionamentoDetalhado(architecture, processorGraph.obtemPosicionamento());
        router = new Roteamento(architecture, dflow, Double.valueOf(time));        
        ArrayList<String> print = new ArrayList<String>();
        String header = "Aprov\tComplet\tPTotal\tPMedio\tTempExe";
        print.add("#Limit\t" + header + "\t" + header);
        int i;
        tempo = 0;
        inicio = System.currentTimeMillis();
        for (i = limiteInf; i <= limiteSup; ++i) {
            //for(int j = 0; j < 200; ++j){
            aproveita = router.FPGAPathFinderAproveitandoOPosicionamento(i);
            //}
            tempo += System.currentTimeMillis() - inicio;
            naoAproveita = router.FPGAPathFinderNaoAproveitandoOPosicionamento(i);
            print.add(util.fill(i, 3, 'a') + "\t" + aproveita + "\t" + naoAproveita);
        }
        tmp += "\t" + util.tempo(tempo);
        Route critico = new Route(processorGraph, rg, benchmark, typeCost, cost);
        critico.routeProcessor();
        Pattern critical = new Pattern();
        critical.load(benchmark);
        caminhoCritico = critical.getCriticalPath(critico.arcos);
        tempos.add(tmp);
        util.printList(tempos, "files/temposPath.txt", true);
        util.printList(print, name);
    }

    public int getCaminhoCritico() {
        return caminhoCritico;
    }

    public String getAproveita() {
        return aproveita;
    }

    public String getNaoAproveita() {
        return naoAproveita;
    }
}
