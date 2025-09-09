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


    private static boolean perguntarOrdenacao(Scanner sc) {
        while (true) {
            System.out.print("Deseja lista ORDENADA por matrícula? (s/n): ");
            String resp = sc.nextLine().trim().toLowerCase();
            if (resp.equals("s") || resp.equals("sim")) return true;
            if (resp.equals("n") || resp.equals("nao") || resp.equals("não")) return false;
            System.out.println("Resposta inválida. Digite 's' ou 'n'. ");
        }
    }

    private static void exibirMenu() {
        System.out.println("==== MENU ====");
        System.out.println("1) Adicionar aluno (padrão)");
        System.out.println("2) Listar alunos");
        System.out.println("3) Pesquisar aluno por matrícula");
        System.out.println("4) Remover aluno por matrícula");
        System.out.println("5) Inserir em posição (apenas lista NÃO ordenada)");
        System.out.println("0) Sair");
    }

    private static void adicionarAluno(GenericLinkedList<Aluno> lista, String matricula, String nome) {
        Aluno a = new Aluno(matricula, nome);
        lista.adicionar(a);
        //System.out.println("Aluno adicionado. Lista agora: " + lista + " ");
    }


    private static void adicionarAluno(Scanner sc, GenericLinkedList<Aluno> lista) {
        System.out.print("Matrícula: ");
        String mat = sc.nextLine().trim();
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();
        Aluno a = new Aluno(mat, nome);
        lista.adicionar(a);
        System.out.println("Aluno adicionado. Lista agora: " + lista + " ");
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

    private static void pesquisarAluno(Scanner sc, GenericLinkedList<Aluno> lista) {
        System.out.print("Matrícula a pesquisar: ");
        String mat = sc.nextLine().trim();
        Aluno chave = new Aluno(mat, "—");
        Aluno encontrado = lista.pesquisar(chave);
        if (encontrado != null) {
            System.out.println("Encontrado: " + encontrado + " ");
        } else {
            System.out.println("Aluno não encontrado. ");
        }
    }

    private static void removerAluno(Scanner sc, GenericLinkedList<Aluno> lista) {
        System.out.print("Matrícula a remover: ");
        String mat = sc.nextLine().trim();
        Aluno chave = new Aluno(mat, "—");
        Aluno removido = lista.remover(chave);
        if (removido != null) {
            System.out.println("Removido: " + removido);
            System.out.println("Lista agora: " + lista + " ");
        } else {
            System.out.println("Aluno não encontrado para remoção. ");
        }
    }




}






