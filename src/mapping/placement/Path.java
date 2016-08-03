package mapping.placement;

public class Path {

    public PortID another;
    public int cost;
    public String pathCode;
    public boolean used;
    public boolean isTwoWay = false;

    /**
     * Metodo usado para identificar o caminho da conexao
     * atraves dos parametros
     * @param pathCode Codigo do caminho
     * @param another Identificacao da porta utilizada
     * @param cost Custo do caminho
     * @param use Valor que indica se o caminho foi usado ou nao
     * @param is2way Valor que indica se eh de mao dupla ou nao
     */
    public Path(String pathCode, PortID another, int cost,
            boolean use, boolean is2way) {
        this.pathCode = pathCode;
        this.another = another;
        this.cost = cost;
        isTwoWay = is2way;
        used = use;
    }
}
