package heuristicas.simulateannealing;

import utiliters.Utiliter;
import heuristicas.Avaliation;
import java.util.Properties;
import xml.topologyclass.Element;

public class SimulateAnnealing {

    private Utiliter util = new Utiliter();
    private Avaliation avaliate;

    public SimulateAnnealing(Avaliation avaliation){
        avaliate = avaliation;
        avaliate.start();
        avaliate.store(10);
    }

    private double annealingT(int index, double k, double ki) {
        double result = ki, beta = Math.random();
        if (index == 1) {
            result = beta * ki;
        } else if (index >= 2) {
            result = 1 + (gama(index, k, ki) * ki);
        }
        return result;
    }

    private double gama(int index, double k, double ki) {
        double result = .0, dividendo = .0, divisor = .0;
        dividendo = k - ki;
        divisor = (index - 1) * k * ki;
        result = dividendo / divisor;
        return result;
    }

    public void SA(Element element, int maxInterations, int typeVariationT, double initialT, double finalT, String name) {
        SA(element, maxInterations, typeVariationT, initialT, finalT, 0.0, name);
    }

    public void SA(Element element, int maxInterations, int typeVariationT, double initialT, double finalT, double fixT, String name) {
        int index, loop = 0;
        double delta = .0, T = initialT, alfa = Math.random();
        Element indiAtual, indiNovo;
        indiAtual = element;
        String[] auxI;
        String tmpI;
        double iNovo = 10, iAtual, tmp;
        Properties prop = util.getProperties("info.properties");
        tmpI = prop.getProperty("info.individual");
        if (tmpI.contains(" pr ")) {
            auxI = tmpI.split(" pr ");
            util.setProperties("info.properties", "info.individual:" + (Integer.valueOf(auxI[0]) + 1) + " pr " + auxI[1]);
        } else {
            util.setProperties("info.properties", "info.individual:" + (Integer.valueOf(tmpI) + 1));
        }
        if (element.getValue() > .0){
            iAtual = element.getValue();
        } else {
            iAtual = avaliate.calc(element);
        }
        while (T > finalT) {
            index = 0;
            util.setProperties("info.properties", "info.generator:"+ T + " variation " + loop);
            util.setProperties("info.properties", "info.new:true");
            util.setProperties("info.properties", "info.zero:true");
            while (index < maxInterations) {
                tmpI = prop.getProperty("info.individual");
                if (tmpI.contains(" pr ")) {
                    auxI = tmpI.split(" pr ");
                    util.setProperties("info.properties", "info.individual:" + (Integer.valueOf(auxI[0]) + 1) + " pr " + auxI[1]);
                } else {
                    util.setProperties("info.properties", "info.individual:" + index);
                }
                ++index;
                indiNovo = avaliate.perturb(indiAtual);
                tmp = avaliate.calc(indiNovo);
                tmp *= (tmp < .0) ? -1 : 1;
                iNovo = (tmp != .0) ? tmp : iNovo;
                delta = iNovo - iAtual;
                if (delta < 0) {
                    indiAtual = indiNovo;
                    iAtual = iNovo;
                } else if (Math.random() < (Math.pow(Math.E, -1 * (delta / T)))) {
                    indiAtual = indiNovo;
                    iAtual = iNovo;
                }
            }
            switch (typeVariationT) {
                case 0:
                    T *= alfa;
                    break;
                case 1:
                    T /= (1 + alfa * (Math.sqrt(T)));
                    break;
                case 2:
                    T = annealingT(loop, initialT, T);
                    break;
                case 3:
                    T *= fixT;
                    break;
                default:
                    break;
            }
            ++loop;
        }
        util.limparArquivoXml("files/xml/dez/" + name + "_Melhor10.xml", "Topologies");
        util.limparArquivoXml("files/xml/" + name + "_Melhor.xml", "Topologies");
        util.limparArquivoXml("files/xml/pior/" + name + "_Pior.xml", "Topologies");
        avaliate.persists(name + "_Melhor", "pior/"+name + "_Pior", "files/" + name);
        avaliate.position(name);
        avaliate.persistsX("dez/"+name + "_Melhor10");
    }
}
