package ru.otus.YurkovAleksandr.handler;


import ru.otus.YurkovAleksandr.common.HttpStatus;
import ru.otus.YurkovAleksandr.exception.HandlerException;
import ru.otus.YurkovAleksandr.intercept.InterceptorHolder;
import ru.otus.YurkovAleksandr.request.RequestContext;
import ru.otus.YurkovAleksandr.response.ResponseContext;
import ru.otus.YurkovAleksandr.service.HandlerMethodResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RequestHandler implements Runnable {

    private final Socket clientSocket;

    private final HandlerMethodResolver handlerMethodResolver;

    public RequestHandler(Socket socket) {
        this.clientSocket = socket;
        this.handlerMethodResolver = new HandlerMethodResolver();
    }

    @Override
    public void run() {
        try {
            var inputStream = clientSocket.getInputStream();
            var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            var context = RequestContext.buildContext(bufferedReader);

            if (context == null) {
                System.out.println("Context is null!");
                return;
            }

            var os = clientSocket.getOutputStream();
            var handlerMethod = handlerMethodResolver.resolve(context);

            if (handlerMethod == null) {
                os.write(ResponseContext.build(HttpStatus.NOT_FOUND).getResponseAsBytes());
                os.flush();
            } else {
                ResponseContext responseContext = handlerMethod.invoke(context);
                if (responseContext.getStatus().isError()) {
                    os.write(ResponseContext.build(responseContext.getStatus()).getResponseAsBytes());
                    os.flush();
                } else {
                    InterceptorHolder.getInstance().beforeSendResponse(context, responseContext);
                    os.write(responseContext.getResponseAsBytes());
                    os.flush();
                }
            }
        } catch (IOException e) {
            throw new HandlerException("Handler exception", e);
        } finally {
            try {
                clientSocket.close();
                System.out.println("Socket closed");
            } catch (IOException e) {
                System.out.println("Exception trying to close socket");
            }
        }
    }
}
