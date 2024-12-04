package dev.camunda.bpmn.editor.server.handler;

import static java.util.Objects.nonNull;
import static org.apache.commons.io.IOUtils.toByteArray;

import java.io.IOException;

/**
 * Handler for BPMN Editor UI HTTP requests.
 * This class is responsible for serving static files for the BPMN Editor UI.
 * It extends the AbstractServerHandler and implements the getContent method
 * to retrieve static resources from the classpath.
 *
 * <p>The handler uses the class loader to access resources, making it suitable
 * for serving files packaged within the application's JAR or classpath.</p>
 *
 * <p>This handler is typically used in conjunction with an HTTP server to
 * provide the necessary UI files (HTML, CSS, JavaScript, etc.) for the
 * BPMN Editor interface.</p>
 *
 * @author Oleksandr Havrysh
 */
public class UIServerHandler extends AbstractServerHandler {

    /**
     * Retrieves the content of a static file based on the given path.
     *
     * <p>This method attempts to load the requested resource from the classpath
     * using the class loader.
     * It removes the leading slash from the path to
     * correctly locate the resource within the classpath.</p>
     *
     * @param path The path of the requested resource, typically starting with a slash.
     * @return A byte array containing the file content if found, or null if the file
     * does not exist or an IO error occurs during reading.
     */
    @Override
    protected byte[] getContent(String path) {
        try (var fileStream = UIServerHandler.class.getClassLoader().getResourceAsStream(path.substring(1))) {
            return nonNull(fileStream) ? toByteArray(fileStream) : null;
        } catch (IOException e) {
            return null;
        }
    }
}