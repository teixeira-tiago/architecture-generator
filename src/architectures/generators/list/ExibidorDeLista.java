package architectures.generators.list;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import architectures.interfaces.Aggregate;
import architectures.interfaces.Iterator;

/**
 * Classe criada para exibir e para gerar em arquivo
 * as topologias passadas como parametro
 * @author Tiago A. Teixeira - 2006
 */
public class ExibidorDeLista {

    public void exibe(Aggregate<?> lista) {
        Iterator<?> it = lista.createIterator();
        it.first();
        while (!it.isDone()) {
            System.out.println(it.currentItem());
            it.next();
        }
    }

    public void gerarArquivo(Aggregate<?> lista, String tipo, String dir) {
        try {
            FileWriter writer = new FileWriter(new File("teste_gen." + tipo));
            PrintWriter arq = new PrintWriter(writer);

            if (tipo.equals("dot")) {
                if (dir.equals("->")) {
                    arq.println("digraph a {");
                } else if (dir.equals("--")) {
                    arq.println("graph a {");
                }
            }
            Iterator<?> it = lista.createIterator();
            it.first();
            while (!it.isDone()) {
                arq.println(it.currentItem());
                it.next();
            }
            arq.println("}");
            arq.flush();
            arq.close();
            writer.close();
        } catch (Exception e) {
        }
    }

    public void gerarArquivo(Aggregate<?> lista, String nome, String tipo, String dir) {
        try {
            FileWriter writer = new FileWriter(new File(nome + "." + tipo));
            PrintWriter arq = new PrintWriter(writer);

            if (tipo.equals("dot")) {
                if (dir.equals("->")) {
                    arq.println("digraph a {");
                } else if (dir.equals("--")) {
                    arq.println("graph a {");
                }
            }
            Iterator<?> it = lista.createIterator();
            it.first();
            while (!it.isDone()) {
                arq.println(it.currentItem());
                it.next();
            }
            arq.println("}");
            arq.flush();
            arq.close();
            writer.close();
        } catch (Exception e) {
        }
    }
}
