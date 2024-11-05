package dev.camunda.bpmn.editor.service.jsquery;

import dev.camunda.bpmn.editor.service.jsquery.impl.DeleteVirtualFileIdJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.impl.UpdateScriptJSQuery;
import lombok.RequiredArgsConstructor;

/**
 * Service class for executing JavaScript queries related to virtual file operations.
 * This class provides methods to execute queries for deleting virtual files and updating scripts.
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public class JSQueryService {

    private final UpdateScriptJSQuery updateScriptJSQuery;
    private final DeleteVirtualFileIdJSQuery deleteVirtualFileIdJSQuery;

    /**
     * Executes the query to delete the virtual file with the specified ID.
     *
     * @param virtualFileId the ID of the virtual file to delete
     */
    public void executeQueryDeleteVirtualFileId(String virtualFileId) {
        deleteVirtualFileIdJSQuery.executeQuery(virtualFileId);
    }

    /**
     * Executes the query to update the script for the virtual file with the specified ID.
     *
     * @param virtualFileId the ID of the virtual file
     * @param script        the new script content
     */
    public void executeQueryUpdateScript(String virtualFileId, String script) {
        updateScriptJSQuery.executeQuery(script, virtualFileId);
    }
}