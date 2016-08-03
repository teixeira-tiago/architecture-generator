package architectures.generators.topologies;

public class InversePerfectShuffler extends MultiStage {

    @Override
    public int getValue(int value, int length, int bit) {
        int a, b;
        a = value >> 1; // desloca-se um bit a direita
        b = value << (length - 1); // desloca-se o tamanho da string de bits menos
        // um a esquerda
        return ((a | b) & ((1 << length) - 1));
    }

    @Override
    public String getName() {
        return "InversePerfectShuffler";
    }
}
