package org.rinha.services;

import org.rinha.dao.PessoaDAO;

import java.sql.SQLException;
import java.util.UUID;

public class PessoaService {

    private final PessoaDAO pessoaDAO;

    public PessoaService() throws ClassNotFoundException {
        this.pessoaDAO = new PessoaDAO();
    }

    public String cadastrarPessoa(String apelido, String nome, String nascimento, String stack) throws SQLException {
        String uuid = UUID.randomUUID().toString();
        pessoaDAO.cadastrarPessoa(uuid, apelido, nome, nascimento, stack);
        return uuid;
    }

    public String buscarPessoaPorId(String id) throws SQLException {
        return pessoaDAO.buscarPessoaPorId(id).toString();
    }

    public String buscarPessoasPorTermo(String termo) throws SQLException {
        return pessoaDAO.buscarPessoasPorTermo(termo).toString();
    }

    public long contarPessoas() throws SQLException {
        return pessoaDAO.countPessoas();
    }

}
