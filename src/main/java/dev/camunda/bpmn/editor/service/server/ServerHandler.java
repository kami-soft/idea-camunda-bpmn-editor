package dev.camunda.bpmn.editor.service.server;

import static dev.camunda.bpmn.editor.util.Constants.NOT_FOUND_CODE;
import static dev.camunda.bpmn.editor.util.Constants.SUCCESS_CODE;
import static java.util.Objects.nonNull;
import static org.apache.commons.io.IOUtils.toByteArray;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Handler for BPMN Editor UI HTTP requests.
 * This class is responsible for serving static files for the BPMN Editor UI.
 * It implements caching of file contents to improve performance for subsequent requests.
 *
 * @author Oleksandr Havrysh
 */
public class ServerHandler implements HttpHandler {

    private static final String RESOURCE_PATH = "bpmn-editor-ui/%s";
    private static final String ERROR_MESSAGE = "File not found: %s";

    /**
     * Handles incoming HTTP requests.
     * Attempts to serve the requested file from the cache or classpath resources.
     * If the file is not found, returns a 404 error.
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var path = exchange.getRequestURI().getPath();
        var fileContent = loadFile(RESOURCE_PATH.formatted(path));
        if (nonNull(fileContent)) {
            writeBody(exchange, SUCCESS_CODE, fileContent);
        } else {
            writeBody(exchange, NOT_FOUND_CODE, ERROR_MESSAGE.formatted(path).getBytes());
        }
    }

    /**
     * Loads a file from the classpath resources.
     *
     * @param filePath the path of the file to load
     * @return the file contents as a byte array, or null if the file is not found
     */
    private byte[] loadFile(String filePath) {
        try (var fileStream = ServerHandler.class.getClassLoader().getResourceAsStream(filePath)) {
            return nonNull(fileStream) ? toByteArray(fileStream) : null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Writes the response body to the HTTP exchange.
     * Uses a BufferedOutputStream for improved performance.
     *
     * @param exchange       the HTTP exchange to write the response to
     * @param httpStatusCode the HTTP status code to send
     * @param response       the body of the response as a byte array
     * @throws IOException if an I/O error occurs while writing the response
     */
    private static void writeBody(HttpExchange exchange, int httpStatusCode, byte[] response) throws IOException {
        exchange.sendResponseHeaders(httpStatusCode, response.length);
        try (var os = new BufferedOutputStream(exchange.getResponseBody())) {
            os.write(response);
        }
    }
}