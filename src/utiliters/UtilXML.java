package utiliters;

import architectures.generators.topologies.MultiStage;
import architectures.generators.list.ListaNosAggregate;
import xml.topologyclass.Colun;
import xml.topologyclass.Line;

public class UtilXML {

    private ListaNosAggregate list = new ListaNosAggregate();

    public void setList(ListaNosAggregate list) {
        this.list = list;
    }

    public ListaNosAggregate getList() {
        return list;
    }

    public int getLine(Line linhas, int line) {
        int hop = linhas.getHop();
        try {
            Class<?> indLine = Class.forName("architectures.generators.topologies." + linhas.getType());
            if (!linhas.getFamily().equals("Base")) {
                MultiStage mult = (MultiStage) indLine.newInstance();
                line = mult.getValue(line, linhas.getLenghtString(), linhas.getBit());
            }
            line += hop;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return line;
    }

    public int getColun(Colun colunas, int colun) {
        int hop = colunas.getHop();
        try {
            Class<?> indColun = Class.forName("architectures.generators.topologies." + colunas.getType());
            if (!colunas.getFamily().equals("Base")) {
                MultiStage mult = (MultiStage) indColun.newInstance();
                colun = mult.getValue(colun, colunas.getLenghtString(), colunas.getBit());
            }
            colun += hop;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return colun;
    }

    public int[] toroide(int type, int line, int colun, int limit) {
        int aux;
        int[] result = new int[2];
        switch (type) {
            case 1:
                /**
                 * Toroide horizontal ligando linha com linha +1 figura k na
                 * horizontal
                 */
                if ((colun >= (limit - 1)) && (line >= (limit - 2))) {
                    colun = (limit - 1);
                    line = 0;
                } else if (colun >= (limit - 1)) {
                    colun = 0;
                    ++line;
                }
                break;
            case 2:
                /**
                 * Toroide horizontal ligando elementos das linhas
                 */
                if (line == (limit - 1)){
                    aux = line;
                    line = colun;
                    colun = aux;
                }
            case 3:
                /**
                 * Toroide vertical ligando coluna com coluna +1 figura k na
                 * vertical
                 */
                if (colun == (limit - 1)) {
                    aux = line;
                    line = 0;
                    colun = ++aux;
                }
                break;
            case 4:
                /**
                 * Toroide vertical ligando elementos das colunas
                 */
                if (colun == (limit - 1)) {
                    aux = colun;
                    colun = line;
                    line = aux;
                }
                break;
            case 5:
                /**
                 * Toroide ligando os elementos adjacentes na mesma linha
                 * (considerando a primeira e ultima linha)
                 */
                if ((line == 0) || (line == (limit - 1))) {
                    ++colun;
                }
                break;
            case 6:
                /**
                 * Toroide ligando os elementos adjacentes na mesma coluna
                 * (considerando a primeira e ultima coluna)
                 */
                if ((line == 0) || (line == (limit - 1))) {
                    line = colun + 1;
                }
                break;
        }
        line = (line >= limit) ? line - limit : (line < 0) ? limit + line
                : line;
        colun = (colun >= limit) ? colun - limit : (colun < 0) ? limit + colun : colun;
        result[0] = line;
        result[1] = colun;
        return result;
    }

    public void insertLine(int startX, int startY, int endX, int endY,
            int side, double cost, String arco) {
        int norm = (int) Math.ceil(Math.log(side) / Math.log(10));
        Utiliter u = new Utiliter();
        int sX, sY, eX, eY;
        sX = normalize(startX, side);
        sY = normalize(startY, side);
        eX = normalize(endX, side);
        eY = normalize(endY, side);
        String start = "no_" + u.fill(sX, norm, 'a') + "_" + u.fill(sY, norm, 'a');
        String stop = "no_" + u.fill(eX, norm, 'a') + "_" + u.fill(eY, norm, 'a');
        int from = (sX * 1000) + sY;
        int target = (eX * 1000) + eY;
        list.inserir(start + " " + arco + " " + stop + " [label = \"" + u.fill(cost, norm, 'a') + "\"];", from, target);
    }

    public double calcCost(int startX, int startY, int endX, int endY, int typeCost, double cost) {
        double maxDist, custo = .0;
        maxDist = distance(startX, startY, endX, endY,0);
        switch (typeCost) {
            case 0:
                custo = 1.0;
                break;
            case 1:
                custo = (0.065 * maxDist) + 0.85;
                break;
            case 2:
                custo = 1 + (maxDist / cost);
                break;
            default:
                custo = maxDist;
                break;
        }
        return custo;
    }
    
    public double calcCost(int startX, int startY, int endX, int endY, int typeCost) {
        double custo = .0;
        switch (typeCost) {
            case 0:
                custo = 1.0;
                break;
            case 1:
                custo = distance(startX, startY, endX, endY, 0);
                break;
            case 2:
                custo = distance(startX, startY, endX, endY, 1);
                break;
            default:
                custo = distance(startX, startY, endX, endY, 2);
                break;
        }
        return custo;
    }

    public double calcCost(int maxDist, int typeCost, double cost) {
        double custo = .0;
        switch (typeCost) {
            case 0:
                custo = maxDist;
                break;
            case 1:
                custo = (0.065 * maxDist) + 0.85;
                break;
            case 2:
                custo = 1 + (maxDist / cost);
                break;
            default:
                custo = maxDist;
                break;
        }
        return custo;
    }
    
    public double distance(int startX, int startY, int endX, int endY, int type) {
        double distX, distY, maxDist;
        distX = Math.abs(startX - endX);
        distY = Math.abs(startY - endY);
        switch (type) {
            case 0:
                maxDist = distX + distY;
                break;
            case 1:
                if (distX > distY) {
                    maxDist = distX;
                } else {
                    maxDist = distY;
                }
                break;
            default:
                maxDist = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
                break;
        }
        return maxDist;
    }

    public int normalize(int num, int side) {
        return (num >= side) ? (side - num) : (num < 0) ? (side + num) : num;
    }

    public void gerarNos(int side, String arco) {
        gerarNos(side, side, side, arco);
    }

    public void gerarNos(int side, int Line, int Colun, String arco) {
        String line = "";
        int linhas, colunas, i, j;
        linhas = colunas = side;
        String[] colun = new String[colunas];
        for (i = 0; i < colunas; ++i) {
            colun[i] = "";
        }
        for (i = 0; i < linhas; ++i) {
            for (j = 0; j < colunas; ++j) {
                line += "no_" + i + "_" + j + "; ";
            }
            list.inserir("{ rank=same; " + line + "}", -1, -1);
            line = "";
        }
        list.inserir("edge[ color = white];", -1, -1);
        for (i = 0; i < linhas; ++i) {
            for (j = 0; j < colunas; ++j) {
                if (j < colunas - 1) {
                    line += "no_" + i + "_" + j + " " + arco + " ";
                    colun[i] += "no_" + j + "_" + i + " " + arco + " ";
                } else {
                    line += "no_" + i + "_" + j + " ";
                    colun[i] += "no_" + j + "_" + i + " ";
                }
            }
            list.inserir(line + " [style=dotted];", -1, -1);
            line = "";
        }
        for (i = 0; i < colunas; ++i) {
            list.inserir(colun[i] + " [style=dotted];", -1, -1);
        }
        list.inserir("edge[ color = black];", -1, -1);
    }
}
