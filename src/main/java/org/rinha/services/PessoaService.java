package org.rinha.services;

import org.rinha.dao.PessoaDAO;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PessoaService {

    private final PessoaDAO pessoaDAO;
    private final ExecutorService executorService;

    public PessoaService() throws ClassNotFoundException {
        this.pessoaDAO = new PessoaDAO();
        executorService = Executors.newCachedThreadPool();
    }

    public String cadastrarPessoa(String apelido, String nome, String nascimento, String stack) {
        String uuid = UUID.randomUUID().toString();
        executorService.submit(() -> pessoaDAO.cadastrarPessoa(uuid, apelido, nome, nascimento, stack));
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
