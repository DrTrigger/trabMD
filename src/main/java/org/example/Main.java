package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class Main {
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
                        Double inicio = (double) System.currentTimeMillis();
                        adicionarAluno(sc, lista);
                        Double fim = (double) System.currentTimeMillis();
                        System.out.println("Tudo rodou em : " +  (fim - inicio)/1000);
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

        /** 1 - Inserir um elemento no fim das duas listas, exibindo o tempo gasto para inserir em cada uma delas.
            1.1 - GenericLinkedList (ordenada)*/



        lerArquivo("alunosOrdenados.txt", lista);





        sc.close();
    }


    private static  void lerArquivo(String nome_arquivo, GenericLinkedList<Aluno> genericList) {

        Double inicio = (double) System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(new FileReader(nome_arquivo))) {
            int numRegistros = Integer.parseInt(reader.readLine().trim());
            System.out.println("Número de registros: " + numRegistros);

            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");
                int id = Integer.parseInt(partes[0]);
                //botar matricula para ordenada aqui e arrayList aqui

                String nome = partes[1];
                float nota = Float.parseFloat(partes[2]);

                adicionarAluno(genericList, String.valueOf(id), nome);

                //System.out.printf("ID: %d | Nome: %s | Nota: %.2f%n", id, nome, nota);
            }

            Double fim = (double) System.currentTimeMillis();
            System.out.println("Tudo rodou em : " +  (fim - inicio)/1000);
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
        System.out.println("1) Adicionar aluno");
        System.out.println("2) Listar alunos");
        System.out.println("3) Pesquisar aluno por matrícula");
        System.out.println("4) Remover aluno por matrícula");
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

    private static void adicionarAluno(GenericLinkedList<Aluno> lista, String mat, String nome) {
        Aluno a = new Aluno(mat, nome);
        lista.adicionar(a);
        //System.out.println("Aluno adicionado. Lista agora: " + lista + " ");
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
