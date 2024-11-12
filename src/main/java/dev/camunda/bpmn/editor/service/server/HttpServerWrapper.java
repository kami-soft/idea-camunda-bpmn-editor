package dev.camunda.bpmn.editor.service.server;

import static dev.camunda.bpmn.editor.util.Constants.CONTEXT_PATH;
import static dev.camunda.bpmn.editor.util.Constants.ZERO;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import javax.annotation.PreDestroy;
import lombok.Getter;

/**
 * A server class for the BPMN Editor UI.
 * This class creates and manages an HTTP server for handling BPMN Editor UI requests.
 *
 * @author Oleksandr Havrysh
 */
public class HttpServerWrapper {

    private final HttpServer server;

    @Getter
    private final Integer port;

    /**
     * Constructs a new ServerService.
     * Creates and starts an HTTP server with default settings.
     *
     * @throws RuntimeException if the server creation or start fails
     */
    public HttpServerWrapper(ServerHandler serverHandler) {
        try {
            this.server = HttpServer.create(new InetSocketAddress(ZERO), ZERO);
            server.createContext(CONTEXT_PATH, serverHandler);
            server.setExecutor(null);
            server.start();

            this.port = server.getAddress().getPort();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Disposes of the server resources.
     * Stops the server after a short delay.
     */
    @PreDestroy
    public void destroy() {
        server.stop(ZERO);
    }
}