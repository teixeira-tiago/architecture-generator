/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

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
import utiliters.XMLToFiles;

/**
 *
 * @author Apolo
 */
public class GeraBlif {

    
    public static void main(String[] args) {
        GeraBlif p = new GeraBlif();
        XMLToFiles x = new XMLToFiles();
        String[] s = x.getBenchmarks("revista.xml"), aux;
        int i;
        for(i = 0; i< s.length;++i){
            aux = s[i].split(":");
            p = new GeraBlif();
            p.load(aux[0]);
            p.generateBlif(aux[1]+".blif", aux[1]);
        }
    }
    
    private HashMap<Node, ArrayList<Edge>> auxOut = new HashMap<Node, ArrayList<Edge>>();
    private HashMap<Node, ArrayList<Edge>> auxIn = new HashMap<Node, ArrayList<Edge>>();
    private TreeMap<String, Double> elementStatistical = new TreeMap<String, Double>();
    private HashMap<Node, Integer> auxOutDegree = new HashMap<Node, Integer>();
    private HashMap<Node, Integer> auxInDegree = new HashMap<Node, Integer>();
    private HashMap<Node, Integer> posNode = new HashMap<Node, Integer>();
    private LinkedList<Node> noPercorrido = new LinkedList<Node>();
    private ArrayList<Node> root = new ArrayList<Node>();
    private ArrayList<Node> sink = new ArrayList<Node>();
    private Utiliter util = new Utiliter();
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


    public void generateBlif(String path, String nome) {
        noPercorrido = new LinkedList<Node>();
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
                if (!tmpBlif.containsKey(target)) {
                    tmpBlif.put(target.name, tmpAux);
                } else {
                    tmpAux = tmpBlif.get(target);
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
}
