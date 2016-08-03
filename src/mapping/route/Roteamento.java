package mapping.route;

import architectures.dataflows.graph.RGraph;
import mapping.Dataflow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.graph.DefaultDirectedWeightedGraph;
import utiliters.Utiliter;

public class Roteamento {

    private final double pesoBaseDeVertice = 0.05,  fatorDeCongestionamento = 0.1,  fatorDeHistorico = 0.5;
    private final int ordem;
    private final double tempoMaximoDeExecucao;
    private final Dataflow dataflow;
    private final RepositorioDeRoteamento repositorio;
    private HashMap<Object, Integer> indices;
    private HashMap<Integer, Object> vertices;
    private RGraph arquitetura;
    private Utiliter util = new Utiliter();
    private DefaultDirectedWeightedGraph processador;
    private ArrayList<Sinal> listaDeSinais;
    private int congestionamento[],  historico[];
    @SuppressWarnings("unchecked")
    private LinkedList<LinkedList> caminhosTemporarios,  caminhosPermanentes;
    private boolean sinaisCompletos;

    public Roteamento(RGraph arquitetura, Dataflow dataflow, double tempoMaximoDeExecucao) {
        ordem = arquitetura.vertexSet().size();
        this.tempoMaximoDeExecucao = tempoMaximoDeExecucao;
        this.dataflow = dataflow;
        repositorio = new RepositorioDeRoteamento(arquitetura, dataflow);
        inicializaTabelasDeRelacionamentos(arquitetura);
    }

    private void inicializaTabelasDeRelacionamentos(RGraph arquitetura) {
        Iterator<?> i = arquitetura.vertexSet().iterator();
        int indice = 0;
        Object vertice;
        indices = new HashMap<Object, Integer>();
        vertices = new HashMap<Integer, Object>();
        while (i.hasNext()) {
            indices.put((vertice = i.next()), indice);
            vertices.put(indice++, vertice);
        }
    }

    private int max(int a, int b) {
        if (a > b) {
            return a;
        }
        return b;
    }

    @SuppressWarnings("unchecked")
    private int[] criaCongestionamento(HashMap vertices, int numeroMaximoDeConexoes) {
        int arestas = arquitetura.vertexSet().size(), i;
        int[] congested = new int[arestas];
        Object vertice;
        for (i = 0; i < arestas; ++i) {
            if (dataflow.containsVertex(vertice = vertices.get(i))) {
                congested[i] = -max(numeroMaximoDeConexoes, dataflow.inDegreeOf(vertice) + dataflow.outDegreeOf(vertice));
            } else {
                congested[i] = -numeroMaximoDeConexoes;
            }
        }
        return congested;
    }

    private double obtemPesoDeVertice(int posicao) {
        if (congestionamento[posicao] > 0) {
            return (pesoBaseDeVertice + historico[posicao] * fatorDeHistorico) * (congestionamento[posicao] * fatorDeCongestionamento + 1);
        }
        return (pesoBaseDeVertice + historico[posicao] * fatorDeHistorico);
    }
    //Retorna falso se a rede de sinais nao se encontra congestionada

    private boolean atualizaEAvaliaHistorico() {
        int i = 0;
        for (; i < ordem; ++i) {
            if (congestionamento[i] > 0) {
                ++historico[i];
                for (++i; i < ordem; ++i) {
                    if (congestionamento[i] > 0) {
                        ++historico[i];
                    }
                }
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("empty-statement")
    private LinkedList<Object> obtemMenorCaminho(final int fonte, final int sumidouro) {
        LinkedList<Object> caminho = new LinkedList<Object>();
        try {
            int predecessor[] = new int[ordem];
            double distancia[] = new double[ordem];
            boolean verticeVisitado[] = new boolean[ordem];
            int i, j, k = fonte, cont = 0;
            for (i = 0; i < ordem; ++i) {
                distancia[i] = Double.MAX_VALUE;
            }
            distancia[fonte] = 0;
            Object a, b = null;
            double pesoAtual;
            while (true) {
                for (i = 0; (i < ordem) && (verticeVisitado[i]); ++i);
                j = i; //j contem um vertice nao visitado			

                for (++i; i < ordem; ++i) {
                    if ((!verticeVisitado[i]) && (distancia[i] < distancia[j])) {
                        j = i;	//j contem o vertice de menor distancia nao visitado			

                    }
                }
                if (j == sumidouro) {
                    break; //Caminho completo			

                }
                if (distancia[j] == Double.MAX_VALUE) {
                    ++historico[j];
                    j = k;
                    sinaisCompletos = false;
                    break; //Caminho incompleto por falta de conexoes

                }
                verticeVisitado[j] = true;
                a = vertices.get(j);
                k = j; //k contem o ultimo vertice visitado caso o algoritmo nao consiga encontrar um caminho que conecte a fonte ao sumidouro por falta de conexoes no grafo			

                for (i = 0; i < ordem; ++i) {
                    b = vertices.get(i);
                    if ((!verticeVisitado[i]) && (arquitetura.containsEdge(a, b))) {
                        pesoAtual = distancia[j] + this.obtemPesoDeVertice(j) + this.obtemPesoDeVertice(i) + arquitetura.getEdge(a, b).getWeight();
                        if (pesoAtual < distancia[i]) {
                            distancia[i] = pesoAtual;
                            predecessor[i] = j;
                        }
                    }
                }
            }
            i = j;
            cont = 0;
            while ((i != fonte) && (cont <= vertices.size())) {
                caminho.addFirst(vertices.get(i));
                i = predecessor[i];
                ++cont;
            }
            caminho.addFirst(vertices.get(i)); //Inserir fonte no caminho

        } catch (Exception e) {
            System.out.println("Falha obter caminho no path finder");
            Runtime.getRuntime().gc();
        }
        return caminho;
    }

    /**
     * Reduzir a quantidade de sinais a participarem das negociacoes para o roteamento
     * Essa funcao pode produzir valores inadequados caso os arcos do grafo possuam pesos demasiadamente discrepantes
     **/
    private double conectaSinaisComVerticesVizinhos() {
        Iterator<Sinal> i = listaDeSinais.iterator();
        LinkedList<Sinal> sinaisParaRemocao = new LinkedList<Sinal>();
        Sinal sinal;
        LinkedList<Object> caminhoPermanente;
        Object fonte, sumidouro;
        Edge arco;
        double peso = 0;
        while (i.hasNext()) {
            sinal = i.next();
            fonte = sinal.obtemFonte();
            sumidouro = sinal.obtemSumidouro();
            if (arquitetura.containsEdge(fonte, sumidouro)) {
                sinaisParaRemocao.add(sinal);
                caminhoPermanente = new LinkedList<Object>();
                caminhoPermanente.add(fonte);
                caminhoPermanente.add(sumidouro);
                caminhosPermanentes.add(caminhoPermanente);
                arco = arquitetura.removeEdge(fonte, sumidouro);
                processador.addEdge(arco);
                ++congestionamento[indices.get(fonte)];
                ++congestionamento[indices.get(sumidouro)];
                peso += arco.getWeight();
            }
        }
        i = sinaisParaRemocao.iterator();
        while (i.hasNext()) {
            listaDeSinais.remove(i.next());
        }
        return peso;
    }

    private double conectaSinal(Sinal sinal) {
        final int fonte = indices.get(sinal.obtemFonte()),  sumidouro = indices.get(sinal.obtemSumidouro());
        caminhosTemporarios.add(obtemMenorCaminho(fonte, sumidouro));
        Iterator<?> i = caminhosTemporarios.getLast().iterator();
        Object a = i.next(), b;
        double peso = 0;
        Edge arco;
        ++congestionamento[indices.get(a)];
        while (i.hasNext()) {
            b = i.next();
            arco = arquitetura.removeEdge(a, b);
            if (arco != null) {
                processador.addEdge(arco);
                congestionamento[indices.get(b)] += 2;
                peso += arco.getWeight();
                a = b;
            }
        }
        --congestionamento[indices.get(a)]; //Manter consistencia no congestionamento do ultimo vertice do caminho		

        return peso;
    }

    private void desconectaSinal(LinkedList<?> caminho) {
        Iterator<?> i = caminho.iterator();
        Object a = i.next(), b;
        Edge arco;
        --congestionamento[indices.get(a)];
        while (i.hasNext()) {
            arco = processador.removeEdge(a, b = i.next());
            if (arco != null) {
                arquitetura.addEdge(arco);
                congestionamento[indices.get(b)] -= 2;
                a = b;
            }
        }
        ++congestionamento[indices.get(a)]; //Manter a consistencia do congestionamento do ultimo vertice

    }

    private double conectaTodosOsSinaisIgnorandoCongestionamento() {
        sinaisCompletos = true;
        Iterator<Sinal> i = listaDeSinais.iterator();
        double peso = 0;
        while (i.hasNext()) {
            peso += conectaSinal(i.next());
        }
        return peso;
    }

    private double reconectaTodosOsSinais() {
        sinaisCompletos = true;
        Iterator<Sinal> i = listaDeSinais.iterator();
        double peso = 0;
        while (i.hasNext()) {
            desconectaSinal(caminhosTemporarios.remove());
            peso += conectaSinal(i.next());
        }
        return peso;
    }

    @SuppressWarnings("unchecked")
    private void preparaValoresParaRoteamento(int numeroMaximoDeConexoes) {
        arquitetura = repositorio.obtemArquitetura();
        processador = repositorio.obtemProcessador();
        listaDeSinais = repositorio.obtemListaDeSinais();
        congestionamento = criaCongestionamento(vertices, numeroMaximoDeConexoes);
        historico = new int[ordem];
        caminhosTemporarios = new LinkedList<LinkedList>();
    }

    @SuppressWarnings("unchecked")
    public String FPGAPathFinderAproveitandoOPosicionamento(int numeroMaximoDeConexoes) {
        preparaValoresParaRoteamento(numeroMaximoDeConexoes);
        caminhosPermanentes = new LinkedList<LinkedList>();
        StringBuffer resultado = new StringBuffer("Com\t"); //'A' significa roteamento aproveitando o posicionamento

        boolean roteamentoCompleto = true;
        final Long inicio = System.currentTimeMillis();
        double pesoDosSinaisComVerticesVizinhos = conectaSinaisComVerticesVizinhos(), pesoRestante = conectaTodosOsSinaisIgnorandoCongestionamento(), pesoTotal;
        String tempoDeExecucao;
        while (this.atualizaEAvaliaHistorico() || !sinaisCompletos) {
            if ((System.currentTimeMillis() - inicio) > tempoMaximoDeExecucao) {
                roteamentoCompleto = false;
                break;
            }
            pesoRestante = this.reconectaTodosOsSinais();
        }
        tempoDeExecucao = String.valueOf(System.currentTimeMillis() - inicio);
        if (tempoDeExecucao.length() < 7) {
            tempoDeExecucao = util.fill(Double.valueOf(tempoDeExecucao), 7, 'a');
        } else {
            tempoDeExecucao = tempoDeExecucao.substring(0, 7);
        }
        //Completude
        if (roteamentoCompleto) {
            resultado.append("Compl\t");
        } else {
            resultado.append("InCom\t");
        }
        pesoTotal = pesoDosSinaisComVerticesVizinhos + pesoRestante;
        resultado.append(util.fill(pesoTotal, 5, 'a')).append("\t"); //Peso total
        String tmp = String.valueOf(pesoTotal / dataflow.edgeSet().size());
        if (tmp.length() < 7) {
            tmp = util.fill(Double.valueOf(tmp), 7, 'd');
        } else {
            tmp = tmp.substring(0, 7);
        }
        resultado.append(tmp).append("\t"); //Peso medio por sinal

        resultado.append(tempoDeExecucao); //Tempo de execucao

        return resultado.toString();
    }

    public String FPGAPathFinderNaoAproveitandoOPosicionamento(int numeroMaximoDeConexoes) {
        preparaValoresParaRoteamento(numeroMaximoDeConexoes);
        StringBuffer resultado = new StringBuffer("Sem\t"); //'B' significa roteamento aproveitando o posicionamento

        boolean roteamentoCompleto = true;
        final Long inicio = System.currentTimeMillis();
        double pesoTotal = conectaTodosOsSinaisIgnorandoCongestionamento();
        String tempoDeExecucao;
        while (this.atualizaEAvaliaHistorico() || !sinaisCompletos) {
            if ((System.currentTimeMillis() - inicio) > tempoMaximoDeExecucao) {
                roteamentoCompleto = false;
                break;
            }
            pesoTotal = reconectaTodosOsSinais();
        }
        tempoDeExecucao = String.valueOf(System.currentTimeMillis() - inicio);
        if (tempoDeExecucao.length() < 7) {
            tempoDeExecucao = util.fill(Double.valueOf(tempoDeExecucao), 7, 'a');
        } else {
            tempoDeExecucao = tempoDeExecucao.substring(0, 7);
        }
        //Completude
        if (roteamentoCompleto) {
            resultado.append("Compl\t");
        } else {
            resultado.append("InCom\t");
        }
        resultado.append(util.fill(pesoTotal, 5, 'a')).append("\t"); //Peso total

        String peso = String.valueOf(pesoTotal / dataflow.edgeSet().size());
        if (peso.length() < 7) {
            peso = util.fill(Double.valueOf(peso), 7, 'd');
        } else {
            peso = peso.substring(0, 7);
        }
        resultado.append(peso).append("\t"); //Peso medio por sinal

        resultado.append(tempoDeExecucao); //Tempo de execucao

        return resultado.toString();
    }

    public RepositorioDeRoteamento obtemRepositorio() {
        return repositorio;
    }
}
