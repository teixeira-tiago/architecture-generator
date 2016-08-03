package architectures.generators.list;

import java.util.ArrayList;
import java.util.Arrays;

public class ListaNos {

    public static class Nos {

        private String nome;
        private int from;
        private int target;

        public Nos(String nome) {
            this.nome = nome;
        }

        public Nos(String nome, int from, int target) {
            this.nome = nome;
            this.from = from;
            this.target = target;
        }

        public String getLinha() {
            return nome;
        }

        public int getFrom() {
            return from;
        }

        public int getTarget() {
            return target;
        }

        @Override
        public String toString() {
            return nome;
        }
    }
    private ArrayList<Nos> linhas;

    public ListaNos() {
        linhas = new ArrayList<Nos>();
    }

    public void limpar() {
        linhas.clear();
    }

    public void ordenar() {
        String[] corpo;
        String[] cabeca;
        String[] aux;
        String body = "", head = "";
        int i;
        for (i = 0; i < linhas.size(); ++i) {
            if (linhas.get(i).getFrom() > 0) {
                body += linhas.get(i).getLinha() + "#" + linhas.get(i).getFrom() + "#" + linhas.get(i).getTarget() + ":";
            } else {
                head += linhas.get(i).getLinha() + ":";
            }
        }
        corpo = body.split(":");
        Arrays.sort(corpo);
        cabeca = head.split(":");
        linhas.clear();
        for (i = 0; i < cabeca.length; ++i) {
            linhas.add(new Nos(cabeca[i], -1, 0));
        }
        for (i = 0; i < corpo.length; ++i) {
            aux = corpo[i].split("#");
            linhas.add(new Nos(aux[0], Integer.valueOf(aux[1]), Integer.valueOf(aux[2])));
        }
    }

    public void inserir(Nos linha) {
        linhas.add(linha);
    }

    public void inserir(String nome) {
        linhas.add(new Nos(nome));
    }

    public void inserir(String nome, int pos, int dist) {
        linhas.add(new Nos(nome, pos, dist));
    }

    public int numLinhas() {
        return linhas.size();
    }

    public Nos getLinha(int i) {
        return linhas.get(i);
    }

    public void setLinha(int index, Nos linha) {
        linhas.add(index, linha);
    }

    public int getFrom(int index) {
        return linhas.get(index).getFrom();
    }

    public int getTarget(int index) {
        return linhas.get(index).getTarget();
    }
}