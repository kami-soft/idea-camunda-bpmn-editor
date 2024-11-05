package dev.camunda.bpmn.editor.service.server;

import static dev.camunda.bpmn.editor.util.Constants.CONTEXT_PATH;
import static dev.camunda.bpmn.editor.util.Constants.ZERO;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import javax.annotation.PreDestroy;

/**
 * A server class for the BPMN Editor UI.
 * This class creates and manages an HTTP server for handling BPMN Editor UI requests.
 *
 * @author Oleksandr Havrysh
 */
public class ServerService {

    private final HttpServer server;

    /**
     * Constructs a new ServerService.
     * Creates and starts an HTTP server with default settings.
     *
     * @param handler The ServerHandler instance to handle HTTP requests
     * @throws RuntimeException if the server creation or start fails
     */
    public ServerService(ServerHandler handler) {
        try {
            this.server = HttpServer.create(new InetSocketAddress(ZERO), ZERO);
            server.createContext(CONTEXT_PATH, handler);
            server.setExecutor(null);
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the port number on which the server is listening.
     *
     * @return the port number
     */
    public int getPort() {
        return server.getAddress().getPort();
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