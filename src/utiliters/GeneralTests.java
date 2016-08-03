package utiliters;

import architectures.dataflows.extractor.Pattern;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe destinada a testes
 */
public class GeneralTests {

    public static void main(String[] args) {
        Utiliter u = new Utiliter();
        Pattern p = new Pattern();
        XMLToFiles x = new XMLToFiles();
        String[] s = x.getBenchmarks("files/benchmarks/revista.xml"), aux;
        int i;
        for(i = 0; i< s.length;++i){
            aux = s[i].split(":");
            p = new Pattern();
            p.load(aux[0]);
//            u.convertRGraphToDOT(u.getGraph(aux[0]), "files/"+aux[1]+".dot");
            p.generateBlif2("files/dataflow/"+aux[1]+".blif", aux[1]);
//            try {
//                Runtime.getRuntime().exec("sis12.exe");
//            } catch (IOException ex) {
//                Logger.getLogger(GeneralTests.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
//        p.load("architectures.dataflows.real_partitioned.fir2");
//        p.generateBlif("fir2.blif", "fir2");
//        p = new Pattern();
//        p.load("architectures.dataflows.real_partitioned.fir2");
//        p.generateBlif2("fir2_1.blif", "fir2");
    }

}
