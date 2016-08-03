package utiliters;

public class MakePopulation {

    private FilesToXML ftx = new FilesToXML();

    public static void main(String[] args) {
        Utiliter util = new Utiliter();
        MakePopulation m = new MakePopulation();
        String file = "files/xml/input/6hop.xml";
        util.limparArquivoXml(file, "Topologies");
        m.make6hop(file);
//        file = "xml/input/8hop.xml";
//        util.limparArquivoXml(file, "Topologies");
//        m.make8hop(file);
//        file = "xml/input/12hop.xml";
//        util.limparArquivoXml(file, "Topologies");
//        m.make12hop(file);
//        file = "xml/input/16hop.xml";
//        util.limparArquivoXml(file, "Topologies");
//        m.make16hop(file);
//        file = "xml/input/4bit.xml";
//        util.limparArquivoXml(file, "Topologies");
//        m.make4bit(file);
//        file = "xml/input/8bit.xml";
//        util.limparArquivoXml(file, "Topologies");
//        m.make8bit(file);
    }

    public void make4hop(String fileName) {
        String linha, coluna, base = "Base:HOP:";
        int hop = 5, i, j;
        for ( i = 1; i < hop; ++i) {
            linha = "0;" + i + ";0;-" + i;
            coluna = i + ";0;-" + i + ";0";
            ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 4);
            linha = i + ";" + i + ";-" + i + ";-" + i;
            coluna = i + ";-" + i + ";-" + i + ";" + i;
            ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 4);
            for ( j = 1; j < hop; ++j) {
                if (i != j) {
                    linha = "0;" + i + ";0;-" + j;
                    coluna = i + ";0;-" + j + ";0";
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 4);
                    linha = i + ";" + i + ";-" + j + ";-" + j;
                    coluna = i + ";-" + i + ";-" + j + ";" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 4);
                    linha = "0;" + j + ";0;-" + i;
                    coluna = j + ";0;-" + i + ";0";
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 4);
                    linha = j + ";" + j + ";-" + i + ";-" + i;
                    coluna = j + ";-" + j + ";-" + i + ";" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 4);
                    linha = "0;" + j + ";0;-" + j;
                    coluna = i + ";0;-" + i + ";0";
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 4);
                    linha = j + ";" + i + ";-" + j + ";-" + i;
                    coluna = j + ";-" + i + ";-" + j + ";" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 4);
                    linha = "0;" + i + ";0;-" + i;
                    coluna = j + ";0;-" + j + ";0";
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 4);
                    linha = i + ";" + j + ";-" + i + ";-" + j;
                    coluna = i + ";-" + j + ";-" + i + ";" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 4);
                }
            }
        }
    }
    
    public void make6hop(String fileName) {
        String linha, coluna, base = "Base:HOP:";
        int hop = 5, i, j;
        for ( i = 1; i < hop; ++i) {            
            linha = "0;" + i + ";" + i + ";0;-" + i + ";-" + i;
            coluna = i + ";" + i + ";-" + i + ";-" + i + ";-" + i + ";" + i;
            ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 6);
            linha = i + ";" + i + ";" + i + ";-" + i + ";-" + i + ";-" + i;
            coluna = i + ";0;-" + i + ";-" + i + ";0;" + i;
            ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 6);
            for ( j = 1; j < hop; ++j) {
                if (i != j) {
                    linha = "0;" + i + ";" + i + ";0;-" + j + ";-" + j;
                    coluna = i + ";" + i + ";-" + i + ";-" + j + ";-" + j + ";" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 6);
                    linha = i + ";" + i + ";" + i + ";-" + j + ";-" + j + ";-" + j;
                    coluna = i + ";0;-" + i + ";-" + j + ";0;" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 6);
                    linha = "0;" + i + ";" + j + ";0;-" + i + ";-" + j;
                    coluna = i + ";" + j + ";-" + i + ";-" + j + ";-" + i + ";" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 6);
                    linha = i + ";" + j + ";" + i + ";-" + j + ";-" + i + ";-" + j;
                    coluna = i + ";0;-" + j + ";-" + i + ";0;" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 6);
                    linha = "0;" + j + ";" + i + ";0;-" + j + ";-" + i;
                    coluna = j + ";" + i + ";-" + j + ";-" + i + ";-" + j + ";" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 6);
                    linha = j + ";" + i + ";" + j + ";-" + i + ";-" + j + ";-" + i;
                    coluna = j + ";0;-" + i + ";-" + j + ";0;" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 6);
                    linha = "0;" + j + ";" + j + ";0;-" + i + ";-" + i;
                    coluna = j + ";" + j + ";-" + j + ";-" + i + ";-" + i + ";" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 6);
                    linha = j + ";" + j + ";" + j + ";-" + i + ";-" + i + ";-" + i;
                    coluna = j + ";0;-" + j + ";-" + i + ";0;" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 6);
                }
            }
        }
    }

    public void make8hop(String fileName) {
        String linha, coluna, base = "Base:HOP:";
        int hop = 5, i, j;
        for ( i = 1; i < hop; ++i) {
            linha = "0;" + i + ";" + i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i;
            coluna = i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + i;
            ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 8);
            for ( j = 1; j < hop; ++j) {
                if (i != j) {
                    linha = "0;" + i + ";" + i + ";" + i + ";0;-" + j + ";-" + j + ";-" + j;
                    coluna = i + ";" + i + ";0;-" + i + ";-" + j + ";-" + j + ";0;" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 8);
                    linha = "0;" + j + ";" + j + ";" + j + ";0;-" + i + ";-" + i + ";-" + i;
                    coluna = j + ";" + j + ";0;-" + j + ";-" + i + ";-" + i + ";0;" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 8);
                    linha = "0;" + i + ";" + j + ";" + i + ";0;-" + j + ";-" + i + ";-" + j;
                    coluna = i + ";" + j + ";0;-" + i + ";-" + j + ";-" + i + ";0;" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 8);
                    linha = "0;" + j + ";" + i + ";" + j + ";0;-" + i + ";-" + j + ";-" + i;
                    coluna = j + ";" + i + ";0;-" + j + ";-" + i + ";-" + j + ";0;" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 8);
                    linha = "0;" + i + ";" + i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i;
                    coluna = j + ";" + j + ";0;-" + j + ";-" + j + ";-" + j + ";0;" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 8);
                    linha = "0;" + j + ";" + j + ";" + j + ";0;-" + j + ";-" + j + ";-" + j;
                    coluna = i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 8);
                    linha = "0;" + i + ";" + i + ";" + i + ";0;-" + j + ";-" + j + ";-" + j;
                    coluna = j + ";" + j + ";0;-" + j + ";-" + i + ";-" + i + ";0;" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 8);
                    linha = "0;" + j + ";" + j + ";" + j + ";0;-" + i + ";-" + i + ";-" + i;
                    coluna = i + ";" + i + ";0;-" + i + ";-" + j + ";-" + j + ";0;" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 8);
                }
            }
        }
    }
    
    public void make12hop(String fileName) {
        String linha, coluna, base = "Base:HOP:";
        int hop = 5, i, j;
        for ( i = 1; i < hop; ++i) {            
            linha = "0;" + i + ";" + i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + i + ";0;-" + i;
            coluna = i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + i +";"+ i + ";0;-" + i + ";0";
            ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 12);
            for ( j = 1; j < hop; ++j) {
                if (i != j) {
                    linha = "0;" + i + ";" + i + ";" + i + ";0;-" + j + ";-" + j + ";-" + j + ";0;" + i + ";0;-" + j;
                    coluna = i + ";" + i + ";0;-" + i + ";-" + j + ";-" + j + ";0;" + j +";"+ i + ";0;-" + j + ";0";
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 12);
                    linha = "0;" + j + ";" + j + ";" + j + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + j + ";0;-" + i;
                    coluna = j + ";" + j + ";0;-" + j + ";-" + i + ";-" + i + ";0;" + i + ";" + j + ";0;-" + i + ";0";
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 12);
                    linha = "0;" + i + ";" + j + ";" + i + ";0;-" + j + ";-" + i + ";-" + j + ";0;" + j + ";0;-" + j;
                    coluna = i + ";" + j + ";0;-" + i + ";-" + j + ";-" + i + ";0;" + j + ";" + i + ";0;-" + i + ";0";
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 12);
                    linha = "0;" + j + ";" + i + ";" + j + ";0;-" + i + ";-" + j + ";-" + i + ";0;" + i + ";0;-" + i;
                    coluna = j + ";" + i + ";0;-" + j + ";-" + i + ";-" + j + ";0;" + i + ";" + j + ";0;-" + j + ";0";
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 12);
                    linha = "0;" + i + ";" + i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";" + i + ";" + i + ";-" + j + ";-" + j;
                    coluna = j + ";" + j + ";0;-" + j + ";-" + j + ";-" + j + ";0;" + j + ";" + i + ";-" + i + ";-" + j + ";" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 12);
                    linha = "0;" + j + ";" + j + ";" + j + ";0;-" + j + ";-" + j + ";-" + j + ";" + j + ";" + j + ";-" + i + ";-" + i;
                    coluna = i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + i + ";" + j + ";-" + j + ";-" + i + ";" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 12);
                    linha = "0;" + i + ";" + i + ";" + i + ";0;-" + j + ";-" + j + ";-" + j + ";" + j + ";" + i + ";-" + j + ";-" + i;
                    coluna = j + ";" + j + ";0;-" + j + ";-" + i + ";-" + i + ";0;" + i + ";" + j + ";-" + i + ";-" + j + ";" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 12);
                    linha = "0;" + j + ";" + j + ";" + j + ";0;-" + i + ";-" + i + ";-" + i + ";" + i + ";" + j + ";-" + i + ";-" + j;
                    coluna = i + ";" + i + ";0;-" + i + ";-" + j + ";-" + j + ";0;" + j + ";" + i + ";-" + j + ";-" + i + ";" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 12);
                }
            }
        }
    }
    
    public void make16hop(String fileName) {
        String linha, coluna, base = "Base:HOP:";
        int hop = 5, i, j;
        for ( i = 1; i < hop; ++i) {            
            linha = "0;" + i + ";" + i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + i + ";" + i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i;
            coluna = i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + i + ";" + i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + i;
            ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 16);
            for ( j = 1; j < hop; ++j) {
                if (i != j) {
                    linha = "0;" + i + ";" + i + ";" + i + ";0;-" + j + ";-" + j + ";-" + j + ";0;" + i + ";" + i + ";" + i + ";0;-" + j + ";-" + j + ";-" + j;
                    coluna = i + ";" + i + ";0;-" + i + ";-" + j + ";-" + j + ";0;" + j + ";" + i + ";" + i + ";0;-" + i + ";-" + j + ";-" + j + ";0;" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 16);
                    linha = "0;" + j + ";" + j + ";" + j + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + j + ";" + j + ";" + j + ";0;-" + i + ";-" + i + ";-" + i;
                    coluna = j + ";" + j + ";0;-" + j + ";-" + i + ";-" + i + ";0;" + i + ";" + j + ";" + j + ";0;-" + j + ";-" + i + ";-" + i + ";0;" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 16);
                    linha = "0;" + i + ";" + j + ";" + i + ";0;-" + j + ";-" + i + ";-" + j + ";0;" + i + ";" + j + ";" + i + ";0;-" + j + ";-" + i + ";-" + j;
                    coluna = i + ";" + j + ";0;-" + i + ";-" + j + ";-" + i + ";0;" + j + ";" + i + ";" + j + ";0;-" + i + ";-" + j + ";-" + i + ";0;" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 16);
                    linha = "0;" + j + ";" + i + ";" + j + ";0;-" + i + ";-" + j + ";-" + i + ";0;" + j + ";" + i + ";" + j + ";0;-" + i + ";-" + j + ";-" + i;
                    coluna = j + ";" + i + ";0;-" + j + ";-" + i + ";-" + j + ";0;" + i + ";" + j + ";" + i + ";0;-" + j + ";-" + i + ";-" + j + ";0;" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 16);
                    linha = "0;" + i + ";" + i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + i + ";" + i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i;
                    coluna = j + ";" + j + ";0;-" + j + ";-" + j + ";-" + j + ";0;" + j + ";" + j + ";" + j + ";0;-" + j + ";-" + j + ";-" + j + ";0;" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 16);
                    linha = "0;" + j + ";" + j + ";" + j + ";0;-" + j + ";-" + j + ";-" + j + ";0;" + j + ";" + j + ";" + j + ";0;-" + j + ";-" + j + ";-" + j;
                    coluna = i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + i + ";" + i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 16);
                    linha = "0;" + i + ";" + i + ";" + i + ";0;-" + j + ";-" + j + ";-" + j + ";0;" + i + ";" + i + ";" + i + ";0;-" + j + ";-" + j + ";-" + j;
                    coluna = j + ";" + j + ";0;-" + j + ";-" + i + ";-" + i + ";0;" + i + ";" + j + ";" + j + ";0;-" + j + ";-" + i + ";-" + i + ";0;" + i;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 16);
                    linha = "0;" + j + ";" + j + ";" + j + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + j + ";" + j + ";" + j + ";0;-" + i + ";-" + i + ";-" + i;
                    coluna = i + ";" + i + ";0;-" + i + ";-" + j + ";-" + j + ";0;" + j + ";" + i + ";" + i + ";0;-" + i + ";-" + j + ";-" + j + ";0;" + j;
                    ftx.generateHomogeneousIndividual(fileName, base + linha, base + coluna, 16);
                }
            }
        }
    }
    
    public void make4bit(String fileName) {
        String linha,coluna ,fator ,base  = "Base:HOP:";
        String[] bit = {"MultiStage:PerfectShuffler:", "MultiStage:BitReverse:",
            "MultiStage:Cube:", "MultiStage:Butterfly:", "MultiStage:BaseLine:"
        };
        String line = "0;" + 1 + ";0;-" + 1;
        String colun = 1 + ";0;-" + 1 + ";0";
        int i;
        for ( i = 1; i < 6; ++i) {
            linha = "0;" + i + ";0;-" + i;
            coluna = i + ";0;-" + i + ";0";
            fator = ":0:3";
            ftx.generateHomogeneousIndividual(fileName, bit[0] + line + fator, base + coluna, 4);
            ftx.generateHomogeneousIndividual(fileName, bit[1] + line + fator, base + coluna, 4);
            ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, base + coluna, 4);
            fator = ":1:3";
            ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, base + coluna, 4);
            ftx.generateHomogeneousIndividual(fileName, bit[3] + line + fator, base + coluna, 4);
            fator = ":2:3";
            ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, base + coluna, 4);
            ftx.generateHomogeneousIndividual(fileName, bit[3] + line + fator, base + coluna, 4);
            ftx.generateHomogeneousIndividual(fileName, bit[4] + line + fator, base + coluna, 4);
            fator = ":3:3";
            ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, base + coluna, 4);
            fator = ":0:3";
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[0] + colun + fator, 4);
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[1] + colun + fator, 4);
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[2] + colun + fator, 4);
            fator = ":1:3";
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[2] + colun + fator, 4);
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[3] + colun + fator, 4);
            fator = ":2:3";
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[2] + colun + fator, 4);
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[3] + colun + fator, 4);
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[4] + colun + fator, 4);
            fator = ":3:3";
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[2] + colun + fator, 4);
        }
        fator = ":0:3";
        ftx.generateHomogeneousIndividual(fileName, bit[0] + line + fator, bit[0] + colun + fator, 4);
        ftx.generateHomogeneousIndividual(fileName, bit[1] + line + fator, bit[1] + colun + fator, 4);
        ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, bit[2] + colun + fator, 4);
        fator = ":1:3";
        ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, bit[2] + colun + fator, 4);
        ftx.generateHomogeneousIndividual(fileName, bit[3] + line + fator, bit[3] + colun + fator, 4);
        fator = ":2:3";
        ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, bit[2] + colun + fator, 4);
        ftx.generateHomogeneousIndividual(fileName, bit[3] + line + fator, bit[3] + colun + fator, 4);
        ftx.generateHomogeneousIndividual(fileName, bit[4] + line + fator, bit[4] + colun + fator, 4);
        fator = ":3:3";
        ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, bit[2] + colun + fator, 4);
        ftx.generateHomogeneousIndividual(fileName, base + line, base + colun, 4);
    }

    public void make8bit(String fileName) {
        String linha,coluna ,fator ,base  = "Base:HOP:";
        String[] bit = {"MultiStage:PerfectShuffler:", "MultiStage:BitReverse:",
            "MultiStage:Cube:", "MultiStage:Butterfly:", "MultiStage:BaseLine:"
        };
        String line = "0;" + 1 + ";" + 1 + ";" + 1 + ";0;-" + 1 + ";-" + 1 + ";-" + 1;
        String colun = 1 + ";" + 1 + ";0;-" + 1 + ";-" + 1 + ";-" + 1 + ";0;" + 1;
        int i;
        for ( i = 1; i < 6; ++i) {
            linha = "0;" + i + ";" + i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i;
            coluna = i + ";" + i + ";0;-" + i + ";-" + i + ";-" + i + ";0;" + i;
            fator = ":0:3";
            ftx.generateHomogeneousIndividual(fileName, bit[0] + line + fator, base + coluna, 8);
            ftx.generateHomogeneousIndividual(fileName, bit[1] + line + fator, base + coluna, 8);
            ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, base + coluna, 8);
            fator = ":1:3";
            ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, base + coluna, 8);
            ftx.generateHomogeneousIndividual(fileName, bit[3] + line + fator, base + coluna, 8);
            fator = ":2:3";
            ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, base + coluna, 8);
            ftx.generateHomogeneousIndividual(fileName, bit[3] + line + fator, base + coluna, 8);
            ftx.generateHomogeneousIndividual(fileName, bit[4] + line + fator, base + coluna, 8);
            fator = ":3:3";
            ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, base + coluna, 8);
            fator = ":0:3";
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[0] + colun + fator, 8);
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[1] + colun + fator, 8);
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[2] + colun + fator, 8);
            fator = ":1:3";
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[2] + colun + fator, 8);
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[3] + colun + fator, 8);
            fator = ":2:3";
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[2] + colun + fator, 8);
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[3] + colun + fator, 8);
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[4] + colun + fator, 8);
            fator = ":3:3";
            ftx.generateHomogeneousIndividual(fileName, base + linha, bit[2] + colun + fator, 8);
        }
        fator = ":0:3";
        ftx.generateHomogeneousIndividual(fileName, bit[0] + line + fator, bit[0] + colun + fator, 8);
        ftx.generateHomogeneousIndividual(fileName, bit[1] + line + fator, bit[1] + colun + fator, 8);
        ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, bit[2] + colun + fator, 8);
        fator = ":1:3";
        ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, bit[2] + colun + fator, 8);
        ftx.generateHomogeneousIndividual(fileName, bit[3] + line + fator, bit[3] + colun + fator, 8);
        fator = ":2:3";
        ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, bit[2] + colun + fator, 8);
        ftx.generateHomogeneousIndividual(fileName, bit[3] + line + fator, bit[3] + colun + fator, 8);
        ftx.generateHomogeneousIndividual(fileName, bit[4] + line + fator, bit[4] + colun + fator, 8);
        fator = ":3:3";
        ftx.generateHomogeneousIndividual(fileName, bit[2] + line + fator, bit[2] + colun + fator, 8);
        ftx.generateHomogeneousIndividual(fileName, base + line, base + colun, 8);
    }
}
