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