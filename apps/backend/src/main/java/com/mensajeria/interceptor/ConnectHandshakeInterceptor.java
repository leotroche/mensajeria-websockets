package com.mensajeria.interceptor;

import com.mensajeria.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class ConnectHandshakeInterceptor implements HandshakeInterceptor {
// esta clase es como una especie de controller que intercepta la conexión de websockets
    // si sale bien, devuelvo ACCEPTED, si sale mal, FORBIDDEN
    private final JwtUtils jwtUtils;

    public ConnectHandshakeInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // TODO borrar si no se va a usar

        Authentication auth = jwtUtils.getAuthFromHeader(request);

        if (auth == null) return reject(response);

        return accept(response, attributes, auth);
    }

    private static boolean accept(ServerHttpResponse response, Map<String, Object> attributes, Authentication auth) {
        attributes.put("principal", auth);
        response.setStatusCode(HttpStatus.ACCEPTED);
        return true;
    }

    private static boolean reject(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return false;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {}
}