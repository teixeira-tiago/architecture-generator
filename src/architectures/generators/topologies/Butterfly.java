package architectures.generators.topologies;

public class Butterfly extends MultiStage {

    @Override
    public int getValue(int value, int length, int bit) {
        int a, b, c;
        a = b = c = 0;
        switch (bit) {
            case 0:
                return value;
            case 1:
                a = (value & 1) << bit;
                b = (value & (1 << bit)) >> bit;
                c = (value & ~((bit << 1) + 1));
                break;
            case 2:
                BitReverse bitR = new BitReverse();
                return bitR.getValue(value, length);
        }
        return ((a | b | c) & ((1 << length) - 1));
    }

    @Override
    public String getName() {
        return "Buterfly";
    }
}
