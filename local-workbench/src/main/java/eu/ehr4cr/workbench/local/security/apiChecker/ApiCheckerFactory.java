package eu.ehr4cr.workbench.local.security.apiChecker;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Component
public class ApiCheckerFactory {
    private final List<HandlerMapping> handlerMappings;

    public ApiCheckerFactory(final List<HandlerMapping> handlerMappings) {
        this.handlerMappings = handlerMappings;
    }

    public Optional<? extends ApiChecker> create(final HttpServletRequest req) {
        return handlerMappings.stream()
                .filter(handlerMapping -> isHandlingRequest(handlerMapping, req))
                .map(handlerMapping -> this.create(handlerMapping, req))
                .findFirst();
    }

    private ApiChecker create(final HandlerMapping handlerMapping, final HttpServletRequest req) {
        Object handler = getHandler(handlerMapping, req).getHandler();
        ApiChecker apiChecker = () -> false;
        if (handler instanceof HandlerMethod) {
            apiChecker = new LocalApiChecker((HandlerMethod) handler);
        }
        return apiChecker;
    }

    private boolean isHandlingRequest(final HandlerMapping handlerMapping, final HttpServletRequest req) {
        return getHandler(handlerMapping, req) != null;
    }

    private HandlerExecutionChain getHandler(final HandlerMapping handlerMapping, final HttpServletRequest req) {
        try {
            return handlerMapping.getHandler(req);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to get handler.", e);
        }
    }
}
