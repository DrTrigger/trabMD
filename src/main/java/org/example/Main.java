package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;


public class Main {

    public static final String NOME_ARQUIVO =  "alunosOrdenados1m.txt";

    public static void main(String[] args) {


        List<Aluno> listaArrayList = new ArrayList<>();
        LinkedList<Aluno> linkedListJDk = new LinkedList<>();


        long ini = System.nanoTime();
        popularLista(listaArrayList);
        long fim = System.nanoTime();
        System.out.printf("tempo (popular arraylist): %.3f ms%n", (fim - ini) / 1_000_000.0);

        System.out.println();
        ini = System.nanoTime();
        popularLista(linkedListJDk);
        fim = System.nanoTime();
        System.out.printf("tempo (popular Linkedlist): %.3f ms%n", (fim - ini) / 1_000_000.0);
        System.out.println();



        // Buscas – ArrayList
        String nome = "ArrayList";
        inserirFim(linkedListJDk, nome);
        inserirInicio(linkedListJDk, nome);
        inserirMeio(linkedListJDk, nome);
        buscarUltimo(listaArrayList, "ArrayList");
        buscarPenultimo(listaArrayList, "ArrayList");
        buscarMeio(listaArrayList, "ArrayList");

        System.out.println("-----------------------------");
        // INSERÇÕES – LinkedList (JDK)
        nome = "LinkedList";
        inserirFim(linkedListJDk, nome);
        inserirInicio(linkedListJDk, nome);
        inserirMeio(linkedListJDk, nome);
        // BUSCAS – LinkedList (JDK)
        buscarUltimo(linkedListJDk);
        buscarPenultimo(linkedListJDk);
        buscarMeio(linkedListJDk);
        System.out.println("-----------------------------");



        //--------------------------------------------------------------------------------------------------------------------------




    }


    private static void inserirFim(List<Aluno> lnk, String label) {
        Aluno novo = new Aluno("LKFIM", "Novo Fim");
        long ini = System.nanoTime();
        lnk.addLast(novo);                  // O(1)
        long fim = System.nanoTime();
        System.out.printf("%s (add FIM): %.3f ms%n", label, (fim - ini) / 1_000_000.0);
        lnk.removeLast();                   // rollback
    }

    private static void inserirInicio(List<Aluno> lnk, String label) {
        Aluno novo = new Aluno("LKINI", "Novo Inicio");
        long ini = System.nanoTime();
        lnk.addFirst(novo);                 // O(1)
        long fim = System.nanoTime();
        System.out.printf("%s (add INÍCIO): %.3f ms%n", label, (fim - ini) / 1_000_000.0);
        lnk.removeFirst();                  // rollback
    }

    private static void inserirMeio(List<Aluno> lnk, String label) {
        int idx = lnk.size() / 2;
        Aluno novo = new Aluno("LKMEIO", "Novo Meio");
        long ini = System.nanoTime();
        lnk.add(idx, novo);                 // localizar + inserir → O(n) no pior
        long fim = System.nanoTime();
        System.out.printf("%s (add MEIO n/2): %.3f ms%n", label, (fim - ini) / 1_000_000.0);
        lnk.remove(idx);                    // rollback
    }




    private static void buscarUltimo(LinkedList<Aluno> lnk) {
        if (lnk.isEmpty()) { System.out.println("LinkedList (buscar ÚLTIMO): lista vazia"); return; }
               // fora do timing
        long ini = System.nanoTime();
        Aluno alvo = lnk.getLast();
        //int idx = lnk.indexOf(alvo);       // busca por valor => O(n)
        long fim = System.nanoTime();
        System.out.printf("LinkedList (buscar ÚLTIMO): %.3f ms%n", (fim - ini) / 1_000_000.0);
    }

    private static void buscarPenultimo(LinkedList<Aluno> lnk) {
        if (lnk.size() < 2) { System.out.println("LinkedList (buscar PENÚLTIMO): tamanho < 2"); return; }
        Aluno alvo = lnk.get(lnk.size() - 2);
        long ini = System.nanoTime();
        int idx = lnk.indexOf(alvo);
        long fim = System.nanoTime();
        System.out.printf("LinkedList (buscar PENÚLTIMO): %.3f ms%n", (fim - ini) / 1_000_000.0);
    }

    private static void buscarMeio(LinkedList<Aluno> lnk) {
        if (lnk.isEmpty()) { System.out.println("LinkedList (buscar MEIO): lista vazia"); return; }
        int i = lnk.size() / 2;
        Aluno alvo = lnk.get(i);
        long ini = System.nanoTime();
        int idx = lnk.indexOf(alvo);
        long fim = System.nanoTime();
        System.out.printf("LinkedList (buscar MEIO n/2): %.3f ms%n", (fim - ini) / 1_000_000.0);
    }

    public static void popularLista(GenericLinkedList<Aluno> list) {
        try (BufferedReader reader = new BufferedReader(new FileReader(NOME_ARQUIVO))) {
            int numRegistros = Integer.parseInt(reader.readLine().trim());
            System.out.println("Número de registros: " + numRegistros);

            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");

                int matricula = Integer.parseInt(partes[0]);
                String nome = partes[1];

                adicionarAluno(list, String.valueOf(matricula), nome);

            }

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erro ao processar um dos valores numéricos: " + e.getMessage());
        }
    }

    public static void popularLista(List<Aluno> list) {
        try (BufferedReader reader = new BufferedReader(new FileReader(NOME_ARQUIVO))) {
            //long ini = System.nanoTime();
            int numRegistros = Integer.parseInt(reader.readLine().trim());
            System.out.println("Número de registros: " + numRegistros);

            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");

                int matricula = Integer.parseInt(partes[0]);
                String nome = partes[1];

                list.add(new Aluno(String.valueOf(matricula), nome));

            }
            //long fim = System.nanoTime();
            //System.out.printf("tempo (add FIM): %.3f ms%n", (fim - ini) / 1_000_000.0);
            //System.out.println();

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erro ao processar um dos valores numéricos: " + e.getMessage());
        }
    }



    private static void adicionarAluno(GenericLinkedList<Aluno> lista, String matricula, String nome) {
        Aluno a = new Aluno(matricula, nome);
        lista.adicionar(a);
    }


    private static void inserirEmPosicao(Scanner sc, GenericLinkedList<Aluno> lista, boolean meio) {
        if (lista.isOrdenada()) {
            System.out.println("Lista é ORDENADA: inserção por posição não é permitida. Use a opção 1 para manter a ordem.");
            return;
        }
        int idx;
        if(!meio){
            System.out.print("Índice (0.." + lista.tamanho() + "): ");
            idx = Integer.parseInt(sc.nextLine().trim());
        }
        else{
            idx = lista.tamanho()/2;
        }
        System.out.print("Matrícula: ");
        String mat = sc.nextLine().trim();
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();
        Aluno a = new Aluno(mat, nome);
        try {
            inserirAtomico(idx, lista, a);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Índice inválido: " + ex.getMessage() + " ");
        }
    }
    private static void inserirAtomico(int idx, GenericLinkedList<Aluno> lista, Aluno aluno){
        long inicio = System.nanoTime();
        lista.adicionarPosicao(idx, aluno);
        long fim = System.nanoTime();
        double ms = (fim - inicio) / 1_000_000.0;
        System.out.printf("Tempo GenericLinkedList: %.3f ms%n", ms);
    }
    private static void listar(GenericLinkedList<Aluno> lista) {
        System.out.println("Conteúdo da lista (tamanho=" + lista.tamanho() + "): ");
        System.out.println(lista + " ");
    }


    private static void buscarUltimo(GenericLinkedList<Aluno> lista, String label) {
        int n = lista.tamanho();
        if (n == 0) { System.out.println(label + " (buscar último): lista vazia"); return; }
        Aluno alvo = lista.obterPorIndice(n - 1); // pega o último como alvo
        long ini = System.nanoTime();
        Aluno res = lista.pesquisar(alvo);        // usa o comparator (matrícula)
        long fim = System.nanoTime();
        double ms = (fim - ini) / 1_000_000.0;
        System.out.printf("%s (buscar ÚLTIMO): %.3f ms%n", label, ms);
    }

    private static void buscarPenultimo(GenericLinkedList<Aluno> lista, String label) {
        int n = lista.tamanho();
        if (n < 2) { System.out.println(label + " (penúltimo): tamanho < 2"); return; }
        Aluno alvo = lista.obterPorIndice(n - 2);
        long ini = System.nanoTime();
        Aluno res = lista.pesquisar(alvo);
        long fim = System.nanoTime();
        double ms = (fim - ini) / 1_000_000.0;
        System.out.printf("%s (buscar PENÚLTIMO): %.3f ms%n", label, ms);
    }

    private static void buscarMeio(GenericLinkedList<Aluno> lista, String label) {
        int n = lista.tamanho();
        if (n == 0) { System.out.println(label + " (meio): lista vazia"); return; }
        Aluno alvo = lista.obterPorIndice(n / 2);
        long ini = System.nanoTime();
        Aluno res = lista.pesquisar(alvo);
        long fim = System.nanoTime();
        double ms = (fim - ini) / 1_000_000.0;
        System.out.printf("%s (buscar MEIO n/2): %.3f ms%n", label, ms);
    }


    private static void buscarUltimo(List<Aluno> lista, String label) {
        int n = lista.size();
        if (n == 0) { System.out.println(label + " (buscar último): lista vazia"); return; }
        Aluno alvo = lista.get(n - 1);
        long ini = System.nanoTime();
        int idx = lista.indexOf(alvo);
        long fim = System.nanoTime();
        double ms = (fim - ini) / 1_000_000.0;
        System.out.printf("%s (buscar ÚLTIMO): %.3f ms%n", label, ms);
    }

    private static void buscarPenultimo(List<Aluno> lista, String label) {
        int n = lista.size();
        if (n < 2) { System.out.println(label + " (penúltimo): tamanho < 2"); return; }
        Aluno alvo = lista.get(n - 2);
        long ini = System.nanoTime();
        int idx = lista.indexOf(alvo);
        long fim = System.nanoTime();
        double ms = (fim - ini) / 1_000_000.0;
        System.out.printf("%s (buscar PENÚLTIMO): %.3f ms%n", label, ms);
    }

    private static void buscarMeio(List<Aluno> lista, String label) {
        int n = lista.size();
        if (n == 0) { System.out.println(label + " (meio): lista vazia"); return; }
        Aluno alvo = lista.get(n / 2);
        long ini = System.nanoTime();
        int idx = lista.indexOf(alvo);
        long fim = System.nanoTime();
        double ms = (fim - ini) / 1_000_000.0;
        System.out.printf("%s (buscar MEIO n/2): %.3f ms%n", label, ms);
    }

}






