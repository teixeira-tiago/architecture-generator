package architectures.dataflows.extractor;

import architectures.dataflows.graph.Node;
import architectures.dataflows.graph.RGraph;
import architectures.dataflows.javagraphs.DataflowGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import org._3pq.jgrapht.Edge;
import utiliters.Utiliter;

public class Pattern {

    private TreeMap<String, TreeMap<String, Integer>> elementStatisticalDetailed = new TreeMap<String, TreeMap<String, Integer>>();
    private HashMap<Node, ArrayList<Edge>> caminhoPercorrido = new HashMap<Node, ArrayList<Edge>>();
    private HashMap<Node, ArrayList<Edge>> auxOut = new HashMap<Node, ArrayList<Edge>>();
    private HashMap<Node, ArrayList<Edge>> auxIn = new HashMap<Node, ArrayList<Edge>>();
    private TreeMap<String, Double> elementStatistical = new TreeMap<String, Double>();
    private HashMap<String, Integer> edgeStatistical = new HashMap<String, Integer>();
    private TreeMap<Double, Double> hopStatistical = new TreeMap<Double, Double>();
    private TreeMap<String, Integer> tmpDetailed = new TreeMap<String, Integer>();
    private HashMap<Node, Integer> auxOutDegree = new HashMap<Node, Integer>();
    private HashMap<Node, Integer> auxInDegree = new HashMap<Node, Integer>();
    private HashMap<Node, Integer> posNode = new HashMap<Node, Integer>();
    private LinkedList<Integer> auxCont = new LinkedList<Integer>();
    private LinkedList<Node> noPercorrido = new LinkedList<Node>();
    private ArrayList<String> weightArc = new ArrayList<String>();
    private ArrayList<String> print = new ArrayList<String>();
    private ArrayList<Arco> caminho = new ArrayList<Arco>();
    private ArrayList<Node> root = new ArrayList<Node>();
    private ArrayList<Node> sink = new ArrayList<Node>();
    private String keyNode,  keyElementRoot,  keyElement;
    private Utiliter util = new Utiliter();
    private Arco tmpCaminho = new Arco();
    private int tmp,  maior = 0,  cont2;
    private Node atual,  proximo;
    private ArrayList<Edge> aux;
    private RGraph grafic;

    public void load(String nome) {
        DataflowGraph graph = null;
        ArrayList<Edge> tmpList;
        Class<?> graphClass;
        List<?> edges;
        RGraph grafo;
        int i;
        try {
            graphClass = Class.forName(nome);
            graph = (DataflowGraph) graphClass.newInstance();
            grafo = graph.getGraph();
            grafic = grafo;
            ArrayList<String> auxNodeName = new ArrayList<String>();
            Iterator<?> itGrafo = grafo.vertexSet().iterator();
            Object vertice;
            Node auxNode = null;
            String key;
            Double tmpElement;
            while (itGrafo.hasNext()) {
                vertice = itGrafo.next();
                auxNode = (Node) vertice;
                auxOutDegree.put(auxNode, grafo.outDegreeOf(vertice));
                edges = grafo.outgoingEdgesOf(auxNode);
                tmpList = new ArrayList<Edge>();
                for (i = 0; i < edges.size(); ++i) {
                    tmpList.add((Edge) edges.get(i));
                }
                auxOut.put(auxNode, tmpList);
                auxInDegree.put(auxNode, grafo.inDegreeOf(vertice));
                edges = grafo.incomingEdgesOf(auxNode);
                tmpList = new ArrayList<Edge>();
                for (i = 0; i < edges.size(); ++i) {
                    tmpList.add((Edge) edges.get(i));
                }
                auxIn.put(auxNode, tmpList);
                if (grafo.inDegreeOf(vertice) == 0) {
                    root.add(auxNode);
                }
                if (grafo.outDegreeOf(vertice) == 0) {
                    sink.add(auxNode);
                }
                auxNodeName.add(auxNode.name);
                key = auxNode.unit;
                if (!elementStatistical.containsKey(key)) {
                    elementStatistical.put(key, new Double(1));
                } else {
                    tmpElement = elementStatistical.get(key);
                    elementStatistical.put(key, new Double(tmpElement + 1));
                }
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    private void inicializar() {
        caminhoPercorrido = new HashMap<Node, ArrayList<Edge>>();
        noPercorrido = new LinkedList<Node>();
        auxCont = new LinkedList<Integer>();
        tmp = maior = 0;
    }

    public int getASAP() {
        inicializar();
        /** todos os vertices e o numero de pais que eles tem
         * hash auxInDegree com nodo e numero de pais
         * será decrementado toda vez que o nodo for visitado por um pai,
         * ao chegar a zero, insere na fila. */
        int i, pais, asap;
        maior = 0;
        // insere as raizes na fila com indice 0
        for (i = 0; i < root.size(); ++i) {
            posNode.put(root.get(i), 0); // criar um hash nodo,indice
            noPercorrido.add(root.get(i)); // insere na fila
        }
        while (!noPercorrido.isEmpty()) { // fila não vazia
            atual = noPercorrido.remove(); // remove um nodo da fila
            aux = auxOut.get(atual); // pega a lista de filhos
            for (i = 0; i < aux.size(); ++i) { // percorre os filhos
                proximo = (Node) (aux.get(i).getTarget());
                pais = auxInDegree.get(proximo);
                --pais;
                if (pais == 0) { // evitar loops...
                    // ultimo pai que insere o nodo na lista
                    noPercorrido.add(proximo);
                    // insere o proximo com o indice do pai + 1
                    asap = posNode.get(atual) + 1;
                    posNode.put(proximo, asap);
                    if (asap > maior) {
                        maior = asap;
                    }
                }
                // atualiza auxInDegree
                tmpCaminho = new Arco();
                tmpCaminho.arc(atual, proximo);
                caminho.add(tmpCaminho);
                statistic(atual, proximo);
                auxInDegree.put(proximo, pais);
            }
        }
        histograma();
        return maior;
    }

    public int getALAP() {
        inicializar();
        /** todos os vertices e o numero de pais que eles tem
         * hash auxInDegree com nodo e numero de pais
         * será decrementado toda vez que o nodo for visitado por um pai,
         * ao chegar a zero, insere na fila. */
        int i, pais, alap;
        maior = 0;
        // insere as raizes na fila com indice 0
        for (i = 0; i < sink.size(); ++i) {
            posNode.put(sink.get(i), 0); // criar um hash nodo,indice
            noPercorrido.add(sink.get(i)); // insere na fila
        }
        while (!noPercorrido.isEmpty()) { // fila não vazia
            atual = noPercorrido.remove(); // remove um nodo da fila
            aux = auxIn.get(atual); // pega a lista de filhos
            for (i = 0; i < aux.size(); ++i) { // percorre os filhos
                proximo = (Node) (aux.get(i).getSource());
                pais = auxOutDegree.get(proximo);
                --pais;
                if (pais == 0) { // evitar loops...
                    // ultimo pai que insere o nodo na lista
                    noPercorrido.add(proximo);
                    // insere o proximo com o indice do pai + 1
                    alap = posNode.get(atual) + 1;
                    posNode.put(proximo, alap);
                    if (alap > maior) {
                        maior = alap;
                    }
                }
                // atualiza auxInDegree
                tmpCaminho = new Arco();
                tmpCaminho.arc(atual, proximo);
                caminho.add(tmpCaminho);
                statistic(atual, proximo);
                auxOutDegree.put(proximo, pais);
            }
        }
        histograma();
        return maior;
    }

    public int getCriticalPath(HashMap<String, Integer> arcos) {
        /** todos os vertices e o numero de pais que eles tem
         * hash auxInDegree com nodo e numero de pais
         * será decrementado toda vez que o nodo for visitado por um pai,
         * ao chegar a zero, insere na fila. */
        int i, pais, number;
        maior = 0;
        // insere as raizes na fila com indice 0
        for (i = 0; i < root.size(); ++i) {
            posNode.put(root.get(i), 0); // criar um hash nodo,indice
            noPercorrido.add(root.get(i)); // insere na fila
        }
        while (!noPercorrido.isEmpty()) { // fila não vazia
            atual = noPercorrido.remove(); // remove um nodo da fila
            aux = auxOut.get(atual); // pega a lista de filhos
            for (i = 0; i < aux.size(); ++i) { // percorre os filhos
                proximo = (Node) (aux.get(i).getTarget());
                pais = auxInDegree.get(proximo);
                --pais;
                if (pais == 0) { // evitar loops...
                    // ultimo pai que insere o nodo na lista
                    noPercorrido.add(proximo);
                // insere o proximo com o indice do pai + 1
                }
                number = posNode.get(atual) + arcos.get(atual + ":" + proximo);
                if (!posNode.containsKey(proximo)) {
                    posNode.put(proximo, number);
                } else if (posNode.get(proximo) < number) {
                    posNode.put(proximo, number);
                }
                // atualiza auxInDegree
                if (number > maior) {
                    maior = number;
                }
                auxInDegree.put(proximo, pais);
            }
        }
        return maior;
    }

    public void generateBlif(String path, String nome) {
        inicializar();
        int i, pais;
        maior = 0;
        HashMap<String, ArrayList<String>> tmpBlif = new HashMap<String, ArrayList<String>>();
        ArrayList<String> blif = new ArrayList<String>();
        ArrayList<String> auxBlif = new ArrayList<String>();
        ArrayList<String> auxBlif2 = new ArrayList<String>();
        ArrayList<String> auxBlif3 = new ArrayList<String>();
        ArrayList<String> auxBlif4 = new ArrayList<String>();
        ArrayList<String> tmpLink = new ArrayList<String>();
        ArrayList<String> tmpAux = new ArrayList<String>();
        ArrayList<String> tmpAux2 = new ArrayList<String>();
        String input = "", output = "", linha = "";
        String linha2 = "";
        // insere as raizes na fila com indice 0
        for (i = 0; i < root.size(); ++i) {
            posNode.put(root.get(i), 0); // criar um hash nodo,indice
            noPercorrido.add(root.get(i)); // insere na fila
            if (!input.contains(root.get(i).name)) {
                input += root.get(i).name + " ";
            }
        }
        blif.add("# Benchmark " + nome + " written by Ricardo Ferreira and Tiago Teixeira");
        blif.add(".model " + nome);
        blif.add(".inputs " + input);
        blif.add("");
        blif.add("");
        while (!noPercorrido.isEmpty()) { // fila não vazia
            atual = noPercorrido.remove(); // remove um nodo da fila
            tmpAux = new ArrayList<String>();
            tmpAux.add(atual.name);
            aux = auxOut.get(atual); // pega a lista de filhos
            if (aux.isEmpty()) {
                if (!output.contains(atual.name)) {
                    output += " " + atual.name;
                }
            }
            for (i = 0; i < aux.size(); ++i) { // percorre os filhos
                proximo = (Node) (aux.get(i).getTarget());
                if (!tmpBlif.containsKey(proximo.name)) {
                    tmpBlif.put(proximo.name, tmpAux);
                } else {
                    tmpAux = tmpBlif.get(proximo.name);
                    tmpAux.add(atual.name);
                    tmpBlif.put(proximo.name, tmpAux);
                }
//                linha += proximo.name + " ";
                pais = auxInDegree.get(proximo);
                --pais;
                if (pais == 0) {
                    noPercorrido.add(proximo);
                }
                auxInDegree.put(proximo, pais);
            }
        }
        Iterator<String> it = tmpBlif.keySet().iterator();
        String auxLinha;
        while (it.hasNext()) {
            auxLinha = it.next();
            tmpAux = tmpBlif.get(auxLinha);
            linha = ".names ";
            tmpLink = new ArrayList<String>();
            for (i = 0; i < tmpAux.size(); ++i) {
                linha += tmpAux.get(i) + " ";
                linha2 = fill(i, tmpAux.size()) + " 1";
                tmpLink.add(linha2);
            }
            if (!output.contains(auxLinha)) {
                linha += auxLinha;
                auxBlif.add(linha);
                for (i = 0; i < tmpLink.size(); ++i) {
                    auxBlif.add(tmpLink.get(i));
                }
//                blif.add("");
            } else {
                linha += auxLinha;
                tmpAux2.add(linha);
                for (i = 0; i < tmpLink.size(); ++i) {
                    tmpAux2.add(tmpLink.get(i));
                }
//                tmpAux2.add(" ");
            }
        }
        for (i = 0; i < tmpAux2.size(); ++i) {
            auxBlif.add(tmpAux2.get(i));
        }
        int index, j, k;
        String tmp1, tmp2[], tmp3, tmp4[];
        auxBlif2 = auxBlif;
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for (i = 0; i < auxBlif.size(); ++i) {
            tmp1 = auxBlif.get(i);
            if (tmp1.contains(".names ")) {
                index = i;
                tmp2 = tmp1.replace(".names ", "").split(" ");
                auxBlif3 = new ArrayList<String>();
                for (j = 0; j < tmp2.length - 1; ++j) {
                    for (k = 0; k < auxBlif2.size(); ++k) {
                        tmp3 = auxBlif2.get(k);
                        if (tmp3.contains(tmp2[j])) {
                            index = auxBlif2.indexOf(tmp3);
                            indexes.add(index);
                        //auxBlif2.remove(index);
                        }
                    }
                    for (k = 0; k < indexes.size(); ++k) {
                        tmp3 = auxBlif.get(indexes.get(k));

                        tmp4 = tmp3.replace(".names ", "").split(" ");
                        if ((tmp4[tmp4.length - 1].equals(tmp2[j])) && (auxBlif2.indexOf(tmp3) != -1)) {
                            auxBlif3.add(tmp3);
                        } else {
                            auxBlif4.add(tmp3);
                        }
                    }
                    for (k = 0; k < auxBlif3.size(); ++k) {
                        auxBlif4.add(auxBlif3.get(k));
                    }

                }
            }
        }



//        blif.add("");
        blif.set(3, ".outputs" + output);
        blif.add(".end");

        util.printList(blif, path);
    }

    public void generateBlif2(String path, String nome) {
        inicializar();
        Iterator<?> it;
        int i, j;
        Node next;
        List<?> nos;
        Node target;
        HashMap<String, ArrayList<String>> tmpBlif = new HashMap<String, ArrayList<String>>();
        ArrayList<String> blif = new ArrayList<String>();
        ArrayList<String> tmpLink = new ArrayList<String>();
        ArrayList<String> tmpAux = new ArrayList<String>();
        ArrayList<String> tmpAux2 = new ArrayList<String>();
        String input = "", output = "", linha = "";
        String linha2 = "";
        // insere as raizes na fila com indice 0
        for (i = 0; i < root.size(); ++i) {
            posNode.put(root.get(i), 0); // criar um hash nodo,indice
            noPercorrido.add(root.get(i)); // insere na fila
            if (!input.contains(root.get(i).name)) {
                input += root.get(i).name + " ";
            }
        }
        blif.add("# Benchmark " + nome + " written by Ricardo Ferreira and Tiago Teixeira");
        blif.add(".model " + nome);
        blif.add(".inputs " + input);
        blif.add("");
        blif.add("");
        it = grafic.vertexSet().iterator();
        while (it.hasNext()) {
            next = (Node) it.next();
            nos = auxOut.get(next);
            tmpAux = new ArrayList<String>();
            tmpAux.add(next.name);
            if (nos.isEmpty()) {
                if (!output.contains(next.name)) {
                    output += " " + next.name;
                }
            }
            for (j = 0; j < nos.size(); ++j) {
                target = (Node) ((Edge) nos.get(j)).getTarget();
                if (!tmpBlif.containsKey(target.name)) {
                    tmpBlif.put(target.name, tmpAux);
                } else {
                    tmpAux = tmpBlif.get(target.name);
                    tmpAux.add(next.name);
                    tmpBlif.put(target.name, tmpAux);
                }
            }
        }
        it = tmpBlif.keySet().iterator();
        String auxLinha;
        while (it.hasNext()) {
            auxLinha = it.next().toString();
            tmpAux = tmpBlif.get(auxLinha);
            linha = ".names ";
            tmpLink = new ArrayList<String>();
            for (i = 0; i < tmpAux.size(); ++i) {
                linha += tmpAux.get(i) + " ";
                linha2 = fill(i, tmpAux.size()) + " 1";
                tmpLink.add(linha2);
            }
            if (!output.contains(auxLinha)) {
                linha += auxLinha;
                blif.add(linha);
                for (i = 0; i < tmpLink.size(); ++i) {
                    blif.add(tmpLink.get(i));
                }
            } else {
                linha += auxLinha;
                tmpAux2.add(linha);
                for (i = 0; i < tmpLink.size(); ++i) {
                    tmpAux2.add(tmpLink.get(i));
                }
            }
        }
        for (i = 0; i < tmpAux2.size(); ++i) {
            blif.add(tmpAux2.get(i));
        }
        blif.set(3, ".outputs" + output);
        blif.add(".end");
        util.printList(blif, path);
    }

    private String fill(int x, int y) {
        int i;
        String full = "";
        for (i = 0; i < y; ++i) {
            if (i == x) {
                full += "1";
            } else {
                full += "-";
            }
        }
        return full;
    }

    public int getBeadthDes() {
        int i;
//        for (i = 0; i < root.size(); ++i) {
//            inicializar();
//            breadthDes(root.get(i));
//        }
        inicializar();
        breadthDes2();
        histograma();
        return maior;
    }

    private void breadthDes(Node inicio) {
        System.out.println("inicio " + inicio.toString());
        int auxD, degree;
        if (auxOut.size() > 0) {
            aux = auxOut.get(inicio);
            System.out.println("Aux " + aux.toString());
            auxD = degree = aux.size();
            if (auxD > 0) {
                proximo = (Node) aux.get(--degree).getTarget();
                System.out.println("proximo " + proximo.toString());
                aux.remove(degree);
                System.out.println("aux " + aux.toString());
                auxOut.put(inicio, aux);
                tmpCaminho = new Arco();
                tmpCaminho.arc(inicio, proximo);
                caminho.add(tmpCaminho);
                statistic(inicio, proximo);
                if (!posNode.containsKey(inicio)) {
                    posNode.put(inicio, maior);
                }
                System.out.println("posnode " + posNode.toString());
                if (auxD > 1) {
                    caminhoPercorrido.put(inicio, aux);
                    noPercorrido.add(inicio);
                    System.out.println("no percorrido " + noPercorrido.toString());
                    auxCont.add(maior);
                }
                System.out.println("numero de filhos " + auxD);
                auxD = auxInDegree.get(proximo);
                System.out.println("numero de pais " + auxD);
                auxInDegree.put(proximo, --auxD);
                if (auxInDegree.get(proximo) != 0) {
                    if (!noPercorrido.isEmpty()) {
                        System.out.println("entrou no remove nopercorrido");
                        System.out.println("atual antes " + inicio.toString());
                        atual = noPercorrido.removeLast();
                        System.out.println("atual agora " + atual.toString());
                        cont2 = auxCont.removeLast();
                        aux = caminhoPercorrido.get(atual);
                        degree = aux.size();
                        System.out.println("numero de filhos " + degree);
                        proximo = (Node) aux.get(--degree).getTarget();
                        System.out.println("proximo " + proximo);
                        aux.remove(degree);
                        tmpCaminho = new Arco();
                        tmpCaminho.arc(atual, proximo);
                        caminho.add(tmpCaminho);
                        statistic(atual, proximo);
                        if (!posNode.containsKey(atual)) {
                            posNode.put(atual, cont2);
                        }
                        if (sink.contains(proximo)) {
                            posNode.put(proximo, (maior + 1));
                            sink.remove(proximo);
                            return;
                        }
                        System.out.println("entrou no recursivo1");
                        breadthDes(proximo);
                    }
                } else {
                    ++maior;
                    if (sink.contains(proximo)) {
                        posNode.put(proximo, maior);
                        sink.remove(proximo);
                        return;
                    }
                    System.out.println("entrou no recurso2");
                    breadthDes(proximo);
                }
            }
        }
    }

    private void breadthDes2() {
        LinkedList<Node> noPercorrido2 = new LinkedList<Node>();
        Node inicio;
        int i, auxFilho, auxPai, degree, great = 0;
        maior = 0;
        for (i = 0; i < root.size(); ++i) {
            posNode.put(root.get(i), 0); // criar um hash nodo,indice
            noPercorrido2.add(root.get(i)); // insere na fila
        }
        while (!noPercorrido2.isEmpty()) {
            inicio = noPercorrido2.removeLast();
            do {
                aux = auxOut.get(inicio);
                auxFilho = degree = aux.size();
                if (auxFilho > 0) {
                    proximo = (Node) aux.get(--degree).getTarget();
                    aux.remove(degree);
                    auxOut.put(inicio, aux);
                    tmpCaminho = new Arco();
                    tmpCaminho.arc(inicio, proximo);
                    caminho.add(tmpCaminho);
                    statistic(inicio, proximo);
                    if (!posNode.containsKey(proximo)) {
                        if (!posNode.containsKey(inicio)) {
                            posNode.put(inicio, 0);
                            posNode.put(proximo, 1);
                            maior = 1;
                        } else {
                            maior = posNode.get(inicio) + 1;
                            posNode.put(proximo, maior);
                        }
                    }
                    if (maior > great) {
                        great = maior;
                    }
                    if (auxFilho > 1) {
                        caminhoPercorrido.put(inicio, aux);
                        noPercorrido.add(inicio);
                        auxCont.add(maior);
                    }
                    auxPai = auxInDegree.get(proximo);
                    auxInDegree.put(proximo, --auxPai);
                    if ((auxPai > 0) && (!noPercorrido.isEmpty())) {
                        while (!noPercorrido.isEmpty()) {
                            atual = noPercorrido.removeLast();
                            cont2 = auxCont.removeLast();
                            aux = caminhoPercorrido.get(atual);
                            degree = aux.size();
                            if (degree > 0) {
                                proximo = (Node) aux.get(--degree).getTarget();
                                aux.remove(degree);
                                tmpCaminho = new Arco();
                                tmpCaminho.arc(atual, proximo);
                                caminho.add(tmpCaminho);
                                statistic(atual, proximo);
                                if (!posNode.containsKey(atual)) {
                                    posNode.put(atual, cont2);
                                }
                                if (sink.contains(proximo)) {
                                    ++great;
                                    posNode.put(proximo, great);
                                    break;
                                }
                                inicio = proximo;
                            }
                        }
                    } else {
                        if (sink.contains(proximo)) {
                            posNode.put(proximo, maior);
                            break;
                        }
                        inicio = proximo;
                    }
                }
            } while (auxFilho > 0);
        }
        maior = (maior < great) ? great : maior;
    }

    public int getBeadthAsc() {
        int i;
        for (i = 0; i < sink.size(); ++i) {
            inicializar();
            breadthAsc(sink.get(i));
        }
        histograma();
        return maior;
    }

    private void breadthAsc(Node inicio) {
        int auxD, degree;
        if (auxIn.size() > 0) {
            aux = auxIn.get(inicio);
            auxD = degree = aux.size();
            if (auxD > 0) {
                proximo = (Node) aux.get(--degree).getSource();
                aux.remove(degree);
                auxIn.put(inicio, aux);
                tmpCaminho = new Arco();
                tmpCaminho.arc(inicio, proximo);
                caminho.add(tmpCaminho);
                statistic(inicio, proximo);
                if (!posNode.containsKey(inicio)) {
                    posNode.put(inicio, maior);
                }
                if (auxD > 1) {
                    caminhoPercorrido.put(inicio, aux);
                    noPercorrido.add(inicio);
                    auxCont.add(maior);
                }
                auxD = auxOutDegree.get(proximo);
                auxOutDegree.put(proximo, --auxD);
                if (auxOutDegree.get(proximo) != 0) {
                    if (!noPercorrido.isEmpty()) {
                        atual = noPercorrido.removeLast();
                        cont2 = auxCont.removeLast();
                        aux = caminhoPercorrido.get(atual);
                        degree = aux.size();
                        proximo = (Node) aux.get(--degree).getSource();
                        aux.remove(degree);
                        tmpCaminho = new Arco();
                        tmpCaminho.arc(atual, proximo);
                        caminho.add(tmpCaminho);
                        statistic(atual, proximo);
                        if (!posNode.containsKey(atual)) {
                            posNode.put(atual, cont2);
                        }
                        if (root.contains(proximo)) {
                            posNode.put(proximo, (maior + 1));
                            root.remove(proximo);
                            return;
                        }
                        breadthAsc(proximo);
                    }
                } else {
                    ++maior;
                    if (root.contains(proximo)) {
                        posNode.put(proximo, maior);
                        root.remove(proximo);
                        return;
                    }
                    breadthAsc(proximo);
                }
            }
        }
    }

    private void statistic(Node inicio, Node fim) {
        keyElementRoot = inicio.unit;
        keyElement = fim.unit;
        keyNode = inicio.name;
        if (!edgeStatistical.containsKey(keyNode)) {
            edgeStatistical.put(keyNode, 1);
        } else {
            tmp = edgeStatistical.get(keyNode);
            edgeStatistical.put(keyNode, tmp + 1);
        }
        if (!elementStatisticalDetailed.containsKey(keyElementRoot)) {
            tmpDetailed = new TreeMap<String, Integer>();
            tmpDetailed.put(keyElement, 1);
            elementStatisticalDetailed.put(keyElementRoot, tmpDetailed);
        } else {
            tmpDetailed = elementStatisticalDetailed.get(keyElementRoot);
            if (!tmpDetailed.containsKey(keyElement)) {
                tmpDetailed.put(keyElement, 1);
            } else {
                tmp = tmpDetailed.get(keyElement);
                tmpDetailed.put(keyElement, tmp + 1);
            }
            elementStatisticalDetailed.put(keyElementRoot, tmpDetailed);
        }
    }

    private void histograma() {
        Node origem, destino;
        int i;
        Double dist;
        for (i = 0; i < caminho.size(); ++i) {
            tmpCaminho = caminho.get(i);
            origem = tmpCaminho.getOrigem();
            destino = tmpCaminho.getDestino();
            if ((posNode.get(origem) != null) && (posNode.get(destino) != null)) {
                dist = new Double(Math.abs(posNode.get(destino) - posNode.get(origem)));
                dist %= 6;
                dist = (dist == 0) ? 1 : dist;
                weightArc.add(origem + " -> " + destino + ":" + dist);
                print.add(origem + " -> " + destino + " { " + dist + " }");
                if (!hopStatistical.containsKey(dist)) {
                    hopStatistical.put(dist, new Double(1));
                } else {
                    hopStatistical.put(dist, hopStatistical.get(dist) + 1);
                }
            }
        }
    }

    public ArrayList<String> getPrintedArcs() {
        return print;
    }

    public ArrayList<String> getWeightArcs() {
        return weightArc;
    }

    public TreeMap<Double, Double> getHopStatistical() {
        return hopStatistical;
    }

    public TreeMap<String, Double> getElementStatistical() {
        return elementStatistical;
    }

    public TreeMap<String, TreeMap<String, Integer>> getElementStatisticDetailed() {
        return elementStatisticalDetailed;
    }
}
