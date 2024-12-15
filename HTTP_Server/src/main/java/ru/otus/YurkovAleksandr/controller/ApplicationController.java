package ru.otus.YurkovAleksandr.controller;


import ru.otus.YurkovAleksandr.bind.RequestMapping;
import ru.otus.YurkovAleksandr.common.ApplicationParameters;
import ru.otus.YurkovAleksandr.common.HttpHeaders;
import ru.otus.YurkovAleksandr.common.HttpMethod;
import ru.otus.YurkovAleksandr.common.HttpStatus;
import ru.otus.YurkovAleksandr.request.RequestContext;
import ru.otus.YurkovAleksandr.response.ResponseContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.lang.System.exit;

public class ApplicationController {

    @RequestMapping(path = "/", method = HttpMethod.GET)
    public ResponseContext simpleOk(RequestContext context) {
        return ResponseContext.build(HttpStatus.OK);
    }

    @RequestMapping(path = "/echo/{command}", method = HttpMethod.GET)
    public ResponseContext echo(RequestContext context) {
        var responseBody = context.getLastPart();

        return ResponseContext.build(
                HttpStatus.OK,
                HttpHeaders.fromHeaderMap(Map.of("Content-Type", "text/plain",
                        "Content-Length", String.valueOf(responseBody.getBytes().length))),
                responseBody
        );
    }

    @RequestMapping(path = "/user-agent", method = HttpMethod.GET)
    public ResponseContext userAgent(RequestContext context) {
        var responseBody = context.getHeaders().getFirst("User-Agent");

        return ResponseContext.build(
                HttpStatus.OK,
                HttpHeaders.fromHeaderMap(Map.of("Content-Type", "text/plain",
                        "Content-Length", String.valueOf(responseBody.getBytes().length))),
                responseBody
        );
    }

    @RequestMapping(path = "/shutdown", method = HttpMethod.GET)
    public void shutdown() {
        exit(0);
    }

    @RequestMapping(path = "/files/{file}", method = HttpMethod.GET)
    public ResponseContext readFileByName(RequestContext context) {
        if (!ApplicationParameters.getInstance().isDirectoryExists()) {
            return ResponseContext.build(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        var directory = ApplicationParameters.getInstance().getFileDirectory();
        directory = directory.endsWith("/") ? directory : directory + "/";
        var fileName = context.getLastPart();
        var file = new File(String.format("%s%s", directory, fileName));
        if (!file.exists() || !file.isFile()) {
            return ResponseContext.build(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        var fileContent = readFile(file);

        return ResponseContext.build(
                HttpStatus.OK,
                HttpHeaders.fromHeaderMap(Map.of("Content-Type", "application/octet-stream",
                        "Content-Length", String.valueOf(fileContent.getBytes().length))),
                fileContent
        );
    }
    @RequestMapping(path = "/files/{file}", method = HttpMethod.POST)
    public ResponseContext saveFile(RequestContext context) {
        if (!ApplicationParameters.getInstance().isDirectoryExists()) {
            return ResponseContext.build(HttpStatus.NOT_FOUND);
        }

        var directory = ApplicationParameters.getInstance().getFileDirectory();
        directory = directory.endsWith("/") ? directory : directory + "/";
        var fileName = context.getLastPart();
        saveFile(String.format("%s%s", directory, fileName), context.getBody());

        return ResponseContext.build(HttpStatus.CREATED);
    }

    private String readFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return new String(data);
        } catch (IOException ex) {
            throw new RuntimeException("Exception trying to read file", ex);
        }
    }

    private void saveFile(String fullPath, String content) {
        try {
            Path path = Paths.get(fullPath);

            if (Files.exists(path)) {
                Files.delete(path);
            }

            Files.write(path, content.getBytes());
        } catch (IOException ex) {
            throw new RuntimeException("Exception trying to save file", ex);
        }
    }
}
