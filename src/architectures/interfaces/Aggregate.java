package architectures.interfaces;

/**
 * Interface para agregados, que so colees
 * que oferecem iteradores para serem percorridas.
 * @author Vladimir Oliveira Di Iorio
 */
public interface Aggregate<T> {

    /**
     * Mtodo para criar um iterador para a coleo.
     * @return Iterador criado, que inicialmente pode no
     * estar indicando nenhum elemento vlido.
     */
    Iterator<T> createIterator();
}
