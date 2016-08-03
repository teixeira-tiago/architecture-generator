/**
 * Este programa foi desenvolvido no Departamento de Inforamtica
 * da Universidade Federal de Vicosa dentro do convenio CNPqD/DPI.
 * Criado em 04/12/2004.
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
 * Interface para individuos de uma populacao para o algoritmo genetico.
 * 
 * @author Vladimir Oliveira Di Iorio
 * @author Marcus Vincius Alvim Andrade
 * @author Marco Antonio Carim Filho
 * @author Luana Cristina Lima da Fonseca
 * @author Angelo Souza Cavalier
 */
public interface Individual {

    /**
     * Executa casamento entre o individuo e um segundo, gerando
     * dois filhos.
     * @param other O outro individuo envolvido no casamento.
     * @param firstChild Primeiro filho do casamento. Este objeto ja deve
     * ter sido previamente alocado, e pode ser modificado pelo metodo.
     * @param secondChild Segundo filho do casamento. Este objeto ja deve
     * ter sido previamente alocado, e pode ser modificado pelo metodo.
     */
    void crossOver(Individual other,
            Individual firstChild, Individual secondChild);
    
    void crossOver(int type, Individual other,
            Individual firstChild, Individual secondChild);

    /**
     * Executa mutacao sobre o individuo.
     */
    void mutation();

    /**
     * Retorna valor da funcao de fitness do individuo.
     * Individuos mais adaptados devem retornar valores maiores.
     * @return Valor da funcao de fitness (numero real).
     */
    double fitnessValue();
}
