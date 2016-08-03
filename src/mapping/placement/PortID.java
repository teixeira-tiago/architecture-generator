package mapping.placement;

public class PortID {

    public String peName;
    public String portCode;

    /**
     * Metodo usado para identificar as portas atraves de
     * um nome e de um codigo
     * @param name Nome da porta
     * @param portC Codigo da porta
     */
    public PortID(String name, String portC) {
        peName = name;
        portCode = portC;
    }
}
