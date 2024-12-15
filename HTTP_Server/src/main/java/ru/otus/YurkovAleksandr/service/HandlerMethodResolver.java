package ru.otus.YurkovAleksandr.service;


import ru.otus.YurkovAleksandr.bind.HandlerHolder;
import ru.otus.YurkovAleksandr.bind.HandlerMethod;
import ru.otus.YurkovAleksandr.request.RequestContext;

public class HandlerMethodResolver {
    public HandlerMethod resolve(RequestContext context) {
        return HandlerHolder.getInstance().getHandlerMethods()
                .stream()
                .filter(it -> context.getMethod() == it.getMethod() &&
                        PathPattern.path(it.getPath()).match(context.getPath()))
                .findFirst()
                .orElseGet(() -> {
                    System.out.println("Handler by context not found: " + context);
                    return null;
                });
    }
}
