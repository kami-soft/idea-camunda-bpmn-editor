package dev.camunda.bpmn.editor.service.jsquery;

import static dev.camunda.bpmn.editor.util.Base64Utils.encode;

import dev.camunda.bpmn.editor.service.browser.JBCefBrowserWrapper;
import lombok.RequiredArgsConstructor;

/**
 * Service class for executing JavaScript queries related to virtual file operations.
 * This class provides methods to execute queries for deleting virtual files and updating scripts.
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public class JSQueryService {

    private static final String UPDATE_SCRIPT_JS = "updateScript('%s', `%s`);";
    private static final String DELETE_VIRTUAL_FILE_ID_JS = "deleteVirtualFileId('%s');";

    private final JBCefBrowserWrapper browser;

    /**
     * Executes the query to delete the virtual file with the specified ID.
     *
     * @param virtualFileId the ID of the virtual file to delete
     */
    public void executeQueryDeleteVirtualFileId(String virtualFileId) {
        browser.executeQuery(DELETE_VIRTUAL_FILE_ID_JS.formatted(virtualFileId));
    }

    /**
     * Executes the query to update the script for the virtual file with the specified ID.
     *
     * @param virtualFileId the ID of the virtual file
     * @param script        the new script content
     */
    public void executeQueryUpdateScript(String virtualFileId, String script) {
        browser.executeQuery(UPDATE_SCRIPT_JS.formatted(virtualFileId, encode(script)));
    }
}