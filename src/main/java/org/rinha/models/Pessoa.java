package org.rinha.models;

public class Pessoa {

    private final String id;
    private final String apelido;
    private final String nome;
    private final String nascimento;
    private final String stack;

    public Pessoa(String id, String apelido, String nome, String nascimento, String stack) {
        this.id = id;
        this.apelido = apelido;
        this.nome = nome;
        this.nascimento = nascimento;
        this.stack = stack;
    }

    @Override
    public String toString() {
        return String.format("""
                {
                    "id": "%s",
                    "apelido": "%s",
                    "nome": "%s",
                    "nascimento": "%s",
                    "stack": %s
                }
                """, id, apelido, nome, nascimento, stack);
    }
}
