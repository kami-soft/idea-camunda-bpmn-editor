package dev.camunda.bpmn.editor.service.jsquery.impl;

import static dev.camunda.bpmn.editor.util.Base64Utils.encode;

import lombok.RequiredArgsConstructor;
import org.cef.browser.CefBrowser;

/**
 * A specific implementation of a JavaScript query for updating script content in the browser.
 * This class creates a JavaScript query to call a function that updates the script content
 * associated with a specific virtual file ID.
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public class UpdateScriptJSQuery {

    /**
     * The JavaScript code template for calling the updateScript function.
     * The first %s placeholder will be replaced with the virtual file ID,
     * and the second %s placeholder will be replaced with the Base64 encoded script content.
     */
    private static final String UPDATE_SCRIPT_JS = "updateScript('%s', `%s`);";

    private final CefBrowser cefBrowser;

    /**
     * Executes the JavaScript query to update the script content.
     *
     * @param script        The new script content
     * @param virtualFileId The ID of the virtual file associated with the script
     */
    public void executeQuery(String script, String virtualFileId) {
        var query = UPDATE_SCRIPT_JS.formatted(virtualFileId, encode(script));
        cefBrowser.executeJavaScript(query, cefBrowser.getURL(), 0);
    }
}