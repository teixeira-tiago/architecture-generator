package architectures.generators.list;

import architectures.generators.list.ListaNos.Nos;
import architectures.interfaces.Aggregate;
import architectures.interfaces.Iterator;

public class ListaNosAggregate
        extends ListaNos
        implements Aggregate<Nos> {

    @Override
    public Iterator<Nos> createIterator() {
        return new IteratorNos();
    }

    private class IteratorNos
            implements Iterator<Nos> {

        private int index;

        @Override
        public void first() {
            index = 0;
        }

        @Override
        public void next() {
            ++index;
        }

        @Override
        public Nos currentItem() {
            return getLinha(index);
        }

        @Override
        public boolean isDone() {
            return index >= numLinhas();
        }
    }
}
