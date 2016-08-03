package architectures.interfaces;

/**
 * Iterador para uma coleo (agregado).
 * @author Vladimir Oliveira Di Iorio
 */
public interface Iterator<T> {

    /**
     * Iterador passa a indicar a primeira posio da coleo.
     */
    void first();

    /**
     * Iterador passa a indicar a posio seguinte  que
     * estava indicando.
     *
     */
    void next();

    /**
     * Obtm elemento da posio atual.
     * @return Elemento da posio atual.
     */
    T currentItem();

    /**
     * Indica se iterador ultrapassou o final da coleo.
     * @return true, se iterador indica posio aps o final da coleo;
     * false, caso contrrio.
     */
    boolean isDone();
}
