package mapping.route;

import architectures.dataflows.graph.RGraph;
import mapping.Dataflow;
import java.util.ArrayList;
import java.util.Iterator;
import org._3pq.jgrapht.graph.DefaultDirectedWeightedGraph;

public class RepositorioDeRoteamento {

    private final RGraph arquitetura;
    private final DefaultDirectedWeightedGraph processador;
    private final ArrayList<Sinal> listaDeSinais;

    public RepositorioDeRoteamento(RGraph arquitetura, Dataflow dataflow) {
        this.arquitetura = arquitetura;
        processador = criaProcessador();
        listaDeSinais = Sinal.criaSinais(dataflow);
    }

    private DefaultDirectedWeightedGraph criaProcessador() {
        DefaultDirectedWeightedGraph processor = new DefaultDirectedWeightedGraph();
        Iterator<?> i = arquitetura.vertexSet().iterator();
        while (i.hasNext()) {
            processor.addVertex(i.next());
        }
        return processor;
    }

    public RGraph obtemArquitetura() {
        return (RGraph) arquitetura.clone();
    }

    public DefaultDirectedWeightedGraph obtemProcessador() {
        return (DefaultDirectedWeightedGraph) processador.clone();
    }

    public ArrayList<Sinal> obtemListaDeSinais() {
        return listaDeSinais;
    }
}
