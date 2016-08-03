package mapping.placement;

import java.util.ArrayList;
import java.util.HashMap;
import architectures.dataflows.graph.Node;

public class PE {

    public ArrayList<Path> outputConnections;
    public ArrayList<Path> inputConnections;
    protected ArrayList<Path> inoutConnections;
    protected ArrayList<Path> CInConnections;
    protected ArrayList<Path> COutConnections;
    public HashMap<String, DistRoute> distanceTable;
    public int inNumber;
    public int outNumber;
    public int inOutNumber;
    public int CInNumber;
    public int COutNumber;
    public String name;
    public Node nodo;
    protected ArrayList<String> internalPaths;

    public PE() {
        outputConnections = new ArrayList<Path>();
        inputConnections = new ArrayList<Path>();
        inoutConnections = new ArrayList<Path>();
        COutConnections = new ArrayList<Path>();
        CInConnections = new ArrayList<Path>();
        distanceTable = new HashMap<String, DistRoute>();
    }

    public PE(String nameAux) {
        outputConnections = new ArrayList<Path>();
        inputConnections = new ArrayList<Path>();
        inoutConnections = new ArrayList<Path>();
        COutConnections = new ArrayList<Path>();
        CInConnections = new ArrayList<Path>();
        distanceTable = new HashMap<String, DistRoute>();
        name = nameAux;
    }

    public void setNode(Node node) {
        nodo = node;
    }

    public ArrayList<Path> returnInputConnections() {
        return inputConnections;
    }

    public ArrayList<Path> returnOutputConnections() {
        return outputConnections;
    }

    public ArrayList<Path> returnOutConnections() {
        ArrayList<Path> aux = new ArrayList<Path>();
        int i;
        for (i = 0; i < outputConnections.size(); i++) {
            aux.add(outputConnections.get(i));
        }
        for (i = 0; i < inoutConnections.size(); i++) {
            aux.add(inoutConnections.get(i));
        }
        return aux;
    }

    public ArrayList<Path> returnNotUsedOutputConnections() {
        ArrayList<Path> aux = new ArrayList<Path>();
        int i;
        for (i = 0; i < outputConnections.size(); i++) {
            if (outputConnections.get(i).used == false) {
                aux.add(outputConnections.get(i));
            }
        }
        return aux;
    }

    public void addOutputConnection(String outPort, String targetPE,
            String targetPort, int cost, boolean used) {
        outputConnections.add(new Path(outPort,
                new PortID(targetPE, targetPort), cost, used, false));
    }

    public void addInputConnection(String inPort, String sourcePE,
            String sourcePort, int cost, boolean used) {
        inputConnections.add(new Path(inPort, new PortID(sourcePE, sourcePort),
                cost, used, false));
    }

    public void addInOutConnection(String ioPort, String anotherPE,
            String anotherPort, int cost, boolean used) {
        inoutConnections.add(new Path(ioPort,
                new PortID(anotherPE, anotherPort), cost, used, false));
    }

    public void setDistanceTable(HashMap<String, DistRoute> dist) {
        distanceTable = dist;
    }
}
