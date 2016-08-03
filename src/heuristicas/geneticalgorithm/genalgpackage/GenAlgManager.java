/**
 * Este programa foi desenvolvido no Departamento de Informatica
 * da Universidade Federal de Vicosa dentro do convenio CNPqD/DPI.
 * Criado em 03/12/2004.
 * 
 * Equipe de desenvolvimento composta por: 
 * 
 * @author Vladimir Oliveira Di Iorio  
 * @author Marcus Vincius Alvim Andrade
 * @author Marco Antonio Carim Filho
 * @author Luana Cristina Lima da Fonseca
 * @author Angelo Souza Cavalier
 */
package heuristicas.geneticalgorithm.genalgpackage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xml.topologyclass.Element;
import utiliters.Utiliter;
import utiliters.XMLToFiles;
import heuristicas.Avaliation;
import heuristicas.geneticalgorithm.TopologyIndividual;

/**
 * Gerenciador de algoritmo genetico.
 * eh possivel estabelecer diversos parametros que controlam o seu
 * funcionamento. Caracteristicas associadas aos individuos da populacao
 * utilizada sao definidas por meio da interface Individual. 
 * @see genalgpackage.Individual
 * 
 * @author Vladimir Oliveira Di Iorio
 * @author Marcus Vincius Alvim Andrade
 * @author Marco Antonio Carim Filho
 * @author Luana Cristina Lima da Fonseca
 * @author Angelo Souza Cavalier
 */
public class GenAlgManager {

    /**
     * Valor default para a taxa de renovacao da populacao
     * (cliente da classe pode escolher um novo valor).
     * Indica a porcentagem de individuos que irao ser eliminados
     * a cada iteracao do algoritmo genetico, e em seu lugar serao
     * colocados novos individuos resultante do cruzamento de pais
     * provavelmente mais bem adaptados.
     */
    private static final double DEFAULT_RENOVATION_TAX = 0.10;
    /**
     * Valor default para probabilidade de ocorrer uma mutacao
     * (cliente da classe pode escolher um novo valor).
     */
    private static final double DEFAULT_MUTATION_PROB = 0.01;
    /**
     * Valor default para numero maximo de geracoes sem melhora da funcao
     * de fitness total
     * (cliente da classe pode escolher um novo valor).
     * Afeta a condicao de parada. Quanto maior o valor, mais lenta a
     * convergencia do algoritmo.
     */
    private static final int DEFAULT_MAX_GENERATIONS_WITHOUT_IMPROVEMENT = 5;
    /**
     * Valor default para o incremento usado na distribuicao
     * de probabilidades de escolha de individuos da populacao
     * (cliente da classe pode escolher um novo valor).
     * Este valor indica a razao entre a probabilidade de se escolher o
     * individuo MAIS adaptado da populacao e a probabilidade de se escolher
     * o individuo MENOS adaptado da populacao. Quanto maior for este valor,
     * mais se privilegia a escolha de individuos mais adaptados para o
     * casamento, e mais se privilegia a escolha de individuos menos
     * adaptados para a eliminacao.
     */
    private static final double DEFAULT_PROB_INCREMENT = 10.0;
    /**
     * Tamanho minimo aceitavel para uma populacao.
     */
    private static final double MIN_SIZE_POPULATION = 10;
    /**
     * Constante que representa 1/2.
     */
    private static final double HALF = 0.5;
    /**
     * Constante que representa a ausencia de geracao de log
     * @author Tiago Teixeira
     */
    private static final boolean DEFAULT_LOG = false;
    /**
     * Horario de inicio da execucao do algoritmo genetico.
     */
    private Date initialTime;
    /**
     * Probabilidade de ocorrer mutacao.
     */
    private double dMutationProb;
    /**
     * Numero maximo de geracoes sem melhora da funcao de fitness total.
     * Afeta a condicao de parada. Quanto maior o valor, mais lenta a
     * convergencia do algoritmo.
     */
    private int intMaxGenerationsWithoutImprovement;
    /**
     * Numero atual de geracoes sem melhora na funcao de fitness total.
     */
    private int intNumGenerationsWithoutImprovement;
    /**
     * Tamanho da populacao.
     */
    private int intSizePopulation;
    /**
     * Array com os individuos da populacao.
     */
    private Individual[] individuals;
    /**
     * Valor do somatorio da funcao de fitness de todos os individuos
     * da populacao.
     */
    private double dTotalFitness;
    /**
     * Numero de elementos que sao renovados na populacao,
     * a cada iteracao do algoritmo genetico.
     */
    private int intRenovation;
    /**
     * Subconjunto de individuos que iro ser usados como pais da
     * proxima geracao.
     */
    private Individual[] subsetToMate;
    /**
     * Subconjunto de individuos que serao eliminados da proxima geracao.
     */
    private Individual[] subsetToDie;
    /**
     * Tabela com as probabilidades para escolha aleatoria de
     * individuos na populacao.
     */
    private double[] tabProb;
    /**
     * Auxiliar para determinacao dos subconjuntos de individuos
     * que irao se tornar pais da proxima gerao e os que irao
     * ser eliminados.
     */
    private boolean[] boolEscolhidos;
    /**
     * Lista contendo os individuos que permanecem em cada geracao
     * e os individuos que serao eliminados na formacao da proxima geracao
     */
    private ArrayList<String> print = new ArrayList<String>();
    /**
     * Lista contendo os individuos ordenados por geracao e os melhores
     * e piores individuos de cada geracao
     */
    private ArrayList<String> printOrdenate = new ArrayList<String>();
    /**
     * Lista contendo todos os individuos da ultima geracao
     */
    private List<Element> elementos;
    /**
     * Subconjunto contendo os individuos que serao eliminados das proximas geracoes
     */
    private int[] posSubSetToDie;
    /**
     * Metodo que possue a melhor topologia
     */
    private TopologyIndividual bestTopology;
    /**
     * Metodo que possue a pior topologia
     */
    private TopologyIndividual worseTopology;
    /**
     * Metodos utilitarios
     */
    private String name;
    private int contGe = 0;
    private Avaliation avaliate;
    private Utiliter util = new Utiliter();

    /**
     * Construtor que usa os parametros default.
     * O algoritmo eh imediatamente executado sobre a populacao fornecida.
     * Ao final da execuo, o array fornecido como parametro contem
     * individuos modificados, ordenados de forma crescente de acordo com
     * a sua funcao de fitness.
     * @param individuals Array com individuos da populacao inicial.
     * @throws GenAlgException Se os valores default nao estiverem
     * corretamente estabelecidos (nao deve acontecer nunca). 
     */
    public GenAlgManager(Individual[] individuals)
            throws GenAlgException {
        this(individuals, DEFAULT_RENOVATION_TAX,
                DEFAULT_PROB_INCREMENT, DEFAULT_MUTATION_PROB,
                DEFAULT_MAX_GENERATIONS_WITHOUT_IMPROVEMENT);
    }

    /**
     * Construtor que usa parametros definidos pelo usuario para
     * customizar o funcionamento do algoritmo genetico.
     * O algoritmo eh imediatamente executado sobre a populacao fornecida.
     * Ao final da execucao, o array fornecido como parametro contem
     * individuos modificados, ordenados de forma crescente de acordo com
     * a sua funcao de fitness.
     * @param individuals Array com individuos da populacao inicial.
     * @param dRenovationTax Taxa de renovacao da populacao.
     * Indica a porcentagem de individuos que irao ser eliminados
     * a cada iteracao do algoritmo genetico, e em seu lugar serao
     * colocados novos individuos resultante do cruzamento de pais
     * provavelmente mais bem adaptados.
     * Deve ser valor tipicamente maior que zero e menor que 0.5.
     * @param dProbDistributionIncrement Incremento usado na distribuicao
     * de probabilidades para escolha de individuos da populacao.
     * Este valor indica a razao entre a probabilidade de se escolher o
     * individuo MAIS adaptado da populacao e a probabilidade de se escolher
     * o individuo MENOS adaptado da populacao. Quanto maior for este valor,
     * mais se privilegia a escolha de individuos mais adaptados para casamento,
     * e mais se privilegia a escolha de individuos menos adaptados para
     * eliminacao. Deve ser valor maior que 1.
     * @param dMutationProb Probabilidade de ocorrer uma mutacao.
     * @param intMaxGenerationsWithoutImprovement Numero maximo de geracoes
     * sem melhora da funcao de fitness total. Afeta a condicao de parada.
     * Quanto maior o valor, mais lenta a convergencia do algoritmo.
     * Deve ser valor maior que zero.
     * @throws GenAlgException Se os valores fornecidos nao estiverem
     * dentro dos limites aceitaveis pre-determinados.
     */
    public GenAlgManager(
            Individual[] individuals,
            double dRenovationTax,
            double dProbDistributionIncrement,
            double dMutationProb,
            int intMaxGenerationsWithoutImprovement)
            throws GenAlgException {

        if (individuals.length < MIN_SIZE_POPULATION) {
            throw new GenAlgException(
                    "Initial population must have at least " + MIN_SIZE_POPULATION + " individuals");
        }
        if (dRenovationTax <= 0 || dRenovationTax > HALF) {
            throw new GenAlgException(
                    "Renovation tax must be inside (0," + HALF + "] interval");
        }
        if (dProbDistributionIncrement <= 1) {
            throw new GenAlgException(
                    "Increment for distribution of probabilities must be" + " greater than 1");
        }
        if (dMutationProb < 0 || dMutationProb >= 1) {
            throw new GenAlgException(
                    "Probability of mutation must be inside [0,1) interval");
        }
        if (intMaxGenerationsWithoutImprovement <= 0) {
            throw new GenAlgException(
                    "Maximum number of generations without improvement must be " + " greater than 0");
        }

        this.individuals = individuals;
        this.intMaxGenerationsWithoutImprovement =
                intMaxGenerationsWithoutImprovement;
        this.dMutationProb = dMutationProb;
        intSizePopulation = individuals.length;
        sort();

        intRenovation = (int) Math.round(intSizePopulation * dRenovationTax);
        if (intRenovation % 2 == 1) {
            // garante que o numero de renovacao eh par, maior ou igual a 2
            if (intRenovation == 1) {
                intRenovation = 2;
            } else {
                intRenovation -= 1;
            }
        }
        subsetToMate = new Individual[intRenovation];
        subsetToDie = new Individual[intRenovation];
        posSubSetToDie = new int[intRenovation];

        tabProb = new double[intSizePopulation + 1];
        double a = (2 * dProbDistributionIncrement - 2) / (2 * intSizePopulation - 1 - dProbDistributionIncrement);
        double doisArea = ((a * intSizePopulation + 2) * intSizePopulation);
        int i;
        for (i = 0; i <= intSizePopulation; ++i) {
            tabProb[i] = ((a * i + 2) * i) / doisArea;
        }

        boolEscolhidos = new boolean[intSizePopulation];

        intNumGenerationsWithoutImprovement = 0;
    }

    /**
     * Ordena a populacao de forma crescente, usando a funcao de fitness
     * de cada individuo.
     */
    private void sort() {
        int minIndex, i, j;
        Individual minIndividual, otherIndividual;
        for (i = 0; i < intSizePopulation - 1; ++i) {
            minIndex = i;
            minIndividual = individuals[i];
            for (j = i + 1; j < intSizePopulation; ++j) {
                otherIndividual = individuals[j];
                if (otherIndividual.fitnessValue() < minIndividual.fitnessValue()) {
                    minIndex = j;
                    minIndividual = otherIndividual;
                }
            }
            individuals[minIndex] = individuals[i];
            individuals[i] = minIndividual;
        }
    }

    /**
     * Dado um valor no intervalo [0,1], encontra a posicao do elemento
     * da populacao associado a essa probabilidade, de acordo com a
     * distribuicao estabelecida pelos parametros da classe. 
     * @param dProb Probabilidade procurada (valor no intervalo [0,1]).
     * @return Posicao do elemento da populacao associado.
     */
    public int findPosProb(double dProb) {
        int min = 0, max = intSizePopulation, mid;
        while (min < max - 1) {
            mid = (min + max) / 2;
            if (dProb > tabProb[mid]) {
                min = mid;
            } else {
                max = mid;
            }
        }
        return min;
    }

    /**
     * Calcula os individuos que irao ser usados como pais da
     * nova geracao e aqueles que irao ser eliminados.
     * O algoritmo abaixo foi concebido supondo que a taxa de
     * renovao da populacao eh baixa, a cada iteracao do algoritmo
     * genetico. Se essa taxa for alta, aconselha-se a
     * construir um metodo diferente, pois o metodo abaixo pode
     * tornar-se lento para altas taxas de renovacao.
     * O metodo implementado pode gerar instancias repetidas no
     * conjunto de pais. No conjunto de individuos que irao ser
     * eliminados, entretanto, nao sao geradas repeticoes, nem
     * mesmo nenhuma intersecao com o conjunto de pais - garante
     * que serao eliminados realmente o numero de individuos
     * esperado pela taxa de renovacao.
     */
    private void calculateMateAndDie() {
        int i;
        for (i = 0; i < intSizePopulation; ++i) {
            boolEscolhidos[i] = false;
        }
        double r;
        int pos;
        for (i = 0; i < intRenovation; ++i) {
            r = MyRandom.nextDouble();
            pos = findPosProb(r);
            subsetToMate[i] = individuals[pos];
            boolEscolhidos[pos] = true;
        }
        for (i = 0; i < intRenovation; ++i) {
            while (true) {
                r = MyRandom.nextDouble();
                pos = intSizePopulation - 1 - findPosProb(r);
                if (!boolEscolhidos[pos]) {
                    subsetToDie[i] = individuals[pos];
                    posSubSetToDie[i] = pos;
                    boolEscolhidos[pos] = true;
                    break;
                }
            }
        }
    }

    /**
     * Um passo (iteracao) no algoritmo genetico.
     */
    private void step() throws GenAlgException {
        calculateMateAndDie();
        Individual pai1, pai2, filho1, filho2;
        double r, f1, f2;
        int i;
        for (i = 0; i < intRenovation; i += 2) {
            pai1 = subsetToMate[i];
            pai2 = subsetToMate[i + 1]; // espero que sempre exista...

            filho1 = subsetToDie[i];
            filho2 = subsetToDie[i + 1]; // espero que sempre exista...

            dTotalFitness -= filho1.fitnessValue();
            dTotalFitness -= filho2.fitnessValue();
            pai1.crossOver(pai2, filho1, filho2);
            r = MyRandom.nextDouble();
            if (r <= dMutationProb) {
                filho1.mutation();
            }
            r = MyRandom.nextDouble();
            if (r <= dMutationProb) {
                filho2.mutation();
            }
            f1 = filho1.fitnessValue();
            f2 = filho2.fitnessValue();
            if ((f1 == Double.MIN_VALUE) || (f2 == Double.MIN_VALUE)) {
                throw new GenAlgException(
                        "Invalid fitness value in genetic algorithm");
            }
            dTotalFitness += f1;
            dTotalFitness += f2;
        }
    }

    private void step(int typeCross) throws GenAlgException {
        calculateMateAndDie();
        Individual pai1, pai2, filho1, filho2;
        double r, f1, f2;
        int i;
        for (i = 0; i < intRenovation; i += 2) {
            pai1 = subsetToMate[i];
            pai2 = subsetToMate[i + 1]; // espero que sempre exista...

            filho1 = subsetToDie[i];
            filho2 = subsetToDie[i + 1]; // espero que sempre exista...

            dTotalFitness -= filho1.fitnessValue();
            dTotalFitness -= filho2.fitnessValue();
            pai1.crossOver(typeCross, pai2, filho1, filho2);
            r = MyRandom.nextDouble();
            if (r <= dMutationProb) {
                filho1.mutation();
            }
            r = MyRandom.nextDouble();
            if (r <= dMutationProb) {
                filho2.mutation();
            }
            f1 = filho1.fitnessValue();
            f2 = filho2.fitnessValue();
            if ((f1 == Double.MIN_VALUE) || (f2 == Double.MIN_VALUE)) {
                throw new GenAlgException(
                        "Invalid fitness value in genetic algorithm");
            }
            dTotalFitness += f1;
            dTotalFitness += f2;
        }
    }

    /**
     * Executa o algoritmo genetico, ate que a condicao de parada seja alcancada.
     * @param dMaxTime Tempo maximo de execucao do algoritmo genetico
     * (em milissegundos).
     * @return Individuo mais bem adaptado da populacao, apos evolucao.
     */
    public Individual run(double dMaxTime)
            throws GenAlgException {
        dTotalFitness = 0;
        double f;
        int i;
        for (i = 0; i < intSizePopulation; ++i) {
            f = individuals[i].fitnessValue();
            if (f == Double.MIN_VALUE) {
                throw new GenAlgException(
                        "Invalid fitness value in genetic algorithm");
            }
            dTotalFitness += f;
        }

        sort();
        initialTime = new Date();
        double dLastFitness, dif;
        Date currentTime;
        while (true) {
            dLastFitness = dTotalFitness;
            step();
            sort();
            if (dTotalFitness > dLastFitness) {
                intNumGenerationsWithoutImprovement = 0;
            } else {
                ++intNumGenerationsWithoutImprovement;
            }
            // Para se o numero de geracoes sem melhora na funcao
            // de fitness total ultrapassou o limite estabelecido            
            if (intNumGenerationsWithoutImprovement > intMaxGenerationsWithoutImprovement) {
                break;
            }
            // Para se o tempo total transcorrido ultrapassou
            // o limite estabelecido
            currentTime = new Date();
            dif = currentTime.getTime() - initialTime.getTime();
            if (dif > dMaxTime) {
                break;
            }
        }
        return individuals[intSizePopulation - 1];
    }

    public Individual run(double dMaxTime, int generations, Avaliation avaliate, String name, int typeCross)
            throws GenAlgException {
        this.avaliate = avaliate;
        this.name = name;
        dTotalFitness = 0;
        double f;
        int i;
        for (i = 0; i < intSizePopulation; ++i) {
            f = individuals[i].fitnessValue();
            if (f == Double.MIN_VALUE) {
                throw new GenAlgException(
                        "Invalid fitness value in genetic algorithm");
            }
            dTotalFitness += f;
        }
        sort();
        initialTime = new Date();
        double dLastFitness, dif;
        Date currentTime;
        while (true) {
            dLastFitness = dTotalFitness;
            ++contGe;
            print.add("\t\t\t-------Geracao--" + contGe + "---------");
            System.out.println("--Generation--" + contGe + "--");
            util.setProperties("info.properties", "info.generator:" + contGe);
            util.setProperties("info.properties", "info.new:true");
            util.setProperties("info.properties", "info.zero:true");
            step(typeCross);
            sort();
            if (dTotalFitness > dLastFitness) {
                intNumGenerationsWithoutImprovement = 0;
            } else {
                ++intNumGenerationsWithoutImprovement;
            }
            // Para se o numero de geracoes sem melhora na funcao
            // de fitness total ultrapassou o limite estabelecido            
            if (intNumGenerationsWithoutImprovement > intMaxGenerationsWithoutImprovement) {
                break;
            }
            // Para se o tempo total transcorrido ultrapassou
            // o limite estabelecido
            currentTime = new Date();
            dif = currentTime.getTime() - initialTime.getTime();
            if (generations > 0) {
                if (contGe > generations) {
                    break;
                }
            } else {
                if (dif > dMaxTime) {
                    break;
                }
            }
        }
        return individuals[intSizePopulation - 1];
    }

    public void ultimateGeneration() {
        XMLToFiles xtf = new XMLToFiles();
        util.limparArquivoXml("files/xml/ultima/" + name + "_ultima_geracao.xml", "Topologies");
        sort();
        Element elemento;
        int i;
        for (i = 0; i < individuals.length; ++i) {
            elemento = ((TopologyIndividual) individuals[i]).Elemento();
            if (elemento != null) {
                elemento.setId(i);
                xtf.generateFile(elemento, "ultima/"+name + "_ultima_geracao");
            }
        }
    }

    public Element getBest() {
        return bestTopology.Elemento();
    }

    public Element getWorse() {
        return worseTopology.Elemento();
    }

    public Avaliation getAvaliate() {
        return avaliate;
    }
}