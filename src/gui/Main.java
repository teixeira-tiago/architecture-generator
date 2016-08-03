/*
 * Main.java
 *
 * Created on 22 de Dezembro de 2007, 15:59
 */
package gui;

/**
 *
 * @author  Thiagus
 */
public class Main extends javax.swing.JFrame {

    /** Creates new form Main */
    public Main() {
        java.io.File dir = new java.io.File("files/dot/.");
        dir.mkdirs();
        dir = new java.io.File("files/xml/.");
        dir.mkdirs();
        dir = new java.io.File("files/xml/pior/.");
        dir.mkdirs();
        dir = new java.io.File("files/xml/ultima/.");
        dir.mkdirs();
        dir = new java.io.File("files/xml/input/.");
        dir.mkdirs();
        dir = new java.io.File("files/xml/dez/.");
        dir.mkdirs();
        dir = new java.io.File("files/benchmarks/.");
        dir.mkdirs();
        dir = new java.io.File("files/extract/.");
        dir.mkdirs();
        dir = new java.io.File("files/path/.");
        dir.mkdirs();
        dir = new java.io.File("files/dataflow/.");
        dir.mkdirs();
        dir = new java.io.File("files/mapping/.");
        dir.mkdirs();
        dir = new java.io.File("files/positioned/.");
        dir.mkdirs();
        try {
            if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
                javax.swing.UIManager.setLookAndFeel(laf[0].getClassName());
            } else {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
        }
        initComponents();
    }

    private void genetic(String name, String population, double time, int generations, int crossOver) {
        heuristicas.geneticalgorithm.GeneticAlgorithm ga = new heuristicas.geneticalgorithm.GeneticAlgorithm(name);
        ga.generatePopulation(avaliate, population);
        ga.initializeGA();
        try {
            ga.runGA(time, generations, crossOver);
        } catch (heuristicas.geneticalgorithm.genalgpackage.GenAlgException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void relinking(String name, String population, int totalPop) {
        java.util.List<xml.topologyclass.Element> elementsTemp = XMLToFiles.getPopulation(population);
        java.util.List<xml.topologyclass.Element> elements = new java.util.ArrayList<xml.topologyclass.Element>();
        java.util.Random rand;
        java.util.HashMap<String, Object> used = new java.util.HashMap<String, Object>();
        int index, i;
        for (i = 0; i < totalPop; ++i) {
            rand = new java.util.Random();
            index = Math.abs(rand.nextInt() % elementsTemp.size());
            if (!used.containsKey(String.valueOf(index))) {
                elements.add(elementsTemp.get(index));
                used.put(String.valueOf(index), null);
            } else {
                while (!used.containsKey(String.valueOf(index))) {
                    rand = new java.util.Random();
                    index = (int) Math.round((float) Math.abs(Math.sqrt(rand.nextDouble() * rand.nextFloat())));
                    index = Math.abs(index % elementsTemp.size());
                }
                elements.add(elementsTemp.get(index));
                used.put(String.valueOf(index), null);
            }
        }
        heuristicas.pathrelinking.PathRelinking pr =
                new heuristicas.pathrelinking.PathRelinking(avaliate);
        pr.pathRelinking(elements, name);
    }

    private void annealing(String name, String population, int maxInterations, int typeVariationT, double initialTemp, double finalTemp, double fixedValue) {
        xml.topologyclass.Element element = XMLToFiles.getPopulation(population).get(0);
        heuristicas.simulateannealing.SimulateAnnealing sa =
                new heuristicas.simulateannealing.SimulateAnnealing(avaliate);
        sa.SA(element, maxInterations, typeVariationT, initialTemp, finalTemp, fixedValue, name);
    }

    private java.util.ArrayList<String> getPrintArq(java.util.ArrayList<String> printArq, java.util.ArrayList<String> printArqTemp, String auxArq) {
        String auxFormat;
        String[] auxPrint;
        int i;
        if (printArq.isEmpty()) {
            printArq.add("Benchmarks\t" + auxArq);
        } else {
            printArq.set(0, printArq.get(0) + "\t" + auxArq);
        }
        for (i = 0; i < printArqTemp.size(); ++i) {
            auxFormat = printArqTemp.get(i);
            if (auxFormat.contains(".")) {
                auxFormat = auxFormat.replace(".", ",");
            }
            auxPrint = auxFormat.split("\t");
            if (printArq.size() > (i + 1)) {
                if (auxPrint.length > 2) {
                    printArq.set(i + 1, printArq.get(i + 1) + "\t" + auxPrint[2]);
                } else if (auxPrint.length > 1) {
                    printArq.set(i + 1, printArq.get(i + 1) + "\t" + auxPrint[1]);
                }
            } else {
                if (auxPrint.length > 2) {
                    printArq.add(auxPrint[0] + "\t" + auxPrint[2]);
                } else {
                    printArq.add(auxFormat);
                }
            }
        }
        return printArq;
    }

    private String[] benchmarks() {
        String benchFile = "files/benchmarks/bench.xml";
        util.limparArquivoXml(benchFile, "Benchmarks");
        String[] aux;
        int i;
        if (listSaveBench.getSize() == 0) {
            for (i = 0; i < listBenchFull.size(); ++i) {
                aux = listBenchFull.get(i).toString().split(":");
                filesToXML.generateBenchmarkFile(benchFile, aux[1], aux[0]);
            }
        } else {
            for (i = 0; i < listSaveBench.size(); ++i) {
                aux = listSaveBench.get(i).toString().split(":");
                filesToXML.generateBenchmarkFile(benchFile, aux[1], aux[0]);
            }
        }
        return XMLToFiles.getBenchmarks(benchFile);
    }

    class geneticAlgorithm implements Runnable {

        @Override
        public void run() {
            jButton1.setEnabled(false);
            fim = false;
            long min = System.currentTimeMillis();
            jLabel9.setText("Initial Time: " + util.nowMore(0));
            jLabel10.setText("Final Time: " + util.nowMore(Long.valueOf(jSpinner1.getValue().toString())));
            jLabel29.setText("Total Time:");

            int variationPlacement = jComboBox1.getSelectedIndex();
            int timeMapping = Integer.valueOf(jSpinner14.getValue().toString());
            int popMapping = Integer.valueOf(jSpinner36.getValue().toString());
            int upLimit = Integer.valueOf(jSpinner5.getValue().toString());
            int downLimit = Integer.valueOf(jSpinner6.getValue().toString());

            int crossOver = jComboBox3.getSelectedIndex();
            int toroide = jComboBox2.getSelectedIndex();
            String population = jTextField5.getText();
            double time = Double.valueOf(jSpinner1.getValue().toString());
            int generations = Integer.valueOf(jSpinner2.getValue().toString());
            int algorithmFitness = jComboBox12.getSelectedIndex();
            int typeAlgoFitness = jComboBox15.getSelectedIndex();
            String orient = (jComboBox43.getSelectedIndex() == 0) ? "->" : "--";
            // final manipulate topologies options

            // initial fix options
            int typeCost = jComboBox14.getSelectedIndex(); // sem tratamento

            double cost = .0;
            if (typeCost == 2) {
                cost = Double.valueOf(jSpinner7.getValue().toString());
            }
            int fixType = jComboBox13.getSelectedIndex();
            String fixFile = jTextField8.getText();
            // final fix options

            String[] benchmark = benchmarks();

            // initial genetic algorithmFitness manipulate
            String name = "ga_" + file;
            jLabel75.setVisible(true);
            constGen = 100 / generations;
            jProgressBar1.setVisible(true);
            jProgressBar1.setMinimum(0);
            jProgressBar1.setMaximum(100);
            jProgressBar1.setStringPainted(true);
            jProgressBar1.setValue(0);

            if (jCheckBox12.isSelected()) {
                if (file.contains(".xml")) {
                    util.limparArquivoXml(path + "/" + name, "Topologies");
                } else {
                    util.limparArquivo(path + "/" + name);
                }
            }
            avaliate = new heuristicas.Avaliation(toroide, benchmark,
                    variationPlacement, timeMapping, popMapping, typeCost, cost, fixType,
                    fixFile, algorithmFitness, typeAlgoFitness, upLimit, downLimit, orient);
            heuristicas.geneticalgorithm.GeneticAlgorithm ga = new heuristicas.geneticalgorithm.GeneticAlgorithm(name);
            ga.generatePopulation(avaliate, population);
            ga.initializeGA();
            try {
                ga.runGA(time, generations, crossOver);
            } catch (heuristicas.geneticalgorithm.genalgpackage.GenAlgException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            printTimeTemp = avaliate.getPrintTime();
            printIndTemp = avaliate.getPrintBench();
            printCriTemp = avaliate.getPrintCritical();
            try {
                avaliate.position(name);
            } catch (Exception e) {
            }
            printInd = getPrintArq(printInd, printIndTemp, name);
            printTime = getPrintArq(printTime, printTimeTemp, name);
            printCri = getPrintArq(printCri, printCriTemp, name);
            util.printList(printInd, "files/mapping/genetic.txt");
            util.printList(printTime, "files/mapping/time_genetic.txt");
            util.printList(printCri, "files/mapping/critical_genetic.txt");
            java.util.Properties prop = util.getProperties("info.properties");
            jLabel25.setText("Final Worse Value: " + prop.getProperty("info.finalWorse"));
            jLabel16.setText("Final Best Value: " + prop.getProperty("info.finalBest"));
            jLabel10.setText("Final Time: " + util.nowMore(0));
            jLabel29.setText("Total Time: " + util.tempo(System.currentTimeMillis() - min));

            fim = true;
            Thread play = new Thread(som);
            play.start();
            int end = javax.swing.JOptionPane.showConfirmDialog(null, "Algoritmo Genetico Gerado com Sucesso!", "Concluido", javax.swing.JOptionPane.OK_OPTION);
            if (end != -1) {
                musica.stop();
            }
            jButton1.setEnabled(true);
            capturarTela();
        // final genetic algorithmFitness manipulate
        }
    }

    class pathRelinking implements Runnable {

        @Override
        public void run() {
            jButton15.setEnabled(false);
            jLabel76.setVisible(true);
            jProgressBar2.setVisible(true);
            fim = false;
            long min = System.currentTimeMillis();
            jLabel58.setText("Initial Time: " + util.nowMore(0));

            // initial element manipulate
            int variationPlacement = jComboBox5.getSelectedIndex();
            int toroide = jComboBox4.getSelectedIndex();
            int timeMapping = Integer.valueOf(jSpinner15.getValue().toString());
            int popMapping = Integer.valueOf(jSpinner37.getValue().toString());
            int upLimit = Integer.valueOf(jSpinner15.getValue().toString());
            int downLimit = Integer.valueOf(jSpinner16.getValue().toString());
            int algorithmFitness = jComboBox19.getSelectedIndex();
            int typeAlgoFitness = jComboBox22.getSelectedIndex();
            String elementsFile = jTextField10.getText();
            String orient = (jComboBox44.getSelectedIndex() == 0) ? "->" : "--";
            // final element manipulate

            // initial fix manipulate
            int typeCost = jComboBox24.getSelectedIndex(); // sem tratamento

            double cost = .0;
            if (typeCost == 2) {
                cost = Double.valueOf(jSpinner17.getValue().toString());
            }
            int fixType = jComboBox23.getSelectedIndex();
            String fixFile = jTextField17.getText();
            String name = "pr_" + file;
            // final fix manipulate

            // initial manipulate elements
            java.util.List<xml.topologyclass.Element> elementsTemp = XMLToFiles.getPopulation(elementsFile);
            java.util.List<xml.topologyclass.Element> elements = new java.util.ArrayList<xml.topologyclass.Element>();
            int i;
            if (jRadioButton11.isSelected()) {
                java.util.Random rand;
                java.util.HashMap<String, Object> used = new java.util.HashMap<String, Object>();
                int index;
                for (i = 0; i < Integer.valueOf(jSpinner22.getValue().toString()); ++i) {
                    rand = new java.util.Random();
                    index = Math.abs(rand.nextInt() % elementsTemp.size());
                    if (!used.containsKey(String.valueOf(index))) {
                        elements.add(elementsTemp.get(index));
                        used.put(String.valueOf(index), null);
                    } else {
                        while (!used.containsKey(String.valueOf(index))) {
                            rand = new java.util.Random();
                            index = (int) Math.round((float) Math.abs(Math.sqrt(rand.nextDouble() * rand.nextFloat())));
                            index = Math.abs(index % elementsTemp.size());
                        }
                        elements.add(elementsTemp.get(index));
                        used.put(String.valueOf(index), null);
                    }
                }
            } else if (jRadioButton10.isSelected()) {
                for (i = 0; i < Integer.valueOf(jSpinner21.getValue().toString()); ++i) {
                    elements.add(elementsTemp.get(i));
                }
            } else if (jRadioButton9.isSelected()) {
                elements = elementsTemp;
            }
            int size = elements.size();
            // final manipulate elements

            String[] benchmark = benchmarks();

            // initial path relinking manipulate            
            int total = (size * size) - size;
            constGen = 100 / total;
            jProgressBar2.setMinimum(0);
            jProgressBar2.setMaximum(100);
            jProgressBar2.setStringPainted(true);
            avaliate = new heuristicas.Avaliation(toroide, benchmark,
                    variationPlacement, timeMapping, popMapping, typeCost, cost, fixType,
                    fixFile, algorithmFitness, typeAlgoFitness, upLimit, downLimit, orient);
            heuristicas.pathrelinking.PathRelinking pr =
                    new heuristicas.pathrelinking.PathRelinking(avaliate);
            pr.pathRelinking(elements, name);
            printIndTemp = avaliate.getPrintBench();
            printTimeTemp = avaliate.getPrintTime();
            printCriTemp = avaliate.getPrintCritical();
            try {
                avaliate.position(name);
            } catch (Exception e) {
            }
            printInd = getPrintArq(printInd, printIndTemp, name);
            printTime = getPrintArq(printTime, printTimeTemp, name);
            printCri = getPrintArq(printCri, printCriTemp, name);
            util.printList(printInd, "files/mapping/relinking.txt");
            util.printList(printTime, "files/mapping/time_relinking.txt");
            util.printList(printCri, "files/mapping/critical_relinking.txt");
            java.util.Properties prop = util.getProperties("info.properties");
            jLabel67.setText("Final Worse Value: " + prop.getProperty("info.finalWorse"));
            jLabel66.setText("Final Best Value: " + prop.getProperty("info.finalBest"));
            jLabel59.setText("Final Time: " + util.nowMore(0));
            jLabel60.setText("Total Time: " + util.tempo(System.currentTimeMillis() - min));

            fim = true;
            Thread play = new Thread(som);
            play.start();
            int end = javax.swing.JOptionPane.showConfirmDialog(null, "Path Relinking Concluido com Sucesso!", "Concluido", javax.swing.JOptionPane.OK_OPTION);
            if (end != -1) {
                musica.stop();
            }
            jButton15.setEnabled(true);
            capturarTela();
        // final path relinking manipulate
        }
    }

    class simulatedAnnealing implements Runnable {

        @Override
        public void run() {
            fim = false;
            jButton31.setEnabled(false);
            long min = System.currentTimeMillis();
            jLabel84.setText("Initial Time: " + util.nowMore(0));

            // initial element manipulate
            String orient = (jComboBox45.getSelectedIndex() == 0) ? "->" : "--";
            int timeMapping = Integer.valueOf(jSpinner18.getValue().toString());
            int popMapping = Integer.valueOf(jSpinner39.getValue().toString());
            int upLimit = Integer.valueOf(jSpinner19.getValue().toString());
            int downLimit = Integer.valueOf(jSpinner40.getValue().toString());
            int variationPlacement = jComboBox8.getSelectedIndex();
            int algorithmFitness = jComboBox25.getSelectedIndex();
            int typeAlgoFitness = jComboBox26.getSelectedIndex();
            int toroide = jComboBox7.getSelectedIndex();
            String elementFile = jTextField14.getText();
            // final element manipulate

            // initial fix manipulate
            int typeCost = jComboBox31.getSelectedIndex(); // sem tratamento

            double cost = .0;
            if (typeCost == 2) {
                cost = Double.valueOf(jSpinner24.getValue().toString());
            }
            int fixType = jComboBox28.getSelectedIndex();
            String fixFile = jTextField20.getText();
            String name = "sa_" + file;
            // final fix manipulate

            String[] benchmark = benchmarks();

            // initial temperature manipulate
            int maxInterations = Integer.valueOf(jSpinner28.getValue().toString());
            double initialTemp = Double.valueOf(jSpinner29.getValue().toString());
            double finalTemp = Double.valueOf(jSpinner30.getValue().toString());
            // final temperature manipulate

            // initial type variation temperature manipulate
            int typeVariationT = 0;
            double fixedValue = .0;
            boolean fixed = false;
            if (jRadioButton14.isSelected()) {
                typeVariationT = 0;
            } else if (jRadioButton15.isSelected()) {
                typeVariationT = 1;
            } else if (jRadioButton16.isSelected()) {
                typeVariationT = 2;
            } else if (jRadioButton17.isSelected()) {
                typeVariationT = 3;
                fixedValue = Double.valueOf(jSpinner31.getValue().toString());
                fixed = true;
            }
            // final type variation temperature manipulate

            // initial simulated annealing manipulate
            xml.topologyclass.Element element = XMLToFiles.getPopulation(elementFile).get(0);
            avaliate = new heuristicas.Avaliation(toroide, benchmark,
                    variationPlacement, timeMapping, popMapping, typeCost, cost, fixType,
                    fixFile, algorithmFitness, typeAlgoFitness, upLimit, downLimit, orient);
            heuristicas.simulateannealing.SimulateAnnealing sa =
                    new heuristicas.simulateannealing.SimulateAnnealing(avaliate);
            if (!fixed) {
                sa.SA(element, maxInterations, typeVariationT, initialTemp, finalTemp, name);
            } else {
                sa.SA(element, maxInterations, typeVariationT, initialTemp, finalTemp, fixedValue, name);
            }
            printIndTemp = avaliate.getPrintBench();
            printTimeTemp = avaliate.getPrintTime();
            printCriTemp = avaliate.getPrintCritical();
            try {
                avaliate.position(name);
            } catch (Exception e) {
            }
            printInd = getPrintArq(printInd, printIndTemp, name);
            printTime = getPrintArq(printTime, printTimeTemp, name);
            printCri = getPrintArq(printCri, printCriTemp, name);
            util.printList(printInd, "files/mapping/annealing.txt");
            util.printList(printTime, "files/mapping/time_annealing.txt");
            util.printList(printCri, "files/mapping/critical_annealing.txt");
            java.util.Properties prop = util.getProperties("info.properties");
            jLabel98.setText("Final Worse Value: " + prop.getProperty("info.finalWorse"));
            jLabel97.setText("Final Best Value: " + prop.getProperty("info.finalBest"));
            jLabel85.setText("Final Time: " + util.nowMore(0));
            jLabel86.setText("Total Time: " + util.tempo(System.currentTimeMillis() - min));

            fim = true;
            Thread play = new Thread(som);
            play.start();
            int end = javax.swing.JOptionPane.showConfirmDialog(null, "Simulated Annealing Concluido com Sucesso!", "Concluido", javax.swing.JOptionPane.OK_OPTION);
            if (end != -1) {
                musica.stop();
            }
            jButton31.setEnabled(true);
            capturarTela();
        // final simulated annealing manipulate
        }
    }

    class map implements Runnable {

        @Override
        public void run() {
            jButton23.setEnabled(false);
            fim = false;
            int msn = 0;
            long min = System.currentTimeMillis(), tempo;
            StringBuffer tempoTotal = new StringBuffer();
            jLabel101.setText("Initial Time: " + util.nowMore(0));
            String div, aux[];
            if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
                div = "/";
            } else {
                div = "\\";
            }
//            variaveis genericas
//            int variationPlacement = 3;
//            int timeMapping = 1;
//            int popMapping = 11;
//            int upLimit = 25;
//            int downLimit = 25;
//            int toroide = 1;
//            int algorithmFitness = 0;
//            int typeAlgoFitness = 1;
//            String orient = "->";
//            int typeCost = 0;
//            double cost = .0;
//            int fixType = 0;
//            String fixFile = "";

            // especificas do Algortimo Genetico
            double time = 86400000.0;
            int generations = 100;
            int crossOver;

            // especificas do Simulated Annealing
            int maxInterations = 10;
            double initialTemp = 1000.0;
            double finalTemp = 0.001;
            int typeVariationT = 3;
            double fixedValue = .9;

            // especificas escalonamento
            int nodes = 8;
            architectures.dataflows.extractor.Pattern pattern = new architectures.dataflows.extractor.Pattern();
            java.util.ArrayList<String> print = new java.util.ArrayList<String>();
            String tmp, auxBench[] = XMLToFiles.getBenchmarks("files/benchmarks/grupo_total.xml");
            utiliters.FilesToXML convert = new utiliters.FilesToXML();

            // usar varias variacoes
            int heuristic = 0;
            String name = "";
            String population = jTextField18.getText();

            // initial mapping options
            int variationPlacement = jComboBox10.getSelectedIndex();
            int upLimit = Integer.valueOf(jSpinner11.getValue().toString());
            int downLimit = Integer.valueOf(jSpinner12.getValue().toString());
            // final mapping options

            // initial manipulate topologies options
            int toroide = jComboBox9.getSelectedIndex();
            int timeToMapping = Integer.valueOf(jSpinner20.getValue().toString());
            int popToMapping = Integer.valueOf(jSpinner41.getValue().toString());
            int algorithmFitness = jComboBox18.getSelectedIndex();
            int typeAlgoFitness = jComboBox20.getSelectedIndex();
            String orient = (jComboBox46.getSelectedIndex() == 0) ? "->" : "--";
            // final manipulate topologies options

            // initial fix options
            int typeCost = jComboBox33.getSelectedIndex(); // sem tratamento

            double cost = .0;
            if (typeCost == 2) {
                cost = Double.valueOf(jSpinner25.getValue().toString());
            }
            int fixType = jComboBox32.getSelectedIndex();
            String fixFile = jTextField15.getText();
            // final fix options

            // initial benchmarks manipulation
            String nameBench = jTextField11.getText();
            nameBench = nameBench.substring(nameBench.lastIndexOf(div) + 1, nameBench.indexOf("."));
            String[] benchmark = benchmarks();
            // final benchmarks manipulation

            if (file.contains(".")) {
                file = file.substring(0, file.lastIndexOf("."));
            }
            int i, j;
            for (i = 0; i < listHeuristic.getSize(); ++i) {
                heuristic = Integer.valueOf(String.valueOf(listHeuristic.get(i)));
                switch (heuristic) {
                    case 0:
                        name = "ga_cno_" + file;
                        crossOver = 1;
                        tempo = System.currentTimeMillis();
                        avaliate = new heuristicas.Avaliation(toroide, benchmark,
                                variationPlacement, timeToMapping, popToMapping, typeCost, cost, fixType,
                                fixFile, algorithmFitness, typeAlgoFitness, upLimit, downLimit, orient);
                        genetic(name, population, time, generations, crossOver);
                        tempo = System.currentTimeMillis() - tempo;
                        tempoTotal.append(name + "\t" + util.tempo(tempo) + "#");
                        break;
                    case 1:
                        name = "ga_cpr_" + file;
                        crossOver = 2;
                        tempo = System.currentTimeMillis();
                        avaliate = new heuristicas.Avaliation(toroide, benchmark,
                                variationPlacement, timeToMapping, popToMapping, typeCost, cost, fixType,
                                fixFile, algorithmFitness, typeAlgoFitness, upLimit, downLimit, orient);
                        genetic(name, population, time, generations, crossOver);
                        tempo = System.currentTimeMillis() - tempo;
                        tempoTotal.append(name + "\t" + util.tempo(tempo) + "#");
                        break;
                    case 2:
                        if (name.equals("")) {
                            name = "pr_initial_" + file;
                        } else {
                            if (name.contains("sa")) {
                                name = name.replaceAll("sa_", "");
                                if (!name.contains(file)) {
                                    name += file;
                                }
                                population = "files/xml/ultima/" + name + "_ultima_geracao.xml";
                            }
                            name = "pr_" + name;
                        }
                        tempo = System.currentTimeMillis();
                        avaliate = new heuristicas.Avaliation(toroide, benchmark,
                                variationPlacement, timeToMapping, popToMapping, typeCost, cost, fixType,
                                fixFile, algorithmFitness, typeAlgoFitness, upLimit, downLimit, orient);
                        relinking(name, population, 10);
                        tempo = System.currentTimeMillis() - tempo;
                        tempoTotal.append(name + "\t" + util.tempo(tempo) + "#");
                        break;
                    case 3:
                        if (name.equals("")) {
                            javax.swing.JOptionPane.showMessageDialog(null, "Essa heuristica deve ser usada apos outra!", "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        population = "files/xml/" + name + "_Melhor.xml";
                        name = "sa_" + name;
                        tempo = System.currentTimeMillis();
                        avaliate = new heuristicas.Avaliation(toroide, benchmark,
                                variationPlacement, timeToMapping, popToMapping, typeCost, cost, fixType,
                                fixFile, algorithmFitness, typeAlgoFitness, upLimit, downLimit, orient);

                        annealing(name, population, maxInterations, typeVariationT, initialTemp, finalTemp, fixedValue);
                        tempo = System.currentTimeMillis() - tempo;
                        tempoTotal.append(name + "\t" + util.tempo(tempo) + "#");
                        break;
                    case 4:
                        name = "ASAP_" + nameBench;
                        util.limparArquivoXml("files/xml/" + name + "_Melhor.xml", "Topologies");
                        util.limparArquivo("files/extract/" + name + ".txt");
                        print.add("Benchmark\tASAP");
                        for (j = 0; j < auxBench.length; ++j) {
                            aux = auxBench[j].split(":");
                            pattern.load(aux[0]);
                            tmp = aux[1] + "\t" + pattern.getASAP();
                            print.add(tmp);
                        }
                        convert.convertHisToTopology(pattern.getElementStatisticDetailed(), pattern.getElementStatistical(), pattern.getHopStatistical(), nodes, "files/xml/" + name + "_Melhor", false);
                        util.printList(print, "files/extract/resumeAll_" + name + ".txt");
                        break;
                    case 5:
                        name = "ASAP_half" + nameBench;
                        util.limparArquivoXml("files/xml/" + name + "_Melhor.xml", "Topologies");
                        util.limparArquivo("files/extract/" + name + ".txt");
                        print.add("Benchmark\tASAP half");
                        for (j = 0; j < auxBench.length; ++j) {
                            aux = auxBench[j].split(":");
                            pattern.load(aux[0]);
                            tmp = aux[1] + "\t" + pattern.getASAP();
                            print.add(tmp);
                        }
                        convert.convertHisToTopology(pattern.getElementStatisticDetailed(), pattern.getElementStatistical(), pattern.getHopStatistical(), nodes, "files/xml/" + name + "_Melhor", true);
                        util.printList(print, "files/extract/resumeAll_" + name + ".txt");
                        break;
                    case 6:
                        name = "ALAP_" + nameBench;
                        util.limparArquivoXml("files/xml/" + name + "_Melhor.xml", "Topologies");
                        util.limparArquivo("files/extract/" + name + ".txt");
                        print.add("Benchmark\tALAP");
                        for (j = 0; j < auxBench.length; ++j) {
                            aux = auxBench[j].split(":");
                            pattern.load(aux[0]);
                            tmp = aux[1] + "\t" + pattern.getALAP();
                            print.add(tmp);
                        }
                        convert.convertHisToTopology(pattern.getElementStatisticDetailed(), pattern.getElementStatistical(), pattern.getHopStatistical(), nodes, "files/xml/" + name + "_Melhor", false);
                        util.printList(print, "files/extract/resumeAll_" + name + ".txt");
                        break;
                    case 7:
                        name = "ALAP_half" + nameBench;
                        util.limparArquivoXml("files/xml/" + name + "_Melhor.xml", "Topologies");
                        util.limparArquivo("files/extract/" + name + ".txt");
                        print.add("Benchmark\tALAP half");
                        for (j = 0; j < auxBench.length; ++j) {
                            aux = auxBench[j].split(":");
                            pattern.load(aux[0]);
                            tmp = aux[1] + "\t" + pattern.getALAP();
                            print.add(tmp);
                        }
                        convert.convertHisToTopology(pattern.getElementStatisticDetailed(), pattern.getElementStatistical(), pattern.getHopStatistical(), nodes, "files/xml/" + name + "_Melhor", true);
                        util.printList(print, "files/extract/resumeAll_" + name + ".txt");
                        break;
                }
                if (heuristic < 4) {
                    printIndTemp = avaliate.getPrintBench();
                    printTimeTemp = avaliate.getPrintTime();
                    printCriTemp = avaliate.getPrintCritical();
                    try {
                        avaliate.position(name);
                    } catch (Exception e) {
                        System.out.println("Falha posicionamento");
                        e.printStackTrace();
                    }
                    printInd = getPrintArq(printInd, printIndTemp, name);
                    printTime = getPrintArq(printTime, printTimeTemp, name);
                    printCri = getPrintArq(printCri, printCriTemp, name);
                }
            }
            printTime.add("");
            aux = tempoTotal.toString().split("#");
            for (i = 0; i < aux.length; ++i) {
                printTime.add(aux[i]);
            }

            String auxArq = "null";
            if (listHeuristic.size() == 0) {
                avaliate = new heuristicas.Avaliation(toroide, benchmark,
                        variationPlacement, timeToMapping, popToMapping, typeCost, cost, fixType,
                        fixFile, algorithmFitness, typeAlgoFitness, upLimit, downLimit, orient);
                avaliate.start();
                xml.topologyclass.Element elemento;
                if (jCheckBox16.isSelected()) {
                    java.io.File diretorio = new java.io.File(System.getProperty("user.dir") + div + "files" + div + "xml");
                    diretorio.mkdirs();
                    String[] arquivos = diretorio.list();
                    java.util.Arrays.sort(arquivos);
                    for (i = 0; i < arquivos.length; ++i) {
                        auxArq = arquivos[i];
                        if (auxArq.endsWith(".xml")) {
                            elemento = XMLToFiles.getPopulation("files/xml/" + auxArq).get(0);
                            avaliate.calc(elemento);
                            printIndTemp = avaliate.getPrintBench();
                            printCriTemp = avaliate.getPrintCritical();
                            printTimeTemp = avaliate.getPrintTime();
                            try {
                                avaliate.position("mapping_" + auxArq);
                            } catch (Exception e) {
                                System.out.println("Falha criacao do arquivo de mapeamento");
                                e.printStackTrace();
                            }
                            if (auxArq.contains("_Melhor.xml")) {
                                auxArq = auxArq.substring(0, auxArq.indexOf("_Melhor.xml"));
                            } else {
                                auxArq = auxArq.substring(0, auxArq.indexOf(".xml"));
                            }
                            printInd = getPrintArq(printInd, printIndTemp, auxArq);
                            printTime = getPrintArq(printTime, printTimeTemp, auxArq);
                            printCri = getPrintArq(printCri, printCriTemp, auxArq);

                        }
                    }
                    util.printList(printInd, "files/mapping/mapping_all_ind.txt");
                    util.printList(printTime, "files/mapping/mapping_all_time.txt");
                    util.printList(printCri, "files/mapping/mapping_all_critical.txt");
                } else {
                    elemento = XMLToFiles.getPopulation(jTextField18.getText()).get(0);
                    avaliate.calc(elemento);
                    avaliate.persists("files/mapping/mapping_" + file);
                    avaliate.position("mapping_" + file);
                }
                msn = 0;
            } else {
                util.printList(printInd, "files/mapping/group_" + nameBench + ".txt");
                util.printList(printCri, "files/mapping/critical_group_" + nameBench + ".txt");
                util.printList(printTime, "files/mapping/time_group_" + nameBench + ".txt");
                msn = 1;
            }
            java.util.Properties prop = util.getProperties("info.properties");
            jLabel118.setText("Final Value: " + prop.getProperty("info.finalBest"));
            jLabel102.setText("Final Time: " + util.nowMore(0));
            jLabel103.setText("Total Time: " + util.tempo(System.currentTimeMillis() - min));
            fim = true;
            Thread play = new Thread(som);
            play.start();
            int end = 0;
            if (msn == 0) {
                end = javax.swing.JOptionPane.showConfirmDialog(null, "Mapeamento realizado com Sucesso!", "Concluido", javax.swing.JOptionPane.OK_OPTION);
            } else if (msn == 1) {
                end = javax.swing.JOptionPane.showConfirmDialog(null, "Tudo Concluido com Sucesso!", "Concluido", javax.swing.JOptionPane.OK_OPTION);
            } else {
                end = 0;
            }
            if (end != -1) {
                musica.stop();
            }
            jButton23.setEnabled(true);
            capturarTela();
        }
    }

    class graphics implements Runnable {

        @Override
        public void run() {
            jButton8.setEnabled(false);

            // initial xml.topologyclass.Element manipulate
            xml.topologyclass.Element elemento = XMLToFiles.getPopulation(jTextField7.getText()).get(0);
            // final element manipulate

            // initial statistic manipulate
            String name = nameGraph.split("\\.")[0];
            String statisticFile = jTextField34.getText();
            // final statistic manipulate

            // initial fix manipulate
            int typeCost = jComboBox17.getSelectedIndex(); // sem tratamento

            double cost = .0;
            if (typeCost == 2) {
                cost = Double.valueOf(jSpinner8.getValue().toString());
            }
            String fixFile = jTextField22.getText();
            int toroide = jComboBox6.getSelectedIndex();
            int fixType = jComboBox16.getSelectedIndex();
            int coluns = Integer.valueOf(jSpinner9.getValue().toString());
            int lines = Integer.valueOf(jSpinner10.getValue().toString());
            // final fix manipulate

            // initial graphic manipulate
            if (jCheckBox3.isSelected()) {
                architectures.generators.list.ExibidorDeLista fileDot = new architectures.generators.list.ExibidorDeLista();
                int side = (int) Math.abs(Math.sqrt(coluns * lines));
                fileDot.gerarArquivo(XMLToFiles.getList(elemento, side, toroide, "->", typeCost, cost, fixType, fixFile), "files/dot/" + name + "_Arq", "dot", "->");
                fileDot.gerarArquivo(XMLToFiles.getList(elemento, side, 4, 4, toroide, "->", typeCost, cost, fixType, fixFile), "files/dot/" + name + "_Graph_Element", "dot", "->");
                String imgDot = "files/dot/" + name + "_Graph_Element.png";
                util.executarDotGraph("files/dot/" + name + "_Graph_Element");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                java.awt.Image img = new javax.swing.ImageIcon(imgDot).getImage().getScaledInstance(jLabel44.getWidth(), jLabel44.getHeight(), java.awt.Image.SCALE_DEFAULT);
                jLabel44.setIcon(new javax.swing.ImageIcon(img));
                jLabel44.repaint();
            }
            int cols;
            utiliters.Plot plot = new utiliters.Plot();
            cols = util.contCols(statisticFile, "\t", 0);
            if (jCheckBox1.isSelected()) {
                String imgCon = "files/" + name + "_Graphic_Con.png";
                plot.Graph(statisticFile, imgCon, "", 0, cols);
                java.awt.Image img = new javax.swing.ImageIcon(imgCon).getImage().getScaledInstance(jLabel20.getWidth(), jLabel20.getHeight(), java.awt.Image.SCALE_DEFAULT);
                jLabel20.setIcon(new javax.swing.ImageIcon(img));
                jLabel20.repaint();
            }
            if (jCheckBox2.isSelected()) {
                String imgEst = "files/" + name + "_Graphic_Est.png";
                plot.Graph(statisticFile, imgEst, "", 1, cols);
                java.awt.Image img = new javax.swing.ImageIcon(imgEst).getImage().getScaledInstance(jLabel21.getWidth(), jLabel21.getHeight(), java.awt.Image.SCALE_DEFAULT);
                jLabel21.setIcon(new javax.swing.ImageIcon(img));
                jLabel21.repaint();
            }
            jButton8.setEnabled(true);
        // final graphic manipulate
        }
    }

    class dataflow implements Runnable {

        @Override
        public void run() {
            jButton37.setEnabled(false);
            String name, path, uri = "", div = "";
            String pathGraph, fileGraph;
            boolean auxPath = true;
            path = name = jTextField47.getText();
            try {
                if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
                    div = "/";
                } else {
                    div = "\\";
                }
                name = name.substring(name.lastIndexOf(div) + 1);
            } catch (Exception e) {
                auxPath = false;
            }
            if (auxPath) {
                path = path.substring(0, (path.length() - name.length()));
            } else {
                path = "files" + div + "dataflow" + div;
            }
            fileGraph = name.split(".")[0];
            uri = path + fileGraph;
            // initial manipulate graph
            if (jCheckBox4.isSelected()) {
                int nodos = Integer.valueOf(jSpinner32.getValue().toString());
                int input = Integer.valueOf(jSpinner33.getValue().toString());
                int output = Integer.valueOf(jSpinner34.getValue().toString());
                utilDataflows.generateFlowGraph(uri + ".tgffopt", nodos, input, output);
                util.executarTGFF(uri);
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                }
            }
            // final manipulate graph

            if (jCheckBox5.isSelected()) {
                util.limparArquivoXml(uri + ".xml", "DESIGN");
                filesToXML.convertTGFFtoXML(uri + ".tgff", uri + ".xml", uri, jCheckBox8.isSelected());
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                }
            }
            if (jCheckBox6.isSelected()) {
                utilDataflows.generateDataflow(uri + ".xml", "xml2java.xsl", uri + ".java");
                if (jCheckBox7.isSelected()) {
                    util.limparArquivoXml("files/benchmarks/tgffBench.xml", "Benchmarks");
                }
                pathGraph = uri.replaceAll(div, ".").substring(1);
                filesToXML.generateBenchmarkFile("files/benchmarks/tgffBench.xml", fileGraph, pathGraph);
                util.generateDataflowGraph(uri + ".vcg");
                String imgLabel = uri + ".png";
                java.awt.Image img = new javax.swing.ImageIcon(imgLabel).getImage().getScaledInstance(jLabel22.getWidth(), jLabel22.getHeight(), java.awt.Image.SCALE_DEFAULT);
                jLabel22.setIcon(new javax.swing.ImageIcon(img));
                jLabel22.repaint();
            }
            jButton37.setEnabled(true);
        }
    }

    class extract implements Runnable {

        @Override
        public void run() {
            long min = System.currentTimeMillis();
            jButton39.setEnabled(false);
            String arq = jTextField48.getText(), name;
            int nodes = Integer.valueOf(jSpinner35.getValue().toString());
            String elementFile = jTextField9.getText();
            String orient = (jComboBox34.getSelectedIndex() == 0) ? "->" : "--";
            int typeCost = jComboBox35.getSelectedIndex(); // sem tratamento
            String tmpCost = String.valueOf(typeCost);

            double cost = .0;
            if (typeCost == 2) {
                cost = Double.valueOf(jSpinner13.getValue().toString());
                tmpCost = tmpCost + " -> " + cost;
            }

            int toroide = 4;
            int heuristica = 3;
            int timeToMapping = 1000;
            int popToMapping = 50;
            int fixType = 0;
            String fixFile = "";

            String div;
            if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
                div = "/";
            } else {
                div = "\\";
            }
            arq = arq.substring((arq.lastIndexOf(div) + 1), arq.lastIndexOf("."));
            if (jCheckBox9.isSelected()) {
                util.limparArquivoXml("files/extract/ASAP_" + arq + ".xml", "Topologies");
                util.limparArquivoXml("files/extract/ALAP_" + arq + ".xml", "Topologies");
                util.limparArquivoXml("files/extract/breadthAsc_" + arq + ".xml", "Topologies");
                util.limparArquivoXml("files/extract/breadthDes_" + arq + ".xml", "Topologies");
                util.limparArquivo("files/extract/ASAP_" + arq + ".txt");
                util.limparArquivo("files/extract/ALAP_" + arq + ".txt");
                util.limparArquivo("files/extract/breadthAsc_" + arq + ".txt");
                util.limparArquivo("files/extract/breadthDes_" + arq + ".txt");
            }
            architectures.dataflows.extractor.Pattern[] pattern = new architectures.dataflows.extractor.Pattern[4];
            java.util.ArrayList<String> print = new java.util.ArrayList<String>();
            String[] benchmark = benchmarks(), aux;
            xml.topologyclass.Element element = null;
            mapping.placement.Placement route = null;
            String tmp;
            int i, auxPattern[] = new int[pattern.length];
            int maximo[] = new int[pattern.length];
            for (i = 0; i < pattern.length; ++i) {
                pattern[i] = new architectures.dataflows.extractor.Pattern();
                maximo[i] = 0;
                auxPattern[i] = 0;
            }
            boolean arch = false;
            print.add("Benchmark\tASAP\tALAP\tbreadthAsc\tbreadthDes");
            if (!elementFile.equals("")) {
                arq = elementFile;
                arq = arq.substring((arq.lastIndexOf(div) + 1), arq.lastIndexOf("."));
                print.set(0, print.get(0) + "\tCritical " + arq + " Cost " + typeCost);
                element = XMLToFiles.getPopulation(elementFile).get(0);
                route = new mapping.placement.Placement();
                arch = true;
            }
            for (i = 0; i < benchmark.length; ++i) {
                aux = benchmark[i].split(":");
                pattern[0].load(aux[0]);
                auxPattern[0] = pattern[0].getASAP();
                if (auxPattern[0] > maximo[0]) {
                    maximo[0] = auxPattern[0];
                }
                pattern[1].load(aux[0]);
                auxPattern[1] = pattern[1].getALAP();
                if (auxPattern[1] > maximo[1]) {
                    maximo[1] = auxPattern[1];
                }
                pattern[2].load(aux[0]);
                auxPattern[2] = pattern[2].getBeadthAsc();
                if (auxPattern[2] > maximo[2]) {
                    maximo[2] = auxPattern[2];
                }
                pattern[3].load(aux[0]);
                auxPattern[3] = pattern[3].getBeadthDes();
                if (auxPattern[3] > maximo[3]) {
                    maximo[3] = auxPattern[3];
                }
                tmp = aux[1] + "\t" + auxPattern[0] + "\t" + auxPattern[1] + "\t" + auxPattern[2] + "\t" + auxPattern[3];
                if (arch) {
                    route.homogeneous(element, toroide, benchmark[i], heuristica, timeToMapping, popToMapping, typeCost, cost, fixType, fixFile, orient);
                    tmp = tmp + "\t" + route.critical;
                }
                print.add(tmp);
            }

            utiliters.FilesToXML convert = new utiliters.FilesToXML();
            java.util.ArrayList<String> histogram;
            java.util.Iterator<String> it;

            jTextArea1.setRows(pattern[0].getPrintedArcs().size() + 4);
            for (i = 0; i < pattern[0].getPrintedArcs().size(); ++i) {
                jTextArea1.append(pattern[0].getPrintedArcs().get(i) + "\n");
            }
            jTextArea1.append("\n-----------------------\n");
            histogram = convert.convertHisToTopology(pattern[0].getElementStatisticDetailed(), pattern[0].getElementStatistical(), pattern[0].getHopStatistical(), nodes, "files/extract/ASAP_" + arq, jCheckBox10.isSelected());
            it = histogram.iterator();
            jTextArea2.setRows(histogram.size());
            while (it.hasNext()) {
                jTextArea2.append(it.next() + "\n");
            }
            jTextArea2.append("\nProfundidade maxima: " + maximo[0] + "\n");

            jTextArea3.setRows(pattern[1].getPrintedArcs().size() + 4);
            for (i = 0; i < pattern[1].getPrintedArcs().size(); ++i) {
                jTextArea3.append(pattern[1].getPrintedArcs().get(i) + "\n");
            }
            jTextArea3.append("\n-----------------------\n");
            histogram = convert.convertHisToTopology(pattern[1].getElementStatisticDetailed(), pattern[1].getElementStatistical(), pattern[1].getHopStatistical(), nodes, "files/extract/ALAP_" + arq, jCheckBox10.isSelected());
            it = histogram.iterator();
            jTextArea4.setRows(histogram.size());
            while (it.hasNext()) {
                jTextArea4.append(it.next() + "\n");
            }
            jTextArea4.append("\nProfundidade maxima: " + maximo[1] + "\n");

            jTextArea5.setRows(pattern[2].getPrintedArcs().size() + 4);
            for (i = 0; i < pattern[2].getPrintedArcs().size(); ++i) {
                jTextArea5.append(pattern[2].getPrintedArcs().get(i) + "\n");
            }
            jTextArea5.append("\n-----------------------\n");
            histogram = convert.convertHisToTopology(pattern[2].getElementStatisticDetailed(), pattern[2].getElementStatistical(), pattern[2].getHopStatistical(), nodes, "files/extract/breadthAsc_" + arq, jCheckBox10.isSelected());
            it = histogram.iterator();
            jTextArea6.setRows(histogram.size());
            while (it.hasNext()) {
                jTextArea6.append(it.next() + "\n");
            }
            jTextArea6.append("\nProfundidade maxima: " + maximo[2] + "\n");

            jTextArea7.setRows(pattern[3].getPrintedArcs().size() + 4);
            for (i = 0; i < pattern[3].getPrintedArcs().size(); ++i) {
                jTextArea7.append(pattern[3].getPrintedArcs().get(i) + "\n");
            }
            jTextArea7.append("\n-----------------------\n");
            histogram = convert.convertHisToTopology(pattern[3].getElementStatisticDetailed(), pattern[3].getElementStatistical(), pattern[3].getHopStatistical(), nodes, "files/extract/breadthDes_" + arq, jCheckBox10.isSelected());
            it = histogram.iterator();
            jTextArea8.setRows(histogram.size());
            while (it.hasNext()) {
                jTextArea8.append(it.next() + "\n");
            }
            jTextArea8.append("\nProfundidade maxima: " + maximo[3] + "\n");

            it = print.iterator();
            jTextArea9.setRows(histogram.size());
            while (it.hasNext()) {
                jTextArea9.append(it.next() + "\n");
            }
            util.printList(print, "files/extract/resumeAll_" + arq + ".txt", true);

            /*
            architectures.generators.list.ExibidorDeLista fileDot = new architectures.generators.list.ExibidorDeLista();
            xml.topologyclass.Element elemento;
            String imgDot;
            java.awt.Image img;
            //fileDot.gerarArquivo(XMLToFiles.getList(elemento, 9, 4, 4, 1, "->", 3, 1, 3, "xml/input/fix.xml"), "files/dot/" + file + "_Extracted_Graph", "dot", "->");

            elemento = XMLToFiles.getPopulation(asapF + ".xml").get(0);
            fileDot.gerarArquivo(XMLToFiles.getList(elemento, 9, 4, 4, 1, "->", 3, 1, 3, ""), "files/dot/" + arq + "_ASAP_Extracted_Graph", "dot", "->");
            imgDot = "files/dot/" + arq + "_ASAP_Extracted_Graph.png";
            util.executarDotGraph(arq + "_ASAP_Extracted_Graph");
            try {
            Thread.sleep(5000);
            } catch (InterruptedException e) {
            e.printStackTrace();
            }
            img = new javax.swing.ImageIcon(imgDot).getImage().getScaledInstance(jLabel89.getWidth(), jLabel89.getHeight(), java.awt.Image.SCALE_DEFAULT);
            jLabel89.setIcon(new javax.swing.ImageIcon(img));
            jLabel89.repaint();

            elemento = XMLToFiles.getPopulation(alapF + ".xml").get(0);
            fileDot.gerarArquivo(XMLToFiles.getList(elemento, 9, 4, 4, 1, "->", 3, 1, 3, ""), "files/dot/" + arq + "_ALAP_Extracted_Graph", "dot", "->");
            imgDot = "files/dot/" + arq + "_ALAP_Extracted_Graph.png";
            util.executarDotGraph(arq + "_ALAP_Extracted_Graph");
            try {
            Thread.sleep(5000);
            } catch (InterruptedException e) {
            e.printStackTrace();
            }
            img = new javax.swing.ImageIcon(imgDot).getImage().getScaledInstance(jLabel100.getWidth(), jLabel100.getHeight(), java.awt.Image.SCALE_DEFAULT);
            jLabel100.setIcon(new javax.swing.ImageIcon(img));
            jLabel100.repaint();

            elemento = XMLToFiles.getPopulation(criticalF + ".xml").get(0);
            fileDot.gerarArquivo(XMLToFiles.getList(elemento, 9, 4, 4, 1, "->", 3, 1, 3, ""), "files/dot/" + arq + "_Critical_Extracted_Graph", "dot", "->");
            imgDot = "files/dot/" + arq + "_Critical_Extracted_Graph.png";
            util.executarDotGraph(arq + "_Critical_Extracted_Graph");
            try {
            Thread.sleep(5000);
            } catch (InterruptedException e) {
            e.printStackTrace();
            }
            img = new javax.swing.ImageIcon(imgDot).getImage().getScaledInstance(jLabel120.getWidth(), jLabel120.getHeight(), java.awt.Image.SCALE_DEFAULT);
            jLabel120.setIcon(new javax.swing.ImageIcon(img));
            jLabel120.repaint();
             */ String tempoTotal = util.tempo(System.currentTimeMillis() - min);
            javax.swing.JOptionPane.showConfirmDialog(null, "Extração realizada com Sucesso!\nEm " + tempoTotal, "Concluido", javax.swing.JOptionPane.OK_OPTION);
            jButton39.setEnabled(true);
        }
    }

    class infoGenetic implements Runnable {

        @Override
        public void run() {
            int gen;
            java.util.Properties prop;
            while (!fim) {
                prop = util.getProperties("info.properties");
                gen = 0;
                try {
                    gen = Integer.valueOf(prop.getProperty("info.generator"));
                } catch (Exception e) {
                }
                jProgressBar1.setValue((int) (gen * constGen));
                if (gen == 0) {
                    jLabel11.setText("Generation: Initial Population");
                } else {
                    jLabel11.setText("Generation: " + prop.getProperty("info.generator"));
                }
                jLabel12.setText("Individual: " + prop.getProperty("info.individual"));
                jLabel13.setText("Actual Value: " + prop.getProperty("info.value"));
                jLabel14.setText("Partial Best: " + prop.getProperty("info.partialBest"));
                jLabel15.setText("Partial Worse: " + prop.getProperty("info.partialWorse"));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class infoRelinking implements Runnable {

        @Override
        public void run() {
            int gen;
            java.util.Properties prop;
            while (!fim) {
                prop = util.getProperties("info.properties");
                gen = 0;
                try {
                    gen = Integer.valueOf(prop.getProperty("info.generator"));
                } catch (Exception e) {
                }
                jProgressBar2.setValue((int) (gen * constGen));
                if (gen == 0) {
                    jLabel65.setText("Generation: Initial Population");
                } else {
                    jLabel65.setText("Generation: " + prop.getProperty("info.generator"));
                }
                jLabel64.setText("Individual: " + prop.getProperty("info.individual"));
                jLabel63.setText("Actual Value: " + prop.getProperty("info.value"));
                jLabel62.setText("Partial Best: " + prop.getProperty("info.partialBest"));
                jLabel61.setText("Partial Worse: " + prop.getProperty("info.partialWorse"));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class infoAnnealing implements Runnable {

        @Override
        public void run() {
            java.util.Properties prop;
            while (!fim) {
                prop = util.getProperties("info.properties");
                jLabel95.setText("Temperature: " + prop.getProperty("info.generator"));
                jLabel94.setText("Individual: " + prop.getProperty("info.individual"));
                jLabel93.setText("Actual Value: " + prop.getProperty("info.value"));
                jLabel92.setText("Partial Best: " + prop.getProperty("info.partialBest"));
                jLabel91.setText("Partial Worse: " + prop.getProperty("info.partialWorse"));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class sound implements Runnable {

        @Override
        public void run() {
            musica.play();
            musica.loop();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        relinkingElements = new javax.swing.ButtonGroup();
        annealingElements = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        selecionar1 = new javax.swing.JMenuItem();
        salvar1 = new javax.swing.JMenuItem();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        selecionar2 = new javax.swing.JMenuItem();
        salvar2 = new javax.swing.JMenuItem();
        mainPanel = new javax.swing.JPanel();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        formGenetic = new javax.swing.JInternalFrame();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jLabel139 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jComboBox43 = new javax.swing.JComboBox();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jSpinner14 = new javax.swing.JSpinner();
        jSpinner36 = new javax.swing.JSpinner();
        jLabel32 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jPanel32 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jComboBox12 = new javax.swing.JComboBox();
        jLabel30 = new javax.swing.JLabel();
        jSpinner5 = new javax.swing.JSpinner();
        jComboBox15 = new javax.swing.JComboBox();
        jLabel26 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jSpinner6 = new javax.swing.JSpinner();
        jLabel167 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jCheckBox12 = new javax.swing.JCheckBox();
        jButton58 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jSpinner2 = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        jComboBox13 = new javax.swing.JComboBox();
        jComboBox14 = new javax.swing.JComboBox();
        jButton10 = new javax.swing.JButton();
        jSpinner7 = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel16 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        formGraphics = new javax.swing.JInternalFrame();
        jLabel21 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        jTextField22 = new javax.swing.JTextField();
        jLabel117 = new javax.swing.JLabel();
        jComboBox16 = new javax.swing.JComboBox();
        jLabel122 = new javax.swing.JLabel();
        jComboBox17 = new javax.swing.JComboBox();
        jLabel42 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox();
        jLabel43 = new javax.swing.JLabel();
        jButton21 = new javax.swing.JButton();
        jSpinner8 = new javax.swing.JSpinner();
        jSpinner9 = new javax.swing.JSpinner();
        jSpinner10 = new javax.swing.JSpinner();
        jPanel6 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jLabel19 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jLabel79 = new javax.swing.JLabel();
        jTextField34 = new javax.swing.JTextField();
        jButton27 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLabel44 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        formPathRelinking = new javax.swing.JInternalFrame();
        jPanel16 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jProgressBar2 = new javax.swing.JProgressBar();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel72 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jLabel77 = new javax.swing.JLabel();
        jLabel121 = new javax.swing.JLabel();
        jComboBox23 = new javax.swing.JComboBox();
        jComboBox24 = new javax.swing.JComboBox();
        jButton12 = new javax.swing.JButton();
        jSpinner17 = new javax.swing.JSpinner();
        jPanel23 = new javax.swing.JPanel();
        jRadioButton9 = new javax.swing.JRadioButton();
        jRadioButton10 = new javax.swing.JRadioButton();
        jLabel68 = new javax.swing.JLabel();
        jRadioButton11 = new javax.swing.JRadioButton();
        jLabel71 = new javax.swing.JLabel();
        jSpinner21 = new javax.swing.JSpinner();
        jSpinner22 = new javax.swing.JSpinner();
        jPanel8 = new javax.swing.JPanel();
        jPanel33 = new javax.swing.JPanel();
        jLabel140 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBox44 = new javax.swing.JComboBox();
        jComboBox5 = new javax.swing.JComboBox();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jSpinner15 = new javax.swing.JSpinner();
        jSpinner37 = new javax.swing.JSpinner();
        jLabel37 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jButton11 = new javax.swing.JButton();
        jPanel34 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jComboBox19 = new javax.swing.JComboBox();
        jLabel51 = new javax.swing.JLabel();
        jSpinner16 = new javax.swing.JSpinner();
        jComboBox22 = new javax.swing.JComboBox();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jSpinner38 = new javax.swing.JSpinner();
        jLabel168 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jCheckBox13 = new javax.swing.JCheckBox();
        jButton59 = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        jLabel56 = new javax.swing.JLabel();
        jTextField27 = new javax.swing.JTextField();
        jButton24 = new javax.swing.JButton();
        jLabel57 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList5 = new javax.swing.JList();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList6 = new javax.swing.JList();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        formSimulateAnnealing = new javax.swing.JInternalFrame();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jPanel27 = new javax.swing.JPanel();
        jLabel87 = new javax.swing.JLabel();
        jTextField41 = new javax.swing.JTextField();
        jButton33 = new javax.swing.JButton();
        jLabel88 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList7 = new javax.swing.JList();
        jScrollPane8 = new javax.swing.JScrollPane();
        jList8 = new javax.swing.JList();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel104 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jLabel108 = new javax.swing.JLabel();
        jLabel123 = new javax.swing.JLabel();
        jComboBox28 = new javax.swing.JComboBox();
        jComboBox31 = new javax.swing.JComboBox();
        jButton16 = new javax.swing.JButton();
        jSpinner24 = new javax.swing.JSpinner();
        jPanel12 = new javax.swing.JPanel();
        jPanel35 = new javax.swing.JPanel();
        jLabel141 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jComboBox45 = new javax.swing.JComboBox();
        jComboBox8 = new javax.swing.JComboBox();
        jLabel35 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jSpinner18 = new javax.swing.JSpinner();
        jSpinner39 = new javax.swing.JSpinner();
        jLabel55 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jButton14 = new javax.swing.JButton();
        jPanel36 = new javax.swing.JPanel();
        jLabel70 = new javax.swing.JLabel();
        jComboBox25 = new javax.swing.JComboBox();
        jLabel73 = new javax.swing.JLabel();
        jSpinner19 = new javax.swing.JSpinner();
        jComboBox26 = new javax.swing.JComboBox();
        jLabel78 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jSpinner40 = new javax.swing.JSpinner();
        jLabel169 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jCheckBox14 = new javax.swing.JCheckBox();
        jButton60 = new javax.swing.JButton();
        jPanel28 = new javax.swing.JPanel();
        jRadioButton14 = new javax.swing.JRadioButton();
        jRadioButton15 = new javax.swing.JRadioButton();
        jRadioButton16 = new javax.swing.JRadioButton();
        jLabel90 = new javax.swing.JLabel();
        jRadioButton17 = new javax.swing.JRadioButton();
        jSpinner31 = new javax.swing.JSpinner();
        jLabel45 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        jSpinner28 = new javax.swing.JSpinner();
        jSpinner29 = new javax.swing.JSpinner();
        jLabel106 = new javax.swing.JLabel();
        jSpinner30 = new javax.swing.JSpinner();
        jLabel107 = new javax.swing.JLabel();
        formDataflow = new javax.swing.JInternalFrame();
        jLabel22 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jLabel111 = new javax.swing.JLabel();
        jTextField47 = new javax.swing.JTextField();
        jSpinner32 = new javax.swing.JSpinner();
        jLabel110 = new javax.swing.JLabel();
        jLabel112 = new javax.swing.JLabel();
        jLabel113 = new javax.swing.JLabel();
        jSpinner33 = new javax.swing.JSpinner();
        jSpinner34 = new javax.swing.JSpinner();
        jButton37 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        formPattern = new javax.swing.JInternalFrame();
        jPanel24 = new javax.swing.JPanel();
        jLabel115 = new javax.swing.JLabel();
        jTextField48 = new javax.swing.JTextField();
        jLabel116 = new javax.swing.JLabel();
        jSpinner35 = new javax.swing.JSpinner();
        jLabel130 = new javax.swing.JLabel();
        jComboBox34 = new javax.swing.JComboBox();
        jLabel129 = new javax.swing.JLabel();
        jComboBox35 = new javax.swing.JComboBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jButton41 = new javax.swing.JButton();
        jSpinner13 = new javax.swing.JSpinner();
        jPanel29 = new javax.swing.JPanel();
        jLabel96 = new javax.swing.JLabel();
        jTextField42 = new javax.swing.JTextField();
        jButton42 = new javax.swing.JButton();
        jLabel99 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jList9 = new javax.swing.JList();
        jScrollPane10 = new javax.swing.JScrollPane();
        jList10 = new javax.swing.JList();
        jButton44 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jButton20 = new javax.swing.JButton();
        jCheckBox11 = new javax.swing.JCheckBox();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jLabel89 = new javax.swing.JLabel();
        jSplitPane4 = new javax.swing.JSplitPane();
        jScrollPane19 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane20 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jLabel100 = new javax.swing.JLabel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane15 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jScrollPane16 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        jLabel120 = new javax.swing.JLabel();
        jSplitPane5 = new javax.swing.JSplitPane();
        jScrollPane21 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jScrollPane22 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
        jTabbedPane6 = new javax.swing.JTabbedPane();
        jLabel125 = new javax.swing.JLabel();
        jSplitPane6 = new javax.swing.JSplitPane();
        jScrollPane23 = new javax.swing.JScrollPane();
        jTextArea7 = new javax.swing.JTextArea();
        jScrollPane24 = new javax.swing.JScrollPane();
        jTextArea8 = new javax.swing.JTextArea();
        jScrollPane13 = new javax.swing.JScrollPane();
        jTextArea9 = new javax.swing.JTextArea();
        jButton39 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        formMapping = new javax.swing.JInternalFrame();
        jButton28 = new javax.swing.JButton();
        jPanel30 = new javax.swing.JPanel();
        jLabel137 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jButton17 = new javax.swing.JButton();
        jLabel138 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jPanel31 = new javax.swing.JPanel();
        jLabel142 = new javax.swing.JLabel();
        jComboBox9 = new javax.swing.JComboBox();
        jLabel27 = new javax.swing.JLabel();
        jComboBox46 = new javax.swing.JComboBox();
        jComboBox10 = new javax.swing.JComboBox();
        jLabel36 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jSpinner20 = new javax.swing.JSpinner();
        jSpinner41 = new javax.swing.JSpinner();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jButton13 = new javax.swing.JButton();
        jCheckBox16 = new javax.swing.JCheckBox();
        jPanel37 = new javax.swing.JPanel();
        jLabel74 = new javax.swing.JLabel();
        jComboBox18 = new javax.swing.JComboBox();
        jLabel81 = new javax.swing.JLabel();
        jSpinner11 = new javax.swing.JSpinner();
        jComboBox20 = new javax.swing.JComboBox();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jSpinner12 = new javax.swing.JSpinner();
        jLabel170 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jCheckBox15 = new javax.swing.JCheckBox();
        jButton61 = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jLabel119 = new javax.swing.JLabel();
        jLabel124 = new javax.swing.JLabel();
        jComboBox32 = new javax.swing.JComboBox();
        jComboBox33 = new javax.swing.JComboBox();
        jButton29 = new javax.swing.JButton();
        jSpinner25 = new javax.swing.JSpinner();
        jLabel109 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel101 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel118 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jPanel38 = new javax.swing.JPanel();
        jLabel143 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jList11 = new javax.swing.JList();
        jScrollPane12 = new javax.swing.JScrollPane();
        jList12 = new javax.swing.JList();
        jButton47 = new javax.swing.JButton();
        jButton48 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jCheckBox17 = new javax.swing.JCheckBox();
        menuBar1 = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu1 = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem1 = new javax.swing.JMenuItem();
        generateMenu1 = new javax.swing.JMenu();
        selectHeuristic1 = new javax.swing.JMenu();
        genetic1 = new javax.swing.JMenuItem();
        relinking1 = new javax.swing.JMenuItem();
        sa1 = new javax.swing.JMenuItem();
        mapping1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        statistic1 = new javax.swing.JMenu();
        graphic1 = new javax.swing.JMenuItem();
        graphs1 = new javax.swing.JMenu();
        extract1 = new javax.swing.JMenuItem();
        dataflow1 = new javax.swing.JMenuItem();
        customizationMenu1 = new javax.swing.JMenu();
        aparenceMenu1 = new javax.swing.JMenu();
        jSeparator2 = new javax.swing.JSeparator();
        screen1 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu1 = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem1 = new javax.swing.JMenuItem();

        selecionar1.setText("Select All");
        selecionar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selecionar1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(selecionar1);

        salvar1.setText("Save");
        salvar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salvar1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(salvar1);

        selecionar2.setText("Select All");
        selecionar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selecionar2ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(selecionar2);

        salvar2.setText("Save");
        salvar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salvar2ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(salvar2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Architecture Generator");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1360, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 648, Short.MAX_VALUE)
        );

        jDesktopPane1.setAutoscrolls(true);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainPanel, org.jdesktop.beansbinding.ELProperty.create("${preferredSize}"), jDesktopPane1, org.jdesktop.beansbinding.BeanProperty.create("preferredSize"));
        bindingGroup.addBinding(binding);

        formGenetic.setTitle("Algorithm Genetic");

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton1.setText("Start");
        jButton1.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton2.setText("Close");
        jButton2.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mapping", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(410, 215));

        jPanel26.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Placement", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel139.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel139.setText("Orientation");

        jComboBox2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- NULL --", "Between Lines + 1", "Between Lines", "Between Coluns + 1", "Between Coluns", "Next Lines", "Next Coluns" }));
        jComboBox2.setSelectedIndex(1);
        jComboBox2.setMinimumSize(new java.awt.Dimension(150, 25));
        jComboBox2.setPreferredSize(new java.awt.Dimension(150, 25));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Variation");

        jComboBox43.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox43.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Unidirectional", "Directional" }));
        jComboBox43.setMaximumSize(new java.awt.Dimension(160, 23));
        jComboBox43.setPreferredSize(new java.awt.Dimension(115, 25));

        jComboBox1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Depth", "Random", "Modified Depth" }));
        jComboBox1.setSelectedIndex(3);
        jComboBox1.setPreferredSize(new java.awt.Dimension(127, 25));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("Type of Toroide");

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel31.setText("Time");

        jSpinner14.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(1L), null, null, Long.valueOf(1L)));
        jSpinner14.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner14, ""));
        jSpinner14.setPreferredSize(new java.awt.Dimension(98, 25));

        jSpinner36.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(11), null, null, Integer.valueOf(1)));
        jSpinner36.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner36, ""));
        jSpinner36.setPreferredSize(new java.awt.Dimension(50, 25));

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel32.setText("Population");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setText("File with Population");

        jTextField5.setText("population.xml");
        jTextField5.setAutoscrolls(false);
        jTextField5.setMaximumSize(new java.awt.Dimension(285, 20));
        jTextField5.setPreferredSize(new java.awt.Dimension(370, 25));

        jButton3.setText("...");
        jButton3.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGap(291, 291, 291)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel139)))
                    .addComponent(jLabel8)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(1, 1, 1)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31)
                    .addComponent(jSpinner36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32)
                    .addComponent(jSpinner14, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jLabel139)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addGap(31, 31, 31)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel32))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jSpinner36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24))
        );

        jPanel32.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Routing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel23.setText("Algorithm");

        jComboBox12.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dijsktra - Homogeneous", "Path Finder - Homogeneous" }));
        jComboBox12.setPreferredSize(new java.awt.Dimension(205, 25));

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel30.setText("Up");

        jSpinner5.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(25), null, null, Integer.valueOf(1)));
        jSpinner5.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner5, ""));
        jSpinner5.setPreferredSize(new java.awt.Dimension(42, 25));

        jComboBox15.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox15.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Average distance", "Sum conections" }));
        jComboBox15.setSelectedIndex(1);
        jComboBox15.setPreferredSize(new java.awt.Dimension(135, 25));
        jComboBox15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox15ActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel26.setText("Format of Calc");

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel39.setText("Down");

        jSpinner6.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(25), null, null, Integer.valueOf(1)));
        jSpinner6.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner6, ""));
        jSpinner6.setPreferredSize(new java.awt.Dimension(42, 25));

        jLabel167.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel167.setText("File for Statistics");

        jTextField12.setText("statistics.txt");
        jTextField12.setAutoscrolls(false);
        jTextField12.setMaximumSize(new java.awt.Dimension(285, 20));
        jTextField12.setPreferredSize(new java.awt.Dimension(370, 25));

        jCheckBox12.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox12.setSelected(true);
        jCheckBox12.setText("Clear File");

        jButton58.setText("...");
        jButton58.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton58.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton58ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26)
                            .addComponent(jComboBox15, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel30)
                            .addComponent(jSpinner5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39)
                            .addComponent(jSpinner6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel32Layout.createSequentialGroup()
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton58, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addComponent(jLabel167)
                        .addGap(34, 34, 34)
                        .addComponent(jCheckBox12)
                        .addGap(156, 156, 156)))
                .addContainerGap())
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel167)
                    .addComponent(jCheckBox12, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton58, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(197, 197, 197))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Algorithm Genetic Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Format of CrossOver");

        jComboBox3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cross Topology", "Cross Each Node", "Cross with PathRelinking" }));
        jComboBox3.setSelectedIndex(1);
        jComboBox3.setMaximumSize(new java.awt.Dimension(160, 23));
        jComboBox3.setPreferredSize(new java.awt.Dimension(153, 25));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel7.setText("Time of Generation");

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(86400000L), null, null, Long.valueOf(1L)));
        jSpinner1.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner1, ""));
        jSpinner1.setPreferredSize(new java.awt.Dimension(98, 25));

        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), null, null, Integer.valueOf(1)));
        jSpinner2.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner2, ""));
        jSpinner2.setPreferredSize(new java.awt.Dimension(50, 25));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Number of Generations");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Benchmarks", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel4.setPreferredSize(new java.awt.Dimension(330, 150));

        jTextField6.setText("benchmarks.xml");
        jTextField6.setPreferredSize(new java.awt.Dimension(116, 25));

        jButton4.setText("...");
        jButton4.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel18.setText("File with Benchmarks");

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jList1);

        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jList2);

        jButton5.setText(">");
        jButton5.setPreferredSize(new java.awt.Dimension(45, 25));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("<");
        jButton6.setPreferredSize(new java.awt.Dimension(45, 25));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, 0, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGap(14, 14, 14)))
                .addGap(0, 0, 0))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(334, 334, 334))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fixed Values", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel7.setPreferredSize(new java.awt.Dimension(410, 150));

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel24.setText("File with Fixed Values");

        jTextField8.setPreferredSize(new java.awt.Dimension(370, 25));

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel38.setText("Type of Cost");

        jLabel114.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel114.setText("Type of Crossing");

        jComboBox13.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox13.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cross", "Diagonal" }));
        jComboBox13.setPreferredSize(new java.awt.Dimension(83, 25));

        jComboBox14.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox14.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "(0.065 * distance) + 0.85", "(1 / Cost) + 1 =>" }));
        jComboBox14.setPreferredSize(new java.awt.Dimension(200, 25));

        jButton10.setText("...");
        jButton10.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jSpinner7.setPreferredSize(new java.awt.Dimension(34, 25));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel114)
                            .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jSpinner7, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel38))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel114)
                            .addComponent(jLabel38))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(106, 106, 106))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preview Values", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(330, 310));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setText("Initial Time:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel10.setText("Final Time (estimative 1):");

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel29.setText("Total Time:");

        jPanel9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel15.setText("Partial Worse:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel14.setText("Partial Best:");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel13.setText("Fintess Value:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel12.setText("Individual:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel11.setText("Generation:");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addContainerGap(220, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel15)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel75.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel75.setText("Progress");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel16.setText("Final Best:");

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel25.setText("Final Worse:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel25)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel75)
                .addGap(258, 258, 258))
            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel29))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addGap(12, 12, 12)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel75)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout formGeneticLayout = new javax.swing.GroupLayout(formGenetic.getContentPane());
        formGenetic.getContentPane().setLayout(formGeneticLayout);
        formGeneticLayout.setHorizontalGroup(
            formGeneticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formGeneticLayout.createSequentialGroup()
                .addGroup(formGeneticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(formGeneticLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(93, 93, 93)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formGeneticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        formGeneticLayout.setVerticalGroup(
            formGeneticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formGeneticLayout.createSequentialGroup()
                .addGroup(formGeneticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formGeneticLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(formGeneticLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(formGeneticLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        formGenetic.setBounds(10, 0, 860, 560);
        jDesktopPane1.add(formGenetic, javax.swing.JLayeredPane.DEFAULT_LAYER);

        formGraphics.setTitle("Graphics Generator");

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Statistical", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        jPanel17.setPreferredSize(new java.awt.Dimension(305, 490));

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fixed Values", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel15.setPreferredSize(new java.awt.Dimension(300, 100));

        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel50.setText("File with Fixed Values");

        jTextField22.setPreferredSize(new java.awt.Dimension(320, 25));

        jLabel117.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel117.setText("Type of Crossing");

        jComboBox16.setFont(new java.awt.Font("Dialog", 1, 11));
        jComboBox16.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cross", "Diagonal" }));
        jComboBox16.setPreferredSize(new java.awt.Dimension(84, 25));

        jLabel122.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel122.setText("Type of Cost");

        jComboBox17.setFont(new java.awt.Font("Dialog", 1, 11));
        jComboBox17.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "(0.065 * distance) + 0.85", "(1 / Cost) + 1 =>" }));
        jComboBox17.setPreferredSize(new java.awt.Dimension(173, 25));

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel42.setText("Coluns");

        jLabel41.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel41.setText("Type of Toroide");

        jComboBox6.setFont(new java.awt.Font("Dialog", 1, 11));
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- NULL --", "Between Lines + 1", "Between Lines", "Between Coluns + 1", "Between Coluns", "Next Lines", "Next Coluns" }));
        jComboBox6.setSelectedIndex(1);
        jComboBox6.setPreferredSize(new java.awt.Dimension(146, 25));

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel43.setText("Lines");

        jButton21.setText("...");
        jButton21.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jSpinner8.setModel(new javax.swing.SpinnerNumberModel());
        jSpinner8.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner8, ""));
        jSpinner8.setPreferredSize(new java.awt.Dimension(34, 25));

        jSpinner9.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(9), null, null, Integer.valueOf(1)));
        jSpinner9.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner9, ""));
        jSpinner9.setPreferredSize(new java.awt.Dimension(34, 25));

        jSpinner10.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(9), null, null, Integer.valueOf(1)));
        jSpinner10.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner10, ""));
        jSpinner10.setPreferredSize(new java.awt.Dimension(34, 25));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel50)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel117, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jComboBox16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel41)))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jComboBox17, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinner8, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel122))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSpinner9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel43)
                            .addComponent(jSpinner10, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel117)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel122)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel50))
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel15Layout.createSequentialGroup()
                            .addComponent(jLabel43)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSpinner10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel15Layout.createSequentialGroup()
                            .addComponent(jLabel42)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSpinner9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Graphics Types and Files", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel6.setPreferredSize(new java.awt.Dimension(360, 150));

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Concentration");

        jCheckBox2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox2.setSelected(true);
        jCheckBox2.setText("Statistical");

        jCheckBox3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox3.setSelected(true);
        jCheckBox3.setText("Dot");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel19.setText("File with Element");

        jTextField7.setText("element.xml");
        jTextField7.setPreferredSize(new java.awt.Dimension(320, 25));

        jButton7.setText("...");
        jButton7.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel79.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel79.setText("File with Statistics");

        jTextField34.setText("statistic.txt");
        jTextField34.setPreferredSize(new java.awt.Dimension(320, 25));

        jButton27.setText("...");
        jButton27.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jCheckBox1)
                .addGap(29, 29, 29)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addComponent(jCheckBox3)
                .addGap(19, 19, 19))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel19)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                            .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel79)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel79)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jButton8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton8.setText("Generate");
        jButton8.setPreferredSize(new java.awt.Dimension(105, 25));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton9.setText("Close");
        jButton9.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(114, 114, 114))
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "DOT", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Concentration", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", 1, 11))); // NOI18N

        javax.swing.GroupLayout formGraphicsLayout = new javax.swing.GroupLayout(formGraphics.getContentPane());
        formGraphics.getContentPane().setLayout(formGraphicsLayout);
        formGraphicsLayout.setHorizontalGroup(
            formGraphicsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formGraphicsLayout.createSequentialGroup()
                .addGroup(formGraphicsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formGraphicsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(103, 103, 103))
        );
        formGraphicsLayout.setVerticalGroup(
            formGraphicsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formGraphicsLayout.createSequentialGroup()
                .addGroup(formGraphicsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, formGraphicsLayout.createSequentialGroup()
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45))
                    .addGroup(formGraphicsLayout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        formGraphics.setBounds(10, 10, 790, 660);
        jDesktopPane1.add(formGraphics, javax.swing.JLayeredPane.DEFAULT_LAYER);

        formPathRelinking.setTitle("Path Relinking Generator");

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preview Values", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel16.setPreferredSize(new java.awt.Dimension(325, 310));

        jLabel58.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel58.setText("Initial Time:");

        jLabel59.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel59.setText("Final Time (estimative 1):");

        jLabel60.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel60.setText("Total Time:");

        jPanel18.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel61.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel61.setText("Partial Worse:");

        jLabel62.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel62.setText("Partial Best:");

        jLabel63.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel63.setText("Fintess Value:");

        jLabel64.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel64.setText("Individual:");

        jLabel65.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel65.setText("Generation:");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel65)
                    .addComponent(jLabel64)
                    .addComponent(jLabel63)
                    .addComponent(jLabel62)
                    .addComponent(jLabel61))
                .addContainerGap(215, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel64)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel63)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel62)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel61)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel76.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel76.setText("Progress");

        jLabel66.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel66.setText("Final Best:");

        jLabel67.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel67.setText("Final Worse:");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel66)
                    .addComponent(jLabel67)
                    .addComponent(jLabel76))
                .addContainerGap(241, Short.MAX_VALUE))
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jProgressBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel58)
                    .addComponent(jLabel59)
                    .addComponent(jLabel60))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel58)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel59)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel60)
                .addGap(12, 12, 12)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel66)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel76)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fixed Values", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel13.setPreferredSize(new java.awt.Dimension(410, 150));

        jLabel72.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel72.setText("File with Fixed Values");

        jTextField17.setPreferredSize(new java.awt.Dimension(370, 25));

        jLabel77.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel77.setText("Type of Cost");

        jLabel121.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel121.setText("Type of Crossing");

        jComboBox23.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox23.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cross", "Diagonal" }));
        jComboBox23.setPreferredSize(new java.awt.Dimension(83, 25));

        jComboBox24.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox24.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "(0.065 * distance) + 0.85", "(1 / Cost) + 1 =>" }));
        jComboBox24.setPreferredSize(new java.awt.Dimension(200, 25));

        jButton12.setText("...");
        jButton12.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jSpinner17.setPreferredSize(new java.awt.Dimension(34, 25));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel72)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel121)
                                    .addComponent(jComboBox23, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel13Layout.createSequentialGroup()
                                        .addComponent(jComboBox24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinner17, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                                    .addComponent(jLabel77)))
                            .addComponent(jTextField17, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel121)
                    .addComponent(jLabel77))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel72)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Path Relinking Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel23.setPreferredSize(new java.awt.Dimension(425, 110));

        relinkingElements.add(jRadioButton9);
        jRadioButton9.setFont(new java.awt.Font("Dialog", 1, 11));
        jRadioButton9.setText("All Elements of Archive");

        relinkingElements.add(jRadioButton10);
        jRadioButton10.setFont(new java.awt.Font("Dialog", 1, 11));
        jRadioButton10.setText("The First");

        jLabel68.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel68.setText("Elements");

        relinkingElements.add(jRadioButton11);
        jRadioButton11.setFont(new java.awt.Font("Dialog", 1, 11));
        jRadioButton11.setSelected(true);
        jRadioButton11.setText("The Randomic");

        jLabel71.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel71.setText("Elements");

        jSpinner21.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(10), null, null, Integer.valueOf(1)));
        jSpinner21.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner21, ""));
        jSpinner21.setPreferredSize(new java.awt.Dimension(42, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton10, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner21, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner22.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(11), null, null, Integer.valueOf(1)));
        jSpinner22.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner22, ""));
        jSpinner22.setPreferredSize(new java.awt.Dimension(42, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton11, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner22, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jRadioButton10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner21, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jLabel68, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jRadioButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner22, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel71))
                    .addComponent(jRadioButton9))
                .addGap(260, 260, 260))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(jRadioButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton10)
                    .addComponent(jSpinner21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel68))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton11)
                    .addComponent(jLabel71)
                    .addComponent(jSpinner22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mapping", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel8.setPreferredSize(new java.awt.Dimension(410, 215));

        jPanel33.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Placement", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel140.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel140.setText("Orientation");

        jComboBox4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- NULL --", "Between Lines + 1", "Between Lines", "Between Coluns + 1", "Between Coluns", "Next Lines", "Next Coluns" }));
        jComboBox4.setSelectedIndex(1);
        jComboBox4.setMinimumSize(new java.awt.Dimension(150, 25));
        jComboBox4.setPreferredSize(new java.awt.Dimension(150, 25));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Variation");

        jComboBox44.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox44.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Unidirectional", "Directional" }));
        jComboBox44.setMaximumSize(new java.awt.Dimension(160, 23));
        jComboBox44.setPreferredSize(new java.awt.Dimension(115, 25));

        jComboBox5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Depth", "Random", "Modified Depth" }));
        jComboBox5.setSelectedIndex(3);
        jComboBox5.setPreferredSize(new java.awt.Dimension(127, 25));

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel33.setText("Type of Toroide");

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel34.setText("Time");

        jSpinner15.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(1L), null, null, Long.valueOf(1L)));
        jSpinner15.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner15, ""));
        jSpinner15.setPreferredSize(new java.awt.Dimension(98, 25));

        jSpinner37.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(11), null, null, Integer.valueOf(1)));
        jSpinner37.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner37, ""));
        jSpinner37.setPreferredSize(new java.awt.Dimension(50, 25));

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel37.setText("Population");

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel46.setText("File with Population");

        jTextField10.setText("population.xml");
        jTextField10.setAutoscrolls(false);
        jTextField10.setMaximumSize(new java.awt.Dimension(285, 20));
        jTextField10.setPreferredSize(new java.awt.Dimension(370, 25));

        jButton11.setText("...");
        jButton11.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addGap(291, 291, 291)
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel140)))
                    .addComponent(jLabel46)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(1, 1, 1)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34)
                    .addComponent(jSpinner37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(jSpinner15, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel140)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addGap(31, 31, 31)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel46)
                            .addComponent(jLabel37))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jSpinner37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24))
        );

        jPanel34.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Routing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel47.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel47.setText("Algorithm");

        jComboBox19.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox19.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dijsktra - Homogeneous", "Path Finder - Homogeneous" }));
        jComboBox19.setPreferredSize(new java.awt.Dimension(205, 25));

        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel51.setText("Up");

        jSpinner16.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(25), null, null, Integer.valueOf(1)));
        jSpinner16.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner16, ""));
        jSpinner16.setPreferredSize(new java.awt.Dimension(42, 25));

        jComboBox22.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox22.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Average distance", "Sum conections" }));
        jComboBox22.setSelectedIndex(1);
        jComboBox22.setPreferredSize(new java.awt.Dimension(135, 25));
        jComboBox22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox22ActionPerformed(evt);
            }
        });

        jLabel52.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel52.setText("Format of Calc");

        jLabel53.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel53.setText("Down");

        jSpinner38.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(25), null, null, Integer.valueOf(1)));
        jSpinner38.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner38, ""));
        jSpinner38.setPreferredSize(new java.awt.Dimension(42, 25));

        jLabel168.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel168.setText("File for Statistics");

        jTextField13.setText("statistics.txt");
        jTextField13.setAutoscrolls(false);
        jTextField13.setMaximumSize(new java.awt.Dimension(285, 20));
        jTextField13.setPreferredSize(new java.awt.Dimension(370, 25));

        jCheckBox13.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox13.setSelected(true);
        jCheckBox13.setText("Clear File");

        jButton59.setText("...");
        jButton59.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton59.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton59ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel47)
                            .addComponent(jComboBox19, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel52)
                            .addComponent(jComboBox22, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel51)
                            .addComponent(jSpinner16, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel53)
                            .addComponent(jSpinner38, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel34Layout.createSequentialGroup()
                            .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton59, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel34Layout.createSequentialGroup()
                            .addComponent(jLabel168)
                            .addGap(34, 34, 34)
                            .addComponent(jCheckBox13)
                            .addGap(156, 156, 156))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addComponent(jLabel51)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel34Layout.createSequentialGroup()
                        .addComponent(jLabel52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel168)
                    .addComponent(jCheckBox13, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton59, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel34, 0, 491, Short.MAX_VALUE)
            .addComponent(jPanel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Benchmarks", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel20.setPreferredSize(new java.awt.Dimension(325, 155));

        jTextField27.setText("benchmarks.xml");
        jTextField27.setPreferredSize(new java.awt.Dimension(116, 25));

        jButton24.setText("...");
        jButton24.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jLabel57.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel57.setText("File with Benchmarks");

        jList5.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(jList5);

        jList6.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane6.setViewportView(jList6);

        jButton25.setText(">");
        jButton25.setPreferredSize(new java.awt.Dimension(45, 25));
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jButton26.setText("<");
        jButton26.setPreferredSize(new java.awt.Dimension(45, 25));
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel56)
                    .addComponent(jLabel57)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                            .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jLabel56)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jButton15.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton15.setText("Start");
        jButton15.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton22.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton22.setText("Close");
        jButton22.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout formPathRelinkingLayout = new javax.swing.GroupLayout(formPathRelinking.getContentPane());
        formPathRelinking.getContentPane().setLayout(formPathRelinkingLayout);
        formPathRelinkingLayout.setHorizontalGroup(
            formPathRelinkingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formPathRelinkingLayout.createSequentialGroup()
                .addGroup(formPathRelinkingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel13, 0, 503, Short.MAX_VALUE)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE))
                .addGroup(formPathRelinkingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, formPathRelinkingLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(formPathRelinkingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(formPathRelinkingLayout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67))))
        );
        formPathRelinkingLayout.setVerticalGroup(
            formPathRelinkingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formPathRelinkingLayout.createSequentialGroup()
                .addGroup(formPathRelinkingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formPathRelinkingLayout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(formPathRelinkingLayout.createSequentialGroup()
                        .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(formPathRelinkingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        formPathRelinking.setBounds(10, 10, 850, 560);
        jDesktopPane1.add(formPathRelinking, javax.swing.JLayeredPane.DEFAULT_LAYER);

        formSimulateAnnealing.setTitle("Simulated Annealing Generator");

        jButton31.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton31.setText("Start");
        jButton31.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jButton32.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton32.setText("Close");
        jButton32.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jPanel27.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Benchmarks", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel27.setPreferredSize(new java.awt.Dimension(320, 155));

        jTextField41.setText("benchmarks.xml");
        jTextField41.setPreferredSize(new java.awt.Dimension(116, 25));

        jButton33.setText("...");
        jButton33.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });

        jLabel88.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel88.setText("File with Benchmarks");

        jList7.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane7.setViewportView(jList7);

        jList8.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane8.setViewportView(jList8);

        jButton34.setText(">");
        jButton34.setPreferredSize(new java.awt.Dimension(45, 25));
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });

        jButton35.setText("<");
        jButton35.setPreferredSize(new java.awt.Dimension(45, 25));
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel87)
                    .addComponent(jLabel88)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel27Layout.createSequentialGroup()
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                            .addComponent(jTextField41, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addContainerGap())
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addComponent(jLabel87)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel88)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preview Values", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel21.setPreferredSize(new java.awt.Dimension(320, 270));

        jLabel84.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel84.setText("Initial Time:");

        jLabel85.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel85.setText("Final Time:");

        jLabel86.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel86.setText("Total Time:");

        jPanel22.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel91.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel91.setText("Partial Worse:");

        jLabel92.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel92.setText("Partial Best:");

        jLabel93.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel93.setText("Fintess Value:");

        jLabel94.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel94.setText("Individual:");

        jLabel95.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel95.setText("Temperature:");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel95)
                    .addComponent(jLabel94)
                    .addComponent(jLabel93)
                    .addComponent(jLabel92)
                    .addComponent(jLabel91))
                .addContainerGap(210, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel95)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel94)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel93)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel92)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel91)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel97.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel97.setText("Final Best:");

        jLabel98.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel98.setText("Final Worse:");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel97)
                    .addComponent(jLabel98)
                    .addComponent(jLabel84)
                    .addComponent(jLabel85)
                    .addComponent(jLabel86))
                .addContainerGap(236, Short.MAX_VALUE))
            .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(jLabel84)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel85)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel86)
                .addGap(12, 12, 12)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel97)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel98))
        );

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fixed Values", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel19.setPreferredSize(new java.awt.Dimension(410, 150));

        jLabel104.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel104.setText("File with Fixed Values");

        jTextField20.setPreferredSize(new java.awt.Dimension(370, 25));

        jLabel108.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel108.setText("Type of Cost");

        jLabel123.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel123.setText("Type of Crossing");

        jComboBox28.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox28.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cross", "Diagonal" }));
        jComboBox28.setPreferredSize(new java.awt.Dimension(83, 25));

        jComboBox31.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox31.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "(0.065 * distance) + 0.85", "(1 / Cost) + 1 =>" }));
        jComboBox31.setPreferredSize(new java.awt.Dimension(200, 25));

        jButton16.setText("...");
        jButton16.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jSpinner24.setPreferredSize(new java.awt.Dimension(34, 25));

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel104)
                    .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel19Layout.createSequentialGroup()
                            .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel123)
                                .addComponent(jComboBox28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel108)
                                .addComponent(jComboBox31, 0, 148, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jSpinner24, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel19Layout.createSequentialGroup()
                            .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(1, 1, 1))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel123)
                    .addComponent(jLabel108))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel104)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mapping", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel12.setPreferredSize(new java.awt.Dimension(410, 215));

        jPanel35.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Placement", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel141.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel141.setText("Orientation");

        jComboBox7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- NULL --", "Between Lines + 1", "Between Lines", "Between Coluns + 1", "Between Coluns", "Next Lines", "Next Coluns" }));
        jComboBox7.setSelectedIndex(1);
        jComboBox7.setMinimumSize(new java.awt.Dimension(150, 25));
        jComboBox7.setPreferredSize(new java.awt.Dimension(150, 25));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Variation");

        jComboBox45.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox45.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Unidirectional", "Directional" }));
        jComboBox45.setMaximumSize(new java.awt.Dimension(160, 23));
        jComboBox45.setPreferredSize(new java.awt.Dimension(115, 25));

        jComboBox8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Depth", "Random", "Modified Depth" }));
        jComboBox8.setSelectedIndex(3);
        jComboBox8.setPreferredSize(new java.awt.Dimension(127, 25));

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel35.setText("Type of Toroide");

        jLabel54.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel54.setText("Time");

        jSpinner18.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(1L), null, null, Long.valueOf(1L)));
        jSpinner18.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner18, ""));
        jSpinner18.setPreferredSize(new java.awt.Dimension(98, 25));

        jSpinner39.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(11), null, null, Integer.valueOf(1)));
        jSpinner39.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner39, ""));
        jSpinner39.setPreferredSize(new java.awt.Dimension(50, 25));

        jLabel55.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel55.setText("Population");

        jLabel69.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel69.setText("File with Population");

        jTextField14.setText("population.xml");
        jTextField14.setAutoscrolls(false);
        jTextField14.setMaximumSize(new java.awt.Dimension(285, 20));
        jTextField14.setPreferredSize(new java.awt.Dimension(370, 25));

        jButton14.setText("...");
        jButton14.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addGap(291, 291, 291)
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel35)
                            .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel141)))
                    .addComponent(jLabel69)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(1, 1, 1)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel54)
                    .addComponent(jSpinner39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55)
                    .addComponent(jSpinner18, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jLabel141)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jLabel54)
                        .addGap(31, 31, 31)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel69)
                            .addComponent(jLabel55))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jSpinner39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24))
        );

        jPanel36.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Routing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel70.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel70.setText("Algorithm");

        jComboBox25.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox25.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dijsktra - Homogeneous", "Path Finder - Homogeneous" }));
        jComboBox25.setPreferredSize(new java.awt.Dimension(205, 25));

        jLabel73.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel73.setText("Up");

        jSpinner19.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(25), null, null, Integer.valueOf(1)));
        jSpinner19.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner19, ""));
        jSpinner19.setPreferredSize(new java.awt.Dimension(42, 25));

        jComboBox26.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox26.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Average distance", "Sum conections" }));
        jComboBox26.setSelectedIndex(1);
        jComboBox26.setPreferredSize(new java.awt.Dimension(135, 25));
        jComboBox26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox26ActionPerformed(evt);
            }
        });

        jLabel78.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel78.setText("Format of Calc");

        jLabel80.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel80.setText("Down");

        jSpinner40.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(25), null, null, Integer.valueOf(1)));
        jSpinner40.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner40, ""));
        jSpinner40.setPreferredSize(new java.awt.Dimension(42, 25));

        jLabel169.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel169.setText("File for Statistics");

        jTextField16.setText("statistics.txt");
        jTextField16.setAutoscrolls(false);
        jTextField16.setMaximumSize(new java.awt.Dimension(285, 20));
        jTextField16.setPreferredSize(new java.awt.Dimension(370, 25));

        jCheckBox14.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox14.setSelected(true);
        jCheckBox14.setText("Clear File");

        jButton60.setText("...");
        jButton60.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton60.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton60ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel70)
                            .addComponent(jComboBox25, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel78)
                            .addComponent(jComboBox26, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel73)
                            .addComponent(jSpinner19, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel80)
                            .addComponent(jSpinner40, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel36Layout.createSequentialGroup()
                        .addComponent(jTextField16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton60, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(jLabel169)
                        .addGap(34, 34, 34)
                        .addComponent(jCheckBox14)
                        .addGap(156, 156, 156)))
                .addContainerGap())
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(jLabel80)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(jLabel73)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(jLabel70)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addComponent(jLabel78)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel169)
                    .addComponent(jCheckBox14, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton60, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel36, 0, 491, Short.MAX_VALUE)
            .addComponent(jPanel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel28.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Simulated Annealing Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel28.setPreferredSize(new java.awt.Dimension(430, 150));

        annealingElements.add(jRadioButton14);
        jRadioButton14.setFont(new java.awt.Font("Dialog", 1, 11));
        jRadioButton14.setText("Tk = αTk-1 ∀k >= 1");
        jRadioButton14.setToolTipText("0 < α < 1");

        annealingElements.add(jRadioButton15);
        jRadioButton15.setFont(new java.awt.Font("Dialog", 1, 11));
        jRadioButton15.setText("Tk = (Tk-1) / (1+(γ√Tk-1)) ∀k >= 1");
        jRadioButton15.setToolTipText("0 < γ < 1");

        annealingElements.add(jRadioButton16);
        jRadioButton16.setFont(new java.awt.Font("Dialog", 1, 11));
        jRadioButton16.setText("Tk =");
        jRadioButton16.setToolTipText("Pass mouse at side");

        jLabel90.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel90.setText("Tk-1");

        annealingElements.add(jRadioButton17);
        jRadioButton17.setFont(new java.awt.Font("Dialog", 1, 11));
        jRadioButton17.setSelected(true);
        jRadioButton17.setText("Tk =");

        jSpinner31.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.9d), null, null, Double.valueOf(0.1d)));
        jSpinner31.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner31, ""));
        jSpinner31.setPreferredSize(new java.awt.Dimension(38, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jRadioButton17, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner31, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel45.setText("<HTML><TABLE><TR><TD ROWSPAN=2><H1>{</H1></TD><TD><P>β Tk-1</P></TD><TD><P>if k = 1</P></TD></TR><TR><TD><P>(Tk-1) / (1+ γ(Tk-1))</P></TD><TD><P>if k >= 2</P></TD></TR></TABLE></HTML>");

        jLabel105.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel105.setText("MaxIterations");

        jSpinner28.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(10), null, null, Integer.valueOf(1)));
        jSpinner28.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner28, ""));
        jSpinner28.setPreferredSize(new java.awt.Dimension(42, 25));

        jSpinner29.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(1000.0d), null, null, Double.valueOf(0.1d)));
        jSpinner29.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner29, ""));
        jSpinner29.setPreferredSize(new java.awt.Dimension(62, 25));

        jLabel106.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel106.setText("Initial Temp");

        jSpinner30.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0010d), null, null, Double.valueOf(0.0010d)));
        jSpinner30.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner30, ""));
        jSpinner30.setPreferredSize(new java.awt.Dimension(54, 25));

        jLabel107.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel107.setText("Final Temp");

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton15)
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addComponent(jRadioButton16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel45))
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addComponent(jRadioButton17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner31, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(107, 107, 107)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel107)
                    .addComponent(jLabel106)
                    .addComponent(jLabel105)
                    .addComponent(jSpinner29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSpinner30, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                    .addComponent(jSpinner28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addComponent(jLabel105)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel106)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel107)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel28Layout.createSequentialGroup()
                        .addComponent(jRadioButton14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton15)
                        .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel45))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioButton17)
                            .addComponent(jLabel90)
                            .addComponent(jSpinner31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        javax.swing.GroupLayout formSimulateAnnealingLayout = new javax.swing.GroupLayout(formSimulateAnnealing.getContentPane());
        formSimulateAnnealing.getContentPane().setLayout(formSimulateAnnealingLayout);
        formSimulateAnnealingLayout.setHorizontalGroup(
            formSimulateAnnealingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formSimulateAnnealingLayout.createSequentialGroup()
                .addGroup(formSimulateAnnealingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(formSimulateAnnealingLayout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(jButton31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formSimulateAnnealingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formSimulateAnnealingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel27, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                        .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.TRAILING, 0, 320, Short.MAX_VALUE))
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(310, 310, 310))
        );
        formSimulateAnnealingLayout.setVerticalGroup(
            formSimulateAnnealingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formSimulateAnnealingLayout.createSequentialGroup()
                .addGroup(formSimulateAnnealingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formSimulateAnnealingLayout.createSequentialGroup()
                        .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(formSimulateAnnealingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(formSimulateAnnealingLayout.createSequentialGroup()
                        .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addGroup(formSimulateAnnealingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        formSimulateAnnealing.setBounds(10, 10, 850, 600);
        jDesktopPane1.add(formSimulateAnnealing, javax.swing.JLayeredPane.DEFAULT_LAYER);

        formDataflow.setTitle("Dataflow Generator");

        jLabel22.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Dataflow Graph", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", 1, 11))); // NOI18N
        jLabel22.setPreferredSize(new java.awt.Dimension(500, 450));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select Generators", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel5.setPreferredSize(new java.awt.Dimension(500, 150));

        jCheckBox5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox5.setSelected(true);
        jCheckBox5.setText("Convert Graph to XML");

        jCheckBox6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox6.setSelected(true);
        jCheckBox6.setText("Generate Dataflow");

        jCheckBox4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox4.setSelected(true);
        jCheckBox4.setText("Generate Graph");

        jLabel111.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel111.setText("File from Graph");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox4, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel111, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jTextField47.setText("graph.tgffopt");
        jTextField47.setPreferredSize(new java.awt.Dimension(260, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox4, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jTextField47, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner32.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(200), null, null, Integer.valueOf(1)));
        jSpinner32.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner32, ""));
        jSpinner32.setPreferredSize(new java.awt.Dimension(50, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox4, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner32, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel110.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel110.setText("Nodos");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox4, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel110, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel112.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel112.setText("Input");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox4, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel112, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel113.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel113.setText("Out");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox4, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel113, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner33.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(2), null, null, Integer.valueOf(1)));
        jSpinner33.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner33, ""));
        jSpinner33.setPreferredSize(new java.awt.Dimension(34, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox4, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner33, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner34.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(2), null, null, Integer.valueOf(1)));
        jSpinner34.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner34, ""));
        jSpinner34.setPreferredSize(new java.awt.Dimension(34, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox4, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner34, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton37.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton37.setText("Start");
        jButton37.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        jButton38.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton38.setText("Close");
        jButton38.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        jButton36.setText("...");
        jButton36.setPreferredSize(new java.awt.Dimension(25, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox4, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jButton36, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        jCheckBox7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox7.setText("Clear tgffBench");

        jCheckBox8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox8.setSelected(true);
        jCheckBox8.setText("Close Graph");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox7)
                            .addComponent(jCheckBox4))
                        .addGap(37, 37, 37)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox8)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jCheckBox6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox5))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinner32, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel110))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSpinner33, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel112))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel113, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSpinner34, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel111)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65)
                        .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(267, 267, 267))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jCheckBox6)
                    .addComponent(jCheckBox5)
                    .addComponent(jCheckBox4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 19, Short.MAX_VALUE)
                    .addComponent(jCheckBox8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel112)
                            .addComponent(jLabel113)
                            .addComponent(jLabel110))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jSpinner33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel111))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout formDataflowLayout = new javax.swing.GroupLayout(formDataflow.getContentPane());
        formDataflow.getContentPane().setLayout(formDataflowLayout);
        formDataflowLayout.setHorizontalGroup(
            formDataflowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formDataflowLayout.createSequentialGroup()
                .addGroup(formDataflowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(formDataflowLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        formDataflowLayout.setVerticalGroup(
            formDataflowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formDataflowLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        formDataflow.setBounds(20, 10, 520, 650);
        jDesktopPane1.add(formDataflow, javax.swing.JLayeredPane.DEFAULT_LAYER);

        formPattern.setTitle("Extractor of Tologies of Benchmarks");

        jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select Generators", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel24.setPreferredSize(new java.awt.Dimension(500, 150));

        jLabel115.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel115.setText("File from New Topologies");

        jTextField48.setText("topology.xml");
        jTextField48.setPreferredSize(new java.awt.Dimension(260, 25));

        jLabel116.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel116.setText("Segments");

        jSpinner35.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(8), null, null, Integer.valueOf(1)));
        jSpinner35.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner35, ""));
        jSpinner35.setPreferredSize(new java.awt.Dimension(42, 25));

        jLabel130.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel130.setText("Orientation");

        jComboBox34.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox34.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Unidirectional", "Directional" }));
        jComboBox34.setPreferredSize(new java.awt.Dimension(83, 25));

        jLabel129.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel129.setText("Type of Cost");

        jComboBox35.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox35.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "(0.065 * distance) + 0.85", "(1 / Cost) + 1 =>" }));
        jComboBox35.setPreferredSize(new java.awt.Dimension(200, 25));

        jCheckBox9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox9.setText("Clear Destination");

        jCheckBox10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox10.setText("Max Conection 50%");

        jButton41.setText("...");
        jButton41.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        jSpinner13.setPreferredSize(new java.awt.Dimension(34, 25));

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addComponent(jComboBox35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinner13, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel129)
                            .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel115)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel130)
                            .addComponent(jComboBox34, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addComponent(jSpinner35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25))
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addComponent(jLabel116)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox10)
                            .addComponent(jCheckBox9))))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel24Layout.createSequentialGroup()
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel130)
                            .addComponent(jLabel116))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel24Layout.createSequentialGroup()
                        .addComponent(jCheckBox9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addComponent(jLabel115)
                .addGap(6, 6, 6)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel129)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel29.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Benchmarks", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel29.setPreferredSize(new java.awt.Dimension(320, 155));

        jTextField42.setText("benchmarks.xml");
        jTextField42.setPreferredSize(new java.awt.Dimension(116, 25));

        jButton42.setText("...");
        jButton42.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });

        jLabel99.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel99.setText("File with Benchmarks");

        jList9.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane9.setViewportView(jList9);

        jList10.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane10.setViewportView(jList10);

        jButton44.setText(">");
        jButton44.setPreferredSize(new java.awt.Dimension(45, 25));
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton44ActionPerformed(evt);
            }
        });

        jButton45.setText("<");
        jButton45.setPreferredSize(new java.awt.Dimension(45, 25));
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton45ActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel28.setText("File with Initial Topology");

        jTextField9.setPreferredSize(new java.awt.Dimension(370, 25));

        jButton20.setText("...");
        jButton20.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jCheckBox11.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox11.setText("Unique Result");

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addComponent(jLabel96)
                .addGap(308, 308, 308))
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel29Layout.createSequentialGroup()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel29Layout.createSequentialGroup()
                                .addComponent(jButton44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                            .addGroup(jPanel29Layout.createSequentialGroup()
                                .addComponent(jButton45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)))
                        .addGap(27, 27, 27))
                    .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel29Layout.createSequentialGroup()
                            .addComponent(jLabel99)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jCheckBox11))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel29Layout.createSequentialGroup()
                            .addComponent(jTextField42, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel29Layout.createSequentialGroup()
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addComponent(jLabel28)
                    .addComponent(jButton20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addComponent(jLabel96)
                .addGap(186, 186, 186))
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel99)
                    .addComponent(jCheckBox11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addComponent(jButton44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel89.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Dataflow Graph", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", 1, 11))); // NOI18N
        jLabel89.setPreferredSize(new java.awt.Dimension(645, 450));
        jTabbedPane3.addTab("Graph", jLabel89);

        jSplitPane4.setDividerLocation(300);

        jTextArea1.setColumns(30);
        jTextArea1.setRows(5);
        jTextArea1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextArea1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextArea1MouseReleased(evt);
            }
        });
        jScrollPane19.setViewportView(jTextArea1);

        jSplitPane4.setLeftComponent(jScrollPane19);

        jTextArea2.setColumns(30);
        jTextArea2.setRows(5);
        jTextArea2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextArea2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextArea2MouseReleased(evt);
            }
        });
        jScrollPane20.setViewportView(jTextArea2);

        jSplitPane4.setRightComponent(jScrollPane20);

        jTabbedPane3.addTab("ASAP Edges and Totals", jSplitPane4);

        jTabbedPane2.addTab("ASAP", jTabbedPane3);

        jLabel100.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Dataflow Graph", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", 1, 11))); // NOI18N
        jLabel100.setPreferredSize(new java.awt.Dimension(645, 450));
        jTabbedPane4.addTab("Graph", jLabel100);

        jSplitPane2.setDividerLocation(300);

        jTextArea3.setColumns(30);
        jTextArea3.setRows(5);
        jTextArea3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextArea3MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextArea3MouseReleased(evt);
            }
        });
        jScrollPane15.setViewportView(jTextArea3);

        jSplitPane2.setLeftComponent(jScrollPane15);

        jTextArea4.setColumns(30);
        jTextArea4.setRows(5);
        jTextArea4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextArea4MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextArea4MouseReleased(evt);
            }
        });
        jScrollPane16.setViewportView(jTextArea4);

        jSplitPane2.setRightComponent(jScrollPane16);

        jTabbedPane4.addTab("ALAP Edges and Totals", jSplitPane2);

        jTabbedPane2.addTab("ALAP", jTabbedPane4);

        jLabel120.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Dataflow Graph", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", 1, 11))); // NOI18N
        jLabel120.setPreferredSize(new java.awt.Dimension(645, 450));
        jTabbedPane5.addTab("Graph", jLabel120);

        jSplitPane5.setDividerLocation(300);

        jTextArea5.setColumns(30);
        jTextArea5.setRows(5);
        jTextArea5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextArea5MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextArea5MouseReleased(evt);
            }
        });
        jScrollPane21.setViewportView(jTextArea5);

        jSplitPane5.setLeftComponent(jScrollPane21);

        jTextArea6.setColumns(30);
        jTextArea6.setRows(5);
        jTextArea6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextArea6MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextArea6MouseReleased(evt);
            }
        });
        jScrollPane22.setViewportView(jTextArea6);

        jSplitPane5.setRightComponent(jScrollPane22);

        jTabbedPane5.addTab("Breadth Asc Edges and Totals", jSplitPane5);

        jTabbedPane2.addTab("Breadth Ascendent", jTabbedPane5);

        jLabel125.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Dataflow Graph", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", 1, 11))); // NOI18N
        jLabel125.setPreferredSize(new java.awt.Dimension(645, 450));
        jTabbedPane6.addTab("Graph", jLabel125);

        jSplitPane6.setDividerLocation(300);

        jTextArea7.setColumns(30);
        jTextArea7.setRows(5);
        jTextArea7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextArea7MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextArea7MouseReleased(evt);
            }
        });
        jScrollPane23.setViewportView(jTextArea7);

        jSplitPane6.setLeftComponent(jScrollPane23);

        jTextArea8.setColumns(30);
        jTextArea8.setRows(5);
        jTextArea8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextArea8MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTextArea8MouseReleased(evt);
            }
        });
        jScrollPane24.setViewportView(jTextArea8);

        jSplitPane6.setRightComponent(jScrollPane24);

        jTabbedPane6.addTab("Breadth Desc Edges and Totals", jSplitPane6);

        jTabbedPane2.addTab("Breadth Descendent", jTabbedPane6);

        jTextArea9.setColumns(20);
        jTextArea9.setRows(5);
        jScrollPane13.setViewportView(jTextArea9);

        jTabbedPane2.addTab("Resume for All Algoritms", jScrollPane13);

        jButton39.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton39.setText("Start");
        jButton39.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton39ActionPerformed(evt);
            }
        });

        jButton40.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton40.setText("Close");
        jButton40.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout formPatternLayout = new javax.swing.GroupLayout(formPattern.getContentPane());
        formPattern.getContentPane().setLayout(formPatternLayout);
        formPatternLayout.setHorizontalGroup(
            formPatternLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formPatternLayout.createSequentialGroup()
                .addGroup(formPatternLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formPatternLayout.createSequentialGroup()
                        .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(formPatternLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(formPatternLayout.createSequentialGroup()
                                .addGap(69, 69, 69)
                                .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(formPatternLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 690, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        formPatternLayout.setVerticalGroup(
            formPatternLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formPatternLayout.createSequentialGroup()
                .addGroup(formPatternLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formPatternLayout.createSequentialGroup()
                        .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(formPatternLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        formPattern.setBounds(20, 10, 700, 670);
        jDesktopPane1.add(formPattern, javax.swing.JLayeredPane.DEFAULT_LAYER);

        formMapping.setTitle("Mapping form");

        jButton28.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton28.setText("Close");
        jButton28.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jPanel30.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Benchmarks", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel30.setPreferredSize(new java.awt.Dimension(330, 150));

        jTextField11.setText("benchmarks.xml");
        jTextField11.setPreferredSize(new java.awt.Dimension(280, 25));

        jButton17.setText("...");
        jButton17.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jLabel138.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel138.setText("File with Benchmarks");

        jList3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(jList3);

        jList4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(jList4);

        jButton18.setText(">");
        jButton18.setPreferredSize(new java.awt.Dimension(45, 25));
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setText("<");
        jButton19.setPreferredSize(new java.awt.Dimension(45, 25));
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel137)
            .addComponent(jLabel138)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel137)
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addComponent(jLabel138)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel30Layout.createSequentialGroup()
                                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mapping", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel10.setPreferredSize(new java.awt.Dimension(410, 215));

        jPanel31.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Placement", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel142.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel142.setText("Orientation");

        jComboBox9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- NULL --", "Between Lines + 1", "Between Lines", "Between Coluns + 1", "Between Coluns", "Next Lines", "Next Coluns" }));
        jComboBox9.setSelectedIndex(1);
        jComboBox9.setMinimumSize(new java.awt.Dimension(150, 25));
        jComboBox9.setPreferredSize(new java.awt.Dimension(150, 25));

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel27.setText("Variation");

        jComboBox46.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox46.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Unidirectional", "Directional" }));
        jComboBox46.setMaximumSize(new java.awt.Dimension(160, 23));
        jComboBox46.setPreferredSize(new java.awt.Dimension(115, 25));

        jComboBox10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Depth", "Random", "Modified Depth" }));
        jComboBox10.setSelectedIndex(3);
        jComboBox10.setPreferredSize(new java.awt.Dimension(127, 25));

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel36.setText("Type of Toroide");

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel40.setText("Time");

        jSpinner20.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(1L), null, null, Long.valueOf(1L)));
        jSpinner20.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner20, ""));
        jSpinner20.setPreferredSize(new java.awt.Dimension(98, 25));

        jSpinner41.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(11), null, null, Integer.valueOf(1)));
        jSpinner41.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner41, ""));
        jSpinner41.setPreferredSize(new java.awt.Dimension(50, 25));

        jLabel48.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel48.setText("Population");

        jLabel49.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel49.setText("File with Population");

        jTextField18.setText("population.xml");
        jTextField18.setAutoscrolls(false);
        jTextField18.setMaximumSize(new java.awt.Dimension(285, 20));
        jTextField18.setPreferredSize(new java.awt.Dimension(370, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox16, org.jdesktop.beansbinding.ELProperty.create("${selected == false}"), jTextField18, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton13.setText("...");
        jButton13.setPreferredSize(new java.awt.Dimension(25, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox16, org.jdesktop.beansbinding.ELProperty.create("${selected == false}"), jButton13, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jCheckBox16.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox16.setText("All Populations off default path");
        jCheckBox16.setToolTipText("{home}/files/xml");

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addGap(291, 291, 291)
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel36)
                            .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel49))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox16)
                            .addComponent(jComboBox46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel142))))
                .addGap(1, 1, 1)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40)
                    .addComponent(jSpinner41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48)
                    .addComponent(jSpinner20, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addComponent(jLabel142)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addGap(31, 31, 31)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel49)
                            .addComponent(jLabel48)
                            .addComponent(jCheckBox16, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel31Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jSpinner41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24))
        );

        jPanel37.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Routing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel74.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel74.setText("Algorithm");

        jComboBox18.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox18.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dijsktra - Homogeneous", "Path Finder - Homogeneous" }));
        jComboBox18.setPreferredSize(new java.awt.Dimension(205, 25));

        jLabel81.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel81.setText("Up");

        jSpinner11.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(25), null, null, Integer.valueOf(1)));
        jSpinner11.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner11, ""));
        jSpinner11.setPreferredSize(new java.awt.Dimension(42, 25));

        jComboBox20.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox20.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Average distance", "Sum conections" }));
        jComboBox20.setSelectedIndex(1);
        jComboBox20.setPreferredSize(new java.awt.Dimension(135, 25));
        jComboBox20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox20ActionPerformed(evt);
            }
        });

        jLabel82.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel82.setText("Format of Calc");

        jLabel83.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel83.setText("Down");

        jSpinner12.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(25), null, null, Integer.valueOf(1)));
        jSpinner12.setEditor(new javax.swing.JSpinner.NumberEditor(jSpinner12, ""));
        jSpinner12.setPreferredSize(new java.awt.Dimension(42, 25));

        jLabel170.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel170.setText("File for Statistics");

        jTextField19.setText("statistics.txt");
        jTextField19.setAutoscrolls(false);
        jTextField19.setMaximumSize(new java.awt.Dimension(285, 20));
        jTextField19.setPreferredSize(new java.awt.Dimension(370, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox16, org.jdesktop.beansbinding.ELProperty.create("${selected == false}"), jTextField19, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jCheckBox15.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox15.setSelected(true);
        jCheckBox15.setText("Clear File");

        jButton61.setText("...");
        jButton61.setPreferredSize(new java.awt.Dimension(25, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox16, org.jdesktop.beansbinding.ELProperty.create("${selected == false}"), jButton61, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton61.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton61ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel37Layout = new javax.swing.GroupLayout(jPanel37);
        jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel37Layout.createSequentialGroup()
                        .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel74)
                            .addComponent(jComboBox18, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel82)
                            .addComponent(jComboBox20, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel81)
                            .addComponent(jSpinner11, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel83)
                            .addComponent(jSpinner12, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel37Layout.createSequentialGroup()
                        .addComponent(jTextField19, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton61, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel37Layout.createSequentialGroup()
                        .addComponent(jLabel170)
                        .addGap(34, 34, 34)
                        .addComponent(jCheckBox15)
                        .addGap(156, 156, 156)))
                .addContainerGap())
        );
        jPanel37Layout.setVerticalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel37Layout.createSequentialGroup()
                        .addComponent(jLabel83)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel37Layout.createSequentialGroup()
                        .addComponent(jLabel81)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel37Layout.createSequentialGroup()
                        .addComponent(jLabel74)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel37Layout.createSequentialGroup()
                        .addComponent(jLabel82)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel170)
                    .addComponent(jCheckBox15, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton61, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel37, 0, 491, Short.MAX_VALUE)
            .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fixed Values", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel11.setPreferredSize(new java.awt.Dimension(410, 150));

        jLabel119.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel119.setText("Type of Cost");

        jLabel124.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel124.setText("Type of Crossing");

        jComboBox32.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox32.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cross", "Diagonal" }));
        jComboBox32.setPreferredSize(new java.awt.Dimension(83, 25));

        jComboBox33.setFont(new java.awt.Font("Tahoma", 1, 11));
        jComboBox33.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "(0.065 * distance) + 0.85", "(1 / Cost) + 1 =>" }));
        jComboBox33.setPreferredSize(new java.awt.Dimension(200, 25));

        jButton29.setText("...");
        jButton29.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jSpinner25.setPreferredSize(new java.awt.Dimension(34, 25));

        jLabel109.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel109.setText("File with Fixed Values");

        jTextField15.setPreferredSize(new java.awt.Dimension(370, 25));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel124)
                    .addComponent(jComboBox32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel119, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox33, javax.swing.GroupLayout.Alignment.TRAILING, 0, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner25, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel109)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel124)
                    .addComponent(jLabel119))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSpinner25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel109)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preview Values", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel14.setPreferredSize(new java.awt.Dimension(330, 310));

        jLabel101.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel101.setText("Initial Time:");

        jPanel25.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jLabel118.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel118.setText("Final Value:");

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addComponent(jLabel118)
                .addContainerGap(239, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel118)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel102.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel102.setText("Final Time:");

        jLabel103.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel103.setText("Total Time:");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel101)
                    .addComponent(jLabel102)
                    .addComponent(jLabel103))
                .addContainerGap(242, Short.MAX_VALUE))
            .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel101)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel102)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel103)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel38.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Heuristics", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel38.setPreferredSize(new java.awt.Dimension(330, 150));

        jList11.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "GA cross No", "GA cross PR", "PR", "SA", "ASAP", "ASAP Limited", "ALAP", "ALAP Limited" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList11.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox17, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jList11, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jScrollPane11.setViewportView(jList11);

        jList12.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox17, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jList12, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jScrollPane12.setViewportView(jList12);

        jButton47.setText(">");
        jButton47.setPreferredSize(new java.awt.Dimension(45, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox17, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jButton47, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton47ActionPerformed(evt);
            }
        });

        jButton48.setText("<");
        jButton48.setPreferredSize(new java.awt.Dimension(45, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox17, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jButton48, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton48ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel143)
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(487, 487, 487))
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel143)
            .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel38Layout.createSequentialGroup()
                    .addComponent(jButton47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jButton30.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton30.setText("Close");
        jButton30.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        jButton23.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton23.setText("Start");
        jButton23.setPreferredSize(new java.awt.Dimension(75, 25));
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jCheckBox17.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox17.setText("Run various heuristics");
        jCheckBox17.setToolTipText("With default parameters, to change these parameters access the screen of each heuristic");

        javax.swing.GroupLayout formMappingLayout = new javax.swing.GroupLayout(formMapping.getContentPane());
        formMapping.getContentPane().setLayout(formMappingLayout);
        formMappingLayout.setHorizontalGroup(
            formMappingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formMappingLayout.createSequentialGroup()
                .addGroup(formMappingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formMappingLayout.createSequentialGroup()
                        .addGap(1041, 1041, 1041)
                        .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(formMappingLayout.createSequentialGroup()
                        .addGroup(formMappingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(formMappingLayout.createSequentialGroup()
                                .addGap(78, 78, 78)
                                .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(42, 42, 42)
                                .addComponent(jButton30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(formMappingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel38, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox17)
                            .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        formMappingLayout.setVerticalGroup(
            formMappingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formMappingLayout.createSequentialGroup()
                .addGroup(formMappingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(formMappingLayout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(formMappingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(54, 54, 54)
                        .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(formMappingLayout.createSequentialGroup()
                        .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox17, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jPanel38, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        formMapping.setBounds(10, 10, 850, 430);
        jDesktopPane1.add(formMapping, javax.swing.JLayeredPane.DEFAULT_LAYER);

        fileMenu1.setText("File");

        exitMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem1.setText("Quit");
        exitMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu1.add(exitMenuItem1);

        menuBar1.add(fileMenu1);

        generateMenu1.setText("Generate");

        selectHeuristic1.setText("Heuristics");

        genetic1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        genetic1.setText("Genetic Algorithm");
        genetic1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genetic1ActionPerformed(evt);
            }
        });
        selectHeuristic1.add(genetic1);

        relinking1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        relinking1.setText("Path Relinking");
        relinking1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relinking1ActionPerformed(evt);
            }
        });
        selectHeuristic1.add(relinking1);

        sa1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        sa1.setText("Simulate Annealing");
        sa1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sa1ActionPerformed(evt);
            }
        });
        selectHeuristic1.add(sa1);

        generateMenu1.add(selectHeuristic1);

        mapping1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        mapping1.setText("Mapping");
        mapping1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mapping1ActionPerformed(evt);
            }
        });
        generateMenu1.add(mapping1);
        generateMenu1.add(jSeparator1);

        statistic1.setText("Statistics");

        graphic1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        graphic1.setText("Graphics");
        graphic1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphic1ActionPerformed(evt);
            }
        });
        statistic1.add(graphic1);

        generateMenu1.add(statistic1);

        graphs1.setText("Graphic");

        extract1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        extract1.setText("Extract");
        extract1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extract1ActionPerformed(evt);
            }
        });
        graphs1.add(extract1);

        dataflow1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        dataflow1.setText("Dataflow");
        dataflow1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataflow1ActionPerformed(evt);
            }
        });
        graphs1.add(dataflow1);

        generateMenu1.add(graphs1);

        menuBar1.add(generateMenu1);

        customizationMenu1.setText("Customization");

        aparenceMenu1.setText("Aparence");
        customizationMenu1.add(aparenceMenu1);
        customizationMenu1.add(jSeparator2);

        screen1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, 0));
        screen1.setText("ScreenShot");
        screen1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                screen1ActionPerformed(evt);
            }
        });
        customizationMenu1.add(screen1);

        menuBar1.add(customizationMenu1);

        helpMenu1.setText("Help");

        aboutMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        aboutMenuItem1.setText("About ...");
        aboutMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItem1ActionPerformed(evt);
            }
        });
        helpMenu1.add(aboutMenuItem1);

        menuBar1.add(helpMenu1);

        setJMenuBar(menuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDesktopPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 903, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jDesktopPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 674, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getAccessibleContext().setAccessibleName("archiGen");

        bindingGroup.bind();

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-916)/2, (screenSize.height-731)/2, 916, 731);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            javax.swing.JFileChooser population = new javax.swing.JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
            population.setCurrentDirectory(new java.io.File("files/xml/input/."));
            population.addChoosableFileFilter(filter);
            int iresposta = population.showOpenDialog(this.jButton1);
            if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            jTextField5.setText(population.getSelectedFile().getCanonicalPath());
        } catch (java.io.IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
	}//GEN-LAST:event_jButton3ActionPerformed

	private void aboutMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItem1ActionPerformed
        AboutBox about = new AboutBox();
        about.setVisible(true);
	}//GEN-LAST:event_aboutMenuItem1ActionPerformed

	private void genetic1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genetic1ActionPerformed
        jLabel75.setVisible(false);
        jProgressBar1.setVisible(false);
        formGenetic.setVisible(true);
	}//GEN-LAST:event_genetic1ActionPerformed

	private void graphic1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphic1ActionPerformed
        formGraphics.setVisible(true);
	}//GEN-LAST:event_graphic1ActionPerformed

	private void exitMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItem1ActionPerformed
        System.exit(0);
	}//GEN-LAST:event_exitMenuItem1ActionPerformed

	private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        try {
            javax.swing.JFileChooser population = new javax.swing.JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
            population.setCurrentDirectory(new java.io.File("files/xml/input/."));
            population.addChoosableFileFilter(filter);
            int iresposta = population.showOpenDialog(this.jButton1);
            if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            jTextField8.setText(population.getSelectedFile().getCanonicalPath());
        } catch (java.io.IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
	}//GEN-LAST:event_jButton10ActionPerformed

	private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            javax.swing.JFileChooser benchmarks = new javax.swing.JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
            benchmarks.setCurrentDirectory(new java.io.File("files/benchmarks/."));
            benchmarks.addChoosableFileFilter(filter);
            int iresposta = benchmarks.showOpenDialog(this.jButton1);
            if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            jTextField6.setText(benchmarks.getSelectedFile().getCanonicalPath());
            String[] programas = XMLToFiles.getBenchmarks(jTextField6.getText());
            listSaveBench = new javax.swing.DefaultListModel();
            listBenchFull = new javax.swing.DefaultListModel();
            listLoadBench = new javax.swing.DefaultListModel();
            int i;
            for (i = 0; i < programas.length; ++i) {
                listLoadBench.addElement(programas[i].split(":")[1]);
                listBenchFull.addElement(programas[i]);
            }
            jList1.setModel(listLoadBench);
        } catch (java.io.IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
	}//GEN-LAST:event_jButton4ActionPerformed

	private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int aux = jList1.getSelectedIndex();
        if (aux == -1) {
            javax.swing.JOptionPane.showMessageDialog(null, "Nao foi selecionado nenhum item a ser incluido!", "Nenhum Iten Selecionado", javax.swing.JOptionPane.ERROR_MESSAGE);
        } else {
            listBench.addElement(jList1.getSelectedValue());
            listLoadBench.remove(aux);
            listSaveBench.addElement(listBenchFull.getElementAt(aux));
            listBenchFull.remove(aux);
            jList1.setModel(listLoadBench);
            jList2.setModel(listBench);
        }
	}//GEN-LAST:event_jButton5ActionPerformed

	private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        int aux = jList2.getSelectedIndex();
        if (aux == -1) {
            javax.swing.JOptionPane.showMessageDialog(null, "Nao foi selecionado nenhum item a ser excluido!", "Nenhum Iten Selecionado", javax.swing.JOptionPane.ERROR_MESSAGE);
        } else {
            listLoadBench.addElement(jList2.getSelectedValue());
            listBench.remove(aux);
            listSaveBench.remove(aux);
            jList2.setModel(listBench);
            jList1.setModel(listLoadBench);
        }
	}//GEN-LAST:event_jButton6ActionPerformed

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        formGenetic.setVisible(false);
	}//GEN-LAST:event_jButton2ActionPerformed

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String info;
        info = "info.generator:0#info.individual:0";
        info += "#info.value:0";
        info += "#info.partialBest:" + Double.MAX_VALUE;
        info += "#info.partialWorse:" + Double.MIN_VALUE;
        info += "#info.finalBest:" + Double.MAX_VALUE;
        info += "#info.finalWorse:" + Double.MIN_VALUE;
        info += "#info.new:true";
        info += "#info.zero:true";
        util.setProperties("info.properties", info);
        jButton1.setEnabled(false);
        fim = false;
        if (threadInfoGenetic == null) {
            threadInfoGenetic = new Thread(informationGenetic);
            threadInfoGenetic.start();
            threadInfoGenetic = null;
        }
        if (threadGenetic == null) {
            threadGenetic = new Thread(genetic);
            threadGenetic.start();
            threadGenetic = null;
        }
	}//GEN-LAST:event_jButton1ActionPerformed

	private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        try {
            javax.swing.JFileChooser element = new javax.swing.JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
            element.setCurrentDirectory(new java.io.File("files/xml/."));
            element.addChoosableFileFilter(filter);
            int iresposta = element.showOpenDialog(this.jButton1);
            if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            jTextField7.setText(element.getSelectedFile().getCanonicalPath());
        } catch (java.io.IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
	}//GEN-LAST:event_jButton7ActionPerformed

	private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        formGraphics.setVisible(false);
	}//GEN-LAST:event_jButton9ActionPerformed

	private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        if (threadGraph == null) {
            threadGraph = new Thread(graph);
            threadGraph.start();
            threadGraph = null;
        }
	}//GEN-LAST:event_jButton8ActionPerformed

	private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        try {
            javax.swing.JFileChooser population = new javax.swing.JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
            population.setCurrentDirectory(new java.io.File("files/xml/input/."));
            population.addChoosableFileFilter(filter);
            int iresposta = population.showOpenDialog(this.jButton1);
            if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            jTextField22.setText(population.getSelectedFile().getCanonicalPath());
        } catch (java.io.IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
	}//GEN-LAST:event_jButton21ActionPerformed

	private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        String info;
        info = "info.generator:0#info.individual:0";
        info += "#info.value:0";
        info += "#info.partialBest:" + Double.MAX_VALUE;
        info += "#info.partialWorse:" + Double.MIN_VALUE;
        info += "#info.finalBest:" + Double.MAX_VALUE;
        info += "#info.finalWorse:" + Double.MIN_VALUE;
        info += "#info.new:true";
        info += "#info.zero:true";
        util.setProperties("info.properties", info);
        fim = false;
        if (threadInfoRelinking == null) {
            threadInfoRelinking = new Thread(informationRelinking);
            threadInfoRelinking.start();
            threadInfoRelinking = null;
        }
        if (threadRelinking == null) {
            threadRelinking = new Thread(relinking);
            threadRelinking.start();
            threadRelinking = null;
        }
	}//GEN-LAST:event_jButton15ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {
        formPathRelinking.setVisible(false);
    }

	private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        try {
            javax.swing.JFileChooser benchmarks = new javax.swing.JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
            benchmarks.setCurrentDirectory(new java.io.File("files/benchmarks/."));
            benchmarks.addChoosableFileFilter(filter);
            int iresposta = benchmarks.showOpenDialog(this.jButton1);
            if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            jTextField27.setText(benchmarks.getSelectedFile().getCanonicalPath());
            String[] programas = XMLToFiles.getBenchmarks(jTextField27.getText());
            listSaveBench = new javax.swing.DefaultListModel();
            listBenchFull = new javax.swing.DefaultListModel();
            listLoadBench = new javax.swing.DefaultListModel();
            int i;
            for (i = 0; i < programas.length; ++i) {
                listLoadBench.addElement(programas[i].split(":")[1]);
                listBenchFull.addElement(programas[i]);
            }
            jList5.setModel(listLoadBench);
        } catch (java.io.IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
	}//GEN-LAST:event_jButton24ActionPerformed

	private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        int aux = jList5.getSelectedIndex();
        if (aux == -1) {
            javax.swing.JOptionPane.showMessageDialog(null, "Nao foi selecionado nenhum item a ser incluido!", "Zero Itens Selecionados", javax.swing.JOptionPane.ERROR_MESSAGE);
        } else {
            listBench.addElement(jList5.getSelectedValue());
            listLoadBench.remove(aux);
            listSaveBench.addElement(listBenchFull.getElementAt(aux));
            listBenchFull.remove(aux);
            jList5.setModel(listLoadBench);
            jList6.setModel(listBench);
        }
	}//GEN-LAST:event_jButton25ActionPerformed

	private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        int aux = jList6.getSelectedIndex();
        if (aux == -1) {
            javax.swing.JOptionPane.showMessageDialog(null, "Nao foi selecionado nenhum item a ser excluido!", "Nenhum Iten Selecionado", javax.swing.JOptionPane.ERROR_MESSAGE);
        } else {
            listLoadBench.addElement(jList6.getSelectedValue());
            listBench.remove(aux);
            listSaveBench.remove(aux);
            jList6.setModel(listBench);
            jList5.setModel(listLoadBench);
        }
	}//GEN-LAST:event_jButton26ActionPerformed

	private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        try {
            javax.swing.JFileChooser openFile = new javax.swing.JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.txt File", "txt");
            openFile.setCurrentDirectory(new java.io.File("files/."));
            openFile.addChoosableFileFilter(filter);
            int iresposta = openFile.showOpenDialog(this.jButton1);
            if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            nameGraph = openFile.getSelectedFile().getName();
            path = openFile.getSelectedFile().getParent();
            jTextField34.setText(openFile.getSelectedFile().getCanonicalPath());
        } catch (java.io.IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
	}//GEN-LAST:event_jButton27ActionPerformed

	private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        String info;
        info = "info.generator:0#info.individual:0";
        info += "#info.value:0";
        info += "#info.partialBest:" + Double.MAX_VALUE;
        info += "#info.partialWorse:" + Double.MIN_VALUE;
        info += "#info.finalBest:" + Double.MAX_VALUE;
        info += "#info.finalWorse:" + Double.MIN_VALUE;
        info += "#info.new:true";
        info += "#info.zero:true";
        util.setProperties("info.properties", info);
        fim = false;
        if (threadInfoAnnealing == null) {
            threadInfoAnnealing = new Thread(informationAnnealing);
            threadInfoAnnealing.start();
            threadInfoAnnealing = null;
        }
        if (threadAnnealing == null) {
            threadAnnealing = new Thread(annealing);
            threadAnnealing.start();
            threadAnnealing = null;
        }
	}//GEN-LAST:event_jButton31ActionPerformed
	private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        formSimulateAnnealing.setVisible(false);
	}//GEN-LAST:event_jButton32ActionPerformed

	private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        try {
            javax.swing.JFileChooser benchmarks = new javax.swing.JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
            benchmarks.setCurrentDirectory(new java.io.File("files/benchmarks/."));
            benchmarks.addChoosableFileFilter(filter);
            int iresposta = benchmarks.showOpenDialog(this.jButton1);
            if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            jTextField41.setText(benchmarks.getSelectedFile().getCanonicalPath());
            String[] programas = XMLToFiles.getBenchmarks(jTextField41.getText());
            listSaveBench = new javax.swing.DefaultListModel();
            listBenchFull = new javax.swing.DefaultListModel();
            listLoadBench = new javax.swing.DefaultListModel();
            int i;
            for (i = 0; i < programas.length; ++i) {
                listLoadBench.addElement(programas[i].split(":")[1]);
                listBenchFull.addElement(programas[i]);
            }
            jList7.setModel(listLoadBench);
        } catch (java.io.IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
	}//GEN-LAST:event_jButton33ActionPerformed

	private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        int aux = jList7.getSelectedIndex();
        if (aux == -1) {
            javax.swing.JOptionPane.showMessageDialog(null, "Nao foi selecionado nenhum item a ser incluido!", "Zero Itens Selecionados", javax.swing.JOptionPane.ERROR_MESSAGE);
        } else {
            listBench.addElement(jList7.getSelectedValue());
            listLoadBench.remove(aux);
            listSaveBench.addElement(listBenchFull.getElementAt(aux));
            listBenchFull.remove(aux);
            jList7.setModel(listLoadBench);
            jList8.setModel(listBench);
        }
	}//GEN-LAST:event_jButton34ActionPerformed

	private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
        int aux = jList8.getSelectedIndex();
        if (aux == -1) {
            javax.swing.JOptionPane.showMessageDialog(null, "Nao foi selecionado nenhum item a ser excluido!", "Nenhum Iten Selecionado", javax.swing.JOptionPane.ERROR_MESSAGE);
        } else {
            listLoadBench.addElement(jList8.getSelectedValue());
            listBench.remove(aux);
            listSaveBench.remove(aux);
            jList8.setModel(listBench);
            jList7.setModel(listLoadBench);
        }
	}//GEN-LAST:event_jButton35ActionPerformed
	private void sa1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sa1ActionPerformed
        formSimulateAnnealing.setVisible(true);
	}//GEN-LAST:event_sa1ActionPerformed

	private void relinking1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relinking1ActionPerformed
        jLabel76.setVisible(false);
        jProgressBar2.setVisible(false);
        formPathRelinking.setVisible(true);
	}//GEN-LAST:event_relinking1ActionPerformed

	private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        try {
            javax.swing.JFileChooser population = new javax.swing.JFileChooser();
            javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("TGFF File", "tgffopt");
            population.setCurrentDirectory(new java.io.File("files/dataflow/."));
            population.addChoosableFileFilter(filter);
            int iresposta = population.showOpenDialog(this.jButton1);
            if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            jTextField47.setText(population.getSelectedFile().getCanonicalPath());
        } catch (java.io.IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
	}//GEN-LAST:event_jButton36ActionPerformed

	private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        if (threadDFlow == null) {
            threadDFlow = new Thread(dflow);
            threadDFlow.start();
            threadDFlow = null;
        }
	}//GEN-LAST:event_jButton37ActionPerformed

	private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        formDataflow.setVisible(false);
	}//GEN-LAST:event_jButton38ActionPerformed

	private void dataflow1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataflow1ActionPerformed
        formDataflow.setVisible(true);
	}//GEN-LAST:event_dataflow1ActionPerformed

private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    rbMenuItem = new javax.swing.JRadioButtonMenuItem[laf.length];
    javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
    int i;
    for (i = 0; i < laf.length; ++i) {
        rbMenuItem[i] = new javax.swing.JRadioButtonMenuItem(laf[i].getName());
        rbMenuItem[i].setSelected(false);
        group.add(rbMenuItem[i]);
        rbMenuItem[i].addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mudaAparenciaActionPerformed(evt);
            }
        });
        aparenceMenu1.add(rbMenuItem[i]);
    }
}//GEN-LAST:event_formWindowOpened

private void screen1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_screen1ActionPerformed
    capturarTela();
}//GEN-LAST:event_screen1ActionPerformed

private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
    if (threadPattern == null) {
        threadPattern = new Thread(pattern);
        threadPattern.start();
        threadPattern = null;
    }
}//GEN-LAST:event_jButton39ActionPerformed

private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
    formPattern.setVisible(false);
}//GEN-LAST:event_jButton40ActionPerformed

private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
    try {
        javax.swing.JFileChooser population = new javax.swing.JFileChooser();
        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
        population.setCurrentDirectory(new java.io.File("files/extract/."));
        population.addChoosableFileFilter(filter);
        int iresposta = population.showSaveDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField48.setText(population.getSelectedFile().getCanonicalPath());
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton41ActionPerformed

private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
    try {
        javax.swing.JFileChooser benchmarks = new javax.swing.JFileChooser();
        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
        benchmarks.setCurrentDirectory(new java.io.File("files/benchmarks/."));
        benchmarks.addChoosableFileFilter(filter);
        int iresposta = benchmarks.showOpenDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField42.setText(benchmarks.getSelectedFile().getCanonicalPath());
        String[] programas = XMLToFiles.getBenchmarks(jTextField42.getText());
        listSaveBench = new javax.swing.DefaultListModel();
        listBenchFull = new javax.swing.DefaultListModel();
        listLoadBench = new javax.swing.DefaultListModel();
        int i;
        for (i = 0; i < programas.length; ++i) {
            listLoadBench.addElement(programas[i].split(":")[1]);
            listBenchFull.addElement(programas[i]);
        }
        jList9.setModel(listLoadBench);
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton42ActionPerformed

private void jButton44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton44ActionPerformed
    int aux = jList9.getSelectedIndex();
    if (aux == -1) {
        javax.swing.JOptionPane.showMessageDialog(null, "Nao foi selecionado nenhum item a ser incluido!", "Zero Itens Selecionados", javax.swing.JOptionPane.ERROR_MESSAGE);
    } else {
        listBench.addElement(jList9.getSelectedValue());
        listLoadBench.remove(aux);
        listSaveBench.addElement(listBenchFull.getElementAt(aux));
        listBenchFull.remove(aux);
        jList9.setModel(listLoadBench);
        jList10.setModel(listBench);
    }
}//GEN-LAST:event_jButton44ActionPerformed

private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
    int aux = jList10.getSelectedIndex();
    if (aux == -1) {
        javax.swing.JOptionPane.showMessageDialog(null, "Nao foi selecionado nenhum item a ser excluido!", "Nenhum Iten Selecionado", javax.swing.JOptionPane.ERROR_MESSAGE);
    } else {
        listLoadBench.addElement(jList10.getSelectedValue());
        listBench.remove(aux);
        listSaveBench.remove(aux);
        jList10.setModel(listBench);
        jList9.setModel(listLoadBench);
    }
}//GEN-LAST:event_jButton45ActionPerformed

private void extract1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extract1ActionPerformed
    formPattern.setVisible(true);
}//GEN-LAST:event_extract1ActionPerformed

private void selecionar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selecionar1ActionPerformed
    jTextArea1.requestFocus();
    jTextArea1.selectAll();
}//GEN-LAST:event_selecionar1ActionPerformed

private void salvar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salvar1ActionPerformed
    javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
    if (fc.showSaveDialog(this.jButton1) != javax.swing.JFileChooser.APPROVE_OPTION) {
        return;
    }
    java.io.File arquivo = fc.getSelectedFile();
    if (arquivo == null) {
        return;
    }
    java.io.FileWriter writer = null;
    try {
        writer = new java.io.FileWriter(arquivo);
        writer.write(jTextArea1.getSelectedText());
    } catch (java.io.IOException ex) {
    } finally {
        if (writer != null) {
            try {
                writer.close();
            } catch (java.io.IOException x) {
            }
        }
    }
}//GEN-LAST:event_salvar1ActionPerformed

private void selecionar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selecionar2ActionPerformed
    jTextArea2.requestFocus();
    jTextArea2.selectAll();
}//GEN-LAST:event_selecionar2ActionPerformed

private void salvar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salvar2ActionPerformed
    javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
    if (fc.showSaveDialog(this.jButton1) != javax.swing.JFileChooser.APPROVE_OPTION) {
        return;
    }
    java.io.File arquivo = fc.getSelectedFile();
    if (arquivo == null) {
        return;
    }
    java.io.FileWriter writer = null;
    try {
        writer = new java.io.FileWriter(arquivo);
        writer.write(jTextArea2.getSelectedText());
    } catch (java.io.IOException ex) {
    } finally {
        if (writer != null) {
            try {
                writer.close();
            } catch (java.io.IOException x) {
            }
        }
    }
}//GEN-LAST:event_salvar2ActionPerformed

private void jButton58ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton58ActionPerformed
    try {
        javax.swing.JFileChooser population = new javax.swing.JFileChooser();
        population.setCurrentDirectory(new java.io.File("files/."));
        population.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("*.txt File", "txt"));
        population.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        int iresposta = population.showSaveDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField12.setText(population.getSelectedFile().getCanonicalPath());
        file = population.getSelectedFile().getName();
        path = population.getSelectedFile().getParent();
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton58ActionPerformed

private void jComboBox15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox15ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jComboBox15ActionPerformed

private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
    try {
        javax.swing.JFileChooser benchmarks = new javax.swing.JFileChooser();
        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
        benchmarks.setCurrentDirectory(new java.io.File("files/benchmarks/."));
        benchmarks.addChoosableFileFilter(filter);
        int iresposta = benchmarks.showOpenDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField11.setText(benchmarks.getSelectedFile().getCanonicalPath());
        String[] programas = XMLToFiles.getBenchmarks(jTextField11.getText());
        listSaveBench = new javax.swing.DefaultListModel();
        listBenchFull = new javax.swing.DefaultListModel();
        listLoadBench = new javax.swing.DefaultListModel();
        int i;
        for (i = 0; i < programas.length; ++i) {
            listLoadBench.addElement(programas[i].split(":")[1]);
            listBenchFull.addElement(programas[i]);
        }
        jList3.setModel(listLoadBench);
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton17ActionPerformed

private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
    int aux = jList3.getSelectedIndex();
    if (aux == -1) {
        javax.swing.JOptionPane.showMessageDialog(null, "Nao foi selecionado nenhum item a ser incluido!", "Nenhum Iten Selecionado", javax.swing.JOptionPane.ERROR_MESSAGE);
    } else {
        listBench.addElement(jList3.getSelectedValue());
        listLoadBench.remove(aux);
        listSaveBench.addElement(listBenchFull.getElementAt(aux));
        listBenchFull.remove(aux);
        jList3.setModel(listLoadBench);
        jList4.setModel(listBench);
    }
}//GEN-LAST:event_jButton18ActionPerformed

private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
    int aux = jList4.getSelectedIndex();
    if (aux == -1) {
        javax.swing.JOptionPane.showMessageDialog(null, "Nao foi selecionado nenhum item a ser excluido!", "Nenhum Iten Selecionado", javax.swing.JOptionPane.ERROR_MESSAGE);
    } else {
        listLoadBench.addElement(jList4.getSelectedValue());
        listBench.remove(aux);
        listSaveBench.remove(aux);
        jList4.setModel(listBench);
        jList3.setModel(listLoadBench);
    }
}//GEN-LAST:event_jButton19ActionPerformed

private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
    if (threadMapping == null) {
        threadMapping = new Thread(mapper);
        threadMapping.start();
        threadMapping = null;
    }
}//GEN-LAST:event_jButton23ActionPerformed

private void mapping1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mapping1ActionPerformed
    formMapping.setVisible(true);
}//GEN-LAST:event_mapping1ActionPerformed

private void jTextArea3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea3MousePressed
// TODO add your handling code here:
}//GEN-LAST:event_jTextArea3MousePressed

private void jTextArea3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea3MouseReleased
// TODO add your handling code here:
}//GEN-LAST:event_jTextArea3MouseReleased

private void jTextArea4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea4MousePressed
// TODO add your handling code here:
}//GEN-LAST:event_jTextArea4MousePressed

private void jTextArea4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea4MouseReleased
// TODO add your handling code here:
}//GEN-LAST:event_jTextArea4MouseReleased

private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
    try {
        javax.swing.JFileChooser population = new javax.swing.JFileChooser();
        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
        population.setCurrentDirectory(new java.io.File("files/xml/input/."));
        population.addChoosableFileFilter(filter);
        int iresposta = population.showOpenDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField17.setText(population.getSelectedFile().getCanonicalPath());
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton12ActionPerformed

private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
    try {
        javax.swing.JFileChooser population = new javax.swing.JFileChooser();
        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
        population.setCurrentDirectory(new java.io.File("files/xml/input/."));
        population.addChoosableFileFilter(filter);
        int iresposta = population.showOpenDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField20.setText(population.getSelectedFile().getCanonicalPath());
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton16ActionPerformed

private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
    try {
        javax.swing.JFileChooser population = new javax.swing.JFileChooser();
        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
        population.setCurrentDirectory(new java.io.File("files/xml/input/."));
        population.addChoosableFileFilter(filter);
        int iresposta = population.showOpenDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField15.setText(population.getSelectedFile().getCanonicalPath());
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton29ActionPerformed

private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
    try {
        javax.swing.JFileChooser population = new javax.swing.JFileChooser();
        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
        population.setCurrentDirectory(new java.io.File("files/xml/input/."));
        population.addChoosableFileFilter(filter);
        int iresposta = population.showOpenDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField9.setText(population.getSelectedFile().getCanonicalPath());
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton20ActionPerformed

private void jTextArea1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea1MousePressed
    // TODO add your handling code here:
}//GEN-LAST:event_jTextArea1MousePressed

private void jTextArea1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea1MouseReleased
    // TODO add your handling code here:
}//GEN-LAST:event_jTextArea1MouseReleased

private void jTextArea2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea2MousePressed
    // TODO add your handling code here:
}//GEN-LAST:event_jTextArea2MousePressed

private void jTextArea2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea2MouseReleased
    // TODO add your handling code here:
}//GEN-LAST:event_jTextArea2MouseReleased

private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
    try {
        javax.swing.JFileChooser population = new javax.swing.JFileChooser();
        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
        population.setCurrentDirectory(new java.io.File("files/xml/input/."));
        population.addChoosableFileFilter(filter);
        int iresposta = population.showOpenDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField10.setText(population.getSelectedFile().getCanonicalPath());
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton11ActionPerformed

private void jComboBox22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox22ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jComboBox22ActionPerformed

private void jButton59ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton59ActionPerformed
    try {
        javax.swing.JFileChooser population = new javax.swing.JFileChooser();
        population.setCurrentDirectory(new java.io.File("files/."));
        population.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("*.txt File", "txt"));
        population.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        int iresposta = population.showSaveDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField13.setText(population.getSelectedFile().getCanonicalPath());
        file = population.getSelectedFile().getName();
        path = population.getSelectedFile().getParent();
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton59ActionPerformed

private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
    try {
        javax.swing.JFileChooser population = new javax.swing.JFileChooser();
        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
        population.setCurrentDirectory(new java.io.File("files/xml/input/."));
        population.addChoosableFileFilter(filter);
        int iresposta = population.showOpenDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField14.setText(population.getSelectedFile().getCanonicalPath());
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton14ActionPerformed

private void jComboBox26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox26ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jComboBox26ActionPerformed

private void jButton60ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton60ActionPerformed
    try {
        javax.swing.JFileChooser population = new javax.swing.JFileChooser();
        population.setCurrentDirectory(new java.io.File("files/."));
        population.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("*.txt File", "txt"));
        population.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        int iresposta = population.showSaveDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField16.setText(population.getSelectedFile().getCanonicalPath());
        file = population.getSelectedFile().getName();
        path = population.getSelectedFile().getParent();
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton60ActionPerformed

private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
    try {
        javax.swing.JFileChooser population = new javax.swing.JFileChooser();
        javax.swing.filechooser.FileNameExtensionFilter filter = new javax.swing.filechooser.FileNameExtensionFilter("*.xml File", "xml");
        population.setCurrentDirectory(new java.io.File("files/xml/input/."));
        population.addChoosableFileFilter(filter);
        int iresposta = population.showOpenDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField18.setText(population.getSelectedFile().getCanonicalPath());
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton13ActionPerformed

private void jComboBox20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox20ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jComboBox20ActionPerformed

private void jButton61ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton61ActionPerformed
    try {
        javax.swing.JFileChooser population = new javax.swing.JFileChooser();
        population.setCurrentDirectory(new java.io.File("files/."));
        population.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("*.txt File", "txt"));
        population.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        int iresposta = population.showSaveDialog(this.jButton1);
        if (iresposta != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        jTextField19.setText(population.getSelectedFile().getCanonicalPath());
        file = population.getSelectedFile().getName();
        path = population.getSelectedFile().getParent();
    } catch (java.io.IOException ex) {
        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton61ActionPerformed

private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
}//GEN-LAST:event_jButton28ActionPerformed

private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
    formMapping.setVisible(false);
}//GEN-LAST:event_jButton30ActionPerformed

private void jButton47ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton47ActionPerformed
    int aux = jList11.getSelectedIndex();
    if (aux == -1) {
        javax.swing.JOptionPane.showMessageDialog(null, "Nao foi selecionado nenhum item a ser incluido!", "Nenhum Iten Selecionado", javax.swing.JOptionPane.ERROR_MESSAGE);
    } else {
        listHeuristicTemp.addElement(jList11.getSelectedValue());
        listHeuristic.addElement(jList11.getSelectedIndex());
        jList12.setModel(listHeuristicTemp);
    }
}//GEN-LAST:event_jButton47ActionPerformed

private void jButton48ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton48ActionPerformed
    int aux = jList12.getSelectedIndex();
    if (aux == -1) {
        javax.swing.JOptionPane.showMessageDialog(null, "Nao foi selecionado nenhum item a ser excluido!", "Nenhum Iten Selecionado", javax.swing.JOptionPane.ERROR_MESSAGE);
    } else {
        listHeuristicTemp.remove(aux);
        listHeuristic.remove(aux);
        jList12.setModel(listHeuristicTemp);
    }
}//GEN-LAST:event_jButton48ActionPerformed

private void jTextArea5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea5MousePressed
    // TODO add your handling code here:
}//GEN-LAST:event_jTextArea5MousePressed

private void jTextArea5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea5MouseReleased
    // TODO add your handling code here:
}//GEN-LAST:event_jTextArea5MouseReleased

private void jTextArea6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea6MousePressed
    // TODO add your handling code here:
}//GEN-LAST:event_jTextArea6MousePressed

private void jTextArea6MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea6MouseReleased
    // TODO add your handling code here:
}//GEN-LAST:event_jTextArea6MouseReleased

private void jTextArea7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea7MousePressed
    // TODO add your handling code here:
}//GEN-LAST:event_jTextArea7MousePressed

private void jTextArea7MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea7MouseReleased
    // TODO add your handling code here:
}//GEN-LAST:event_jTextArea7MouseReleased

private void jTextArea8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea8MousePressed
    // TODO add your handling code here:
}//GEN-LAST:event_jTextArea8MousePressed

private void jTextArea8MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea8MouseReleased
    // TODO add your handling code here:
}//GEN-LAST:event_jTextArea8MouseReleased

    private void mudaAparenciaActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int i;
            for (i = 0; i < laf.length; ++i) {
                if (evt.getActionCommand().equals(laf[i].getName())) {
                    javax.swing.UIManager.setLookAndFeel(laf[i].getClassName());
                    break;
                }
            }
            javax.swing.SwingUtilities.updateComponentTreeUI(rootPane);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (javax.swing.UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void capturarTela() {
        java.awt.Rectangle tela;
        String name;
        try {
            int x = jDesktopPane1.getSelectedFrame().getLocationOnScreen().x;
            int y = jDesktopPane1.getSelectedFrame().getLocationOnScreen().y;
            int w = jDesktopPane1.getSelectedFrame().getWidth();
            int h = jDesktopPane1.getSelectedFrame().getHeight();
            tela = new java.awt.Rectangle(x, y, w, h);
            name = jDesktopPane1.getSelectedFrame().getTitle();
        } catch (Exception e) {
            tela = new java.awt.Rectangle(this.getBounds());
            name = this.getTitle();
        }
        try {
            java.awt.Robot robot = new java.awt.Robot();
            java.awt.image.BufferedImage bi = robot.createScreenCapture(tela);
            java.util.ArrayList<String> listaDir = new java.util.ArrayList<String>();
            String div;
            if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
                div = "/";
            } else {
                div = "\\";
            }
            java.io.File diretorio = new java.io.File(System.getProperty("user.dir") + div + "files" + div + "screenshot");
            diretorio.mkdirs();
            String[] arquivos = diretorio.list();
            int i;
            for (i = 0; i < arquivos.length; ++i) {
                if (arquivos[i].endsWith(".png")) {
                    if (arquivos[i].contains(name)) {
                        listaDir.add(arquivos[i]);
                    }
                }
            }
            if (listaDir.size() > 0) {
                name += listaDir.size();
            } else {
                name += 0;
            }
            javax.imageio.ImageIO.write(bi, "png", new java.io.File(diretorio + div + name + ".png"));
        } catch (java.awt.AWTException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
//                try {   
//                    java.awt.SplashScreen splash = java.awt.SplashScreen.getSplashScreen();   
//                    java.io.File spashImage = new java.io.File("g.png");
//                    
//                    splash.setImageURL(new java.net.URL("file:///home/thiagus/Development/Pesquisa/ArchitecturesGenerator/g.png"));   
//                    java.awt.Graphics2D g = splash.createGraphics();  
//                    } catch (NullPointerException ex) {   
//                    java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);   
//                } catch (java.io.IOException ex) {   
//                    java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);   
//                } catch (IllegalStateException ex) {   
//                    java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);   
//                }   
//                //COMEÇA A APLICAÇÂO   
//                try {   
//                   Thread.sleep(1000);   
//                 } catch (InterruptedException ignored) {   
//                 } 
                new Main().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup annealingElements;
    private javax.swing.JMenu aparenceMenu1;
    private javax.swing.JMenu customizationMenu1;
    private javax.swing.JMenuItem dataflow1;
    private javax.swing.JMenuItem extract1;
    private javax.swing.JInternalFrame formDataflow;
    private javax.swing.JInternalFrame formGenetic;
    private javax.swing.JInternalFrame formGraphics;
    private javax.swing.JInternalFrame formMapping;
    private javax.swing.JInternalFrame formPathRelinking;
    private javax.swing.JInternalFrame formPattern;
    private javax.swing.JInternalFrame formSimulateAnnealing;
    private javax.swing.JMenu generateMenu1;
    private javax.swing.JMenuItem genetic1;
    private javax.swing.JMenuItem graphic1;
    private javax.swing.JMenu graphs1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton58;
    private javax.swing.JButton jButton59;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton60;
    private javax.swing.JButton jButton61;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JCheckBox jCheckBox13;
    private javax.swing.JCheckBox jCheckBox14;
    private javax.swing.JCheckBox jCheckBox15;
    private javax.swing.JCheckBox jCheckBox16;
    private javax.swing.JCheckBox jCheckBox17;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox10;
    private javax.swing.JComboBox jComboBox12;
    private javax.swing.JComboBox jComboBox13;
    private javax.swing.JComboBox jComboBox14;
    private javax.swing.JComboBox jComboBox15;
    private javax.swing.JComboBox jComboBox16;
    private javax.swing.JComboBox jComboBox17;
    private javax.swing.JComboBox jComboBox18;
    private javax.swing.JComboBox jComboBox19;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox20;
    private javax.swing.JComboBox jComboBox22;
    private javax.swing.JComboBox jComboBox23;
    private javax.swing.JComboBox jComboBox24;
    private javax.swing.JComboBox jComboBox25;
    private javax.swing.JComboBox jComboBox26;
    private javax.swing.JComboBox jComboBox28;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox31;
    private javax.swing.JComboBox jComboBox32;
    private javax.swing.JComboBox jComboBox33;
    private javax.swing.JComboBox jComboBox34;
    private javax.swing.JComboBox jComboBox35;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox43;
    private javax.swing.JComboBox jComboBox44;
    private javax.swing.JComboBox jComboBox45;
    private javax.swing.JComboBox jComboBox46;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JComboBox jComboBox9;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel143;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel167;
    private javax.swing.JLabel jLabel168;
    private javax.swing.JLabel jLabel169;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel170;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JList jList1;
    private javax.swing.JList jList10;
    private javax.swing.JList jList11;
    private javax.swing.JList jList12;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JList jList4;
    private javax.swing.JList jList5;
    private javax.swing.JList jList6;
    private javax.swing.JList jList7;
    private javax.swing.JList jList8;
    private javax.swing.JList jList9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private javax.swing.JRadioButton jRadioButton14;
    private javax.swing.JRadioButton jRadioButton15;
    private javax.swing.JRadioButton jRadioButton16;
    private javax.swing.JRadioButton jRadioButton17;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner10;
    private javax.swing.JSpinner jSpinner11;
    private javax.swing.JSpinner jSpinner12;
    private javax.swing.JSpinner jSpinner13;
    private javax.swing.JSpinner jSpinner14;
    private javax.swing.JSpinner jSpinner15;
    private javax.swing.JSpinner jSpinner16;
    private javax.swing.JSpinner jSpinner17;
    private javax.swing.JSpinner jSpinner18;
    private javax.swing.JSpinner jSpinner19;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSpinner jSpinner20;
    private javax.swing.JSpinner jSpinner21;
    private javax.swing.JSpinner jSpinner22;
    private javax.swing.JSpinner jSpinner24;
    private javax.swing.JSpinner jSpinner25;
    private javax.swing.JSpinner jSpinner28;
    private javax.swing.JSpinner jSpinner29;
    private javax.swing.JSpinner jSpinner30;
    private javax.swing.JSpinner jSpinner31;
    private javax.swing.JSpinner jSpinner32;
    private javax.swing.JSpinner jSpinner33;
    private javax.swing.JSpinner jSpinner34;
    private javax.swing.JSpinner jSpinner35;
    private javax.swing.JSpinner jSpinner36;
    private javax.swing.JSpinner jSpinner37;
    private javax.swing.JSpinner jSpinner38;
    private javax.swing.JSpinner jSpinner39;
    private javax.swing.JSpinner jSpinner40;
    private javax.swing.JSpinner jSpinner41;
    private javax.swing.JSpinner jSpinner5;
    private javax.swing.JSpinner jSpinner6;
    private javax.swing.JSpinner jSpinner7;
    private javax.swing.JSpinner jSpinner8;
    private javax.swing.JSpinner jSpinner9;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JSplitPane jSplitPane5;
    private javax.swing.JSplitPane jSplitPane6;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JTabbedPane jTabbedPane6;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextArea jTextArea6;
    private javax.swing.JTextArea jTextArea7;
    private javax.swing.JTextArea jTextArea8;
    private javax.swing.JTextArea jTextArea9;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField34;
    private javax.swing.JTextField jTextField41;
    private javax.swing.JTextField jTextField42;
    private javax.swing.JTextField jTextField47;
    private javax.swing.JTextField jTextField48;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuItem mapping1;
    private javax.swing.JMenuBar menuBar1;
    private javax.swing.JMenuItem relinking1;
    private javax.swing.ButtonGroup relinkingElements;
    private javax.swing.JMenuItem sa1;
    private javax.swing.JMenuItem salvar1;
    private javax.swing.JMenuItem salvar2;
    private javax.swing.JMenuItem screen1;
    private javax.swing.JMenuItem selecionar1;
    private javax.swing.JMenuItem selecionar2;
    private javax.swing.JMenu selectHeuristic1;
    private javax.swing.JMenu statistic1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    private java.applet.AudioClip musica = java.applet.Applet.newAudioClip(getClass().getClassLoader().getResource("som/MissionImpossible.mid"));
    private architectures.generators.dataflow.dataflowGenerator utilDataflows = new architectures.generators.dataflow.dataflowGenerator();
    private javax.swing.UIManager.LookAndFeelInfo[] laf = javax.swing.UIManager.getInstalledLookAndFeels();
    private javax.swing.DefaultListModel listHeuristicTemp = new javax.swing.DefaultListModel();
    private javax.swing.DefaultListModel listHeuristic = new javax.swing.DefaultListModel();
    private javax.swing.DefaultListModel listSaveBench = new javax.swing.DefaultListModel();
    private javax.swing.DefaultListModel listBenchFull = new javax.swing.DefaultListModel();
    private java.util.ArrayList<String> printTimeTemp = new java.util.ArrayList<String>();
    private java.util.ArrayList<String> printIndTemp = new java.util.ArrayList<String>();
    private java.util.ArrayList<String> printCriTemp = new java.util.ArrayList<String>();
    private javax.swing.DefaultListModel listBench = new javax.swing.DefaultListModel();
    private java.util.ArrayList<String> printTime = new java.util.ArrayList<String>();
    private java.util.ArrayList<String> printInd = new java.util.ArrayList<String>();
    private java.util.ArrayList<String> printCri = new java.util.ArrayList<String>();
    private utiliters.FilesToXML filesToXML = new utiliters.FilesToXML();
    private utiliters.XMLToFiles XMLToFiles = new utiliters.XMLToFiles();
    private infoRelinking informationRelinking = new infoRelinking();
    private infoAnnealing informationAnnealing = new infoAnnealing();
    private simulatedAnnealing annealing = new simulatedAnnealing();
    private infoGenetic informationGenetic = new infoGenetic();
    private utiliters.Utiliter util = new utiliters.Utiliter();
    private geneticAlgorithm genetic = new geneticAlgorithm();
    private pathRelinking relinking = new pathRelinking();
    private javax.swing.JRadioButtonMenuItem[] rbMenuItem;
    private javax.swing.DefaultListModel listLoadBench;
    private graphics graph = new graphics();
    private dataflow dflow = new dataflow();
    private extract pattern = new extract();
    private heuristicas.Avaliation avaliate;
    private sound som = new sound();
    private map mapper = new map();
    private String nameGraph = "";
    private boolean fim = false;
    private String file = "";
    private String path = "";
    private double constGen;
    Thread threadInfoRelinking;
    Thread threadInfoAnnealing;
    Thread threadInfoGenetic;
    Thread threadRelinking;
    Thread threadAnnealing;
    Thread threadPattern;
    Thread threadGenetic;
    Thread threadMapping;
    Thread threadGraph;
    Thread threadDFlow;
}