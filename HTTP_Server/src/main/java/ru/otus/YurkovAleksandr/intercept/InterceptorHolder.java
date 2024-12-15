package ru.otus.YurkovAleksandr.intercept;


import ru.otus.YurkovAleksandr.request.RequestContext;
import ru.otus.YurkovAleksandr.response.ResponseContext;

import java.util.ArrayList;
import java.util.List;

public class InterceptorHolder {
    private static volatile InterceptorHolder INSTANCE;

    private final List<Interceptor> interceptors;

    private InterceptorHolder() {
        this.interceptors = new ArrayList<>();
        interceptors.add(new EncodeInterceptor());
    }

    public static InterceptorHolder getInstance() {
        if (INSTANCE == null) {
            synchronized (InterceptorHolder.class) {
                if (INSTANCE == null) {
                    INSTANCE = new InterceptorHolder();
                }
            }
        }
        return INSTANCE;
    }

    public void beforeSendResponse(RequestContext requestContext, ResponseContext responseContext) {
        interceptors.forEach(it -> it.beforeSendResponse(requestContext, responseContext));
    }
}
