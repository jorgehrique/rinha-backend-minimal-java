package org.rinha;

import com.sun.net.httpserver.HttpServer;
import org.rinha.handlers.ContagemHandler;
import org.rinha.handlers.PessoaHandler;

import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) {
        try {
            String appName = args[0];

            System.out.println("Starting... " + appName);

            int socketBacklog = 0;
            HttpServer server = HttpServer.create(new InetSocketAddress(appName, 8080), socketBacklog);
            server.createContext("/pessoas", new PessoaHandler());
            server.createContext("/contagem-pessoas", new ContagemHandler());
            server.start();

            System.out.println("End.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
