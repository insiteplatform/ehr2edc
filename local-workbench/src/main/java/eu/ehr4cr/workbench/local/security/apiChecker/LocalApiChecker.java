package eu.ehr4cr.workbench.local.security.apiChecker;

import org.springframework.web.method.HandlerMethod;

public class LocalApiChecker implements ApiChecker {

    private final HandlerMethod handlerMethod;

    public LocalApiChecker(final HandlerMethod handlerMethod) {
        this.handlerMethod = handlerMethod;
    }

    @Override
    public boolean isApiCall() {
        return handlerMethod.getBean()
                .getClass()
                .getName()
                .contains(".api.");
    }
}
