package org.rinha.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.rinha.services.PessoaService;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class ContagemHandler implements HttpHandler {

    private final PessoaService pessoaService;

    public ContagemHandler() throws ClassNotFoundException {
        this.pessoaService = new PessoaService();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String method = exchange.getRequestMethod();

        if(method.equals("GET")) {
            try {
                long count = pessoaService.contarPessoas();

                OutputStream responseStream = exchange.getResponseBody();

                exchange.sendResponseHeaders(200, 0);
                responseStream.write(String.valueOf(count).getBytes());

                responseStream.flush();
                responseStream.close();

            } catch (SQLException e) {
                System.err.println("Erro ao contar pessoas: " + e.getMessage());
            }
        }
    }

}
