package utiliters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
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
import xml.benchmarkclass.Bench;
import xml.benchmarkclass.Benchmarks;
import java.util.Iterator;
import xml.patternclass.COMPONENT;
import xml.patternclass.DESIGN;
import xml.patternclass.PORT;
import xml.patternclass.SIGNAL;
import xml.patternclass.SINK;
import xml.patternclass.SOURCE;
import xml.topologyclass.Colun;
import xml.topologyclass.Element;
import xml.topologyclass.Line;
import xml.topologyclass.Link;
import xml.topologyclass.Topologies;
import architectures.generators.dataflow.dataflowGenerator;
import java.util.Date;
import java.util.TreeMap;

public class FilesToXML {

    private UtilXML utilXML = new UtilXML();
    private dataflowGenerator utilDataflow = new dataflowGenerator();
    private Utiliter util = new Utiliter();

    /**
     * Gerador de individuos homogeneos
     * 
     * @param file
     *            arquivo onde o individuo sera incluido
     * @param line
     *            formato da linha devendo conter
     *            family:style:hop:bit:lenghtString
     * @param colun
     *            formato da coluna devendo conter
     *            family:style:hop:bit:lenghtString
     * @param totalLink
     *            o numero total de ligacoes do individuo
     */
    public void generateHomogeneousIndividual(String file, String line,
            String colun, int totalLink) {
        generateHeterogeneousIndividual(file, line, colun, 100, "", "",
                totalLink);
    }

    /**
     * Gerador de individuos heterogeneos
     * 
     * @param file
     *            arquivo onde o individuo sera incluido
     * @param line1
     *            formato1 da linha devendo conter
     *            family:style:hop:bit:lenghtString
     * @param colun1
     *            formato1 da coluna devendo conter
     *            family:style:hop:bit:lenghtString
     * @param percentLink
     *            porcentagem para o primeiro formato
     * @param line2
     *            formato2 da linha devendo conter
     *            family:style:hop:bit:lenghtString
     * @param colun2
     *            formato2 da coluna devendo conter
     *            family:style:hop:bit:lenghtString
     * @param totalLink
     *            o numero total de ligacoes do individuo
     */
    public void generateHeterogeneousIndividual(String file, String line1,
            String colun1, double percentLink, String line2, String colun2,
            int totalLink) {
        try {
            JAXBContext jc = JAXBContext.newInstance("xml.topologyclass", this.getClass().getClassLoader());
            Unmarshaller u = jc.createUnmarshaller();
            try {
                SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
                Schema schema = sf.newSchema(new File(
                        "xml/schemas/topologia.xsd"));
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
                System.out.println("Nao eh possivel validar o arquivo por causa dos erros abaixo.");
                se.printStackTrace();
            }
            Object poe;
            poe = u.unmarshal(new File(file));
            xml.topologyclass.ObjectFactory fac = new xml.topologyclass.ObjectFactory();

            Topologies topologias = (Topologies) poe;
            List<Element> listaElementos = topologias.getElement();
            Element elementos = fac.createElement();
            elementos.setId(listaElementos.size());
            elementos.setValue(elementos.getValue());
            List<Link> listaLigacoes;
            Link ligacoes = fac.createLink();
            listaLigacoes = elementos.getLink();
            Line linha;
            Colun coluna;
            String[] tmpLine, tmpColun, tmp;
            int part = 0;
            if (percentLink < 100) {
                part = (int) Math.ceil(totalLink * (percentLink / 100));
            } else {
                part = totalLink;
            }
            tmpLine = line1.split(":");
            tmpColun = colun1.split(":");
            String tmpHopL, tmpHopC;
            int cont = 0, i;
            double convert;
            for (i = 0; i < part; ++i) {
                tmp = tmpLine[2].split(";");
                tmpHopL = tmp[i];
                tmp = tmpColun[2].split(";");
                tmpHopC = tmp[i];
                listaLigacoes = elementos.getLink();
                ligacoes = fac.createLink();
                ligacoes.setId(cont);
                linha = fac.createLine();
                linha.setType(tmpLine[1]);
                linha.setFamily(tmpLine[0]);
                convert = Double.valueOf(tmpHopL);
                linha.setHop((int) convert);
                if (!tmpLine[0].equals("Base")) {
                    linha.setBit(Integer.valueOf(tmpLine[3]));
                    linha.setLenghtString(Integer.valueOf(tmpLine[4]));
                }
                coluna = fac.createColun();
                coluna.setType(tmpColun[1]);
                coluna.setFamily(tmpColun[0]);
                convert = Double.valueOf(tmpHopC);
                coluna.setHop((int) convert);
                if (!tmpColun[0].equals("Base")) {
                    coluna.setBit(Integer.valueOf(tmpColun[3]));
                    coluna.setLenghtString(Integer.valueOf(tmpColun[4]));
                }
                ligacoes.setLine(linha);
                ligacoes.setColun(coluna);
                listaLigacoes.add(ligacoes);
                ++cont;
            }
            if (part < totalLink) {
                tmpLine = line2.split(":");
                tmpColun = colun2.split(":");
                for (i = part; i < totalLink; ++i) {
                    tmp = tmpLine[2].split(";");
                    tmpHopL = tmp[i];
                    tmp = tmpColun[2].split(";");
                    tmpHopC = tmp[i];
                    listaLigacoes = elementos.getLink();
                    ligacoes = fac.createLink();
                    ligacoes.setId(cont);
                    linha = fac.createLine();
                    linha.setType(tmpLine[1]);
                    linha.setFamily(tmpLine[0]);
                    convert = Double.valueOf(tmpHopL);
                    linha.setHop((int) convert);
                    if (!tmpLine[0].equals("Base")) {
                        linha.setBit(Integer.valueOf(tmpLine[3]));
                        linha.setLenghtString(Integer.valueOf(tmpLine[4]));
                    }
                    coluna = fac.createColun();
                    coluna.setType(tmpColun[1]);
                    coluna.setFamily(tmpColun[0]);
                    convert = Double.valueOf(tmpHopC);
                    coluna.setHop((int) convert);
                    if (!tmpColun[0].equals("Base")) {
                        coluna.setBit(Integer.valueOf(tmpColun[3]));
                        coluna.setLenghtString(Integer.valueOf(tmpColun[4]));
                    }
                    ligacoes.setLine(linha);
                    ligacoes.setColun(coluna);
                    listaLigacoes.add(ligacoes);
                    ++cont;
                }
            }
            listaElementos.add(elementos);

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(poe, System.out);
            m.marshal(poe, new PrintStream(new File(file)));

        } catch (UnmarshalException ue) {
            System.out.println("Caught UnmarshalException");
        } catch (JAXBException je) {
            je.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void generateHeterogeneousIndividual(String file, String[] line,
            String[] colun, int totalLink) {
        try {
            JAXBContext jc = JAXBContext.newInstance("xml.topologyclass", this.getClass().getClassLoader());
            Unmarshaller u = jc.createUnmarshaller();
            try {
                SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
                Schema schema = sf.newSchema(new File(
                        "xml/schemas/topologia.xsd"));
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
                System.out.println("Nao eh possivel validar o arquivo por causa dos erros abaixo.");
                se.printStackTrace();
            }
            Object poe;
            poe = u.unmarshal(new File(file));

            xml.topologyclass.ObjectFactory fac = new xml.topologyclass.ObjectFactory();

            Topologies topologias = (Topologies) poe;
            List<Element> listaElementos = topologias.getElement();
            Element elementos = fac.createElement();
            elementos.setId(listaElementos.size());
            List<Link> listaLigacoes;
            Link ligacoes = fac.createLink();
            listaLigacoes = elementos.getLink();
            Line linha;
            Colun coluna;
            String[] tmp, tmpLine, tmpColun;
            String tmpHopL, tmpHopC;
            int i;
            for (i = 0; i < totalLink; ++i) {
                tmpLine = line[i].split(":");
                tmp = tmpLine[2].split(";");
                tmpHopL = tmp[i];
                tmpColun = colun[i].split(":");
                tmp = tmpColun[2].split(";");
                tmpHopC = tmp[i];
                listaLigacoes = elementos.getLink();
                ligacoes = fac.createLink();
                ligacoes.setId(i);
                linha = fac.createLine();
                linha.setType(tmpLine[1]);
                linha.setFamily(tmpLine[0]);
                linha.setHop(Integer.valueOf(tmpHopL));
                if (!tmpLine[0].equals("Base")) {
                    linha.setBit(Integer.valueOf(tmpLine[3]));
                    linha.setLenghtString(Integer.valueOf(tmpLine[4]));
                }
                coluna = fac.createColun();
                coluna.setType(tmpColun[1]);
                coluna.setFamily(tmpColun[0]);
                coluna.setHop(Integer.valueOf(tmpHopC));
                if (!tmpColun[0].equals("Base")) {
                    coluna.setBit(Integer.valueOf(tmpColun[3]));
                    coluna.setLenghtString(Integer.valueOf(tmpColun[4]));
                }
                ligacoes.setLine(linha);
                ligacoes.setColun(coluna);
                listaLigacoes.add(ligacoes);
            }
            listaElementos.add(elementos);

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(poe, System.out);
            m.marshal(poe, new PrintStream(new File(file)));

        } catch (UnmarshalException ue) {
            System.out.println("Caught UnmarshalException");
        } catch (JAXBException je) {
            je.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void generateBenchmarkFile(String file, String name,
            String path) {
        try {
            JAXBContext jc = JAXBContext.newInstance("xml.benchmarkclass", this.getClass().getClassLoader());
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
                System.out.println("Nao eh possivel validar o arquivo por causa dos erros abaixo.");
                se.printStackTrace();
            }
            Object poe;
            poe = u.unmarshal(new File(file));

            xml.benchmarkclass.ObjectFactory fac = new xml.benchmarkclass.ObjectFactory();

            String[] tmpSizes = util.getSizesBenchmarks(path).split(":");
            Benchmarks bench = (Benchmarks) poe;
            List<Bench> listaSoftwares = bench.getBench();
            Bench software = fac.createBench();
            software.setId(listaSoftwares.size());
            software.setName(name);
            software.setPath(path);
            software.setWires(Integer.valueOf(tmpSizes[0]));
            software.setNodes(Integer.valueOf(tmpSizes[1]));
            listaSoftwares.add(software);

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(poe, System.out);
            m.marshal(poe, new PrintStream(new File(file)));

        } catch (UnmarshalException ue) {
            System.out.println("Caught UnmarshalException");
        } catch (JAXBException je) {
            je.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
   
    public void convertDOTtoXML(String source) throws FileNotFoundException {
        try {
            ArrayList<String> file = util.loadList(source);
            String tmp, aux;
            String[] edge;
            int cont = 0;
            JAXBContext jcPattern = JAXBContext.newInstance("xml.patternclass",
                    this.getClass().getClassLoader());
            Unmarshaller uPattern = jcPattern.createUnmarshaller();
            try {
                SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
                Schema schemaPattern = sf.newSchema(new File(
                        "xml/schemas/pattern.xsd"));
                uPattern.setSchema(schemaPattern);
                uPattern.setEventHandler(new ValidationEventHandler() {

                    @Override
                    public boolean handleEvent(ValidationEvent ve) {
                        if (ve.getSeverity() != ValidationEvent.WARNING) {
                            ValidationEventLocator vel = ve.getLocator();
                            System.out.println("Padrao -> Linha/Coluna[" + vel.getLineNumber() + ":" + vel.getColumnNumber() + "]:" + ve.getMessage());
                        }
                        return true;
                    }
                });
            } catch (org.xml.sax.SAXException se) {
                System.out.println("Nao eh possivel validar o arquivo por causa dos erros abaixo.");
                se.printStackTrace();
            }
            tmp = file.get(0);
            aux = tmp.substring(tmp.indexOf(" "), tmp.lastIndexOf(" ")).trim();
            String fileName = "files/dataflow/" + aux + ".xml";
            util.limparArquivoXml(fileName, "DESIGN");
            Marshaller mPattern;
            Object objPattern = uPattern.unmarshal(new File(fileName));
            xml.patternclass.ObjectFactory facPattern = new xml.patternclass.ObjectFactory();
            DESIGN desPattern = (DESIGN) objPattern;

            mPattern = jcPattern.createMarshaller();
            mPattern.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            desPattern.setName(aux);
            desPattern.setTrace("true");
            int i;
            for (i = 1; i < file.size() - 1; ++i) {
                List<COMPONENT> listaComP = desPattern.getCOMPONENT();
                COMPONENT comP = facPattern.createCOMPONENT();
                List<SIGNAL> listaSigP = desPattern.getSIGNAL();
                SIGNAL sigP = facPattern.createSIGNAL();
                List<SINK> listaSinP = sigP.getSINK();
                SINK sinP = facPattern.createSINK();
                List<SOURCE> listaSouP = sigP.getSOURCE();
                SOURCE souP = facPattern.createSOURCE();
                tmp = file.get(i);
                tmp = tmp.replaceAll(" ", "").replaceAll("\t", "");
                if (!tmp.contains("{") || !tmp.contains("}") || !tmp.equals("")) {
                    aux = tmp.substring(0, tmp.indexOf("["));
                    if (tmp.contains("label")) {
                        listaComP = desPattern.getCOMPONENT();
                        comP.setName(aux);
                        aux = aux.split("_")[0];
                        aux = (aux.equals("MUL")) ? "MULT" : aux;
                        comP.setOperation(aux);
                        comP.setUnit(aux);
                        listaComP.add(comP);
                    } else if (tmp.contains("name")) {
                        listaSigP = desPattern.getSIGNAL();
                        edge = aux.split("->");
                        souP.setName(edge[0]);
                        listaSouP.add(souP);
                        sinP.setName(edge[1]);
                        listaSinP.add(sinP);
                        sigP.setName("edge_" + cont);
                        listaSigP.add(sigP);
                        ++cont;
                    }
                }
            }
            mPattern.marshal(objPattern, System.out);
            mPattern.marshal(objPattern, new PrintStream(new File(fileName)));
        } catch (UnmarshalException ue) {
            System.out.println("Caught UnmarshalException");
            ue.printStackTrace();
        } catch (JAXBException je) {
            je.printStackTrace();
        }
    }

    public void convertTGFFtoXML(String source, String sink, String nome, boolean close) {
        HashMap<String, String> fins;
        String[] finais = null;
        if (close) {
            fins = utilDataflow.closeGraph(source);
        } else {
            finais = utilDataflow.loadFinals(source);
            fins = utilDataflow.ends(finais);
        }
        String[] componentes = utilDataflow.loadComponent(source);
        String[] sinais = utilDataflow.loadSignal(source);

        try {
            JAXBContext jc = JAXBContext.newInstance(
                    "xml.patternclass", this.getClass().getClassLoader());
            Unmarshaller u = jc.createUnmarshaller();
            try {
                SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
                Schema schema = sf.newSchema(new File(
                        "xml/schemas/pattern.xsd"));
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
            Object obj = u.unmarshal(new File(sink));
            xml.patternclass.ObjectFactory fac = new xml.patternclass.ObjectFactory();
            DESIGN design = (DESIGN) obj;
            List<COMPONENT> listaComponentes = design.getCOMPONENT();
            design.setName(nome);
            COMPONENT componente;
            PORT porta;
            String[] tmp;
            HashMap<String, String> arcos = new HashMap<String, String>();
            int i;
            for (i = 0; i < sinais.length; ++i) {
                tmp = sinais[i].split("#");
                arcos.put(tmp[1], tmp[2]);
            }

            int unit, contPort = 0;
            boolean fim;
            for (i = 0; i < componentes.length; ++i) {
                tmp = componentes[i].split("#");
                componente = fac.createCOMPONENT();
                componente.setName(tmp[0]);
                fim = fins.containsKey(tmp[0]);
                if ((i == 0) || (fim) || (i == (componentes.length - 1))) {
                    componente.setOperation("IO");
                } else {
                    componente.setOperation("ALU");
                }
                if (!fim) {
                    unit = (Integer.valueOf(tmp[1]) % 3);
                    switch (unit) {
                        case 0:
                            componente.setUnit("IMUL");
                            break;
                        case 1:
                            componente.setUnit("IADD");
                            break;
                        case 2:
                            componente.setUnit("COPY");
                            break;
                    }
                } else {
                    tmp = finais[contPort].split("#");
                    if (arcos.containsKey(tmp[1])) {
                        componente.setUnit("ISHR");
                    } else {
                        componente.setUnit("ANA");
                    }
                    porta = fac.createPORT();
                    porta.setName(tmp[0]);
                    porta.setValue(Integer.valueOf(tmp[2]));
                    componente.getPORT().add(porta);
                    ++contPort;
                }
                listaComponentes.add(componente);
            }
            List<SIGNAL> listaSinais = design.getSIGNAL();
            SIGNAL sinal;
            SOURCE fonte;
            SINK destino;
            for (i = 0; i < sinais.length; ++i) {
                tmp = sinais[i].split("#");
                sinal = fac.createSIGNAL();
                sinal.setName(tmp[0]);
                fonte = fac.createSOURCE();
                fonte.setName(tmp[1]);
                fonte.setPort("A" + i);
                destino = fac.createSINK();
                destino.setName(tmp[2]);
                destino.setPort("B" + i);
                sinal.getSOURCE().add(fonte);
                sinal.getSINK().add(destino);
                listaSinais.add(sinal);
            }
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            //m.marshal(obj, System.out);
            m.marshal(obj, new PrintStream(new File(sink)));
        } catch (UnmarshalException ue) {
            System.out.println("Caught UnmarshalException");
            ue.printStackTrace();
        } catch (JAXBException je) {
            je.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> convertHisToTopology(TreeMap<Double, Double> hopStatistical,
            int links, String name, boolean half) {
        return convertHisToTopology(null, null, hopStatistical, links, name, half, false);
    }

    public ArrayList<String> convertHisToTopology(TreeMap<String, TreeMap<String, Integer>> elementStatisticDetailed,
            TreeMap<String, Double> elementStatistical, TreeMap<Double, Double> hopStatistical,
            int links, String name, boolean half) {
        return convertHisToTopology(elementStatisticDetailed, elementStatistical, hopStatistical, links, name, half, true);
    }

    public ArrayList<String> convertHisToTopology(TreeMap<String, TreeMap<String, Integer>> elementStatisticDetailed,
            TreeMap<String, Double> elementStatistical, TreeMap<Double, Double> hopStatistical,
            int links, String name, boolean half, boolean disElements) {
        ArrayList<String> histogram = new ArrayList<String>();
        ArrayList<Integer> quantLink = new ArrayList<Integer>();
        ArrayList<Double> quantHop = new ArrayList<Double>();
        Iterator<Double> it = hopStatistical.keySet().iterator();
        int i;
        Double auxEst, fator = 1.0;
        double[] dist = new double[links];
        double total = 0, tmpEst;
        double tmpHop, auxLink;
        String aux1, aux2;
        histogram.add("-----------------------------");
        histogram.add(name);
        histogram.add("-----------------------------");
        while (it.hasNext()) {
            total += hopStatistical.get(it.next());
        }
        it = hopStatistical.keySet().iterator();
        while (it.hasNext()) {
            tmpHop = it.next();
            if (tmpHop != 0) {
                auxEst = hopStatistical.get(tmpHop);
                tmpEst = (auxEst / total);
                if (half) {
                    if (tmpEst > .5) {
                        fator = 1 - tmpEst;
                        tmpEst = .5;
                    } else {
                        if (tmpHop != 1) {
                            tmpEst = ((tmpEst / fator) / 2);
                        }
                    }
                }
                auxLink = (tmpEst * links);
                auxLink = (tmpHop != 1) ? Math.ceil(auxLink) : auxLink;
                auxLink = (auxLink < 1.0) ? 1.0 : auxLink;
                quantLink.add((int) auxLink);
                quantHop.add(tmpHop);
                aux1 = String.valueOf(tmpEst * links);
                if (aux1.length() > 5) {
                    aux1 = aux1.substring(0, 5);
                } else {
                    aux1 = util.fill(aux1, "0", 5, 'd');
                }
                aux2 = String.valueOf(tmpEst * 100);
                if (aux2.length() > 5) {
                    aux2 = aux2.substring(0, 5);
                } else {
                    aux2 = util.fill(aux2, "0", 5, 'd');
                }
                histogram.add("Hop: " + tmpHop + " proportion: " + aux1 + " / " + aux2 + "%");
            }
        }
        /* Para priorizar os maiores hops hopStatistic tem que ser hashMap
         * para uma distribuicao mais igualitaria tem que tirar esse comentario
        int proportion;
        for (i = 0; i < links; ++i) {
        if (!keyHop.isEmpty()) {

        proportion = i % keyHop.size();
        dist[i] = keyHop.get(proportion);
        if (quantLink.get(proportion) > 1) {
        quantLink.set(proportion, quantLink.get(proportion) - 1);
        } else {
        keyHop.remove(proportion);
        quantLink.remove(proportion);
        } 

        } else {
        dist[i] = 1;
        }
        } */
        int cont = 0, inicio = 0, fim = 0, quant;
        while (cont < quantLink.size()) {
            quant = quantLink.get(cont);
            fim += quant;
            for (i = inicio; (i < fim) && (i < links); ++i) {
                dist[i] = (quantHop.get(cont) < 1.0) ? quantHop.get(1) : quantHop.get(cont);
            }
            inicio += quant;
            ++cont;
        }
        String line = "", colun = "";
        String prefix = "Base:HOP:";
        String sufix = ":0:0";
        for (i = 0; i < links; ++i) {
            switch (i % 8) {
                case 0:
                    line += "0;";
                    colun += dist[i] + ";";
                    break;
                case 1:
                    line += dist[i] + ";";
                    colun += "0;";
                    break;
                case 2:
                    line += "0;";
                    colun += (-1 * dist[i]) + ";";
                    break;
                case 3:
                    line += (-1 * dist[i]) + ";";
                    colun += "0;";
                    break;
                case 4:
                    line += dist[i] + ";";
                    colun += dist[i] + ";";
                    break;
                case 5:
                    line += dist[i] + ";";
                    colun += (-1 * dist[i]) + ";";
                    break;
                case 6:
                    line += (-1 * dist[i]) + ";";
                    colun += (-1 * dist[i]) + ";";
                    break;
                case 7:
                    line += (-1 * dist[i]) + ";";
                    colun += dist[i] + ";";
                    break;
            }
        }
        line = line.substring(0, (line.lastIndexOf(';')));
        colun = colun.substring(0, (colun.lastIndexOf(';')));
        generateHomogeneousIndividual(name + ".xml", prefix + line + sufix, prefix + colun + sufix, links);
        histogram.add("");
        histogram.add("Total edges: " + total);
        histogram.add("-----------------------------");
        for (i = 0; i < links; ++i) {
            histogram.add("Edges Distribution: " + dist[i]);
        }
        if (!disElements) {
            histogram.add("");
            Date data = new Date();
            histogram.add(data.toString());
            util.printList(histogram, name + ".txt");
            return histogram;
        }
        histogram.add("");
        histogram.add("-----------------------------");
        histogram.add("Elements distribution");
        histogram.add("-----------------------------");
        histogram.add("");
        TreeMap<String, Integer> tmpDetailed = new TreeMap<String, Integer>();
        Iterator<String> itElement = elementStatistical.keySet().iterator();
        Iterator<String> itDetailed;
        String tmp, tmp3, aux;
        Double tmp2;
        Integer tmp4;
        while (itElement.hasNext()) {
            tmp = itElement.next();
            tmp2 = elementStatistical.get(tmp);
            aux = String.valueOf((Double.valueOf(tmp2) / total) * 100);
            if (aux.length() > 5) {
                aux = aux.substring(0, 5);
            } else {
                aux = util.fill(aux, "0", 5, 'd');
            }
            histogram.add(tmp + " -> " + tmp2 + " / " + aux + "%");
            tmpDetailed = elementStatisticDetailed.get(tmp);
            if (tmpDetailed != null) {
                itDetailed = tmpDetailed.keySet().iterator();
                while (itDetailed.hasNext()) {
                    tmp3 = itDetailed.next();
                    tmp4 = tmpDetailed.get(tmp3);
                    aux = String.valueOf((Double.valueOf(tmp4) / total/*Double.valueOf(tmp2)*/) * 100);
                    if (aux.length() > 5) {
                        aux = aux.substring(0, 5);
                    } else {
                        aux = util.fill(aux, "0", 5, 'd');
                    }
                    histogram.add("\t" + tmp3 + " -> " + tmp4 + " / " + aux + "%");
                }
            }
        }
        histogram.add("");
        Date data = new Date();
        histogram.add(data.toString());
        histogram.add("");
        util.printList(histogram, name + ".txt");
        return histogram;
    }
}
