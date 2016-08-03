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

/**
 * @author Vladimir Oliveira Di Iorio
 * @author Marcus Vinicius Alvim Andrade
 * @author Marco Antonio Carim Filho
 * @author Luana Cristina Lima da Fonseca
 * @author Angelo Souza Cavalier
 *
 */
public class GenAlgException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constroi uma excecao do algoritmo genetico.
     * @param msg Texto da excecao.
     */
    public GenAlgException(String msg) {
        super(msg);
    }
}
