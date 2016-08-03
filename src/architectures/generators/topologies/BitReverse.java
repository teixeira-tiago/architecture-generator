package architectures.generators.topologies;

public class BitReverse extends MultiStage {

    @Override
    public int getValue(int value, int length, int bit) {
        int a, b, c;
        a = (value & 1) << (length - 1);
        b = (value & (1 << (length - 1))) >> (length - 1);
        if ((length % 2) == 0) {
            c = (value & length) | (value & (length >> 1));
        } else {
            c = (value & (length - 1));
        }
        return ((a | b | c) & ((1 << length) - 1));
    }

    @Override
    public String getName() {
        return "BitReverse";
    }
}
