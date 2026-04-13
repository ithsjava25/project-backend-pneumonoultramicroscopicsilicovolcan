package org.example.crimearchive.permissions;

import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.method.MethodAuthorizationDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class NullAuthzDeniedHandler implements MethodAuthorizationDeniedHandler {
    @Override
    public @Nullable Object handleDeniedInvocation(MethodInvocation methodInvocation, AuthorizationResult authorizationResult) {
        // when authz fails, return null
        return null;
    }
}
