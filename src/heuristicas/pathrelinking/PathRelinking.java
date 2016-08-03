package heuristicas.pathrelinking;

import heuristicas.Avaliation;

import java.util.List;
import java.util.Properties;
import utiliters.Utiliter;
import xml.topologyclass.Element;

public class PathRelinking {

    private Utiliter util = new Utiliter();
    private int[] equal;
    private Avaliation avaliate;
    private Properties prop;

    public PathRelinking(Avaliation avaliation, int store) {
        this.avaliate = avaliation;
        avaliate.start();
        avaliate.store(store);
    }

    public PathRelinking(Avaliation avaliate) {
        this.avaliate = avaliate;
    }

    private void compare(Element s1, Element s2) {
        equal = new int[s2.getLink().size()];
        boolean linha, coluna;
        int i;
        for (i = 0; i < s1.getLink().size(); ++i) {
            linha = s1.getLink().get(i).getLine().getHop() == s2.getLink().get(i).getLine().getHop();
            coluna = s1.getLink().get(i).getColun().getHop() == s2.getLink().get(i).getColun().getHop();
            if (linha && coluna) {
                equal[i] = 1;
            } else {
                equal[i] = 0;
            }
        }
    }

    private void relinking(Element s1, Element s2) {
        compare(s1, s2);
        int i;
        String tmp, aux[];
        boolean igual;
        if (equal.toString().contains("0")) {
            for (i = 0; i < equal.length; ++i) {
                prop = util.getProperties("info.properties");
                tmp = prop.getProperty("info.individual");
                if (tmp.contains(" pr ")) {
                    aux = tmp.split(" pr ");
                    util.setProperties("info.properties", "info.individual:" + aux[0] + " pr " + (Integer.valueOf(aux[1]) + 1));
                } else {
                    util.setProperties("info.properties", "info.individual:" + tmp + " pr " + i);
                }
                if (equal[i] == 0) {
                    igual = s1.getLink().get(i).getColun().getHop() == s2.getLink().get(i).getColun().getHop();
                    if (!igual) {
                        s1.getLink().get(i).setColun(s2.getLink().get(i).getColun());
                        avaliate.calc(s1);
                    }
                    igual = s1.getLink().get(i).getLine().getHop() == s2.getLink().get(i).getLine().getHop();
                    if (!igual) {
                        s1.getLink().get(i).setLine(s2.getLink().get(i).getLine());
                        avaliate.calc(s1);
                    }
                }
            }
        }
    }

    public void pathRelinking(List<Element> elements, String name) {
        int index = elements.size();
        int cont = 0, i, j;
        for (i = 0; i < index; ++i) {
            for (j = 0; j < index; ++j) {
                if (i != j) {
                    util.setProperties("info.properties", "info.generator:" + cont);
                    util.setProperties("info.properties", "info.new:true");
                    relinking(elements.get(i), elements.get(j));
                    ++cont;
                }
            }
        }
        --cont;
        util.setProperties("info.properties", "info.generator:" + cont);
        util.limparArquivoXml("files/xml/dez/" + name + "_Melhor10.xml", "Topologies");
        util.limparArquivoXml("files/xml/" + name + "_Melhor.xml", "Topologies");
        util.limparArquivoXml("files/xml/pior/" + name + "_Pior.xml", "Topologies");
        avaliate.persists(name + "_Melhor", "pior/" + name + "_Pior", "files/" + name);
        avaliate.position(name);
    }

    public Element pathRelinking(Element s1, Element s2) {
        relinking(s1, s2);
        String tmp;
        String[] aux;
        prop = util.getProperties("info.properties");
        tmp = prop.getProperty("info.individual");
        if (tmp.contains(" pr ")) {
            aux = tmp.split(" pr ");
            util.setProperties("info.properties", "info.individual:" + aux[0]);
        } else {
            util.setProperties("info.properties", "info.individual:" + tmp);
        }
        return avaliate.best();
    }
}
