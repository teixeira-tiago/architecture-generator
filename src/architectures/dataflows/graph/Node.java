package architectures.dataflows.graph;

import mapping.placement.ProcessorGraph;

public class Node {

    public Node() {
    }

    public Node(String name) {
        this.name = name;
    }

    public Node(String name, String op, String unit) {
        this.name = name;
        this.operation = op;
        this.unit = unit;
    }
    public String unit;
    public String operation;
    public String name;
    public ProcessorGraph subProcessor;
    private String portValue = "-1";

    @Override
    public String toString() {
        return name;
    }

    public String getOperation() {
        return operation;
    }

    public String getUnit() {
        return unit;
    }

    public String getPortConstantValue(String port) {
        return portValue;
    }

    public void setPortValue(String port, String value) {
        portValue = value;
    }
}