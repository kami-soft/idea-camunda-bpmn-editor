package dev.camunda.bpmn.editor.browser.jsquery.impl;

import com.intellij.ui.jcef.JBCefBrowser;
import dev.camunda.bpmn.editor.browser.jsquery.SimpleJSQuery;
import org.jetbrains.annotations.NotNull;

/**
 * A specific implementation of SimpleJSQuery for deleting a virtual file ID.
 * This class creates a JavaScript query to call a function that deletes a virtual file ID in the browser.
 *
 * @author Oleksandr Havrysh
 */
public class DeleteVirtualFileIdJSQuery extends SimpleJSQuery {

    /**
     * The JavaScript code template for calling the deleteVirtualFileId function.
     * The %s placeholder will be replaced with the actual virtual file ID.
     */
    private static final String DELETE_VIRTUAL_FILE_ID_SCRIPT_JS = "deleteVirtualFileId('%s');";

    /**
     * The virtual file ID to be deleted.
     */
    private final String virtualFileId;

    /**
     * Constructs a new DeleteVirtualFileIdJSQuery.
     *
     * @param browser       The JBCefBrowser instance on which the JavaScript query will be executed.
     * @param virtualFileId The ID of the virtual file to be deleted.
     */
    public DeleteVirtualFileIdJSQuery(@NotNull JBCefBrowser browser, @NotNull String virtualFileId) {
        super(browser);
        this.virtualFileId = virtualFileId;
    }

    /**
     * Returns the JavaScript query string to be executed.
     * This query calls the deleteVirtualFileId function in the browser with the specified virtual file ID.
     *
     * @return A String representing the JavaScript query to delete the virtual file ID.
     */
    @Override
    public @NotNull String getQuery() {
        return DELETE_VIRTUAL_FILE_ID_SCRIPT_JS.formatted(virtualFileId);
    }
}