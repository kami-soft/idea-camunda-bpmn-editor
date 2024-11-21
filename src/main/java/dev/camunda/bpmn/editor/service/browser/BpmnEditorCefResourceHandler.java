package dev.camunda.bpmn.editor.service.browser;

import static java.lang.System.arraycopy;
import static java.util.Objects.nonNull;
import static org.apache.commons.io.IOUtils.toByteArray;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

/**
 * Handles resource requests for the BPMN editor using the CEF (Chromium Embedded Framework).
 * This class processes requests for various file types and serves their contents from the classpath resources.
 * It implements caching to improve performance for frequently accessed resources.
 */
public class BpmnEditorCefResourceHandler implements CefResourceHandler {

    /**
     * Map of file extensions to their corresponding MIME types.
     */
    private static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html",
            "css", "text/css",
            "js", "application/javascript",
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "gif", "image/gif",
            "txt", "text/plain",
            "json", "application/json"
    );

    /**
     * Character used to separate file name from extension.
     */
    private static final char DOT = '.';

    /**
     * HTTP status code for successful requests.
     */
    private static final int SUCCESS_CODE = 200;

    /**
     * MIME type for plain text content.
     */
    private static final String TEXT_PLAIN = "text/plain";

    /**
     * Format string for resource paths in the classpath.
     */
    private static final String RESOURCE_PATH = "bpmn-editor-ui/%s";

    /**
     * Format string for error messages when a file is not found.
     */
    private static final String ERROR_MESSAGE = "File not found: %s";

    /**
     * Default MIME type for unknown file types.
     */
    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    /**
     * Cache for storing loaded file contents to improve performance.
     */
    private final Cache<String, byte[]> contentCache = Caffeine.newBuilder()
            .maximumSize(10)
            .build();

    private byte[] fileContent;
    private int offset;
    private String mimeType;

    /**
     * Processes a resource request and loads the corresponding file content.
     * This method attempts to load the requested file from the cache or the classpath resources.
     *
     * @param request  the CefRequest containing the resource request details
     * @param callback the CefCallback to signal when the request processing is complete
     * @return true if the request was processed successfully, regardless of whether the file was found
     */
    @Override
    public boolean processRequest(CefRequest request, CefCallback callback) {
        var path = getPath(request);
        fileContent = contentCache.get(path, this::loadFile);
        if (nonNull(fileContent)) {
            mimeType = MIME_TYPES.getOrDefault(getExtension(path), APPLICATION_OCTET_STREAM);
        } else {
            fileContent = ERROR_MESSAGE.formatted(path).getBytes();
            mimeType = TEXT_PLAIN;
        }

        callback.Continue();
        return true;
    }

    /**
     * Extracts the path from the given CefRequest.
     * This method attempts to parse the URL as a URI to extract the path.
     * If parsing fails, it falls back to using the full URL.
     *
     * @param request the CefRequest to extract the path from
     * @return the extracted path as a String
     */
    private static String getPath(CefRequest request) {
        try {
            return new URI(request.getURL()).getPath();
        } catch (URISyntaxException e) {
            return request.getURL();
        }
    }

    /**
     * Sets the response headers for the resource request.
     * This method sets the MIME type, status code, and content length for the response.
     *
     * @param response       the CefResponse to set headers on
     * @param responseLength an IntRef to set the length of the response
     * @param redirectUrl    a StringRef for any redirect URL (not used in this implementation)
     */
    @Override
    public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
        response.setMimeType(mimeType);
        response.setStatus(SUCCESS_CODE);
        responseLength.set(fileContent.length);
    }

    /**
     * Reads the response data for the resource request.
     * This method copies the requested amount of data from the file content to the output buffer.
     *
     * @param dataOut     the byte array to write the response data to
     * @param bytesToRead the number of bytes to read
     * @param bytesRead   an IntRef to set the number of bytes actually read
     * @param callback    the CefCallback to signal when reading is complete (not used in this implementation)
     * @return true if there is more data to read, false if the end of the data has been reached
     */
    @Override
    public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
        if (offset >= fileContent.length) {
            bytesRead.set(0);
            return false;
        }

        var bytesToCopy = Math.min(bytesToRead, fileContent.length - offset);
        arraycopy(fileContent, offset, dataOut, 0, bytesToCopy);
        offset += bytesToCopy;
        bytesRead.set(bytesToCopy);
        return true;
    }

    /**
     * Cancels the current resource request, releasing any resources held.
     * This method clears the file content buffer.
     */
    @Override
    public void cancel() {
        fileContent = null;
        mimeType = null;
    }

    /**
     * Loads a file from the classpath resources.
     * This method attempts to load the file from the specified path in the classpath.
     *
     * @param path the path of the file to load
     * @return the file contents as a byte array, or null if the file is not found or an error occurs
     */
    private byte[] loadFile(String path) {
        try (var fileStream = BpmnEditorCefResourceHandler.class.getClassLoader()
                .getResourceAsStream(RESOURCE_PATH.formatted(path))) {
            return nonNull(fileStream) ? toByteArray(fileStream) : null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Retrieves the file extension from the given file name.
     * This method extracts the part of the file name after the last dot character.
     *
     * @param fileName the name of the file
     * @return the file extension as a String in lowercase, or an empty string if no extension is found
     */
    private String getExtension(String fileName) {
        var dotIndex = fileName.lastIndexOf(DOT);
        return dotIndex > 0 ? fileName.substring(dotIndex + 1).toLowerCase() : "";
    }
}