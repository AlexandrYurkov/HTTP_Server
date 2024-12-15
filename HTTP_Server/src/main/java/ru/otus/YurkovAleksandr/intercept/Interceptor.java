package ru.otus.YurkovAleksandr.intercept;


import ru.otus.YurkovAleksandr.request.RequestContext;
import ru.otus.YurkovAleksandr.response.ResponseContext;

public interface Interceptor {
    void beforeSendResponse(RequestContext requestContext, ResponseContext responseContext);
}
