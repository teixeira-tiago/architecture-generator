package utiliters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org._3pq.jgrapht.edge.DirectedWeightedEdge;
import xml.benchmarkclass.Bench;
import xml.benchmarkclass.Benchmarks;
import architectures.generators.list.ListaNosAggregate;
import architectures.dataflows.graph.RGraph;
import java.util.TreeMap;
import xml.topologyclass.Colun;
import xml.topologyclass.Element;
import xml.topologyclass.Line;
import xml.topologyclass.Link;
import xml.topologyclass.Topologies;

public class XMLToFiles {

    private String raiz;
    private ListaNosAggregate list;
    private UtilXML utilXML = new UtilXML();
    private TreeMap<String, String> nameToFunction = new TreeMap<String, String>();
    private TreeMap<String, String> functionToName = new TreeMap<String, String>();

    /**
     * Carrega a lista de elementos da populacao existente no arquivo xml
     * 
     * @param target
     *            nome do arquivo xml que contem a populacao
     * @return retorna a lista de elementos membros da populacao
     */
    public List<Element> getPopulation(String name) {
        List<Element> population = null;
        try {
            JAXBContext jcTopology = JAXBContext.newInstance(
                    "xml.topologyclass", this.getClass().getClassLoader());
            Unmarshaller uTopology = jcTopology.createUnmarshaller();
            try {
                SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
                Schema schemaTopology = sf.newSchema(new File(
                        "xml/schemas/topologia.xsd"));
                uTopology.setSchema(schemaTopology);
                uTopology.setEventHandler(new ValidationEventHandler() {

                    @Override
                    public boolean handleEvent(ValidationEvent ve) {
                        if (ve.getSeverity() != ValidationEvent.WARNING) {
                            ValidationEventLocator vel = ve.getLocator();
                            System.out.println("Topologia -> Linha/Coluna[" + vel.getLineNumber() + ":" + vel.getColumnNumber() + "]:" + ve.getMessage());
                        }
                        return true;
                    }
                });
            } catch (org.xml.sax.SAXException se) {
                System.out.println("Nao foi possivel validar o arquivo por causa dos erros abaixo.");
                se.printStackTrace();
            }
            Object objTopology = uTopology.unmarshal(new File(name));
            Topologies topologias = (Topologies) objTopology;
            population = topologias.getElement();
        } catch (UnmarshalException ue) {
            System.out.println("Caught UnmarshalException");
            ue.printStackTrace();
        } catch (JAXBException je) {
            je.printStackTrace();
        }
        return population;
    }

    public String[] getBenchmarks(String name, String graphPart) {
        String[] result = null;
        try {
            JAXBContext jc = JAXBContext.newInstance(
                    "xml.benchmarkclass", this.getClass().getClassLoader());
            Unmarshaller u = jc.createUnmarshaller();
            try {
                SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
                Schema schema = sf.newSchema(new File(
                        "xml/schemas/softwares.xsd"));
                u.setSchema(schema);
                u.setEventHandler(new ValidationEventHandler() {

                    @Override
                    public boolean handleEvent(ValidationEvent ve) {
                        if (ve.getSeverity() != ValidationEvent.WARNING) {
                            ValidationEventLocator vel = ve.getLocator();
                            System.out.println("Linha/Coluna[" + vel.getLineNumber() + ":" + vel.getColumnNumber() + "]:" + ve.getMessage());
                        }
                        return true;
                    }
                });
            } catch (org.xml.sax.SAXException se) {
                System.out.println("Nao foi possivel validar o arquivo por causa dos erros abaixo.");
                se.printStackTrace();
            }
            int i;
            Object obj = u.unmarshal(new File(name));
            Benchmarks softwares = (Benchmarks) obj;
            List<Bench> listaSoftwares = softwares.getBench();
            result = new String[listaSoftwares.size()];
            for (i = 0; i < result.length; ++i) {
                result[i] = listaSoftwares.get(i).getPath();
                result[i] += ":" + listaSoftwares.get(i).getName();
                if (graphPart.equals("nodes")) {
                    result[i] += ":" + listaSoftwares.get(i).getNodes();
                } else {
                    result[i] += ":" + listaSoftwares.get(i).getWires();
                }
            }
        } catch (UnmarshalException ue) {
            System.out.println("Caught UnmarshalException");
            ue.printStackTrace();
        } catch (JAXBException je) {
            je.printStackTrace();
        }
        return result;
    }

    public String[] getBenchmarks(String name) {
        String[] result = null;
        try {
            JAXBContext jc = JAXBContext.newInstance(
                    "xml.benchmarkclass", this.getClass().getClassLoader());
            Unmarshaller u = jc.createUnmarshaller();
            try {
                SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
                Schema schema = sf.newSchema(new File(
                        "xml/schemas/softwares.xsd"));
                u.setSchema(schema);
                u.setEventHandler(new ValidationEventHandler() {

                    @Override
                    public boolean handleEvent(ValidationEvent ve) {
                        if (ve.getSeverity() != ValidationEvent.WARNING) {
                            ValidationEventLocator vel = ve.getLocator();
                            System.out.println("Linha/Coluna[" + vel.getLineNumber() + ":" + vel.getColumnNumber() + "]:" + ve.getMessage());
                        }
                        return true;
                    }
                });
            } catch (org.xml.sax.SAXException se) {
                System.out.println("Nao foi possivel validar o arquivo por causa dos erros abaixo.");
                se.printStackTrace();
            }
            int i;
            Object obj = u.unmarshal(new File(name));
            Benchmarks softwares = (Benchmarks) obj;
            List<Bench> listaSoftwares = softwares.getBench();
            result = new String[listaSoftwares.size()];
            for (i = 0; i < result.length; ++i) {
                result[i] = listaSoftwares.get(i).getPath();
                result[i] += ":" + listaSoftwares.get(i).getName();
                result[i] += ":" + listaSoftwares.get(i).getNodes();
                result[i] += ":" + listaSoftwares.get(i).getWires();
            }
        } catch (UnmarshalException ue) {
            System.out.println("Caught UnmarshalException");
            ue.printStackTrace();
        } catch (JAXBException je) {
            je.printStackTrace();
        }
        return result;
    }

    public RGraph getGraph(Element individuo, int side, int toroide, int typeCost, double cost, int fixType, String fixFile, String orient) {
        RGraph graph = new RGraph();
        List<Link> listaLigacoes;
        int[] linha_coluna;
        int linha, coluna, end, linked, i, j, lig, k;
        Link ligacoes;
        Colun colunas;
        int custo;
        Line linhas;
        String nome, ij;
        List<Link> fixedLigacoes;
        for (i = 0; i < side; ++i) {
            for (j = 0; j < side; ++j) {
                listaLigacoes = individuo.getLink();
                ij = i + "_" + j;
                graph.addVertex(ij);
                end = listaLigacoes.size();
                for (lig = 0; lig < end; ++lig) {
                    ligacoes = listaLigacoes.get(lig);
                    linhas = ligacoes.getLine();
                    colunas = ligacoes.getColun();
                    linha_coluna = utilXML.toroide(toroide, utilXML.getLine(linhas, i),
                            utilXML.getColun(colunas, j), side);
                    linha = linha_coluna[0];
                    coluna = linha_coluna[1];
                    linha = (linha < 0) ? (linha * -1) : linha;
                    coluna = (coluna < 0) ? (coluna * -1) : coluna;
                    nome = linha + "_" + coluna;
                    custo = (int) utilXML.calcCost(i, j, linha, coluna, typeCost);
                    graph.addVertex(nome);
                    graph.addEdge(new DirectedWeightedEdge(ij, nome,
                            custo));
                    if (orient.equals("--")) {
                        graph.addEdge(new DirectedWeightedEdge(nome, ij,
                                custo));
                    }
                }
                if (!fixFile.equals("")) {
                    fixedLigacoes = getPopulation(fixFile).get(0).getLink();
                    linked = fixedLigacoes.size();
                    if (listaLigacoes.size() < 12) {
                        for (k = 0; k < linked; ++k) {
                            if ((k % 2) == fixType) {
                                ligacoes = fixedLigacoes.get(k);
                                linhas = ligacoes.getLine();
                                colunas = ligacoes.getColun();
                                linha_coluna = utilXML.toroide(toroide,
                                        (linhas.getHop() + i), (colunas.getHop() + j),
                                        side);
                                linha = linha_coluna[0];
                                coluna = linha_coluna[1];
                                linha = (linha < 0) ? (linha * -1) : linha;
                                coluna = (coluna < 0) ? (coluna * -1) : coluna;
                                nome = linha + "_" + coluna;
                                custo = (int) utilXML.calcCost(i, j, linha, coluna, typeCost);
                                graph.addVertex(nome);
                                graph.addEdge(new DirectedWeightedEdge(ij, nome, custo));
                                if (orient.equals("--")) {
                                    graph.addEdge(new DirectedWeightedEdge(nome, ij, custo));
                                }
                            }
                        }
                    }
                }
            }
        }
        return graph;
    }

    // pode apagar depois que o cacau ver o resultado
    public RGraph getGraph2(Element individuo, int side, int toroide, int typeCost, double cost, int fixType, String fixFile, String orient) {
        RGraph graph = new RGraph();
        List<Link> listaLigacoes;
        int[] linha_coluna;
        int linha, coluna, end, i, j, lig;
        Link ligacoes;
        Colun colunas;
        int custo;
        Line linhas;
        String nome, ij;
        for (i = 0; i < side; ++i) {
            for (j = 0; j < side; ++j) {
                listaLigacoes = individuo.getLink();
                ij = i + "_" + j;
                graph.addVertex(ij);
                end = listaLigacoes.size();
                for (lig = 0; lig < end; ++lig) {
                    ligacoes = listaLigacoes.get(lig);
                    linhas = ligacoes.getLine();
                    colunas = ligacoes.getColun();
                    linha_coluna = utilXML.toroide(toroide, utilXML.getLine(linhas, i),
                            utilXML.getColun(colunas, j), side);
                    linha = linha_coluna[0];
                    coluna = linha_coluna[1];
                    linha = (linha < 0) ? (linha * -1) : linha;
                    coluna = (coluna < 0) ? (coluna * -1) : coluna;
                    nome = linha + "_" + coluna;
                    custo = (int) utilXML.calcCost(i, j, linha, coluna, typeCost);
                    graph.addVertex(nome);
                    graph.addEdge(new DirectedWeightedEdge(ij, nome,
                            custo));
                    if (orient.equals("--")) {
                        graph.addEdge(new DirectedWeightedEdge(nome, ij,
                                custo));
                    }
                }
                if ((j % 2) != 0){
                    linha_coluna = utilXML.toroide(toroide, (i + 1), (j - 1), side);
                    linha = linha_coluna[0];
                    coluna = linha_coluna[1];
                    linha = (linha < 0) ? (linha * -1) : linha;
                    coluna = (coluna < 0) ? (coluna * -1) : coluna;
                    nome = linha + "_" + coluna;
                    custo = (int) utilXML.calcCost(i, j, linha, coluna, typeCost);
                    graph.addVertex(nome);
                    graph.addEdge(new DirectedWeightedEdge(ij, nome,
                            custo));
                    if (orient.equals("--")) {
                        graph.addEdge(new DirectedWeightedEdge(nome, ij,
                                custo));
                    }
                    linha_coluna = utilXML.toroide(toroide, (i + 1), (j + 1), side);
                    linha = linha_coluna[0];
                    coluna = linha_coluna[1];
                    linha = (linha < 0) ? (linha * -1) : linha;
                    coluna = (coluna < 0) ? (coluna * -1) : coluna;
                    nome = linha + "_" + coluna;
                    custo = (int) utilXML.calcCost(i, j, linha, coluna, typeCost);
                    graph.addVertex(nome);
                    graph.addEdge(new DirectedWeightedEdge(ij, nome,
                            custo));
                    if (orient.equals("--")) {
                        graph.addEdge(new DirectedWeightedEdge(nome, ij,
                                custo));
                    }
                } else {
                    linha_coluna = utilXML.toroide(toroide, (i - 1), (j - 1), side);
                    linha = linha_coluna[0];
                    coluna = linha_coluna[1];
                    linha = (linha < 0) ? (linha * -1) : linha;
                    coluna = (coluna < 0) ? (coluna * -1) : coluna;
                    nome = linha + "_" + coluna;
                    custo = (int) utilXML.calcCost(i, j, linha, coluna, typeCost);
                    graph.addVertex(nome);
                    graph.addEdge(new DirectedWeightedEdge(ij, nome,
                            custo));
                    if (orient.equals("--")) {
                        graph.addEdge(new DirectedWeightedEdge(nome, ij,
                                custo));
                    }
                    linha_coluna = utilXML.toroide(toroide, (i - 1), (j + 1), side);
                    linha = linha_coluna[0];
                    coluna = linha_coluna[1];
                    linha = (linha < 0) ? (linha * -1) : linha;
                    coluna = (coluna < 0) ? (coluna * -1) : coluna;
                    nome = linha + "_" + coluna;
                    custo = (int) utilXML.calcCost(i, j, linha, coluna, typeCost);
                    graph.addVertex(nome);
                    graph.addEdge(new DirectedWeightedEdge(ij, nome,
                            custo));
                    if (orient.equals("--")) {
                        graph.addEdge(new DirectedWeightedEdge(nome, ij,
                                custo));
                    }
                }
            }
        }
        return graph;
    }
    
    public ListaNosAggregate getList(Element individuo, int side, int toroide,
            String arco, int typeCost, double cost, int fixType, String fixFile) {
        list = new ListaNosAggregate();
        utilXML.gerarNos(side, arco);
        List<Link> listaLigacoes;
        List<Link> fixedLigacoes;
        Link ligacoes;
        Line linhas;
        Colun colunas;
        int[] linha_coluna;
        double custo;
        int linha, coluna, linked;
        int i, j, lig, k;
        for (i = 0; i < side; ++i) {
            for (j = 0; j < side; ++j) {
                listaLigacoes = individuo.getLink();
                for (lig = 0; lig < listaLigacoes.size(); ++lig) {
                    ligacoes = listaLigacoes.get(lig);
                    linhas = ligacoes.getLine();
                    colunas = ligacoes.getColun();
                    linha_coluna = utilXML.toroide(toroide, utilXML.getLine(linhas, i),
                            utilXML.getColun(colunas, j), side);
                    linha = linha_coluna[0];
                    coluna = linha_coluna[1];
                    linha = (linha < 0) ? (linha * -1) : linha;
                    coluna = (coluna < 0) ? (coluna * -1) : coluna;
                    custo = utilXML.calcCost(i, j, linha, coluna, typeCost, cost);
                    utilXML.insertLine(i, j, linha, coluna, side, custo, arco);
                }
                if (!fixFile.equals("")) {
                    fixedLigacoes = getPopulation(fixFile).get(0).getLink();
                    linked = fixedLigacoes.size();
                    if (listaLigacoes.size() < 12) {
                        for (k = 0; k < linked; ++k) {
                            if ((k % 2) == fixType) {
                                ligacoes = fixedLigacoes.get(k);
                                linhas = ligacoes.getLine();
                                colunas = ligacoes.getColun();
                                linha_coluna = utilXML.toroide(toroide,
                                        (linhas.getHop() + i), (colunas.getHop() + j),
                                        side);
                                linha = linha_coluna[0];
                                coluna = linha_coluna[1];
                                linha = (linha < 0) ? (linha * -1) : linha;
                                coluna = (coluna < 0) ? (coluna * -1) : coluna;
                                utilXML.insertLine(i, j, linha, coluna, side, utilXML.calcCost(i, j, linha, coluna, typeCost, cost), arco);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    public ListaNosAggregate getList(Element individuo, int side, int Line, int Colun, int toroide,
            String arco, int typeCost, double cost, int fixType, String fixFile) {
        list = new ListaNosAggregate();
        utilXML.setList(list);
        utilXML.gerarNos(side, Line, Colun, arco);
        List<Link> listaLigacoes;
        Link ligacoes;
        Line linhas;
        Colun colunas;
        int[] linha_coluna;
        double custo;
        int linha, coluna, linked;
        int i, j, lig, k;
        List<Link> fixedLigacoes;
        for (i = 0; i < side; ++i) {
            for (j = 0; j < side; ++j) {
                if ((i == Line) && (j == Colun)) {
                    listaLigacoes = individuo.getLink();
                    for (lig = 0; lig < listaLigacoes.size(); ++lig) {
                        ligacoes = listaLigacoes.get(lig);
                        linhas = ligacoes.getLine();
                        colunas = ligacoes.getColun();
                        linha_coluna = utilXML.toroide(toroide, utilXML.getLine(linhas, i),
                                utilXML.getColun(colunas, j), side);
                        linha = linha_coluna[0];
                        coluna = linha_coluna[1];
                        linha = (linha < 0) ? (linha * -1) : linha;
                        coluna = (coluna < 0) ? (coluna * -1) : coluna;
                        custo = utilXML.calcCost(i, j, linha, coluna, typeCost, cost);
                        utilXML.insertLine(i, j, linha, coluna, side, custo, arco);
                    }
                    if (!fixFile.equals("")) {
                        fixedLigacoes = getPopulation(fixFile).get(0).getLink();
                        linked = fixedLigacoes.size();
                        if (listaLigacoes.size() < 12) {
                            for (k = 0; k < linked; ++k) {
                                if ((k % 2) == fixType) {
                                    ligacoes = fixedLigacoes.get(k);
                                    linhas = ligacoes.getLine();
                                    colunas = ligacoes.getColun();
                                    linha_coluna = utilXML.toroide(toroide,
                                            (linhas.getHop() + i), (colunas.getHop() + j),
                                            side);
                                    linha = linha_coluna[0];
                                    coluna = linha_coluna[1];
                                    linha = (linha < 0) ? (linha * -1) : linha;
                                    coluna = (coluna < 0) ? (coluna * -1) : coluna;
                                    utilXML.insertLine(i, j, linha, coluna, side, utilXML.calcCost(i, j, linha, coluna, typeCost, cost), arco);
                                }
                            }
                        }
                    }
                }
            }
        }
        return list = utilXML.getList();
    }

    public void generateFile(Element elemento, String name) {
        try {
            JAXBContext jcTopology = JAXBContext.newInstance(
                    "xml.topologyclass", this.getClass().getClassLoader());
            Unmarshaller uTopology = jcTopology.createUnmarshaller();
            try {
                SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
                Schema schemaTopology = sf.newSchema(new File(
                        "xml/schemas/topologia.xsd"));
                uTopology.setSchema(schemaTopology);
                uTopology.setEventHandler(new ValidationEventHandler() {

                    @Override
                    public boolean handleEvent(ValidationEvent ve) {
                        if (ve.getSeverity() != ValidationEvent.WARNING) {
                            ValidationEventLocator vel = ve.getLocator();
                            System.out.println("Topologia -> Linha/Coluna[" + vel.getLineNumber() + ":" + vel.getColumnNumber() + "]:" + ve.getMessage());
                        }
                        return true;
                    }
                });
            } catch (org.xml.sax.SAXException se) {
                System.out.println("Nao eh possivel validar o arquivo por causa dos erros abaixo.");
                se.printStackTrace();
            }
            Object objTopology = uTopology.unmarshal(new File("files/xml/" + name + ".xml"));
            Topologies topologias = (Topologies) objTopology;
            List<Element> listaElementos = topologias.getElement();
            listaElementos.add(elemento);
            Marshaller mTopology = jcTopology.createMarshaller();
            mTopology.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
//            if (name.contains("ior")){
//                mTopology.marshal(objTopology, new PrintStream(new File(
//                    "files/xml/pior/" + name + ".xml")));
//            } else if (name.contains("10")){
//                mTopology.marshal(objTopology, new PrintStream(new File(
//                    "files/xml/dez/" + name + ".xml")));
//            } else if (name.contains("ulti")){
//                mTopology.marshal(objTopology, new PrintStream(new File(
//                    "files/xml/utlima/" + name + ".xml")));
//            } else {
                mTopology.marshal(objTopology, new PrintStream(new File(
                    "files/xml/" + name + ".xml")));
//            }
        // mTopology.marshal(objTopology, System.out);
        } catch (UnmarshalException ue) {
            System.out.println("Caught UnmarshalException");
            ue.printStackTrace();
        } catch (JAXBException je) {
            je.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public TreeMap<String, String> getNameToFunction() {
        return nameToFunction;
    }

    public TreeMap<String, String> getFunctionToName() {
        return functionToName;
    }

    public String getRoot() {
        return raiz;
    }
}
