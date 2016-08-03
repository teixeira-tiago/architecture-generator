package architectures.interfaces;

public interface Topology {

    public String getName();

    public int getValue(int value, int length, int bit);

    public int getValue(int value, int length);
}
