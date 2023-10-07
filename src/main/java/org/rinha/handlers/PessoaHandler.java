package org.rinha.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.rinha.exceptions.InvalidBodyException;
import org.rinha.exceptions.InvalidQueryParam;
import org.rinha.exceptions.PessoaNotFound;
import org.rinha.services.PessoaService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

public class PessoaHandler implements HttpHandler {

    private final PessoaService pessoaService;

    public PessoaHandler() throws ClassNotFoundException {
        this.pessoaService = new PessoaService();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            final String method = exchange.getRequestMethod();
            final URI uri = exchange.getRequestURI();

            if(method.equals("POST")){
                cadastrarPessoa(exchange);

            } else if(method.equals("GET") && uri.getQuery() != null){
                buscarPorTermo(exchange);

            } else if(method.equals("GET") && uri.getPath().split("/").length > 2){
                buscarPorId(exchange);
            } else {
                throw new InvalidQueryParam();
            }
        } catch (PessoaNotFound e){
            OutputStream responseStream = exchange.getResponseBody();
            exchange.sendResponseHeaders(404, 0);
            responseStream.flush();
            responseStream.close();

        } catch (InvalidQueryParam e){
            OutputStream responseStream = exchange.getResponseBody();
            exchange.sendResponseHeaders(400, 0);
            responseStream.flush();
            responseStream.close();

        } catch (Exception e){
            OutputStream responseStream = exchange.getResponseBody();
            exchange.sendResponseHeaders(422, 0);
            responseStream.flush();
            responseStream.close();
        }
    }

    private void cadastrarPessoa(HttpExchange exchange) throws IOException {
        InputStream bodyStream = exchange.getRequestBody();
        String body = new String(bodyStream.readAllBytes());

        String apelido = null;
        String nome = null;
        String nascimento = null;
        String stack = null;

        String[] splitComma = body.split(",");

        for (String line : splitComma){
            if(line.contains("apelido")){
                apelido = removeInvalidChars(line);
                if(apelido.length() > 32){
                    throw new InvalidBodyException("Apelido invalido");
                }
            }
            if(line.contains("nome")){
                nome = removeInvalidChars(line);
                if(nome.length() > 100){
                    throw new InvalidBodyException("Nome invalido");
                }
            }
            if(line.contains("nascimento")){
                nascimento = LocalDate
                        .parse(removeInvalidChars(line), DateTimeFormatter.ISO_LOCAL_DATE)
                        .toString();
            }
        }

        if(body.contains("stack")){
            stack = removeInvalidCharsStack(body);
            if(stack != null){
                boolean invalidElement = Arrays.stream(stack.replace("[", "")
                        .replace("]", "")
                        .split(","))
                        .anyMatch(string -> string.length() > 34 || !string.contains("\""));

                if(invalidElement ||
                        (!stack.contains(",") && (stack.length() > 36 || !stack.contains("\"")))){
                    throw new InvalidBodyException("Stack invalida");
                }
            }
        }

        Objects.requireNonNull(apelido);
        Objects.requireNonNull(nome);
        Objects.requireNonNull(nascimento);

        String uuid = pessoaService.cadastrarPessoa(apelido, nome, nascimento, stack);

        OutputStream responseStream = exchange.getResponseBody();

        exchange.getResponseHeaders().set("location", "/pessoas/" + uuid);
        exchange.sendResponseHeaders(201, 0);

        responseStream.flush();
        responseStream.close();
    }

    private void buscarPorTermo(HttpExchange exchange) throws IOException, SQLException {
        String[] split = exchange.getRequestURI().getQuery().split("=");

        if(!split[0].contains("t")){
            throw new InvalidQueryParam();
        }

        String pessoas = pessoaService.buscarPessoasPorTermo(split[1]);

        OutputStream responseStream = exchange.getResponseBody();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, 0);
        responseStream.write(pessoas.getBytes());

        responseStream.flush();
        responseStream.close();
    }

    private void buscarPorId(HttpExchange exchange) throws SQLException, IOException {
        String[] split = exchange.getRequestURI().getPath().split("/");

        String pessoaJson = pessoaService.buscarPessoaPorId(split[2]);

        OutputStream responseStream = exchange.getResponseBody();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, pessoaJson.length());

        responseStream.write(pessoaJson.getBytes());

        responseStream.flush();
        responseStream.close();
    }

    private String removeInvalidChars(String text){
        try {
            return text.split("\"")[3];
        } catch (IndexOutOfBoundsException e){
            throw new InvalidBodyException("Invalid String: " + text);
        }
    }

    private String removeInvalidCharsStack(String text){
        try {
            String rawText = text.split("\\[")[1].split("\\]")[0];
            return "[" + rawText + "]";
        } catch (IndexOutOfBoundsException e){
            if(!text.split("\"stack\":")[1].contains("null")) {
                throw new InvalidBodyException("Invalid stack");
            }

            return null;
        }
    }

}
