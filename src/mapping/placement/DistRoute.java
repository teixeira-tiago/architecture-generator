package mapping.placement;

public class DistRoute {

    public String targetPE;
    public int cost;
    public String outPE;

    /**
     * Metodo utilizado para calculo da distancia entre PEs
     * @param targetPE PE de destino
     * @param cost Custo da rota
     * @param outPE PE de saida
     */
    public DistRoute(String targetPE, int cost, String outPE) {
        this.targetPE = targetPE;
        this.cost = cost;
        this.outPE = outPE;
    }
}
