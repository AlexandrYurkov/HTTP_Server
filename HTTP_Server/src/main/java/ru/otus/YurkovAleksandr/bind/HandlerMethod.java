package ru.otus.YurkovAleksandr.bind;



import ru.otus.YurkovAleksandr.common.HttpMethod;
import ru.otus.YurkovAleksandr.exception.HandlerException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerMethod {

    private final Object handlerObject;

    private final HttpMethod method;

    private final String path;

    private final Method handler;

    public HandlerMethod(Object handlerType, HttpMethod method, String path, Method handler) {
        this.handlerObject = handlerType;
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    @SuppressWarnings("unchecked")
    public <T> T invoke(Object... args) {
        try {
            return (T) handler.invoke(handlerObject, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new HandlerException(e);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
