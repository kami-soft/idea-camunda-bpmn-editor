package dev.camunda.bpmn.editor.service.jsquery.impl;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static com.intellij.openapi.vfs.VirtualFileUtil.writeBytes;
import static dev.camunda.bpmn.editor.util.Base64Utils.decodeBytes;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.InitJSQuery;
import dev.camunda.bpmn.editor.util.HashComparator;

/**
 * A specific implementation of InitJSQuery for saving BPMN XML content.
 * This class creates a JavaScript function that can be called from the browser
 * to save the BPMN XML content to a file and update the hash comparator.
 *
 * @author Oleksandr Havrysh
 */
public class SaveBpmnJSQuery extends InitJSQuery {

    /**
     * The JavaScript code template for creating the updateBpmnXml function.
     * The %s placeholder will be replaced with the injected handler code.
     */
    private static final String SAVE_BPMN_JS = """
            window.updateBpmnXml = function(xml) {
                %s
            }
            """;

    /**
     * Constructs a new SaveBpmnJSQuery.
     *
     * @param file           The VirtualFile representing the file where the BPMN XML will be saved.
     * @param browser        The JBCefBrowser instance on which the JavaScript query will be executed.
     * @param hashComparator The HashComparator used to update the hash of the BPMN XML content.
     */
    public SaveBpmnJSQuery(VirtualFile file, JBCefBrowser browser, HashComparator hashComparator) {
        super(browser,
                jbCefJSQuery -> SAVE_BPMN_JS.formatted(jbCefJSQuery.inject("xml")),
                encodedXml -> {
                    var xml = decodeBytes(encodedXml);
                    getApplication().invokeLater(() -> getApplication().runWriteAction(() -> {
                        writeBytes(file, xml);
                        hashComparator.updateHash(xml);
                    }));
                    return new JBCefJSQuery.Response(null);
                });
    }
}