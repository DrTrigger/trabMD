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
* `Demo.java` — programa simples que ilustra criação, inserção, busca e remoção.

---

## GenericLinkedList.java

```java
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

## Demo.java

```java
import java.util.Scanner;

public class Demo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("==== Configuração inicial ====");
        boolean ordenada = perguntarOrdenacao(sc);
        GenericLinkedList<Aluno> lista = new GenericLinkedList<>(ordenada, AlunoComparators.porMatricula());
        System.out.println("Lista criada: " + (lista.isOrdenada() ? "ORDENADA" : "NÃO ORDENADA") + " (Comparator: matrícula)
");

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
                    default:
                        System.out.println("Opção inexistente. Tente novamente.
");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: digite um número correspondente à opção.
");
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
            System.out.println("Resposta inválida. Digite 's' ou 'n'.
");
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
        System.out.println("Aluno adicionado. Lista agora: " + lista + "
");
    }

    private static void listar(GenericLinkedList<Aluno> lista) {
        System.out.println("Conteúdo da lista (tamanho=" + lista.tamanho() + "): ");
        System.out.println(lista + "
");
    }

    private static void pesquisarAluno(Scanner sc, GenericLinkedList<Aluno> lista) {
        System.out.print("Matrícula a pesquisar: ");
        String mat = sc.nextLine().trim();
        Aluno chave = new Aluno(mat, "—");
        Aluno encontrado = lista.pesquisar(chave);
        if (encontrado != null) {
            System.out.println("Encontrado: " + encontrado + "
");
        } else {
            System.out.println("Aluno não encontrado.
");
        }
    }

    private static void removerAluno(Scanner sc, GenericLinkedList<Aluno> lista) {
        System.out.print("Matrícula a remover: ");
        String mat = sc.nextLine().trim();
        Aluno chave = new Aluno(mat, "—");
        Aluno removido = lista.remover(chave);
        if (removido != null) {
            System.out.println("Removido: " + removido);
            System.out.println("Lista agora: " + lista + "
");
        } else {
            System.out.println("Aluno não encontrado para remoção.
");
        }
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
