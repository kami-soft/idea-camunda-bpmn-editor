package dev.camunda.bpmn.editor.service.jsquery.impl;

import lombok.RequiredArgsConstructor;
import org.cef.browser.CefBrowser;

/**
 * A specific implementation of a JavaScript query for deleting a virtual file ID.
 * This class creates a JavaScript query to call a function that deletes a virtual file ID in the browser.
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public class DeleteVirtualFileIdJSQuery {

    /**
     * The JavaScript code template for calling the deleteVirtualFileId function.
     * The %s placeholder will be replaced with the actual virtual file ID.
     */
    private static final String DELETE_VIRTUAL_FILE_ID_SCRIPT_JS = "deleteVirtualFileId('%s');";

    private final CefBrowser cefBrowser;

    /**
     * Executes the JavaScript query to delete the virtual file ID.
     *
     * @param virtualFileId The ID of the virtual file to delete
     */
    public void executeQuery(String virtualFileId) {
        var query = DELETE_VIRTUAL_FILE_ID_SCRIPT_JS.formatted(virtualFileId);
        cefBrowser.executeJavaScript(query, cefBrowser.getURL(), 0);
    }
}