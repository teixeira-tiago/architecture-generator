package heuristicas;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import xml.topologyclass.Element;
import utiliters.XMLToFiles;
import java.util.Random;
import mapping.placement.Placement;
import mapping.placement.ProcessorGraph;
import mapping.route.PathFinder;
import utiliters.Utiliter;
import xml.topologyclass.Colun;
import xml.topologyclass.Line;
import xml.topologyclass.Link;

public class Avaliation {

    private xml.topologyclass.ObjectFactory fac = new xml.topologyclass.ObjectFactory();
    private ArrayList<String> printTime = new ArrayList<String>();
    private ArrayList<String> printInd = new ArrayList<String>();
    private ArrayList<String> printCri = new ArrayList<String>();
    private ArrayList<String> print = new ArrayList<String>();
    private Element worseInd = fac.createElement();
    private Element bestInd = fac.createElement();
    private ProcessorGraph[] bestProcessorGraph;
    private double tmpWorse = Double.MIN_VALUE;
    private double tmpBest = Double.MAX_VALUE;
    private double worse = Double.MIN_VALUE;
    private ProcessorGraph[] processorGraph;
    private double best = Double.MAX_VALUE;
    private Utiliter util = new Utiliter();
    private Element[] bestXInd = null;
    private String[] benchmark;
    private String format = "";
    private double[] bestX;
    private int heuristica;
    private int population;
    private String fixFile;
    private String orient;
    private int algorithm;
    private int downLimit;
    private int total = 0;
    private int typeCost;
    private int typeAlgo;
    private double nulo = .0;
    private int upLimit;
    private int toroide;
    private int fixType;
    private double cost;
    private int size;
    private int time;

    public Avaliation(int toroide, String[] benchmark, int heuristica,
            int time, int population, int typeCost, double cost, int fixType,
            String fixFile, int algorithm, int typeAlgo, int upLimit, int downLimit,
            String orient) {
        this.population = population;
        this.heuristica = heuristica;
        this.algorithm = algorithm;
        this.benchmark = benchmark;
        this.downLimit = downLimit;
        this.typeAlgo = typeAlgo;
        this.typeCost = typeCost;
        this.toroide = toroide;
        this.fixType = fixType;
        this.fixFile = fixFile;
        this.upLimit = upLimit;
        this.orient = orient;
        this.time = time;
        this.cost = cost;
        size = benchmark.length;
        processorGraph = new ProcessorGraph[size];
        bestProcessorGraph = new ProcessorGraph[size];
    }

    public void store(int index) {
        bestXInd = new Element[index];
        int i;
        for (i = 0; i < index; ++i) {
            bestXInd[i] = fac.createElement();
        }
        bestX = new double[index];
        for (i = 0; i < index; ++i) {
            bestX[i] = Double.MAX_VALUE;
        }
    }

    public void start() {
        String info;
        info = "info.generator:0#info.individual:0";
        info += "#info.value:0";
        info += "#info.partialBest:" + Double.MAX_VALUE;
        info += "#info.partialWorse:" + Double.MIN_VALUE;
        info += "#info.finalBest:" + Double.MAX_VALUE;
        info += "#info.finalWorse:" + Double.MIN_VALUE;
        info += "#info.new:true";
        util.setProperties("info.properties", info);
    }

    public void persists(String name) {
        util.printList(printInd, name + ".txt");
    }

    public ArrayList<String> getPrintTime() {
        return printTime;
    }

    public ArrayList<String> getPrintBench() {
        return printInd;
    }

    public ArrayList<String> getPrintCritical() {
        return printCri;
    }

    public void persists(String nameBest, String nameWorse, String namePrint) {
        int tam = (int) Math.ceil(Math.log(print.size()) / Math.log(10));
        int aux, max, i, j;
        String[] formato = new String[3];
        String[] fim = new String[3];
        fim[0] = "#General best: " + best + "\tUltimate best: " + tmpBest;
        fim[1] = "#General worse: " + worse + "\tUltimate worse: " + tmpWorse;
        fim[2] = "#Total values: " + total + "\tNull values: " + (int) nulo;
        XMLToFiles xtf = new XMLToFiles();
        xtf.generateFile(bestInd, nameBest);
        xtf.generateFile(worseInd, nameWorse);
        if (nulo > .0) {
            fim[2] += "\tNulls: " + ((nulo / total) * 100) + "%";
        } else {
            fim[2] += "\tNulls: 0.00%";
        }
        double value;
        String[] valores;
        aux = 0;
        for(i = 0; i<print.size(); ++i){
            formato[1] = print.get(i).replace("\t", "");
            if (aux < formato[1].length()){
                aux = formato[1].length();
            }
        }
        for (i = 0; i < print.size(); ++i) {
            formato[0] = "";
            formato[1] = print.get(i).replace("\t", "");
            max = (aux - formato[1].length());
            if (max > 0) {
                max = max / 18;
                for (j = 0; j < max; ++j) {
                    formato[0] += util.fill("NULL", "-", 11, 'a') + util.fill("-", "-", 7, 'd') + "\t";
                }
            } else {
                formato[0] = "";
            }
            print.set(i, print.get(i) + formato[0]);
        }
        for (i = 0; i < print.size(); ++i) {
            valores = print.get(i).split("\t");
            tmpBest = Double.MAX_VALUE;
            tmpWorse = Double.MIN_VALUE;
            for (j = 0; j < valores.length; ++j) {
                if (!valores[j].contains("-")) {
                    value = Double.valueOf(valores[j]);
                    if ((value < tmpBest) && (value > 1)) {
                        tmpBest = value;
                    }
                    if (value > tmpWorse) {
                        tmpWorse = value;
                    }
                }
            }
            formato[0] = util.fill(String.valueOf(i), "0", tam, 'a') + "\t";
            formato[1] = util.fill(String.valueOf(tmpBest), "0", 18, 'd') + "\t";
            formato[2] = util.fill(String.valueOf(tmpWorse), "0", 18, 'd');
            print.set(i, formato[0] + print.get(i) + formato[1] + formato[2]);
        }
        print.add("");
        print.add(fim[0]);
        print.add(fim[1]);
        print.add(fim[2]);
        util.printList(print, namePrint + ".txt");
    }

    public void position(String name) {
        Hashtable<String, String> posProcessor = new Hashtable<String, String>();
        ArrayList<String> printPos;
        String[] auxBench;
        String nameBench, auxPrint = "", auxC = "", aux, nome;
        int side, tam, maxTam = 0, i, j, k;

        for (i = 0; i < bestProcessorGraph.length; ++i) {
            if (bestProcessorGraph[i] != null) {
                posProcessor = bestProcessorGraph[i].obtemPosicionamento();
                printPos = new ArrayList<String>();
                printPos.clear();
                auxBench = benchmark[i].split(":");
                nameBench = auxBench[1];
                maxTam = 0;
                side = (int) Math.ceil(Math.sqrt(Integer.valueOf(auxBench[2])));
                for (j = 0; j < side; ++j) {
                    for (k = 0; k < side; ++k) {
                        aux = posProcessor.get(j + "_" + k);
                        if (aux != null) {
                            if (aux.length() > maxTam) {
                                maxTam = aux.length();
                            }
                        }
                    }
                }
                tam = maxTam + 1;
                for (j = 0; j < side; ++j) {
                    for (k = 0; k < side; ++k) {
                        if (posProcessor.get(j + "_" + k) != null) {
                            auxPrint += "| " + util.fill(posProcessor.get(j + "_" + k), " ", tam, 'd');
                        } else {
                            auxPrint += "| " + util.fill("", " ", tam, 'd');
                        }
                        if (j == 0) {
                            auxC += "|-" + util.fill("", "-", tam, 'd');
                        }
                    }
                    if (j == 0) {
                        printPos.add(auxC + "-|");
                    }
                    printPos.add(auxPrint + " |");
                    printPos.add(auxC + "-|");
                    auxPrint = "";
                }
                nome = "files/positioned/" + name + "_" + nameBench + ".txt";
                util.limparArquivo(nome);
                util.printList(printPos, nome);
                auxC = "";
            }
        }
    }

    public void persistsX(String nameX) {
        XMLToFiles xtf = new XMLToFiles();
        int i;
        for (i = 0; i < bestX.length; ++i) {
            xtf.generateFile(bestXInd[i], nameX);
        }
    }

    public double calc(Element individuo) {
        switch (algorithm) {
            case 0:
                return calcDijsktraHomogeneous(individuo);
            case 1:
                return calcPathFinderHomogeneous(individuo);
            default:
                return Double.MAX_VALUE;
        }
    }

    public Element best() {
        return bestInd;
    }

    public Element perturb(Element individual) {
        Element result = fac.createElement();
        Link link = fac.createLink();
        Colun coluna = fac.createColun();
        Line linha = fac.createLine();
        Random randOpt, randLink;
        randLink = new Random();
        int index = Math.abs(randLink.nextInt() % individual.getLink().size());
        link = individual.getLink().get(index);
        coluna = link.getColun();
        linha = link.getLine();
        randOpt = new Random();
        int opt = Math.abs(randOpt.nextInt() % 3);
        switch (opt) {
            case 0:
                if (!coluna.getType().equals("HOP")) {
                    result = perturbBit(individual, index, coluna, 0);
                } else {
                    result = perturbHop(individual, index, coluna, 0);
                }
                break;
            case 1:
                if (!linha.getType().equals("HOP")) {
                    result = perturbBit(individual, index, linha, 1);
                } else {
                    result = perturbHop(individual, index, linha, 1);
                }
                break;
            case 2:
                Element resultAUX = fac.createElement();
                if (!coluna.getType().equals("HOP")) {
                    resultAUX = perturbBit(individual, index, coluna, 0);
                } else {
                    resultAUX = perturbHop(individual, index, coluna, 0);
                }
                if (!linha.getType().equals("HOP")) {
                    result = perturbBit(resultAUX, index, linha, 1);
                } else {
                    result = perturbHop(resultAUX, index, linha, 1);
                }
                break;
        }
        return result;
    }

    private Element perturbHop(Element individual, int index, Object direct, int type) {
        Link link = fac.createLink();
        link = individual.getLink().get(index);
        Random randHop;
        if (type == 0) {
            Colun coluna = (Colun) direct;
            randHop = new Random();
            coluna.setHop(randHop.nextInt() % 5);
            link.setColun(coluna);
            individual.getLink().set(index, link);
        } else {
            Line linha = (Line) direct;
            randHop = new Random();
            linha.setHop(randHop.nextInt() % 5);
            link.setLine(linha);
            individual.getLink().set(index, link);
        }
        return individual;
    }

    private Element perturbBit(Element individual, int index, Object direct, int type) {
        Link link = fac.createLink();
        link = individual.getLink().get(index);
        Random randBit, randOpt;
        randOpt = new Random();
        int opt = Math.abs(randOpt.nextInt() % 2);
        if (opt == 0) {
            if (type == 0) {
                Colun coluna = (Colun) direct;
                randBit = new Random();
                coluna.setBit(randBit.nextInt() % 4);
                link.setColun(coluna);
                individual.getLink().set(index, link);
            } else {
                Line linha = (Line) direct;
                randBit = new Random();
                linha.setBit(randBit.nextInt() % 4);
                link.setLine(linha);
                individual.getLink().set(index, link);
            }
        } else {
            return perturbHop(individual, index, direct, type);
        }
        return individual;
    }

    private double calcDijsktraHomogeneous(Element individuo) {
        double value = .0;
        double medium = .0, totals = .0;
        Placement m;
        String bench, tempo;
        int valid = 0, i;
        printTime.clear();
        printInd.clear();
        printCri.clear();
        long min;
        util.limparArquivo("files/temposGenetic.txt");
        ArrayList<String> tempos = new ArrayList<String>();
        tempos.add("Benchmark\tPlacement\tRoute");
        util.printList(tempos, "files/temposGenetic.txt");
        for (i = 0; i < size; ++i) {
            m = new Placement();
            bench = benchmark[i].split(":")[1];
            min = System.currentTimeMillis();
            m.homogeneous(individuo, toroide, benchmark[i], heuristica, time,
                    population, typeCost, cost, fixType, fixFile, orient);
            min = System.currentTimeMillis() - min;
            tempo = util.tempo(min);
            System.out.println("Tempo de geracao: " + tempo);
            System.out.println("-----------------------------");
            if (m.media > .0) {
                totals += m.total;
                medium += m.media;
                ++valid;
                processorGraph[i] = m.processorGraph;
                printTime.add(bench + "\t" + tempo);
                printInd.add(bench + "\t" + m.media + "\t" + m.total);
                printCri.add(bench + "\t" + m.critical);
            } else {
                printInd.add(bench + "\tNULL\tNULL");
                printTime.add(bench + "\tNULL");
                printCri.add(bench + "\tNULL");
            }
        }
        printInd.add("");
        printInd.add("Total\t" + totals);
        if (valid > 0) {
            printInd.add("Media\t" + (medium / valid));
        }
        if (valid == size) {
            if (typeAlgo != 0) {
                value = totals;
            } else {
                value = (medium / valid);
            }
            individuo.setValue(value);
            compare(individuo);
            value *= -1;
        } else {
            individuo.setValue(.0);
            compare(individuo);
            value = -0.0;
        }
        return value;
    }

    private double calcPathFinderHomogeneous(Element individuo) {
        double value = .0;
        String[] tmpBench, values;
        String bench, tempo;
        int length, valid = 0, i;
        double medium = .0, totals = .0, media, sum;
        printInd.clear();
        PathFinder path = new PathFinder();
        long min;
        util.limparArquivo("files/temposPath.txt");
        ArrayList<String> tempos = new ArrayList<String>();
        tempos.add("Benchmark\tPlacement\tRoute");
        util.printList(tempos, "files/temposPath.txt");
        for (i = 0; i < size; ++i) {
            tmpBench = benchmark[i].split(":");
            bench = tmpBench[1];
            length = (int) Math.ceil(Math.sqrt(Integer.valueOf(tmpBench[2])));
            try {
                min = System.currentTimeMillis();
                path.calculate(individuo, tmpBench[0], population, heuristica, toroide,
                        time, downLimit, upLimit, length, "files/path/path_" +
                        tmpBench[1] + ".txt", typeCost, cost, fixType, fixFile, orient);
                min = System.currentTimeMillis() - min;
                tempo = util.tempo(min);
                values = path.getAproveita().split("\t");
                sum = Double.valueOf(values[2]);
                media = Double.valueOf(values[3]);
                System.out.println("-----------------------------");
                System.out.println("Benchmark: " + bench);
                System.out.println("Total segmentos: " + sum);
                System.out.println("Media segmentos: " + media);
                System.out.println("Tempo de geracao: " + tempo);
                System.out.println("-----------------------------");
                System.out.println("");
                if (Double.valueOf(values[3]) > .0) {
                    totals += sum;
                    medium += media;
                    ++valid;
                    printTime.add(bench + "\t" + tempo);
                    printCri.add(bench + "\t" + path.getCaminhoCritico());
                    printInd.add(bench + "\t" + media + "\t" + sum);
                } else {
                    printInd.add(bench + "\tNULL\tNULL");
                    printCri.add(bench + "\tNULL");
                    printTime.add(bench + "\tNULL");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Falha!!!!");
            }
        }
        printInd.add("");
        printInd.add("Total\t" + totals);
        if (valid > 0) {
            printInd.add("Media\t" + (medium / valid));
        }
        if (valid == size) {
            if (typeAlgo != 0) {
                value = totals;
            } else {
                value = (medium / valid);
            }
            individuo.setValue(value);
            compare(individuo);
            value *= -1;
        } else {
            individuo.setValue(.0);
            compare(individuo);
            value = -0.0;
        }
        return value;
    }

    private void compare(Element individuo) {
        Properties prop = util.getProperties("info.properties");
        double value = individuo.getValue();
        if (Boolean.valueOf(prop.getProperty("info.new"))) {
            tmpBest = Double.MAX_VALUE;
            tmpWorse = Double.MIN_VALUE;
            if (!format.equals("")) {
                print.add(format);
            }
            format = "";
            util.setProperties("info.properties", "info.new:false");
            if (Boolean.valueOf(prop.getProperty("info.zero"))) {
                util.setProperties("info.properties", "info.individual:0");
            }
        } else {
            tmpBest = Double.valueOf(prop.getProperty("info.partialBest"));
            tmpWorse = Double.valueOf(prop.getProperty("info.partialWorse"));
        }
        util.setProperties("info.properties", "info.value:" + value);
        if (value != .0) {
            if (value < tmpBest) {
                tmpBest = value;
                util.setProperties("info.properties", "info.partialBest:" + tmpBest);
            }
            if (value > tmpWorse) {
                tmpWorse = value;
                util.setProperties("info.properties", "info.partialWorse:" + tmpWorse);
            }
            if ((bestX != null) && (bestX.length > 0)) {
                compareX(individuo);
            }
            int i;
            if (value < best) {
                best = value;
                bestInd.setId(individuo.getId());
                bestInd.setValue(individuo.getValue());
                for (i = 0; i < individuo.getLink().size(); ++i) {
                    if (bestInd.getLink().size() < individuo.getLink().size()) {
                        bestInd.getLink().add(individuo.getLink().get(i));
                    } else {
                        bestInd.getLink().set(i, individuo.getLink().get(i));
                    }
                }
                bestProcessorGraph = processorGraph;
                util.setProperties("info.properties", "info.finalBest:" + best);
            }
            if (value > worse) {
                worse = value;
                worseInd.setId(individuo.getId());
                worseInd.setValue(individuo.getValue());
                for (i = 0; i < individuo.getLink().size(); ++i) {
                    if (worseInd.getLink().size() < individuo.getLink().size()) {
                        worseInd.getLink().add(individuo.getLink().get(i));
                    } else {
                        worseInd.getLink().set(i, individuo.getLink().get(i));
                    }
                }
                util.setProperties("info.properties", "info.finalWorse:" + worse);
            }
            format += util.fill(String.valueOf(value), "0", 18, 'd') + "\t";
        } else {
            format += util.fill("NULL", "-", 11, 'a') + util.fill("-", "-", 7, 'd') + "\t";
            ++nulo;
        }
        ++total;
    }

    private void compareX(Element individuo) {
        double value = individuo.getValue();
        int i, j;
        if (value != .0) {
            if (bestX[0] >= value) {
                bestX[0] = value;
                bestXInd[0].setId(individuo.getId());
                bestXInd[0].setValue(individuo.getValue());
                for (j = 0; j < individuo.getLink().size(); ++j) {
                    if (bestXInd[0].getLink().size() < individuo.getLink().size()) {
                        bestXInd[0].getLink().add(individuo.getLink().get(j));
                    } else {
                        bestXInd[0].getLink().set(j, individuo.getLink().get(j));
                    }
                }
            }
            for (i = 1; i < bestX.length; ++i) {
                if ((value > bestX[i - 1]) && (bestX[i] >= value)) {
                    bestX[i] = value;
                    bestXInd[i].setId(individuo.getId());
                    bestXInd[i].setValue(individuo.getValue());
                    for (j = 0; j < individuo.getLink().size(); ++j) {
                        if (bestXInd[i].getLink().size() < individuo.getLink().size()) {
                            bestXInd[i].getLink().add(individuo.getLink().get(j));
                        } else {
                            bestXInd[i].getLink().set(j, individuo.getLink().get(j));
                        }
                    }
                }
            }
        }
    }
}
