/**
 * Este programa foi desenvolvido no Departamento de Informatica
 * da Universidade Federal de Vicosa dentro do convenio CNPqD/DPI.
 * Criado em 05/12/2004.
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

import java.util.Date;
import java.util.Random;

/**
 * Um unico gerador de numeros aleatorios.
 * 
 * @author Vladimir Oliveira Di Iorio
 * @author Marcus Vincius Alvim Andrade
 * @author Marco Antonio Carim Filho
 * @author Luana Cristina Lima da Fonseca
 * @author Angelo Souza Cavalier
 */
public class MyRandom {

    /**
     * Semente para a sequencia aleatoria.
     */
    private static final int SEED = (int) (new Date()).getTime();
    /**
     * Um gerador de numeros aleatorios.
     */
    private static Random random = new Random(SEED);

    /**
     * Gera um double aleatorio.
     * @return Valor aleatorio gerado, no intervalo [0,1).
     */
    public static double nextDouble() {
        return random.nextDouble();
    }
}
