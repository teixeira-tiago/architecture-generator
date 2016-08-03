package mapping.placement;

import architectures.dataflows.graph.RGraph;
import java.util.Hashtable;
import java.util.Iterator;
import mapping.Dataflow;

public class Posicionamento {

    public void posicionamentoDetalhado(RGraph a, Hashtable<?, ?> posicoes) {
        Iterator<?> i = posicoes.keySet().iterator();
        Object chave;
        while (i.hasNext()) {
            chave = i.next();
            a.substituiVertice(chave, posicoes.get(chave));
        }
    }
    //Funcao que verifica se o posicionamento gerado resulta em condicoes que nao impossibilitem a execucao do roteamento segundo o dataflow fornecido
    public boolean verificaPosicionamento(RGraph a, Dataflow d) {
        Object vertice;
        Iterator<?> i = d.vertexSet().iterator();
        while (i.hasNext()) {
            vertice = i.next();
            if ((d.inDegreeOf(vertice) + d.outDegreeOf(vertice)) > (a.inDegreeOf(vertice) + a.outDegreeOf(vertice))) {
                return false;
            }
        }
        return true;
    }
}
