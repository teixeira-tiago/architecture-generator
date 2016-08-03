package mapping;

import java.util.Iterator;
import architectures.dataflows.graph.RGraph;
import org._3pq.jgrapht.graph.DefaultDirectedGraph;

public class Dataflow extends DefaultDirectedGraph {

    private static final long serialVersionUID = 1L;

    public Dataflow(RGraph grafoBase) {
        super();
        Iterator<?> i, j;
        Object a, b;
        i = grafoBase.vertexSet().iterator();
        while (i.hasNext()) {
            this.addVertex(i.next().toString());
        }
        for (i = grafoBase.vertexSet().iterator(); i.hasNext();) {
            a = i.next();
            for (j = grafoBase.vertexSet().iterator(); j.hasNext();) {
                b = j.next();
                if (grafoBase.containsEdge(a, b)) {
                    this.addEdge(a.toString(), b.toString());
                }
            }
        }
    }
}
