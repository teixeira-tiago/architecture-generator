package utiliters;

import architectures.dataflows.extractor.Arco;
import architectures.dataflows.graph.Node;
import architectures.dataflows.graph.RGraph;
import architectures.dataflows.javagraphs.DataflowGraph;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JOptionPane;

public class Utiliter {

    private Lock lock = new ReentrantLock();
    private Condition finalizar = lock.newCondition();
    private HashMap<Double, Double> hopStatistical = new HashMap<Double, Double>();

    public boolean xor(boolean a, boolean b) {
        return (!(a && b) && (a || b));
    }

    public String fill(int num, int tam, char pos) {
        return fill(String.valueOf(num), "0", tam, pos);
    }

    public String fill(double num, int tam, char pos) {
        return fill(String.valueOf(num), "0", tam, pos);
    }

    public String fill(String str, String ch, int tam, char pos) {
        String tmp = "";
        int tamStr = str.length();
        while (tamStr < tam) {
            tmp += ch;
            ++tamStr;
        }
        if (pos == 'a') {
            return tmp + str;
        }
        return str + tmp;
    }

    public void printList(ArrayList<String> print, String name) {
        printList(print, name, false);
    }

    public void printList(ArrayList<String> print, String name, boolean clear) {
        try {
            FileWriter writer = new FileWriter(new File(name), clear);
            PrintWriter arq = new PrintWriter(writer);
            int i;
            for (i = 0; i < print.size(); ++i) {
                arq.println(print.get(i));
            }
            arq.flush();
            arq.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public String nowMore(long start) {
        Date d = new Date(System.currentTimeMillis() + start);
        return fill(d.getHours(), 2, 'a') + ":" + fill(d.getMinutes(), 2, 'a') + ":" + fill(d.getSeconds(), 2, 'a');
    }

    public String tempo(long start) {
        int secs = (int) start / 1000;
        int milesimal = (int)(start - (secs *1000));
        int[] ret = new int[3];
        // calcula numero de horas, minutos, segundos e milesimos
        ret[0] = secs / 3600;
        secs = secs % 3600;
        ret[1] = secs / 60;
        secs = secs % 60;
        ret[2] = secs;
        String time = (ret[0] > 0 ) ? fill(ret[0], 2, 'a') + ":" : "";
        time += fill(ret[1], 2, 'a') + ":" + fill(ret[2], 2, 'a') + "," + fill(milesimal, 3, 'a');
        return time;
    }

    public int contCols(String file, String separador, int line) {
        int cols = 0, cont = 0;
        String linha;
        String[] colunas = null;
        try {
            FileReader reader = new FileReader(new File(file));
            BufferedReader leitor = new BufferedReader(reader);
            while ((leitor.ready()) || (cont <= (line + 1))) {
                linha = leitor.readLine();
                if (cont == line) {
                    colunas = linha.split(separador);
                }
                ++cont;
            }
            cols = colunas.length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cols;
    }

    public void generatePlotFile(String file, String name, String output, int lines, String color, boolean conce, boolean exec) {
        try {
            FileWriter writer = new FileWriter(new File(name), false);
            PrintWriter arq = new PrintWriter(writer);
            arq.println("reset");
            arq.println("set terminal png giant size 800 600");
            arq.println("set title \"Variation Statistics between the Generations\"");
            arq.println("set xlabel \"Generations\"");
            arq.println("set ylabel \"Fitness Values\"");
            lines -= 2;
            if (conce) {
                if (!color.equals("")) {
                    color = "lc " + color;
                }
                arq.println("plot \"" + file + "\" u 1:2 t \"\" w points " + color);
                int i;
                for (i = 3; i <= lines; ++i) {
                    arq.println("replot \"" + file + "\" u 1:" + i + " t \"\" w points " + color);
                }
            } else {
                arq.println("plot \"" + file + "\" u 1:" + (lines + 1) + ":" + (lines + 2) + " t \"\" w filledcu lc 0");
            }
            arq.println("replot \"" + file + "\" u 1:" + (lines + 1) + " t \"\" w lines lc -1");
            arq.println("replot \"" + file + "\" u 1:" + (lines + 2) + " t \"\" w lines lc -1");
            output = System.getProperty("user.dir") + "/" + output;
            arq.println("set output \"" + output + "\"");
            arq.println("replot");
            arq.println("set terminal x11");
            arq.flush();
            arq.close();
            writer.close();
            if (exec) {
                executarGnuPlot(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void musicFile(String name) {
        File arquivo;
        Sequence sequencia;
        Sequencer sequenciador;
        try {
            arquivo = new File(name);
            sequencia = MidiSystem.getSequence(arquivo);
            sequenciador = MidiSystem.getSequencer();
            if (sequenciador == null) {
                System.out.println("No foi possivel obter o sequenciador");
                System.exit(0);
            }
            sequenciador.open();
            sequenciador.setSequence(sequencia);
            sequenciador.start();
            sequenciador.setLoopCount(1);
            long tempoTotal = System.currentTimeMillis() + (sequenciador.getLoopCount() * (sequenciador.getMicrosecondLength() / 1000));
            while (System.currentTimeMillis() < tempoTotal) {
            }
            sequenciador.stop();
        } catch (IOException excecao) {
            excecao.printStackTrace();
        } catch (InvalidMidiDataException excecao) {
            excecao.printStackTrace();
        } catch (MidiUnavailableException excecao) {
            excecao.printStackTrace();
        }
    }

    public void executarGnuPlot(String arquivo) {
        try {
            if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
                Runtime.getRuntime().exec("konsole -e gnuplot " + System.getProperty("user.dir") + "/" + arquivo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executarDotGraph(String arquivo) {
        try {
            if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
                //String path = System.getProperty("user.dir") + "/files/dot/" + arquivo;
                String path = System.getProperty("user.dir") + "/" + arquivo;
                Runtime.getRuntime().exec("konsole -e dot " + path + ".dot -Tpng -o" + path + ".png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executarTGFF(String arquivo) {
        lock.lock();
        try {
            if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
                //String path = System.getProperty("user.dir") + "/" + arquivo;
                Runtime.getRuntime().exec("konsole -e tgff " + arquivo);
            }
            finalizar.signalAll();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void generateDataflowGraph(String arquivo) {
        lock.lock();
        try {
            if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
                //String path = System.getProperty("user.dir") + "/" + arquivo;
                Runtime.getRuntime().exec("konsole -e graph-easy " + arquivo + " --png");
                notifyAll();
            }
            finalizar.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void limparArquivoXml(String arquivo, String root) {
        try {
            FileWriter writer = new FileWriter(new File(arquivo), false);
            PrintWriter arq = new PrintWriter(writer);
            arq.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            arq.println("<" + root + ">");
            arq.println("</" + root + ">");
            arq.flush();
            arq.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void limparArquivo(String arquivo) {
        try {
            FileWriter writer = new FileWriter(new File(arquivo), false);
            PrintWriter arq = new PrintWriter(writer);
            arq.println();
            arq.flush();
            arq.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProperties(String arquivo, String body) {
        FileInputStream fileInput = null;
        FileOutputStream fileOutput = null;
        File file = new File(arquivo);
        Properties props = new Properties();
        try {
            fileInput = new FileInputStream(file);
            props.load(fileInput);
            fileInput.close();
        } catch (IOException e) {
            limparArquivo(arquivo);
        }
        String[] linha = body.split("#");
        String[] conteudo;
        int i;
        for (i = 0; i < linha.length; ++i) {
            conteudo = linha[i].split(":");
            props.setProperty(conteudo[0], conteudo[1]);
        }
        try {
            fileOutput = new FileOutputStream(file);
            props.store(fileOutput, "Arquivo para uso de comunicacao entre processos");
            fileOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties(String arquivo) {
        FileInputStream fileInput = null;
        File file = new File(arquivo);
        Properties props = new Properties();
        try {
            fileInput = new FileInputStream(file);
            props.load(fileInput);
            fileInput.close();
        } catch (IOException e) {
        }
        return props;
    }

    public String[] load(String file) {
        String[] result = null;
        String tmp = "";
        try {
            FileReader reader = new FileReader(new File(file));
            BufferedReader arq = new BufferedReader(reader);
            while (arq.ready()) {
                tmp += arq.readLine() + ":";
            }
            result = tmp.split(":");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<String> loadList(String file) {
        ArrayList result = new ArrayList<String>();
        try {
            FileReader reader = new FileReader(new File(file));
            BufferedReader arq = new BufferedReader(reader);
            while (arq.ready()) {
                result.add(arq.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getSizesBenchmarks(String path) {
        String sizes = "";
        Class<?> graphClass;
        DataflowGraph graph = null;
        RGraph grafo;
        try {
            graphClass = Class.forName(path);
            graph = (DataflowGraph) graphClass.newInstance();
            grafo = graph.getGraph();
            sizes = String.valueOf(grafo.edgeSet().size());
            sizes += ":" + grafo.vertexSet().size();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return sizes;
    }

    public RGraph getGraph(String path) {
        Class<?> graphClass;
        DataflowGraph graph = null;
        RGraph grafo = null;
        try {
            graphClass = Class.forName(path);
            graph = (DataflowGraph) graphClass.newInstance();
            grafo = graph.getGraph();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return grafo;
    }
    private static final String errMsg = "Erro ao tentar abrir o browser";

    public static void openURL(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL",
                        new Class[]{String.class});
                openURL.invoke(null, new Object[]{url});
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else { //assume Unix or Linux   

                String[] browsers = {
                    "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"
                };
                String browser = null;
                int count;
                for (count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime().exec(
                            new String[]{"which", browsers[count]}).waitFor() == 0) {
                        browser = browsers[count];
                    }
                }
                if (browser == null) {
                    throw new Exception("Navegador não encontrado!");
                } else {
                    Runtime.getRuntime().exec(new String[]{browser, url});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, errMsg + ":\n" + e.getLocalizedMessage());
        }
    }

    public ArrayList<String> criticalPath(TreeMap<Arco, Double> ASAP, TreeMap<Arco, Double> ALAP) {
        ArrayList<String> critical = new ArrayList<String>();
        HashMap<Arco, Double> auxALAP = new HashMap<Arco, Double>();
        Iterator<Arco> it;
        int cont = 0;
        double dist, total = .0;
        Arco tmp, aux;
        it = ASAP.keySet().iterator();
        while (it.hasNext()) {
            tmp = it.next();
            if (ALAP.containsKey(tmp)) {
                dist = new Double(Math.abs(ASAP.get(tmp) - ALAP.get(tmp)));
                critical.add(tmp.getOrigem() + " -> " + tmp.getDestino() + " { " + dist + " }");
                total += dist;
                ++cont;
                if (!hopStatistical.containsKey(dist)) {
                    hopStatistical.put(dist, new Double(1));
                } else {
                    hopStatistical.put(dist, hopStatistical.get(dist) + 1);
                }
            }
        }
        if (critical.isEmpty()) {
            it = ALAP.keySet().iterator();
            while (it.hasNext()) {
                tmp = it.next();
                aux = new Arco();
                aux.arc(tmp.getDestino(), tmp.getOrigem());
                auxALAP.put(aux, ALAP.get(tmp));
            }
            it = ASAP.keySet().iterator();
            while (it.hasNext()) {
                tmp = it.next();
                if (auxALAP.containsKey(tmp)) {
                    dist = new Double(Math.abs(ASAP.get(tmp) - auxALAP.get(tmp)));
                    critical.add(tmp.getOrigem() + " -> " + tmp.getDestino() + " { " + dist + " }");
                    total += dist;
                    ++cont;
                    if (!hopStatistical.containsKey(dist)) {
                        hopStatistical.put(dist, new Double(1));
                    } else {
                        hopStatistical.put(dist, hopStatistical.get(dist) + 1);
                    }
                }
            }
        }
        critical.add("");
        critical.add("-----------------");
        critical.add("Total path " + total);
        critical.add("Medium Path Critical " + (total / cont));
        return critical;
    }

    public void convertRGraphToDOT(RGraph graph, String name) {
        Object[] nos = graph.vertexSet().toArray();
        Node no;
        String[] vertices = graph.vertexSet().toString().replaceAll("\\[", "").replaceAll("\\)", "").replaceAll("\\]", "").replaceAll(" ", "").split(",");
        String[] edges = graph.edgeSet().toString().replaceAll("\\[\\(", "").replaceAll("\\]", "").replaceAll("\\), \\(", ";").replaceAll(",", " -> ").replaceAll("\\)", "").replaceAll(" ", "").split(";");
        String tmp;
        ArrayList<String> print = new ArrayList<String>();
        print.add("digraph " + name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf(".")) + " {");
        //print.add("\tnode [fontcolor=white,style=filled,color=blue2];");
        int i;
        for (i = 0; i < vertices.length; ++i) {
            tmp = vertices[i];
            no = (Node) nos[i];
            print.add("\t" + tmp + " [label = " + no.name/*tmp.split("_")[0]*/ + " ];");
        }
        for (i = 0; i < edges.length; ++i) {
            print.add("\t" + edges[i] + " [ name = " + i + " ];");
        }
        print.add("}");
        printList(print, name);
    }

    public HashMap<Double, Double> getHopStatistic() {
        return hopStatistical;
    }
}
