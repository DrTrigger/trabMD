package org.example;

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