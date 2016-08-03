package architectures.dataflows.extractor;

import architectures.dataflows.graph.Node;

public class Arco{

    private Node origem;
    private Node destino;

    public void arc(Node origem, Node destino) {
        this.origem = origem;
        this.destino = destino;
    }

    public Node getOrigem() {
        return origem;
    }

    public Node getDestino() {
        return destino;
    }

    @Override
    public String toString(){
        return origem.name +" -> "+destino.name;
    }

    public void stringToArc(String arco, String div){
        this.origem = new Node(arco.split(div)[0]);
        this.destino = new Node(arco.split(div)[1]);
    }
}
