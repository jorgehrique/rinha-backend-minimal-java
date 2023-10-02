package org.rinha.dao;

import org.rinha.exceptions.PessoaNotFound;
import org.rinha.models.Pessoa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {

    private final static String HOST = "jdbc:postgresql://db/PESSOADB";
    private final static String USER = "rinhaUser";
    private final static String PASS = "rinhaPass";

    private Connection connection;

    public PessoaDAO() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
    }

    public void cadastrarPessoa(String uuid, String apelido, String nome, String nascimento, String stack)
            throws SQLException {
        this.openConnection();

        String sql = "INSERT INTO TB_PESSOA VALUES(?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, uuid);
            statement.setString(2, apelido);
            statement.setString(3, nome);
            statement.setString(4, nascimento);
            statement.setString(5, stack);
            statement.executeUpdate();

        statement.close();
    }

    public Pessoa buscarPessoaPorId(String id) throws SQLException {
        this.openConnection();

        String sql = "SELECT APELIDO, NOME, NASCIMENTO, STACK FROM TB_PESSOA WHERE ID LIKE ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, id);

        ResultSet rs = statement.executeQuery();

        if(rs.next()){
            Pessoa pessoa = new Pessoa(
                id,
                rs.getString("APELIDO"),
                rs.getString("NOME"),
                rs.getString("NASCIMENTO"),
                rs.getString("STACK")
            );
            rs.close();
            statement.close();

            return pessoa;
        }

        throw new PessoaNotFound("Person not found: " + id);
    }

    public List<Pessoa> buscarPessoasPorTermo(String termo) throws SQLException {
        this.openConnection();

        String sql = "SELECT * FROM TB_PESSOA WHERE TERMO_SEARCH LIKE ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, termo);

        ResultSet rs = statement.executeQuery();
        List<Pessoa> pessoas = new ArrayList<>();

        while(rs.next()){
            pessoas.add(
                new Pessoa(
                        rs.getString("ID"),
                        rs.getString("APELIDO"),
                        rs.getString("NOME"),
                        rs.getString("NASCIMENTO"),
                        rs.getString("STACK")
                )
            );
        }

        rs.close();
        statement.close();
        return pessoas;
    }

    public long countPessoas() throws SQLException {
        this.openConnection();

        String sql = "SELECT count(id) as count FROM TB_PESSOA";

        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery();
        rs.next();

        return rs.getLong("count");
    }

    private void openConnection() throws SQLException {
        if(this.connection == null || this.connection.isClosed())
            this.connection = DriverManager.getConnection(HOST, USER, PASS);
    }

}
