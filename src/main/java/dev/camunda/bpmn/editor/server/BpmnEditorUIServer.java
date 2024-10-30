package dev.camunda.bpmn.editor.server;

import com.intellij.openapi.Disposable;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

/**
 * A server class for the BPMN Editor UI.
 * This class creates and manages an HTTP server for handling BPMN Editor UI requests.
 *
 * @author Oleksandr Havrysh
 */
public class BpmnEditorUIServer implements Disposable {

    private static final int DEFAULT_PORT = 0;
    private static final int DEFAULT_BACKLOG = 0;
    private static final String CONTEXT_PATH = "/";
    private static final int DELAY_BEFORE_STOP = 0;

    private final HttpServer server;

    /**
     * Constructs a new BpmnEditorUIServer.
     * Creates and starts an HTTP server with default settings.
     *
     * @throws RuntimeException if the server creation or start fails
     */
    public BpmnEditorUIServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(DEFAULT_PORT), DEFAULT_BACKLOG);
            server.createContext(CONTEXT_PATH, new BpmnEditorUIHandler());
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
    @Override
    public void dispose() {
        server.stop(DELAY_BEFORE_STOP);
    }
}