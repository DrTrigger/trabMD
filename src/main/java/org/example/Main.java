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
        Scanner sc = new Scanner(System.in);
        //Passo 1: Alimentar cada Estrutura de dados com os arquivos que criamos. --------------------------------------------------

        /**
         * @ordenada = TRUE
         */
        GenericLinkedList<Aluno> listaLinkedListOrdenada = new GenericLinkedList<>(true, AlunoComparators.porMatricula());

        /**
         * @ordenada = FALSE
         */
        GenericLinkedList<Aluno> listaLinkedListDesordenada = new GenericLinkedList<>(false, AlunoComparators.porMatricula());

        /**
         * @ArrayList
         */
        List<Aluno> listaArrayList = new ArrayList<>();


        popularLista(listaLinkedListOrdenada);
        popularLista(listaLinkedListDesordenada);
        popularLista(listaArrayList);


        System.out.println("Adicionando (LinkedList Nao ordenada) no começo: ");
        inserirEmPosicao(sc, listaLinkedListDesordenada, false);
        System.out.println("-----------------------------");

        long inicio = System.nanoTime();
        listaArrayList.add(new Aluno("138742", "Ricardo"));
        long fim = System.nanoTime();

        double ms = (fim - inicio) / 1_000_000.0;
        System.out.printf("Tempo ArrayList (add no último): %.3f ms%n", ms);

        inicio = System.nanoTime();
        adicionarAluno(listaLinkedListOrdenada, "282348", "Ricardo");
        fim = System.nanoTime();

        ms = (fim - inicio) / 1_000_000.0;
        System.out.printf("Tempo GenericList Ordenada (add no último): %.3f ms%n", ms);


        System.out.println("Adicionando no meio (LinkedList Nao ordenada): ");
        inserirEmPosicao(sc, listaLinkedListDesordenada, true);
        System.out.println("-----------------------------");



        inicio = System.nanoTime();
        listaArrayList.add(listaArrayList.size()/2, new Aluno("99999", "Luiz"));
        fim = System.nanoTime();
        ms = (fim - inicio) / 1_000_000.0;
        System.out.printf("Tempo ArrayList (add no meio): %.3f ms%n", ms);


        // Buscas – GenericLinkedList ORDENADA
        buscarUltimo(listaLinkedListOrdenada, "GenericLinkedList (ordenada)");
        buscarPenultimo(listaLinkedListOrdenada, "GenericLinkedList (ordenada)");
        buscarMeio(listaLinkedListOrdenada, "GenericLinkedList (ordenada)");
        System.out.println("-----------------------------");

        // Buscas – GenericLinkedList NÃO ordenada
        buscarUltimo(listaLinkedListDesordenada, "GenericLinkedList (não ordenada)");
        buscarPenultimo(listaLinkedListDesordenada, "GenericLinkedList (não ordenada)");
        buscarMeio(listaLinkedListDesordenada, "GenericLinkedList (não ordenada)");
        System.out.println("-----------------------------");

        // Buscas – ArrayList
        buscarUltimo(listaArrayList, "ArrayList");
        buscarPenultimo(listaArrayList, "ArrayList");
        buscarMeio(listaArrayList, "ArrayList");



        //--------------------------------------------------------------------------------------------------------------------------




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
            int numRegistros = Integer.parseInt(reader.readLine().trim());
            System.out.println("Número de registros: " + numRegistros);

            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");

                int matricula = Integer.parseInt(partes[0]);
                String nome = partes[1];

                list.add(new Aluno(String.valueOf(matricula), nome));

            }

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erro ao processar um dos valores numéricos: " + e.getMessage());
        }
    }



    private static void adicionarAluno(GenericLinkedList<Aluno> lista, String matricula, String nome) {
        Aluno a = new Aluno(matricula, nome);
        lista.adicionar(a);
        //System.out.println("Aluno adicionado. Lista agora: " + lista + " ");
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






