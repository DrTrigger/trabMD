# Parte 1 — Biblioteca de Lista Genérica (Java)

Implementação completa de uma **lista encadeada genérica** em Java, que pode ser **ordenada** ou **não ordenada** conforme parâmetro do construtor, usando **Generics** e **Comparator**. Inclui também um modelo de domínio (**Aluno**), comparadores e um programa de demonstração (**Demo**) para criação, busca e remoção de alunos.

---



> Observações
>
> * **Null** não é permitido como valor na lista (checado com `Objects.requireNonNull`).
> * Em listas **ordenadas**, todas as operações de busca/remoção fazem **early stop** quando o elemento atual excede o valor buscado segundo o `Comparator`.
> * Em listas **não ordenadas**, `adicionar` insere **no fim** em O(1) amortizado (mantemos `tail`).

---

## Estrutura dos arquivos

* `GenericLinkedList.java` — a biblioteca solicitada, com: `adicionar`, `contemElemento`, `pesquisar`, `remover`, `tamanho`, `isOrdenada`, `toString`.
* `Aluno.java` — entidade mínima com `matricula` e `nome`.
* `AlunoComparators.java` — fábrica de comparadores (`porMatricula`, `porNome`).
* `Main.java` — programa da seção 4 que popula a LinkedList e a ArrayList com dados gerados do arquivo. Aqui é onde coletamos os dados empíricos.
* `Main2.java` — programa simples que ilustra criação, inserção, busca e remoção MANUAL, ou seja, esta é a parte 1 do trabalho.

---

## GenericLinkedList.java

```java
package org.example;

import java.util.Comparator;
import java.util.Objects;

/**
 * Lista encadeada genérica que pode ser ordenada ou não, conforme definido no construtor.
 * A ordenação e as buscas usam o Comparator informado.
 */
public class GenericLinkedList<T> {
    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    private final boolean ordered;
    private final Comparator<? super T> comparator;

    /**
     * @param ordered    se true, a lista mantém ordem crescente definida pelo comparator
     * @param comparator critério de comparação (não pode ser null)
     */
    public GenericLinkedList(boolean ordered, Comparator<? super T> comparator) {
        this.ordered = ordered;
        this.comparator = Objects.requireNonNull(comparator, "Comparator não pode ser null");
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public boolean isOrdenada() { return ordered; }
    public int tamanho() { return size; }

    /**
     * Adiciona um elemento. Em lista ordenada, insere na posição correta.
     * Em lista não ordenada, insere no fim (mantemos tail para O(1)).
     */
    public void adicionar(T novoValor) {
        Objects.requireNonNull(novoValor, "Valor não pode ser null");
        Node<T> novo = new Node<>(novoValor);

        if (head == null) { // lista vazia
            head = tail = novo;
            size++;
            return;
        }

        if (!ordered) { // inserir no fim
            tail.next = novo;
            tail = novo;
            size++;
            return;
        }

        // Lista ordenada: inserir mantendo ordem crescente
        // Caso 1: inserir no início
        if (comparator.compare(novoValor, head.data) <= 0) {
            novo.next = head;
            head = novo;
            size++;
            return;
        }

        // Caso 2: encontrar ponto de inserção no meio/fim
        Node<T> prev = head;
        Node<T> curr = head.next;
        while (curr != null && comparator.compare(novoValor, curr.data) > 0) {
            prev = curr;
            curr = curr.next;
        }
        // insere entre prev e curr
        prev.next = novo;
        novo.next = curr;
        if (curr == null) { // inseriu no fim
            tail = novo;
        }
        size++;
    }

    /**
     * NOVO: Insere um elemento em uma posição específica [0..size].
     *
     * Regras:
     * - Em **lista ordenada**, NÃO é permitido (lança IllegalStateException) para não quebrar a invariante de ordenação.
     * - Em lista **não ordenada**:
     *   - index==0 => insere no início (O(1))
     *   - index==size => insere no fim (O(1)) usando tail
     *   - 0<index<size => insere no meio (O(k))
     */
    public void adicionarPosicao(int index, T valor) {
        if (ordered) {
            throw new IllegalStateException(
                    "Inserção por posição não permitida em lista ORDENADA; use adicionar(T) para manter a ordem.");
        }
        Objects.requireNonNull(valor, "Valor não pode ser null");
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Índice fora do intervalo: " + index + " (tamanho=" + size + ")");
        }

        Node<T> novo = new Node<>(valor);

        // Inserção no início
        if (index == 0) {
            novo.next = head;
            head = novo;
            if (size == 0) tail = novo; // se estava vazia, tail também aponta pro novo
            size++;
            return;
        }

        // Inserção no fim
        if (index == size) {
            if (tail == null) { // lista estava vazia (size==0)
                head = tail = novo;
            } else {
                tail.next = novo;
                tail = novo;
            }
            size++;
            return;
        }

        // Inserção no meio: avança até o nó anterior à posição
        Node<T> prev = head;
        for (int i = 1; i < index; i++) {
            prev = prev.next;
        }
        novo.next = prev.next;
        prev.next = novo;
        size++;
    }

    /**
     * Retorna true se encontrar um elemento equivalente segundo o Comparator.
     * Em lista ordenada, para cedo quando o atual excede o valor.
     */
    public boolean contemElemento(T valor) {
        return pesquisar(valor) != null;
    }

    /**
     * Busca e retorna a referência armazenada equivalente a {@code valor},
     * ou null se não encontrar. Em lista ordenada, faz early stop.
     */
    public T pesquisar(T valor) {
        Objects.requireNonNull(valor, "Valor de busca não pode ser null");
        Node<T> curr = head;
        while (curr != null) {
            int cmp = comparator.compare(curr.data, valor);
            if (cmp == 0) return curr.data;
            if (ordered && cmp > 0) return null; // early stop
            curr = curr.next;
        }
        return null;
    }

    /**
     * Remove o primeiro elemento equivalente a {@code valor} e o retorna;
     * retorna null se não encontrar. Em lista ordenada, faz early stop.
     */
    public T remover(T valor) {
        Objects.requireNonNull(valor, "Valor de remoção não pode ser null");
        if (head == null) return null;

        int cmpHead = comparator.compare(head.data, valor);
        if (cmpHead == 0) { // remove head
            T removed = head.data;
            head = head.next;
            if (head == null) tail = null; // lista ficou vazia
            size--;
            return removed;
        }
        if (ordered && cmpHead > 0) return null; // early stop

        Node<T> prev = head;
        Node<T> curr = head.next;
        while (curr != null) {
            int cmp = comparator.compare(curr.data, valor);
            if (cmp == 0) {
                T removed = curr.data;
                prev.next = curr.next;
                if (curr == tail) tail = prev;
                size--;
                return removed;
            }
            if (ordered && cmp > 0) return null; // early stop
            prev = curr;
            curr = curr.next;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node<T> curr = head;
        while (curr != null) {
            sb.append(curr.data);
            if (curr.next != null) sb.append(", ");
            curr = curr.next;
        }
        sb.append("]");
        return sb.toString();
    }


    // dentro de GenericLinkedList<T>
    public T obterPorIndice(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice: " + index + ", tamanho=" + size);
        }
        Node<T> curr = head;
        for (int i = 0; i < index; i++) curr = curr.next;
        return curr.data;
    }



}
```

---

## Aluno.java

```java
import java.util.Objects;

public class Aluno {
    private final String matricula; // obrigatório
    private String nome;            // obrigatório

    public Aluno(String matricula, String nome) {
        this.matricula = Objects.requireNonNull(matricula, "matrícula obrigatória");
        this.nome = Objects.requireNonNull(nome, "nome obrigatório");
    }

    public String getMatricula() { return matricula; }
    public String getNome() { return nome; }
    public void setNome(String novoNome) { this.nome = Objects.requireNonNull(novoNome); }

    @Override
    public String toString() {
        return "Aluno{" + "matricula='" + matricula + '\'' + ", nome='" + nome + '\'' + '}';
    }
}
```

---

## AlunoComparators.java

```java
import java.util.Comparator;

public final class AlunoComparators {
    private AlunoComparators() {}

    public static Comparator<Aluno> porMatricula() {
        return new Comparator<Aluno>() {
            @Override public int compare(Aluno a1, Aluno a2) {
                return a1.getMatricula().compareTo(a2.getMatricula());
            }
        };
        // Alternativa (Java 8+): return Comparator.comparing(Aluno::getMatricula);
    }

    public static Comparator<Aluno> porNome() {
        return new Comparator<Aluno>() {
            @Override public int compare(Aluno a1, Aluno a2) {
                return a1.getNome().compareTo(a2.getNome());
            }
        };
        // Alternativa (Java 8+): return Comparator.comparing(Aluno::getNome);
    }
}
```

---

## Main2.java

```java
package org.example;



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

```

## Main.java
```java
package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;


public class Main {

    public static final String NOME_ARQUIVO =  "alunosOrdenados50m.txt";

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
        inserirFim(listaArrayList, nome);
        inserirInicio(listaArrayList, nome);
        inserirMeio(listaArrayList, nome);
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

```


---


# Seção 2 — Análise de Complexidade (linha a linha)

> Objetivo: desenvolver a análise de **tempo** dos métodos `adicionar`, `pesquisar` e `remover` da biblioteca `GenericLinkedList<T>`, separando os casos de **lista ordenada** e **não ordenada**, com referência **linha a linha** ao código. Considera-se `n = tamanho da lista`. Operações em ponteiros e comparações de `Comparator` são tratadas como custo constante.

---

## 2.1 Método `adicionar(T novoValor)` — linha a linha

### Código (com numeração de linhas)

```java
01 | public void adicionar(T novoValor) {
02 |     Objects.requireNonNull(novoValor, "Valor não pode ser null");
03 |     Node<T> novo = new Node<>(novoValor);
04 |
05 |     if (head == null) { // lista vazia
06 |         head = tail = novo;
07 |         size++;
08 |         return;
09 |     }
10 |
11 |     if (!ordered) { // inserir no fim (lista não ordenada)
12 |         tail.next = novo;
13 |         tail = novo;
14 |         size++;
15 |         return;
16 |     }
17 |
18 |     // Lista ORDENADA: manter ordem crescente
19 |     if (comparator.compare(novoValor, head.data) <= 0) { // início
20 |         novo.next = head;
21 |         head = novo;
22 |         size++;
23 |         return;
24 |     }
25 |
26 |     Node<T> prev = head;
27 |     Node<T> curr = head.next;
28 |     while (curr != null && comparator.compare(novoValor, curr.data) > 0) {
29 |         prev = curr;
30 |         curr = curr.next;
31 |     }
32 |     prev.next = novo;      // insere entre prev e curr
33 |     novo.next = curr;
34 |     if (curr == null) {    // inseriu no fim
35 |         tail = novo;
36 |     }
37 |     size++;
38 | }
```

### Análise linha a linha e custos

* **L2–L3**: verificações/alocações ⇒ custo constante **O(1)**.
* **L5–L9 (lista vazia)**: todos constantes ⇒ **O(1)**.
* **L11–L16 (não ordenada)**: liga `tail` e atualiza ponteiros ⇒ **O(1)** amortizado. *Conclusão (não ordenada):* `adicionar` é **O(1)** amortizado.
* **L19–L23 (ordenada, início)**: uma comparação + ajustes ⇒ **O(1)** (melhor caso quando `novoValor ≤ head`).
* **L26–L31 (ordenada, busca do ponto)**: laço avança enquanto `novoValor` é maior que `curr`. Se a posição correta estiver após `k` nós, o custo é **Θ(k)** comparações/avanços. No **pior caso** `k = n−1` ⇒ **O(n)**.
* **L32–L37 (conexão e tail)**: constantes ⇒ **O(1)**.

**Conclusão — pior caso:**

* **Não ordenada:** **O(1)** amortizado (sempre insere no fim via `tail`).
* **Ordenada:** **O(n)** — pior caso quando `novoValor` é o **maior** elemento (inserção no fim após percorrer a lista inteira) ou quando **nenhum** nó satisfaz a condição de parada antecipada do laço da L28.

> Observação opcional: checar `if (comparator.compare(novoValor, tail.data) ≥ 0)` antes do laço permitiria *append* direto no fim em casos crescentes, melhorando casos comuns sem alterar o pior caso.

---

## 2.2 Método `pesquisar(T valor)` — linha a linha

### Código (com numeração de linhas)

```java
01 | public T pesquisar(T valor) {
02 |     Objects.requireNonNull(valor, "Valor de busca não pode ser null");
03 |     Node<T> curr = head;
04 |     while (curr != null) {
05 |         int cmp = comparator.compare(curr.data, valor);
06 |         if (cmp == 0) return curr.data;  // achou
07 |         if (ordered && cmp > 0) return null; // early stop (ordenada)
08 |         curr = curr.next;                // avança
09 |     }
10 |     return null;                         // não achou
11 | }
```

### Análise linha a linha e custos

* **L2–L3**: custo **O(1)**.
* **L4–L9**: percorre nós até encontrar/encerrar; cada iteração faz **1 comparação** (L5) e, na ordenada, uma checagem de parada antecipada (L7).

  * **Melhor caso:** primeiro nó já é igual ⇒ **O(1)**.
  * **Ordenada:** *early stop* (L7) encerra quando `curr.data > valor`. Se os dados estão em ordem e `valor` é pequeno, a busca é curta. Ainda assim, **pior caso** ocorre quando `valor` está no **último** nó ou **não está presente** e todos os nós são `≤ valor` ⇒ percorre `n` nós ⇒ **O(n)**.
  * **Não ordenada:** não há *early stop* ⇒ **Θ(k)** até o nó alvo; **pior caso** `k = n` ⇒ **O(n)**.

**Conclusão — pior caso (ambas):** **O(n)**.

---

## 2.3 Método `remover(T valor)` — linha a linha

### Código (com numeração de linhas)

```java
01 | public T remover(T valor) {
02 |     Objects.requireNonNull(valor, "Valor de remoção não pode ser null");
03 |     if (head == null) return null;         // vazia
04 |
05 |     int cmpHead = comparator.compare(head.data, valor);
06 |     if (cmpHead == 0) {                     // remove head
07 |         T removed = head.data;
08 |         head = head.next;
09 |         if (head == null) tail = null;     // ficou vazia
10 |         size--;
11 |         return removed;
12 |     }
13 |     if (ordered && cmpHead > 0) return null; // early stop (ordenada)
14 |
15 |     Node<T> prev = head;
16 |     Node<T> curr = head.next;
17 |     while (curr != null) {
18 |         int cmp = comparator.compare(curr.data, valor);
19 |         if (cmp == 0) {
20 |             T removed = curr.data;
21 |             prev.next = curr.next;          // unlink
22 |             if (curr == tail) tail = prev;  // ajusta tail
23 |             size--;
24 |             return removed;
25 |         }
26 |         if (ordered && cmp > 0) return null; // early stop
27 |         prev = curr;
28 |         curr = curr.next;
29 |     }
30 |     return null;                             // não encontrou
31 | }
```

### Análise linha a linha e custos

* **L2–L3**: constantes.
* **L5–L12 (remoção em head)**: constantes ⇒ **O(1)** (melhor caso quando o alvo está na cabeça).
* **L13 (ordenada)**: *early stop* se `head > valor` ⇒ **O(1)**.
* **L15–L29 (varredura)**: cada iteração executa uma comparação (L18) e, se achar, ajusta ponteiros (L21–L23) em **O(1)**. Se não achar, avança (L27–L28).

  * **Ordenada (early stop L26):** interrompe quando `curr.data > valor`.
  * **Não ordenada:** percorre até encontrar ou acabar.
* **Pior caso (ambas):** elemento **ausente** ou **no último nó** ⇒ varredura completa de `n−1` nós ⇒ **O(n)**.

**Conclusão — pior caso (ambas):** **O(n)**; **melhor** `O(1)` quando o alvo é `head`.

---

## 2.4 Síntese (pior caso e quando ocorre)

| Método      | Lista não ordenada  | Quando ocorre (pior)                   | Lista ordenada | Quando ocorre (pior)                                                                            |
| ----------- | ------------------- | -------------------------------------- | -------------- | ----------------------------------------------------------------------------------------------- |
| `adicionar` | **O(1)** amortizado | —                                      | **O(n)**       | Inserção no **fim** (novo maior que todos) ou quando nenhuma condição de parada ocorre no laço. |
| `pesquisar` | **O(n)**            | Valor **ausente** ou no **último** nó. | **O(n)**       | Valor **ausente** com todos `≤ valor`, ou presente no **último**.                               |
| `remover`   | **O(n)**            | Valor **ausente** ou no **último** nó. | **O(n)**       | Valor **ausente** com todos `≤ valor`, ou presente no **último**.                               |

> Observações finais:
>
> 1. O *early stop* das versões ordenadas melhora **melhor** e **caso médio** em distribuições favoráveis, mas **não altera** o pior caso. 2) O armazenamento de `tail` garante `adicionar` **O(1)** amortizado na lista não ordenada. 3) Todas as operações usam espaço auxiliar **O(1)**; a estrutura total ocupa **O(n)** nós.


# Seção 3 — Funcionamento interno e complexidade de **ArrayList** e **LinkedList**

## 3.1 ArrayList

### Funcionamento interno

* Implementado como **vetor dinâmico** (`Object[]`).
* Possui **capacidade** interna e cresce automaticamente quando necessário (via *resize*; `ensureCapacity` apenas antecipa esse crescimento).
* Elementos ficam **contíguos na memória**.
* Acesso por índice é **direto** (aritmética de ponteiros), portanto `get(i)`/`set(i)` ⇒ **O(1)**.

### Operações

| Operação                       | Complexidade                                    | Explicação                                                                                                                                                                                                 |
| ------------------------------ | ----------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Adicionar no fim**           | **O(1) amortizado** *(O(n) apenas no *resize*)* | Se houver espaço, coloca na próxima posição ⇒ **O(1)**; quando faltar espaço, cria-se um array maior e **copia** todos os elementos ⇒ **O(n)**. Como o *resize* é **raro**, o custo amortizado é **O(1)**. |
| **Adicionar no início / meio** | **O(n)**                                        | Precisa **deslocar** todos os elementos à direita.                                                                                                                                                         |
| **Remover do fim**             | **O(1)**                                        | Decrementa `size` (e costuma *nullar* a última posição).                                                                                                                                                   |
| **Remover do início / meio**   | **O(n)**                                        | **Desloca** à esquerda os elementos à direita do removido.                                                                                                                                                 |
| **Acessar por índice**         | **O(1)**                                        | Acesso direto pelo índice.                                                                                                                                                                                 |
| **Buscar por valor**           | **O(n)**                                        | Varredura sequencial até encontrar.                                                                                                                                                                        |

---

## 3.2 LinkedList

### Funcionamento interno

* Implementada como **lista duplamente encadeada** (cada nó guarda `prev` e `next`).
* Mantém referências para **`first`** (head), **`last`** (tail) e `size`.
* **Não** há acesso direto por índice: para chegar ao elemento `i`, é preciso **percorrer** pela frente **ou** por trás (o que estiver mais perto).

### Operações

| Operação                | Complexidade                                                    | Explicação                                                                                                      |
| ----------------------- | --------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------- |
| **Adicionar no fim**    | **O(1)**                                                        | Usa `last`: encadeia o novo nó e atualiza ponteiros.                                                            |
| **Adicionar no início** | **O(1)**                                                        | Cria nó antes do `first` e atualiza ponteiros.                                                                  |
| **Adicionar no meio**   | **O(n)** *(localização **O(min(i, n−i))** + inserção **O(1)**)* | É preciso **localizar** a posição (`i`) percorrendo a partir do lado mais próximo; a inserção em si é **O(1)**. |
| **Remover do fim**      | **O(1)**                                                        | Desencadeia `last` e ajusta ponteiros.                                                                          |
| **Remover do início**   | **O(1)**                                                        | Desencadeia `first` e ajusta ponteiros.                                                                         |
| **Remover do meio**     | **O(n)** *(localização **O(min(i, n−i))** + remoção **O(1)**)*  | Encontra o nó e o **desliga** atualizando `prev/next`.                                                          |
| **Acessar por índice**  | **O(min(i, n−i))**                                              | Percorre pela frente **ou** por trás até `i`.                                                                   |
| **Buscar por valor**    | **O(n)**                                                        | Percorre nós comparando com `equals`.                                                                           |

> Observações
>
> * **LinkedList** apresenta bom desempenho em mutações nos **extremos** (início/fim) e ruim em acesso aleatório por índice.
> * **ArrayList** vence em leituras sequenciais e acesso por índice graças à **localidade de cache** (memória contígua).
> * Overhead de memória: `LinkedList` guarda **ponteiros extras** por nó (`prev` e `next`).

---

## 3.3 Comparativo 

| Operação / Estrutura    | ArrayList                                | LinkedList                                  |
| ----------------------- | ---------------------------------------- | ------------------------------------------- |
| **Adicionar no fim**    | **O(n)**                                 | **O(1)**                                    |
| **Adicionar no início** | **O(n)**                                 | **O(1)**                                    |
| **Adicionar no meio**   | **O(n)**                                 | **O(n)** *(localização **O(min(i, n−i))**)* |
| **Remover do fim**      | **O(1)**                                 | **O(1)**                                    |
| **Remover do início**   | **O(n)**                                 | **O(1)**                                    |
| **Remover do meio**     | **O(n)**                                 | **O(n)** *(localização **O(min(i, n−i))**)* |
| **Acessar por índice**  | **O(1)**                                 | **O(min(i, n−i))**                          |
| **Buscar por valor**    | **O(n)**                                 | **O(n)**                                    |

> Dica de uso: **ArrayList** é ótimo quando há muitas leituras/acessos por índice e poucas inserções/remoções no meio; **LinkedList** é útil quando há muitas operações nos **extremos** (fila/deque), com pouco acesso aleatório.

