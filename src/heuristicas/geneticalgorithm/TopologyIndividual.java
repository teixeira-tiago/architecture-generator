package heuristicas.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import utiliters.Utiliter;
import xml.topologyclass.Colun;
import xml.topologyclass.Element;
import xml.topologyclass.Line;
import xml.topologyclass.Link;
import heuristicas.Avaliation;
import heuristicas.geneticalgorithm.genalgpackage.Individual;
import heuristicas.pathrelinking.PathRelinking;

public class TopologyIndividual implements Individual {

    private xml.topologyclass.ObjectFactory fac = new xml.topologyclass.ObjectFactory();
    private static Random Random1 = new Random();
    private static Random Random2 = new Random();
    private Element individual = fac.createElement();
    private double fitness = Double.MAX_VALUE;
    private Avaliation avaliate;

    public TopologyIndividual(Element individuo, Avaliation avaliate) {
        this.individual = individuo;
        this.avaliate = avaliate;
    }

    @Override
    public String toString() {
        return String.valueOf(fitness);
    }

    /**
     * A aplicacao dessa opcao de crossover depende da geracao aleatoria
     * de um ponto de corte, esse ponto de corte sera utilizado para 
     * determinar qual parte de cada um de dois vetores serao combinadas 
     * de forma a gerar dois novos vetores filhos
     * @param other O outro vetor que sera combinado
     * @param firstChild O primeiro filho que sera gerado
     * @param secondChild O segundo filho que sera gerado
     */
    public void crossTopology(Individual other, Individual firstChild,
            Individual secondChild) {
        TopologyIndividual outro = (TopologyIndividual) other;
        TopologyIndividual primFilho = (TopologyIndividual) firstChild;
        TopologyIndividual segFilho = (TopologyIndividual) secondChild;
        int pos, i;
        if (individual.getLink().size() <= outro.individual.getLink().size()) {
            pos = Math.abs(Random1.nextInt()) % individual.getLink().size();
        } else {
            pos = Math.abs(Random1.nextInt()) % outro.individual.getLink().size();
        }
        List<Link> lstLigInd = individual.getLink();
        List<Link> lstLigOut = outro.individual.getLink();
        List<Link> lstLigPriF = primFilho.individual.getLink();
        List<Link> lstLigSegF = segFilho.individual.getLink();
        Link ligInd = null;
        Link ligOut = null;
        Link ligPriF = null;
        Link ligSegF = null;
        for (i = 0; i < pos; i++) {
            ligInd = lstLigInd.get(i);
            ligPriF = fac.createLink();
            ligPriF.setId(i);
            ligPriF.setLine(ligInd.getLine());
            ligPriF.setColun(ligInd.getColun());
            lstLigPriF.add(ligPriF);
        }
        for (i = pos; i < outro.individual.getLink().size(); i++) {
            ligOut = lstLigOut.get(i);
            ligPriF = fac.createLink();
            ligPriF.setId(i);
            ligPriF.setLine(ligOut.getLine());
            ligPriF.setColun(ligOut.getColun());
            lstLigPriF.add(ligPriF);
        }
        for (i = 0; i < pos; i++) {
            ligOut = lstLigOut.get(i);
            ligSegF = fac.createLink();
            ligSegF.setId(i);
            ligSegF.setLine(ligOut.getLine());
            ligSegF.setColun(ligOut.getColun());
            lstLigSegF.add(ligSegF);
        }
        for (i = pos; i < individual.getLink().size(); i++) {
            ligInd = lstLigInd.get(i);
            ligSegF = fac.createLink();
            ligSegF.setId(i);
            ligSegF.setLine(ligInd.getLine());
            ligSegF.setColun(ligInd.getColun());
            lstLigSegF.add(ligSegF);
        }

        if (individual.getLink().size() >= outro.individual.getLink().size()) {
            for (i = primFilho.individual.getLink().size(); i < individual.getLink().size(); ++i) {
                ligInd = lstLigInd.get(i);
                ligPriF = fac.createLink();
                ligPriF.setId(i);
                ligPriF.setLine(ligInd.getLine());
                ligPriF.setColun(ligInd.getColun());
                lstLigPriF.add(ligPriF);

            }
            for (i = segFilho.individual.getLink().size(); i < individual.getLink().size(); ++i) {
                ligInd = lstLigInd.get(i);
                ligSegF = fac.createLink();
                ligSegF.setId(i);
                ligSegF.setLine(ligInd.getLine());
                ligSegF.setColun(ligInd.getColun());
                lstLigSegF.add(ligSegF);
            }
        } else {
            for (i = primFilho.individual.getLink().size(); i < outro.individual.getLink().size(); ++i) {
                ligOut = lstLigOut.get(i);
                ligPriF = fac.createLink();
                ligPriF.setId(i);
                ligPriF.setLine(ligOut.getLine());
                ligPriF.setColun(ligOut.getColun());
                lstLigPriF.add(ligPriF);
            }
            for (i = segFilho.individual.getLink().size(); i < outro.individual.getLink().size(); ++i) {
                ligOut = lstLigOut.get(i);
                ligSegF = fac.createLink();
                ligSegF.setId(i);
                ligSegF.setLine(ligOut.getLine());
                ligSegF.setColun(ligOut.getColun());
                lstLigSegF.add(ligSegF);
            }
        }
        ((TopologyIndividual) primFilho).calcFitness();
        ((TopologyIndividual) segFilho).calcFitness();
    }

    /**
     * Esse metodo de crossover gera um unico ponto de corte 
     * aleatorio para um elemento e replica esse ponto de 
     * corte para todos os outros elementos
     * @param other O outro vetor que sera combinado
     * @param firstChild O primeiro filho que sera gerado
     * @param secondChild O segundo filho que sera gerado
     */
    public void crossNode(Individual other, Individual firstChild,
            Individual secondChild) {
        TopologyIndividual outro = (TopologyIndividual) other;
        TopologyIndividual primFilho = (TopologyIndividual) firstChild;
        TopologyIndividual segFilho = (TopologyIndividual) secondChild;
        int pos, i;
        if (individual.getLink().size() <= outro.individual.getLink().size()) {
            pos = Math.abs(Random1.nextInt()) % individual.getLink().size();
        } else {
            pos = Math.abs(Random1.nextInt()) % outro.individual.getLink().size();
        }
        List<Link> lstLigPriF = primFilho.individual.getLink();
        List<Link> lstLigSegF = segFilho.individual.getLink();
        List<Link> lstLigOut = outro.individual.getLink();
        List<Link> lstLigInd = individual.getLink();
        Link ligPriF = null;
        Link ligSegF = null;
        ArrayList<Line> auxLinePri = new ArrayList<Line>();
        ArrayList<Line> auxLineSeg = new ArrayList<Line>();
        ArrayList<Colun> auxColunPri = new ArrayList<Colun>();
        ArrayList<Colun> auxColunSeg = new ArrayList<Colun>();
        for (i = 0; i < pos; i++) {
            auxLinePri.add(lstLigInd.get(i).getLine());
        }
        for (i = pos; i < outro.individual.getLink().size(); i++) {
            auxLinePri.add(lstLigOut.get(i).getLine());
        }
        for (i = 0; i < pos; i++) {
            auxLineSeg.add(lstLigOut.get(i).getLine());
        }
        for (i = pos; i < individual.getLink().size(); i++) {
            auxLineSeg.add(lstLigInd.get(i).getLine());
        }
        if (individual.getLink().size() >= outro.individual.getLink().size()) {
            for (i = primFilho.individual.getLink().size(); i < individual.getLink().size(); ++i) {
                auxLinePri.add(lstLigInd.get(i).getLine());
            }
            for (i = segFilho.individual.getLink().size(); i < individual.getLink().size(); ++i) {
                auxLineSeg.add(lstLigInd.get(i).getLine());
            }
        } else {
            for (i = primFilho.individual.getLink().size(); i < outro.individual.getLink().size(); ++i) {
                auxLinePri.add(lstLigOut.get(i).getLine());
            }
            for (i = segFilho.individual.getLink().size(); i < outro.individual.getLink().size(); ++i) {
                auxLineSeg.add(lstLigOut.get(i).getLine());
            }
        }
        if (individual.getLink().size() <= outro.individual.getLink().size()) {
            pos = Math.abs(Random2.nextInt()) % individual.getLink().size();
        } else {
            pos = Math.abs(Random2.nextInt()) % outro.individual.getLink().size();
        }
        for (i = 0; i < pos; i++) {
            auxColunPri.add(lstLigInd.get(i).getColun());
        }
        for (i = pos; i < outro.individual.getLink().size(); i++) {
            auxColunPri.add(lstLigOut.get(i).getColun());
        }
        for (i = 0; i < pos; i++) {
            auxColunSeg.add(lstLigOut.get(i).getColun());
        }
        for (i = pos; i < individual.getLink().size(); i++) {
            auxColunSeg.add(lstLigInd.get(i).getColun());
        }
        if (individual.getLink().size() >= outro.individual.getLink().size()) {
            for (i = primFilho.individual.getLink().size(); i < individual.getLink().size(); ++i) {
                auxColunPri.add(lstLigInd.get(i).getColun());
            }
            for (i = segFilho.individual.getLink().size(); i < individual.getLink().size(); ++i) {
                auxColunSeg.add(lstLigInd.get(i).getColun());
            }
        } else {
            for (i = primFilho.individual.getLink().size(); i < outro.individual.getLink().size(); ++i) {
                auxColunPri.add(lstLigOut.get(i).getColun());
            }
            for (i = segFilho.individual.getLink().size(); i < outro.individual.getLink().size(); ++i) {
                auxColunSeg.add(lstLigOut.get(i).getColun());
            }
        }
        lstLigPriF.clear();
        for (i = 0; i < auxLinePri.size(); ++i) {
            ligPriF = fac.createLink();
            ligPriF.setId(i);
            ligPriF.setLine(auxLinePri.get(i));
            ligPriF.setColun(auxColunPri.get(i));
            lstLigPriF.add(ligPriF);
        }
        lstLigSegF.clear();
        for (i = 0; i < auxLineSeg.size(); ++i) {
            ligSegF = fac.createLink();
            ligSegF.setId(i);
            ligSegF.setLine(auxLineSeg.get(i));
            ligSegF.setColun(auxColunSeg.get(i));
            lstLigSegF.add(ligSegF);
        }
        ((TopologyIndividual) primFilho).calcFitness();
        ((TopologyIndividual) segFilho).calcFitness();
    }

    public void crossPathRelinking(Individual other, Individual firstChild,
            Individual secondChild) {
        TopologyIndividual outro = (TopologyIndividual) other;
        TopologyIndividual primFilho = (TopologyIndividual) firstChild;
        TopologyIndividual segFilho = (TopologyIndividual) secondChild;
        PathRelinking pr = new PathRelinking(avaliate);
        if ((individual == null) && (outro.individual == null)){
            System.out.println("Todos pais nulos");
            return;
        } else if (individual == null){
            System.out.println("Pai 1 nulo");
            primFilho.individual = outro.individual;
            segFilho.individual = outro.individual;
        } else if (outro.individual == null){
            System.out.println("Pai 2 nulo");
            primFilho.individual = individual;
            segFilho.individual = individual;
        } else {
            primFilho.individual = pr.pathRelinking(individual, outro.individual);
            segFilho.individual = pr.pathRelinking(outro.individual, individual);
        }
        ((TopologyIndividual) primFilho).calcFitness();
        ((TopologyIndividual) segFilho).calcFitness();
    }

    @Override
    public void crossOver(int type, Individual other, Individual firstChild,
            Individual secondChild) {
        switch (type) {
            case 0:
                crossTopology(other, firstChild, secondChild);
                break;
            case 1:
                crossNode(other, firstChild, secondChild);
                break;
            case 2:
                crossPathRelinking(other, firstChild, secondChild);
                break;
            default:
                crossTopology(other, firstChild, secondChild);
                break;
        }
    }

    @Override
    public double fitnessValue() {
        if (fitness > (Double.MAX_VALUE / 2)) {
            fitness = .0;
            calcFitness();
            return fitness;
        } else {
            return fitness;
        }
    }

    private void calcFitness() {
        String[] aux;
        String tmp;        
        Utiliter util = new Utiliter();
        Properties prop = util.getProperties("info.properties");
        tmp = prop.getProperty("info.individual");
        if (tmp.contains(" pr ")) {
            aux = tmp.split(" pr ");
            util.setProperties("info.properties", "info.individual:" + (Integer.valueOf(aux[0]) + 1) + " pr " + aux[1]);
        } else {
            util.setProperties("info.properties", "info.individual:" + (Integer.valueOf(tmp) + 1));
        }
        fitness = avaliate.calc(individual);
    }

    @Override
    public void mutation() {
        individual = avaliate.perturb(individual);
    }

    public Element Elemento() {
        return individual;
    }

    public Avaliation Avaliacao() {
        return avaliate;
    }

    @Override
    public void crossOver(Individual other, Individual firstChild, Individual secondChild) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
