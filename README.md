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
                    case 5:
                        inserirEmPosicao(sc, lista);
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
        System.out.println("Aluno adicionado. Lista agora: " + lista + "
");
    }

    private static void inserirEmPosicao(Scanner sc, GenericLinkedList<Aluno> lista) {
        if (lista.isOrdenada()) {
            System.out.println("Lista é ORDENADA: inserção por posição não é permitida. Use a opção 1 para manter a ordem.
");
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
            System.out.println("Inserido em " + idx + ". Lista agora: " + lista + "
");
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Índice inválido: " + ex.getMessage() + "
");
        }
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

# Comparação de Performance entre ArrayList e LinkedList em Cenários de Alta Carga

A análise a seguir demonstra o comportamento das classes `ArrayList` e `LinkedList` em operações de adição e busca variando a quantidade (em milhões) até o limite possivel utilizando até 22GB de RAM.

ArrayList

| Número de Registros | Tempo para Popular (ms) | add FIM (ms) | add INÍCIO (ms) | add MEIO (ms) | buscar ÚLTIMO (ms) | buscar PENÚLTIMO (ms) | buscar MEIO (ms) |
| :------------------ | :---------------------- | :----------- | :-------------- | :------------ | :----------------- | :-------------------- | :--------------- |
| 1.000.000           | 186,491                 | 0,027        | 0,001           | 0,002         | 2,788              | 1,811                 | 0,144            |
| 10.000.000          | 1.833,719               | 0,026        | 0,001           | 0,002         | 5,704              | 4,507                 | 1,608            |
| 25.000.000          | 4.303,018               | 0,025        | 0,002           | 0,002         | 9,908              | 8,880                 | 3,672            |
| 50.000.000          | 8.289,216               | 0,024        | 0,001           | 0,002         | 16,938             | 16,010                | 7,005            |
| 75.000.000          | 11.959,346              | 0,025        | 0,001           | 0,002         | 27,279             | 26,170                | 12,299           |
| 100.000.000         | 16.332,261              | 0,025        | 0,001           | 0,002         | 30,644             | 29,557                | 14,185           |

LinkedList

| Número de Registros | Tempo para Popular (ms) | add FIM (ms) | add INÍCIO (ms) | add MEIO (ms) | buscar ÚLTIMO (ms) | buscar PENÚLTIMO (ms) | buscar MEIO (ms) |
| :------------------ | :---------------------- | :----------- | :-------------- | :------------ | :----------------- | :-------------------- | :--------------- |
| 1.000.000           | 394,827                 | 0,003        | 0,001           | 13,863        | 0,003              | 27,534                | 12,928           |
| 10.000.000          | 4.036,840               | 0,003        | 0,001           | 145,585       | 0,003              | 285,331               | 149,237          |
| 25.000.000          | 10.079,167              | 0,003        | 0,002           | 397,322       | 0,003              | 795,858               | 454,535          |
| 50.000.000          | 20.300,199              | 0,003        | 0,002           | 683,659       | 0,003              | 1.349,797             | 789,309          |
| 75.000.000          | 33.187,499              | 0,007        | 0,004           | 1.116,200     | 0,003              | 2.354,251             | 1.242,510        |
| 100.000.000         | 40.895,977              | 0,004        | 0,002           | 1.186,016     | 0,004              | 2.111,533             | 1.842,822        |