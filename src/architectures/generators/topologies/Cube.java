package architectures.generators.topologies;

public class Cube extends MultiStage {

    @Override
    public int getValue(int value, int length, int bit) {
        return ((value ^ (1 << bit)) & ((1 << length) - 1));
    }

    @Override
    public String getName() {
        return "Cube";
    }
}
