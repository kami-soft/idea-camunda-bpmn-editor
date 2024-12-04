package dev.camunda.bpmn.editor.server.handler;

import static java.util.Objects.nonNull;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Abstract base class for server handlers in the BPMN Editor.
 * This class implements the {@link HttpHandler} interface and provides a template
 * for handling HTTP requests in a standardized way.
 * <p>
 * The class defines a common structure for processing requests, retrieving content,
 * and sending responses. It includes error handling for cases where requested content
 * is not found.
 *
 * @author Oleksandr Havrysh
 */
public abstract class AbstractServerHandler implements HttpHandler {

    private static final int SUCCESS_CODE = 200;
    private static final int NOT_FOUND_CODE = 404;
    private static final String ERROR_MESSAGE = "File not found: %s";

    /**
     * Handles incoming HTTP requests.
     * This method implements the {@link HttpHandler#handle(HttpExchange)} method.
     * It extracts the request path, retrieves the corresponding content, and sends
     * an appropriate response.
     *
     * @param exchange the {@link HttpExchange} object representing the current HTTP transaction
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var path = exchange.getRequestURI().getPath();
        var content = getContent(path);
        if (nonNull(content)) {
            writeBody(exchange, SUCCESS_CODE, content);
        } else {
            writeBody(exchange, NOT_FOUND_CODE, ERROR_MESSAGE.formatted(path).getBytes());
        }
    }

    /**
     * Retrieves the content for a given path.
     * This method should be implemented by subclasses to provide the specific
     * logic for content retrieval.
     *
     * @param path the path for which to retrieve content
     * @return a byte array containing the content, or null if no content is found
     */
    protected abstract byte[] getContent(String path);

    /**
     * Writes the response body to the HTTP exchange with improved performance.
     * <p>
     * This method sets the response headers, including the HTTP status code and content length,
     * and then writes the response body using a BufferedOutputStream for efficiency.
     * The method ensures that the output stream is properly closed after writing.
     *
     * @param exchange       the {@link HttpExchange} object representing the current HTTP transaction
     * @param httpStatusCode the HTTP status code to be set in the response
     * @param response       the body of the response as a byte array
     * @throws IOException if an I/O error occurs while sending the response headers or writing the response body
     */
    private static void writeBody(HttpExchange exchange, int httpStatusCode, byte[] response) throws IOException {
        exchange.sendResponseHeaders(httpStatusCode, response.length);
        try (var os = new BufferedOutputStream(exchange.getResponseBody())) {
            os.write(response);
        }
    }
}