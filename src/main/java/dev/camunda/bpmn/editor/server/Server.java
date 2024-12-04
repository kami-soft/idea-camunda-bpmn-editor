package dev.camunda.bpmn.editor.server;

import com.sun.net.httpserver.HttpServer;
import dev.camunda.bpmn.editor.server.handler.ClipboardServerHandler;
import dev.camunda.bpmn.editor.server.handler.LintServerHandler;
import dev.camunda.bpmn.editor.server.handler.UIServerHandler;
import java.net.InetSocketAddress;
import lombok.Getter;

/**
 * A wrapper class for managing an HTTP server dedicated to the BPMN Editor UI.
 * This class encapsulates the creation, configuration, and lifecycle management of an HTTP server
 * that handles requests for the BPMN Editor UI and linting functionality.
 * <p>
 * The server is initialized with two main contexts:
 * <ul>
 *     <li>BPMN Editor UI: Handles requests related to the BPMN editor user interface</li>
 *     <li>Lint: Handles requests related to linting functionality for BPMN diagrams</li>
 * </ul>
 * <p>
 * The server is started automatically upon instantiation and can be stopped using the {@link #stop()} method.
 *
 * @author Oleksandr Havrysh
 */
public class Server {

    private static final int ZERO = 0;
    private static final String LINT_PATH = "/lint";
    private static final String CLIPBOARD_PATH = "/clipboard";
    private static final String BPMN_EDITOR_UI_PATH = "/bpmn-editor-ui";

    private final HttpServer server;

    /**
     * The port number on which the server is listening to.
     * The system automatically assigns this when the server starts.
     */
    @Getter
    private final Integer port;

    /**
     * Constructs a new HttpServerWrapper and initializes the HTTP server.
     * The server is created with default settings and starts listening on a system-assigned port.
     * Two contexts are created for handling BPMN Editor UI and linting requests.
     *
     * @param UIServerHandler the handler for BPMN Editor UI requests
     * @param lintServerHandler   the handler for linting plugin requests
     * @param clipboardServerHandler    the handler for clipboard requests
     * @throws RuntimeException if the server creation or start fails
     */
    public Server(UIServerHandler UIServerHandler,
                  LintServerHandler lintServerHandler,
                  ClipboardServerHandler clipboardServerHandler) {
        try {
            this.server = HttpServer.create(new InetSocketAddress(ZERO), ZERO);
            server.createContext(BPMN_EDITOR_UI_PATH, UIServerHandler);
            server.createContext(LINT_PATH, lintServerHandler);
            server.createContext(CLIPBOARD_PATH, clipboardServerHandler);
            server.setExecutor(null);
            server.start();

            this.port = server.getAddress().getPort();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create or start the HTTP server", e);
        }
    }

    /**
     * Stops the HTTP server and releases associated resources.
     * This method should be called
     * when the bean is about to be removed from the container or when the application is shutting down.
     * <p>
     * The server is stopped immediately without any delay.
     */
    public void stop() {
        server.stop(ZERO);
    }
}