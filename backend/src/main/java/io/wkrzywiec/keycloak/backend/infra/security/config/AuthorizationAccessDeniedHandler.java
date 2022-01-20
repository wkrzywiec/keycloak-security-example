package io.wkrzywiec.keycloak.backend.infra.security.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

public class AuthorizationAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(createErrorBody(accessDeniedException));
    }

    private String createErrorBody(AccessDeniedException exception) {
        JsonObject exceptionMessage = new JsonObject();
        exceptionMessage.addProperty("code", HttpStatus.FORBIDDEN.value());
        exceptionMessage.addProperty("reason", HttpStatus.FORBIDDEN.getReasonPhrase());
        exceptionMessage.addProperty("timestamp", Instant.now().toString());
        exceptionMessage.addProperty("message", exception.getMessage());
        return new Gson().toJson(exceptionMessage);
    }
}
