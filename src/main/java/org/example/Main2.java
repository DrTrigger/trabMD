package org.example;

import org.example.Aluno;
import org.example.AlunoComparators;
import org.example.GenericLinkedList;

import java.util.Scanner;

public class Main2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("==== Configuração inicial ====");
        boolean ordenada = perguntarOrdenacao(sc);
        GenericLinkedList<Aluno> lista = new GenericLinkedList<>(ordenada, AlunoComparators.porMatricula());
        System.out.println("Lista criada: " + (lista.isOrdenada() ? "ORDENADA" : "NÃO ORDENADA") + " (Comparator: matrícula) ");

                // Repetidor controlado por sentinela (opção 0 encerra)
        while (true) {
            try {
                exibirMenu();
                System.out.print("Opção: ");
                String entrada = sc.nextLine().trim();
                int opcao = Integer.parseInt(entrada); // lança NumberFormatException se não for número

                if (opcao == 0) {
                    System.out.println("Encerrando... Até mais!");
                    break; // sentinela
                }

                switch (opcao) {
                    case 1:
                        adicionarAluno(sc, lista);
                        break;
                    case 2:
                        listar(lista);
                        break;
                    case 3:
                        pesquisarAluno(sc, lista);
                        break;
                    case 4:
                        removerAluno(sc, lista);
                        break;
                    case 5:
                        inserirEmPosicao(sc, lista);
                        break;
                    default:
                        System.out.println("Opção inexistente. Tente novamente. ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: digite um número correspondente à opção. ");
            } catch (Exception e) {
                // Tratamento genérico para qualquer outra exceção inesperada
                System.out.println("Ocorreu um erro: " + e.getMessage());
            }
        }
        sc.close();
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

    private static void adicionarAluno(Scanner sc, GenericLinkedList<Aluno> lista) {
        System.out.print("Matrícula: ");
        String mat = sc.nextLine().trim();
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();
        Aluno a = new Aluno(mat, nome);
        lista.adicionar(a);
        System.out.println("Aluno adicionado. Lista agora: " + lista + " ");
    }

    private static void inserirEmPosicao(Scanner sc, GenericLinkedList<Aluno> lista) {
        if (lista.isOrdenada()) {
            System.out.println("Lista é ORDENADA: inserção por posição não é permitida. Use a opção 1 para manter a ordem. ");
            return;
        }
        System.out.print("Índice (0.." + lista.tamanho() + "): ");
        int idx = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Matrícula: ");
        String mat = sc.nextLine().trim();
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();
        Aluno a = new Aluno(mat, nome);
        try {
            lista.adicionarPosicao(idx, a);
            System.out.println("Inserido em " + idx + ". Lista agora: " + lista + " ");
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Índice inválido: " + ex.getMessage() + " ");
        }
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