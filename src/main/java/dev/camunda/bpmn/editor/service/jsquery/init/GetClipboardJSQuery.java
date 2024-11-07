package dev.camunda.bpmn.editor.service.jsquery.init;

import static dev.camunda.bpmn.editor.util.Constants.FAILURE_CALLBACK_QUERY;
import static dev.camunda.bpmn.editor.util.Constants.SUCCESS_CALLBACK_QUERY;
import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.datatransfer.DataFlavor.stringFlavor;
import static java.util.Objects.isNull;

import dev.camunda.bpmn.editor.service.browser.JBCefBrowserWrapper;
import dev.camunda.bpmn.editor.service.jsquery.InitJSQuery;

/**
 * A specific implementation of InitJSQuery for retrieving clipboard content.
 * This class creates a JavaScript function that can be called from the browser
 * to get the current text content of the system clipboard.
 *
 * @author Oleksandr Havrysh
 */
public class GetClipboardJSQuery extends InitJSQuery {

    /**
     * The JavaScript code template for creating the getBpmnClipboard function.
     * The %s placeholder will be replaced with the injected handler code.
     */
    private static final String GET_CLIPBOARD_JS = """
            window.getBpmnClipboard = function(text) {
                return new Promise((resolve, reject) => {
                    %s
                });
            }""";

    /**
     * Constructs a new GetClipboardJSQuery.
     *
     * @param browser The JBCefBrowserWrapper instance on which the JavaScript query will be executed.
     */
    public GetClipboardJSQuery(JBCefBrowserWrapper browser) {
        super(browser,
                jbCefJSQuery -> GET_CLIPBOARD_JS.formatted(jbCefJSQuery.inject("text",
                        SUCCESS_CALLBACK_QUERY, FAILURE_CALLBACK_QUERY)),
                text -> getClipboardString());
    }

    /**
     * Retrieves the current text content of the system clipboard.
     *
     * @return The text content of the clipboard, or null if the clipboard is empty or an error occurs.
     */
    private static String getClipboardString() {
        var clipboard = getDefaultToolkit().getSystemClipboard();
        if (isNull(clipboard) || !clipboard.isDataFlavorAvailable(stringFlavor)) {
            return null;
        }

        try {
            return (String) clipboard.getData(stringFlavor);
        } catch (Exception e) {
            return null;
        }
    }
}