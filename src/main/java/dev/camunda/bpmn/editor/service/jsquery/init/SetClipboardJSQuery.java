package dev.camunda.bpmn.editor.service.jsquery.init;

import com.intellij.openapi.ide.CopyPasteManager;
import dev.camunda.bpmn.editor.service.browser.JBCefBrowserWrapper;
import dev.camunda.bpmn.editor.service.jsquery.InitJSQuery;
import java.awt.datatransfer.StringSelection;

/**
 * A specific implementation of InitJSQuery for setting clipboard content.
 * This class creates a JavaScript function that can be called from the browser
 * to copy the given text content to the system clipboard.
 *
 * @author Oleksandr Havrysh
 */
public class SetClipboardJSQuery extends InitJSQuery {

    /**
     * The JavaScript code template for creating the copyBpmnClipboard function.
     * The %s placeholder will be replaced with the injected handler code.
     */
    private static final String SET_CLIPBOARD_JS = """
            window.copyBpmnClipboard = function(text) {
                %s
            }""";

    /**
     * Constructs a new SetClipboardJSQuery.
     *
     * @param browser The JBCefBrowserWrapper instance on which the JavaScript query will be executed.
     */
    public SetClipboardJSQuery(JBCefBrowserWrapper browser) {
        super(browser,
                jbCefJSQuery -> SET_CLIPBOARD_JS.formatted(jbCefJSQuery.inject("text")),
                text -> CopyPasteManager.getInstance().setContents(new StringSelection(text)),
                10);
    }
}