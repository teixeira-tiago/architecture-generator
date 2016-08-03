package architectures.generators.topologies;

public class BaseLine extends MultiStage {

    @Override
    public int getValue(int value, int length, int bit) {
        MultiStage mult;
        switch (bit) {
            case 0:
                return value;
            case 1:
                mult = new Butterfly();
                return mult.getValue(value, length, bit);
        }
        mult = new InversePerfectShuffler();
        return mult.getValue(value, length);
    }

    @Override
    public String getName() {
        return "BaseLine";
    }
}
