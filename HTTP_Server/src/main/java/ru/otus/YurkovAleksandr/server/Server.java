package ru.otus.YurkovAleksandr.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final Integer port;
    private final ExecutorService executorService;

    public Server(String configFile) throws IOException {
        Properties config = new Properties();
        config.loadFromXML(new FileInputStream(configFile));
        String port = config.getProperty("port");
        this.port = Integer.parseInt(port);
        this.executorService = Executors.newFixedThreadPool(Integer.parseInt(config.getProperty("threads")));
    }



    public void start() {
        try (var serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("accepted new connection");
                executorService.execute(new RequestHandler(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
