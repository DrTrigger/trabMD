package org.example;

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