package architectures.generators.dataflow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import utiliters.Utiliter;

public class dataflowGenerator {

    private Utiliter util = new Utiliter();

    public void generateDataflow(String source, String style, String file) {
        try {
            // TransformerFactory
            TransformerFactory factory = TransformerFactory.newInstance();

            // XSL transformer
            Transformer transformer = factory.newTransformer(new StreamSource(
                    "xml/stylesheets/" + style));

            // properties
            transformer.setOutputProperty("encoding", "UTF-8");
            transformer.setOutputProperty("indent", "true");

            // XML to XSLT
            transformer.transform(new StreamSource(source),
                    new StreamResult(new File(file)));
            System.out.println("Dataflow gerado com sucesso");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateFlowGraph(String file, int nodes, int input, int output) {
        util.limparArquivo(file);
        ArrayList<String> print = new ArrayList<String>();
        print.add("tg_cnt 1");
        print.add("task_cnt " + nodes + " 1");
        print.add("task_degree " + input + " " + output);
        print.add("");
        print.add("tg_write");
        print.add("eps_write");
        print.add("vcg_write");
        print.add("");
        print.add("table_label COMMUN");
        print.add("table_cnt 1");
        print.add("trans_write");
        util.printList(print, file);
    }

    public ArrayList<String> listarClasses() {
        ArrayList<String> listaDir = new ArrayList<String>();
        try {
            File diretorio = new File(System.getProperty("user.dir") + "\\files\\xml");
            String[] arquivos = diretorio.list();
            int i;
            for (i = 0; i < arquivos.length; ++i) {
                if (arquivos[i].endsWith(".xml")) {
                    listaDir.add(arquivos[i]);
                }
            }
        } catch (Exception e) {
        }
        return listaDir;
    }

    public String[] loadComponent(String file) {
        String[] tmpAll, arq = util.load(file);
        String tmp, aux = "", name, number, auxAll;
        boolean comp = false;
        int i;
        for (i = 0; i < arq.length; ++i) {
            tmp = arq[i];
            if (tmp.contains("TASK ")) {
                comp = true;
            } else {
                comp = false;
            }
            if (comp) {
                auxAll = tmp.replaceAll(" ", "").replaceAll("\t", "");
                auxAll = auxAll.replaceAll("TASK", "").replaceAll("TYPE", ":");
                tmpAll = auxAll.split(":");
                name = tmpAll[0];
                number = tmpAll[1];
                aux += name + "#" + number + ":";
            }
        }
        return aux.split(":");
    }

    public String[] loadSignal(String file) {
        String[] tmpAll, arq = util.load(file);
        String tmp, aux = "", auxAll, name, from, to, type;
        boolean sig = false;
        int i;
        for (i = 0; i < arq.length; ++i) {
            tmp = arq[i];
            if (tmp.contains("ARC")) {
                sig = true;
            } else {
                sig = false;
            }
            if (sig) {
                auxAll = tmp.replaceAll(" ", "").replaceAll("\t", "");
                auxAll = auxAll.replaceAll("ARC", "").replaceAll("FROM", ":");
                auxAll = auxAll.replaceAll("TO", ":").replaceAll("TYPE", ":");
                tmpAll = auxAll.split(":");
                name = tmpAll[0];
                from = tmpAll[1];
                to = tmpAll[2];
                type = tmpAll[3];
                aux += name + "#" + from + "#" + to + "#" + type + ":";
            }
        }
        return aux.split(":");
    }

    public HashMap<String, String> ends(String[] finais) {
        HashMap<String, String> fins = new HashMap<String, String>();
        String[] tmp;
        int maiorFim = Integer.MIN_VALUE, i;
        for (i = 0; i < finais.length; ++i) {
            tmp = finais[i].split("#");
            fins.put(tmp[1], tmp[2]);
            if (Integer.valueOf(tmp[2]) > maiorFim) {
                maiorFim = Integer.valueOf(tmp[2]);
            }
        }
        return fins;
    }

    public HashMap<String, String> closeGraph(String source) {
        HashMap<String, String> fins = new HashMap<String, String>();
        ArrayList<String> fims = new ArrayList<String>();
        //ArrayList<String> novo = new ArrayList<String>();
        ArrayList<String> comp = new ArrayList<String>();
        ArrayList<String> arcos = new ArrayList<String>();
        ArrayList<String> file = util.loadList(source);
        ArrayList<String> head = new ArrayList<String>();
        ArrayList<String> foot = new ArrayList<String>();
        String[] finais = loadFinals(source);
        String[] compo = loadComponent(source);
        String[] arcs = loadSignal(source);
        String[] table = loadTable(source);
        Random randTable;
        String[] tmp, fim1, fim2;
        int i = -1, value = 0, type, len;
        String aux = "";
        boolean tabela = false;
        while (!aux.contains("TYPE")) {
            head.add(aux);
            ++i;
            aux = file.get(i);
        }
        for (i = 0; i < file.size(); ++i) {
            aux = file.get(i);
            if (tabela || aux.contains("COMMUN")) {
                foot.add(aux);
                tabela = true;
            }
        }
        file.clear();
        for (i = 0; i < compo.length; ++i) {
            comp.add(compo[i]);
        }
        for (i = 0; i < arcs.length; ++i) {
            arcos.add(arcs[i]);
        }
        if (finais.length > 1) {
            for (i = 0; i < finais.length; ++i) {
                tmp = finais[i].split("#");
                finais[i] = tmp[2] + ":" + tmp[1] + ":" + tmp[0];
            }
            Arrays.sort(finais);
            for (i = 0; i < finais.length; ++i) {
                fims.add(finais[i]);
            }
            int arc = arcos.size();
            int nos = comp.size();
            int period = 0;
            while (fims.size() != 1) {
                randTable = new Random();
                type = Math.abs(randTable.nextInt() % table.length);
                fim1 = fims.get(0).split(":");
                fim2 = fims.get(1).split(":");
                if (fims.size() > 1) {
                    fims.remove(0);
                    fims.remove(0);
                } else {
                    fims.remove(0);
                }
                arcos.add("a0_" + (++arc) + "#" + fim1[1] + "#t0_" + nos + "#" + type);
                randTable = new Random();
                type = Math.abs(randTable.nextInt() % table.length);
                arcos.add("a0_" + (++arc) + "#" + fim2[1] + "#t0_" + nos + "#" + type);
                period = (Integer.valueOf(fim2[0]) + 100);
                randTable = new Random();
                type = Math.abs(randTable.nextInt() % 20);
                comp.add("t0_" + nos + "#" + type);
                fims.add(period + ":t0_" + nos + ":d0_" + fims.size());
                ++nos;
            }
            if (fims.isEmpty()) {
                randTable = new Random();
                type = Math.abs(randTable.nextInt() % table.length);
                len = comp.size();
                comp.add("t0_" + len + "#" + type);
                fims.add(value + ":t0_" + len + ":d0_" + fims.size());
                fins.put("t0_" + len, String.valueOf(value));
            }
        } else {
            fins.put(finais[0].split("#")[1], finais[0].split("#")[2]);
        }
        for (i = 0; i < head.size(); ++i) {
            if (head.get(i).contains("PERIOD")) {
                tmp = head.get(i).split(" ");
                file.add(tmp[0] + " " + fims.get((fims.size() - 1)).split(":")[0]);
            } else {
                file.add(head.get(i));
            }
        }
        file.add("");
        for (i = 0; i < comp.size(); ++i) {
            tmp = comp.get(i).split("#");
            file.add("\tTASK " + tmp[0] + "\tTYPE " + tmp[1] + " ");
        }
        file.add("");
        for (i = 0; i < arcos.size(); ++i) {
            tmp = arcos.get(i).split("#");
            file.add("\tARC " + tmp[0] + "\tFROM " + tmp[1] + "  TO  " + tmp[2] + " TYPE " + tmp[3]);
        }
        file.add("");
        for (i = 0; i < fims.size(); ++i) {
            tmp = fims.get(i).split(":");
            file.add("\tHARD_DEADLINE d0_" + i + " ON " + tmp[1] + " AT " + tmp[0]);
        }
        file.add("}");
        file.add("");
        for (i = 0; i < foot.size(); ++i) {
            file.add(foot.get(i));
        }
        util.limparArquivo(source);
        util.printList(file, source);
        generateVCG(source);
        return fins;
    }

    public String[] loadFinals(String file) {
        String[] tmpAll, arq = util.load(file);
        String tmp, aux = "", auxAll, name, no, value;
        boolean finals = false;
        int i;
        for (i = 0; i < arq.length; ++i) {
            tmp = arq[i];
            if (tmp.contains("HARD_DEADLINE")) {
                finals = true;
            } else {
                finals = false;
            }
            if (finals) {
                auxAll = tmp.replaceAll("HARD_DEADLINE", "").replaceAll("\t", "");
                auxAll = auxAll.replaceAll(" ", "").replaceAll("ON", ":").replaceAll("AT", ":");
                tmpAll = auxAll.split(":");
                name = tmpAll[0];
                no = tmpAll[1];
                value = tmpAll[2];
                aux += name + "#" + no + "#" + value + ":";
            }
        }
        return aux.split(":");
    }

    private String[] loadTable(String file) {
        String[] arq = util.load(file);
        String tmp, aux = "";
        boolean table = false;
        int i;
        for (i = 0; i < arq.length; ++i) {
            tmp = arq[i];
            if (table && (tmp.length() > 11)) {
                aux += tmp.substring(12).trim() + ":";
            }
            if (tmp.contains("# type") && (!table)) {
                table = true;
            }
        }
        return aux.split(":");
    }

    private void generateVCG(String source) {
        String nome = source.replace(".tgff", ".vcg");
        String[] comp = loadComponent(source);
        String[] arco = loadSignal(source);
        ArrayList<String> print = new ArrayList<String>();
        String div;
        if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
            div = "/";
        } else {
            div = "\\";
        }
        String[] tmp;// = nome.split(div);

        print.add("graph: { label: \"" + nome.substring(nome.lastIndexOf(div) + 1) + "\"");
        print.add("display_edge_labels: yes");
        tmp = comp[0].split("#");
        print.add("\tnode: { title: \"" + tmp[0] + "\" label: \"" + tmp[0] + " (" + tmp[1] + ")\"  color: lightgreen } ");
        int i;
        for (i = 1; i < (comp.length - 1); ++i) {
            tmp = comp[i].split("#");
            print.add("\tnode: { title: \"" + tmp[0] + "\" label: \"" + tmp[0] + " (" + tmp[1] + ")\"  color: white } ");
        }
        tmp = comp[comp.length - 1].split("#");
        print.add("\tnode: { title: \"" + tmp[0] + "\" label: \"" + tmp[0] + " (" + tmp[1] + ")\"  color: lightred } ");
        print.add("");
        for (i = 0; i < arco.length; ++i) {
            tmp = arco[i].split("#");
            print.add("\tedge: { thickness: 2 sourcename:\"" + tmp[1] + "\" targetname: \"" + tmp[2] + "\"  label: \"(" + tmp[3] + ")\" }");
            print.add("");
        }
        print.add("}");
        util.limparArquivo(nome);
        util.printList(print, nome);
    }
}
