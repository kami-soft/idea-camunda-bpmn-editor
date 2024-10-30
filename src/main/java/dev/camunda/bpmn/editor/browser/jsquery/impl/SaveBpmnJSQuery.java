package dev.camunda.bpmn.editor.browser.jsquery.impl;

import static com.intellij.openapi.application.ApplicationManager.getApplication;
import static com.intellij.openapi.vfs.VirtualFileUtil.writeBytes;
import static java.util.Base64.getDecoder;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.HandledJSQuery;
import dev.camunda.bpmn.editor.util.HashComparator;
import org.jetbrains.annotations.NotNull;

/**
 * A specific implementation of HandledJSQuery for saving BPMN XML content.
 * This class creates a JavaScript function that can be called from the browser
 * to save the BPMN XML content to a file and update the hash comparator.
 *
 * @author Oleksandr Havrysh
 */
public class SaveBpmnJSQuery extends HandledJSQuery {

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
     * @param browser        The JBCefBrowser instance on which the JavaScript query will be executed.
     * @param hashComparator The HashComparator used to update the hash of the BPMN XML content.
     * @param virtualFile    The VirtualFile representing the file where the BPMN XML will be saved.
     */
    public SaveBpmnJSQuery(@NotNull JBCefBrowser browser,
                           @NotNull HashComparator hashComparator,
                           @NotNull VirtualFile virtualFile) {
        super(browser, encodedXml -> {
            var xml = getDecoder().decode(encodedXml);
            getApplication().invokeLater(() -> getApplication().runWriteAction(() -> {
                writeBytes(virtualFile, xml);
                hashComparator.updateHash(xml);
            }));
            return new JBCefJSQuery.Response(null);
        });
    }

    /**
     * Returns the JavaScript query string to be executed.
     * This query creates a function in the browser's window object that can be called to save the BPMN XML content.
     *
     * @return A String representing the JavaScript query to create the updateBpmnXml function.
     */
    @Override
    public @NotNull String getQuery() {
        return SAVE_BPMN_JS.formatted(jbCefJSQuery.inject("xml"));
    }
}