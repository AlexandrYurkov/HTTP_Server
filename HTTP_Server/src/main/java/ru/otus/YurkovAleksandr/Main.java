package ru.otus.YurkovAleksandr;

import ru.otus.YurkovAleksandr.server.Server;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args){
        try {
            var server = new Server (Paths.get("./config/config.xml").toAbsolutePath().normalize().toString());
            server.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}