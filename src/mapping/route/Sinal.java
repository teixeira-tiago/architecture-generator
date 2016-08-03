package mapping.route;

import mapping.Dataflow;
import java.util.ArrayList;
import java.util.Iterator;

public class Sinal {

    private Object fonte,  sumidouro;

    public Sinal(Object fonte, Object sumidouro) {
        this.fonte = fonte;
        this.sumidouro = sumidouro;
    }

    public Object obtemFonte() {
        return fonte;
    }

    public Object obtemSumidouro() {
        return sumidouro;
    }

    public static ArrayList<Sinal> criaSinais(Dataflow d) {
        ArrayList<Sinal> sinais = new ArrayList<Sinal>();
        Iterator<?> i, j;
        Object a, b;
        for (i = d.vertexSet().iterator(); i.hasNext();) {
            a = i.next();
            for (j = d.vertexSet().iterator(); j.hasNext();) {
                b = j.next();
                if (d.containsEdge(a, b)) {
                    sinais.add(new Sinal(a, b));
                }
            }
        }
        return sinais;
    }
}
