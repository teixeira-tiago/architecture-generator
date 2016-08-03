package mapping.route;

import mapping.Dataflow;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import architectures.dataflows.graph.Node;
import architectures.dataflows.graph.RGraph;
import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.graph.DefaultDirectedWeightedGraph;

/** 
 * Classe para explicitar as concentracoes de conexoes em arquiteturas em grid
 * 
 * Antes de utilizar essa classe construir a matriz fornecendo como entrada
 * para individuo o gerador.getGraph(com os parametros para a matriz)
 */
public class Hotspot {

    private int altura = 0,  largura = 0,  concentracaoDeConexoes[][];

    public Hotspot(RGraph individuo, DefaultDirectedWeightedGraph processador, Hashtable<?, ?> posicionamentoInverso, Dataflow dataflow) {
        Node fonte, sumidouro;
        ArrayList<Node> auxVertex = new ArrayList<Node>();
        Node[] listaNos = individuo.getSources();
        List<?> nos;
        int indice, auxiliar = 0, i, j;
        for (i = 0; i < listaNos.length; i++) {
            if (!auxVertex.contains(listaNos[i])) {
                auxVertex.add(listaNos[i]);
            }
        }
        for (i = 0; i < auxVertex.size(); ++i) {
            nos = individuo.outgoingEdgesOf(auxVertex.get(i));
            for (j = 0; j < nos.size(); ++j) {
                fonte = (Node) ((Edge) nos.get(j)).getSource();
                indice = fonte.name.indexOf('_');
                auxiliar = Integer.parseInt(fonte.name.substring(0, indice));
                if (auxiliar > altura) {
                    altura = auxiliar;
                }
                auxiliar = Integer.parseInt(fonte.name.substring(indice + 1, fonte.name.length()));
                if (auxiliar > largura) {
                    largura = auxiliar;
                }
                sumidouro = (Node) ((Edge) nos.get(j)).getTarget();
                indice = sumidouro.name.indexOf('_');
                auxiliar = Integer.parseInt(sumidouro.name.substring(0, indice));
                if (auxiliar > altura) {
                    altura = auxiliar;
                }
                auxiliar = Integer.parseInt(sumidouro.name.substring(indice + 1, sumidouro.name.length()));
                if (auxiliar > largura) {
                    largura = auxiliar;
                }
            }
        }
        ++altura;
        ++largura;
        concentracaoDeConexoes = new int[altura][largura];
        preencheConcentracaoDeConexoes(processador, posicionamentoInverso, dataflow);
    }

    private void preencheConcentracaoDeConexoes(DefaultDirectedWeightedGraph processador, Hashtable<?, ?> posicionamentoInverso, Dataflow dataflow) {
        Iterator<?> i = processador.vertexSet().iterator();
        Object o;
        String vertice;
        int grau, linha, coluna, indice;
        while (i.hasNext()) {
            grau = processador.inDegreeOf(o = i.next().toString()) + processador.outDegreeOf(o);
            if (grau > 0) {
                if (dataflow.containsVertex(o)) {
                    vertice = posicionamentoInverso.get(o).toString();
                } else {
                    vertice = o.toString();
                }
                indice = vertice.indexOf('_');
                linha = Integer.parseInt(vertice.substring(0, indice));
                coluna = Integer.parseInt(vertice.substring(indice + 1, vertice.length()));
                concentracaoDeConexoes[linha][coluna] = grau;
            }
        }
    }

    public int[][] obtemConcentracaoDeConexoes() {
        return concentracaoDeConexoes;
    }

    public void exibeConcentracaoDeConexoes() {
        int i, j;
        for (i = 0; i < altura; ++i) {
            for (j = 0; j < largura; ++j) {
                System.out.print("| " + concentracaoDeConexoes[i][j] + " ");
            }
            System.out.println("|");
        }
    }
}
