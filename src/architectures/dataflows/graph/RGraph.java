package architectures.dataflows.graph;

import java.util.*;
import org._3pq.jgrapht.EdgeFactory;
import org._3pq.jgrapht.edge.DirectedWeightedEdge;
import org._3pq.jgrapht.graph.DefaultDirectedWeightedGraph;

public class RGraph extends DefaultDirectedWeightedGraph {

	private static final long serialVersionUID = 1L;

	private List<Object> sources = new LinkedList<Object>();

	public RGraph() {
		super();
	}

	public RGraph(EdgeFactory arg0) {
		super(arg0);
		initSourceList();
	}

	private void initSourceList() {
		Set<?> set = vertexSet();
		Iterator<?> i = set.iterator();
		while (i.hasNext()) {
			Object o = i.next();
			if (inDegreeOf(o) == 0)
				sources.add(o);
		}
	}

	public Node[] getSources() {
		initSourceList();
		Node l[] = new Node[sources.size()];
		sources.toArray(l);
		return l;
	}
        
        public void substituiVertice(Object substituido, Object substituto) {
        try {
            this.addVertex(substituto);
            Iterator<?> i = this.incomingEdgesOf(substituido).iterator();
            DirectedWeightedEdge aresta;
            while (i.hasNext()) {
                aresta = (DirectedWeightedEdge) i.next();
                this.addEdge(new DirectedWeightedEdge(aresta.getSource(), substituto, aresta.getWeight()));
            }
            i = this.outgoingEdgesOf(substituido).iterator();
            while (i.hasNext()) {
                aresta = (DirectedWeightedEdge) i.next();
                this.addEdge(new DirectedWeightedEdge(substituto, aresta.getTarget(), aresta.getWeight()));
            }
            this.removeVertex(substituido);
        } catch (Exception e) {
            System.out.println("Falha na substituicao de vertices");
        }
    }
}
